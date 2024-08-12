package aster.amo.astromancy.space.classification.systems

import aster.amo.astromancy.space.classification.constellation.House
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import org.joml.Vector3d
import java.util.*

class Supercluster(
    val name: String,
    val position: Vector3d,
    val galaxies: MutableList<Galaxy> = mutableListOf(),
    var uuid: UUID = UUID.randomUUID(),
    var house: House? = null
) {
    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("name", name)
        tag.putDouble("x", position.x)
        tag.putDouble("y", position.y)
        tag.putDouble("z", position.z)
        val galaxiesTag: ListTag = ListTag()
        galaxies.forEach {
            galaxiesTag.add(it.toNbt())
        }
        tag.put("galaxies", galaxiesTag)
        tag.putUUID("uuid", uuid)
        if(house != null) {
            tag.put("house", house!!.toNbt())
        }
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): Supercluster {
            val name = tag.getString("name")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val galaxies = mutableListOf<Galaxy>()
            val galaxiesTag = tag.getList("galaxies", 10)
            galaxiesTag.forEach {
                galaxies.add(Galaxy.fromNbt(it as CompoundTag))
            }
            val uuid = tag.getUUID("uuid")
            val house = if(tag.contains("house")) {
                House.fromNbt(tag.getCompound("house"))
            } else {
                null
            }
            return Supercluster(name, position, galaxies, uuid, house)
        }

        fun generateSupercluster(): Supercluster {
            val galaxies = mutableListOf<Galaxy>()
            for (i in 0 until 25) {
                galaxies.add(Galaxy.generateGalaxy())
            }
            return Supercluster(generateSuperclusterName(), Vector3d(), galaxies)
        }
        fun generateSuperclusterName(): String {
            val prefixes = listOf("Veil", "Crown", "Whispers", "Echoes", "Tears", "Heart", "Breath", "Dream", "Serpent", "Dragon", "Phoenix", "Griffin")
            val connectors = listOf("of", "in", "beneath", "beyond")
            val suffixes = listOf("Infinity", "Eternity", "the Void", "Creation", "the Ancients", "Fallen Stars", "Cosmic Dawn", "Celestial Harmony")

            return "${prefixes.random()} ${connectors.random()} ${suffixes.random()}"
        }
    }
}