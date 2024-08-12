package aster.amo.astromancy.space.classification.stellarobjects

import net.minecraft.nbt.CompoundTag
import org.joml.Vector3d
import java.util.*

abstract class CelestialBody(
    var name: String,
    val size: Double,
    var mass: Float,
    var axisTilt: Double,
    var position: Vector3d,
) {
    var uuid: UUID = UUID.randomUUID()

    abstract fun update(deltaTime: Float)

    open fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("name", name)
        tag.putFloat("mass", mass)
        tag.putDouble("size", size)
        tag.putDouble("axisTilt", axisTilt)
        tag.putDouble("x", position.x)
        tag.putDouble("y", position.y)
        tag.putDouble("z", position.z)
        tag.putUUID("uuid", uuid)
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): CelestialBody? {
            val type = tag.getString("type")
            return when(type) {
                "star" -> Star.fromNbt(tag)
                "binary_system" -> BinarySystem.fromNbt(tag)
                "planet" -> Planet.fromNbt(tag)
                "moon" -> Moon.fromNbt(tag)
                "meteorite" -> Meteorite.fromNbt(tag)
                else -> null
            }
        }

        fun generateCelestialBody(): CelestialBody {
            return Planet.generatePlanet()
        }
    }
}