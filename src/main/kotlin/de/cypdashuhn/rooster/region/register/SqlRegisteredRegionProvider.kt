package de.cypdashuhn.rooster.region.register

import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.database.findEntry
import de.cypdashuhn.rooster.database.utility_tables.FunctionManager
import de.cypdashuhn.rooster.region.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import kotlin.reflect.KClass

class SqlRegisteredRegionProvider : RegisteredRegionProvider() {
    val functionManager = FunctionManager()

    init {
        Rooster.dynamicTables += RegisteredRegions
    }

    object RegisteredRegions : IntIdTable("RoosterRegisteredPositions") {
        val regionJson = text("region_json")
        val regionKey = varchar("region_key", 256).nullable()
        val functionId = reference("lambda_key", FunctionManager.Functions).nullable()
        val eventTargetJson = text("event_target_json")
        val contextJson = text("context").nullable().default(null)
    }

    class RegisteredRegion(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<RegisteredRegion>(RegisteredRegions)

        var region: Region by RegisteredRegions.regionJson.transform(
            { region -> Gson().toJson(region) },
            { json -> Gson().fromJson(json, Region::class.java) })
        var regionKey by RegisteredRegions.regionKey
        var function by FunctionManager.Function optionalReferencedOn RegisteredRegions.functionId
        var contextJson by RegisteredRegions.contextJson
        var eventTarget: EventTargetDTO by RegisteredRegions.eventTargetJson.transform(
            { eventTarget -> Gson().toJson(eventTarget) },
            { json -> Gson().fromJson(json, EventTargetDTO::class.java) }
        )

        fun <T : Any> context(contextClass: KClass<T>): T? {
            if (contextJson == null) return null
            return Gson().fromJson(contextJson, contextClass.java)
        }

        internal fun toDTO(regionLambda: RegionLambdaWrapper<*>): RegisteredRegionDTO {
            return RegisteredRegionDTO(
                region = region,
                regionKey = regionKey,
                regionId = id.value,
                regionLambda = regionLambda
            )
        }
    }

    override fun add(region: Region, regionKey: String?, regionLambda: RegionLambda) {
        val newEntry = RegisteredRegion.new {
            this.region = region
            this.regionKey = regionKey
            this.function = if (regionLambda.lambdaKey != null) functionManager.addKey(regionLambda.lambdaKey) else null
            this.eventTarget = regionLambda.eventTargetDTO
        }

        regionActions[newEntry.id.value] = RegionLambdaWrapper<Any>(regionLambda)
    }

    override fun <T : Any> add(
        region: Region,
        regionKey: String?,
        regionLambda: RegionLambdaWithContext<T>,
        context: T
    ) {
        val newEntry = RegisteredRegion.new {
            this.region = region
            this.regionKey = regionKey
            this.function = if (regionLambda.lambdaKey != null) functionManager.addKey(regionLambda.lambdaKey) else null
            this.eventTarget = regionLambda.eventTargetDTO
            this.contextJson = Gson().toJson(context)
        }

        regionActions[newEntry.id.value] = RegionLambdaWrapper(regionLambda)
    }

    override fun <T : Any> changeFunctionContext(regionId: RegionId, context: T): ChangeContextReturnType {
        TODO("Not yet implemented")
    }

    override fun get(region: Region): RegisteredRegionDTO? {
        RegisteredRegion.findEntry(RegisteredRegions.regionJson eq Gson().toJson(region))
            ?.toDTO(regionActions[region.id.value]!!)
    }

    override fun get(regionId: RegionId): RegisteredRegions? {
        TODO("Not yet implemented")
    }

    override fun get(key: String): RegisteredRegions? {
        TODO("Not yet implemented")
    }

    override fun delete(regionId: RegionId): DeleteReturnType {
        TODO("Not yet implemented")
    }
}