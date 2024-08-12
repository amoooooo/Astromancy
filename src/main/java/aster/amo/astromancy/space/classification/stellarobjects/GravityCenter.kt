package aster.amo.astromancy.space.classification.stellarobjects

import net.minecraft.nbt.CompoundTag
import org.joml.Vector3d

interface GravityCenter {
    abstract fun update(deltaTime: Float)
    abstract fun toNbt(): CompoundTag

    companion object {
        fun fromNbt(tag: CompoundTag): GravityCenter? {
            val type = tag.getString("type")
            return when(type) {
                "star" -> Star.fromNbt(tag)
                "binary_system" -> BinarySystem.fromNbt(tag)
                else -> null
            }
        }
    }
}