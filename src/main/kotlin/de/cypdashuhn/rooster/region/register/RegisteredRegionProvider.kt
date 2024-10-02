package de.cypdashuhn.rooster.region.register

import de.cypdashuhn.rooster.region.*

abstract class RegisteredRegionProvider {
    init {

    }

    internal val regionActions: MutableMap<RegionId, RegionLambdaWrapper<*>> = mutableMapOf()

    abstract fun add(
        region: Region,
        regionKey: String? = null,
        regionLambda: RegionLambda
    )

    abstract fun <T : Any> add(
        region: Region,
        regionKey: String? = null,
        regionLambda: RegionLambdaWithContext<T>,
        context: T
    )

    enum class ChangeContextReturnType(val success: Boolean) {
        OK(true),
        REGION_NOT_FOUND(false),
        REGION_WITHOUT_CONTEXT_LAMBDA(false),
        CONTEXT_NOT_SAME_TYPE(false)
    }

    abstract fun <T : Any> changeFunctionContext(
        regionId: RegionId,
        context: T
    ): ChangeContextReturnType

    fun <T : Any> changeFunctionContext(
        regionKey: String,
        context: T
    ): ChangeContextReturnType {
        val registeredRegion = get(regionKey) ?: return ChangeContextReturnType.REGION_NOT_FOUND
        return changeFunctionContext(registeredRegion.regionId, context)
    }

    fun <T : Any> changeFunctionContext(
        region: Region,
        context: T
    ): ChangeContextReturnType {
        val registeredRegion = get(region) ?: return ChangeContextReturnType.REGION_NOT_FOUND
        return changeFunctionContext(registeredRegion.regionId, context)
    }

    abstract fun get(region: Region): RegisteredRegion?

    abstract fun get(regionId: RegionId): RegisteredRegion?

    abstract fun get(key: String): RegisteredRegion?


    enum class DeleteReturnType(val success: Boolean) {
        OK(true),
        REGION_NOT_FOUND(false)
    }

    abstract fun delete(regionId: RegionId): DeleteReturnType
    fun delete(regionKey: String): DeleteReturnType {
        val registeredRegion = get(regionKey) ?: return DeleteReturnType.REGION_NOT_FOUND
        return delete(registeredRegion.regionId)
    }

    fun delete(region: Region): DeleteReturnType {
        val registeredRegion = get(region) ?: return DeleteReturnType.REGION_NOT_FOUND
        return delete(registeredRegion.regionId)
    }
}
