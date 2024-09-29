package de.cypdashuhn.rooster.region.register

import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.region.*
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import kotlin.reflect.KClass

class SqlRegisteredRegionProvider : RegisteredRegionProvider() {
    init {
        Rooster.dynamicTables += RegisteredRegions
    }

    object RegisteredRegions : IntIdTable("RoosterRegisteredPositions") {
        val regionJson = text("region_json")
        val regionKey = varchar("region_key", 256).nullable()
        val lambdaKey = varchar("lambda_key", 256).nullable()
        val eventTargetJson = text("event_target_json")
        val contextJson = text("context").nullable()
    }

    class RegisteredRegion(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<RegisteredRegion>(RegisteredRegions)

        val region: Region by RegisteredRegions.regionJson.transform(
            { region -> Gson().toJson(region) },
            { json -> Gson().fromJson(json, Region::class.java) })
        val regionKey by RegisteredRegions.regionKey
        val lambdaKey by RegisteredRegions.lambdaKey
        private val contextJson by RegisteredRegions.contextJson

        fun <T : Any> context(contextClass: KClass<T>): T? {
            if (contextJson == null) return null
            return Gson().fromJson(contextJson, contextClass.java)
        }
    }

    override fun add(region: Region, regionKey: String?, regionLambda: RegionLambda) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> add(
        region: Region,
        regionKey: String?,
        regionLambda: RegionLambdaWithContext<T>,
        context: T
    ) {
        TODO("Not yet implemented")
    }

    override fun <T : Any> changeFunctionContext(regionId: RegionId, context: T): ChangeContextReturnType {
        TODO("Not yet implemented")
    }

    override fun get(region: Region): RegisteredRegions? {
        TODO("Not yet implemented")
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