package de.cypdashuhn.rooster.region

import com.google.gson.Gson
import de.cypdashuhn.rooster.core.Rooster
import de.cypdashuhn.rooster.database.YmlShell
import org.bukkit.configuration.file.FileConfiguration

object RegisteredRegionManager : YmlShell("regions.yml", baseDirectory = Rooster.roosterFolder) {
    private val regionMap = mutableMapOf<RegionId, Region>()
    private val keyMap = mutableMapOf<String, RegionId>()

    fun onEnable(fileConfiguration: FileConfiguration) {
        val regionMapJson = fileConfiguration.getString("regions")
        val keyMapJson = fileConfiguration.getString("keys")
        regionMap.putAll(Gson().fromJson(regionMapJson, regionIdToRegionMapType.rawType) ?: emptyMap())
        keyMap.putAll(Gson().fromJson(keyMapJson, keyToRegionIdMapType) ?: emptyMap())
    }

    fun onDisable(fileConfiguration: FileConfiguration) {
        changeConfig {
            fileConfiguration.set("regions", Gson().toJson(regionMap))
            fileConfiguration.set("keys", Gson().toJson(keyMap))
        }
    }

    fun addRegion(region: Region, key: String? = null) {
        val regionId = firstEmptyId()
        addRegion(region, regionId, key)
    }

    fun addRegion(region: Region, regionId: RegionId, key: String? = null) {
        regionMap[regionId] = region
        key?.let { keyMap[it] = regionId }
    }

    fun firstEmptyId(): RegionId {
        var regionId = 0
        while (true) {
            if (regionById(regionId) == null) {
                return regionId
            }
            regionId += 1
        }
    }

    fun regionById(regionId: RegionId): Region? {
        return regionMap[regionId]
    }

    fun regionByKey(key: String): Region? {
        val regionId = keyMap[key] ?: return null
        return regionById(regionId)
    }

    fun regionIdByKey(key: String): RegionId? {
        return keyMap[key]
    }

    fun regionIdByReference(region: Region): RegionId? {
        return regionMap.filter { it.value == region }.keys.firstOrNull()
    }

    fun regionIdByValue(region: Region): RegionId? {
        return regionMap.filter {
            (it.value.edge1 == region.edge1 &&
                    it.value.edge2 == region.edge2) ||
                    (it.value.edge1 == region.edge2 &&
                            it.value.edge2 == region.edge1)
        }.keys.firstOrNull()
    }

    fun deleteById(regionId: RegionId) {
        regionMap.remove(regionId)
        keyMap.entries.removeIf { it.value == regionId }
    }
}

typealias RegionMap = Map<RegionId, Region>
typealias KeyMap = Map<String, RegionId>