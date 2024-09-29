package de.cypdashuhn.rooster.region

import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

val regionIdToRegionMapType = object : ParameterizedType {
    override fun getRawType(): Type {
        return Map::class.java
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(RegionId::class.java, Region::class.java)
    }

    override fun getOwnerType(): Type? {
        return null
    }
}

val keyToRegionIdMapType = object : ParameterizedType {
    override fun getRawType(): Type {
        return Map::class.java
    }

    override fun getActualTypeArguments(): Array<Type> {
        return arrayOf(String::class.java, RegionId::class.java)
    }

    override fun getOwnerType(): Type? {
        return null
    }
}