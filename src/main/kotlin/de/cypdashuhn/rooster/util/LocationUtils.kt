package de.cypdashuhn.rooster.util

import org.bukkit.Location
import org.bukkit.World
import org.joml.Vector3d

fun Location.toVector3d(): Vector3d {
    return Vector3d(this.x, this.y, this.z)
}

fun Vector3d.toLocation(world: World, yaw: Float = 0f, pitch: Float = 0f): Location {
    return Location(world, this.x, this.y, this.z, yaw, pitch)
}