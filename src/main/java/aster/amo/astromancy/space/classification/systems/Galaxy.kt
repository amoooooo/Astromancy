package aster.amo.astromancy.space.classification.systems

import aster.amo.astromancy.space.classification.constellation.Constellation
import aster.amo.astromancy.space.classification.stellarobjects.Star
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.util.RandomSource
import org.joml.Vector3d
import java.util.*

class Galaxy(
    val name: String,
    val position: Vector3d,
    val starSystems: MutableList<StarSystem> = mutableListOf(),
    var uuid: UUID = UUID.randomUUID(),
    var constellations: MutableList<Constellation> = mutableListOf()
) {

    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("name", name)
        tag.putDouble("x", position.x)
        tag.putDouble("y", position.y)
        tag.putDouble("z", position.z)
        val systems: ListTag = ListTag()
        starSystems.forEach {
            systems.add(it.toNbt())
        }
        tag.put("systems", systems)
        tag.putUUID("uuid", uuid)
        val constellationsTag = ListTag()
        constellations.forEach {
            constellationsTag.add(it.toNbt())
        }
        tag.put("constellations", constellationsTag)
        return tag
    }

    fun getStars(): List<Star> {
        return starSystems.map { it.gravityCenter }.filterIsInstance<Star>()
    }

    companion object {
        fun fromNbt(tag: CompoundTag): Galaxy {
            val name = tag.getString("name")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val systems = mutableListOf<StarSystem>()
            val systemsTag = tag.getList("systems", 10)
            systemsTag.forEach {
                systems.add(StarSystem.fromNbt(it as CompoundTag))
            }
            val uuid = tag.getUUID("uuid")
            val constellations = mutableListOf<Constellation>()
            val constellationsTag = tag.getList("constellations", 10)
            constellationsTag.forEach {
                constellations.add(Constellation.fromNbt(it as CompoundTag))
            }
            return Galaxy(name, position, systems, uuid, constellations)
        }

        fun generateGalaxy(): Galaxy {
            val systems = mutableListOf<StarSystem>()
            for (i in 0 until 25) {
                systems.add(StarSystem.generateStarSystem())
            }
            return Galaxy(generateGalaxyName(), Vector3d(), systems)
        }
        fun generateGalaxyName(): String {
            val prefixes = listOf(
                "Aether", "Astra", "Celestial", "Cosmic", "Ember", "Eternal", "Galactic", "Harmony",
                "Luminous", "Nebula", "Nova", "Orion", "Phantom", "Quantum", "Radiant", "Serene",
                "Solar", "Spectral", "Starlight", "Stellar", "Twilight", "Void", "Whispering"
            )

            val suffixes = listOf(
                "Arc", "Belt", "Cluster", "Core", "Current", "Drift", "Echo", "Field", "Flow",
                "Garden", "Halo", "Heart", "Maelstrom", "Nebula", "Reach", "Rift", "Spiral",
                "Stream", "Surge", "Swirl", "Tide", "Trail", "Wake", "Wind", "Zone"
            )

            val adjectives = listOf(
                "Ancient", "Azure", "Blazing", "Crimson", "Dark", "Diamond", "Emerald", "Fading",
                "Frozen", "Golden", "Hidden", "Inner", "Lost", "Outer", "Pale", "Phantom",
                "Radiant", "Scarlet", "Shadow", "Silver", "Spectral", "Whispering", "Wild"
            )

            val rand = RandomSource.createNewThreadLocalInstance()
            // Randomly choose name structure:
            val nameStructure = rand.nextInt(1, 5) // 4 different structures
            val name = when (nameStructure) {
                1 -> "${prefixes.random()} ${suffixes.random()}" // "Aether Rift"
                2 -> "${adjectives.random()} ${suffixes.random()}" // "Hidden Nebula"
                3 -> "${prefixes.random()} ${adjectives.random()} ${suffixes.random()}" // "Celestial Golden Stream"
                4 -> "${adjectives.random()} ${prefixes.random()} ${suffixes.random()}" // "Golden Aether Arc"
                else -> "Unknown Galaxy" // Default (shouldn't happen)
            }

            // Add Roman numeral for extra variations (optional)
            val romanNumerals = listOf("I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X")
            return if (rand.nextBoolean()) {
                "$name ${romanNumerals.random()}"
            } else {
                name
            }
        }
    }
}