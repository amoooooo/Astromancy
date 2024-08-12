package aster.amo.astromancy.space.classification.stellarobjects

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import org.joml.Vector3d

class BinarySystem(
    name: String,
    size: Double, // Size of the whole system
    axisTilt: Double,
    position: Vector3d,
    val star1: Star,
    val star2: Star,
    mass: Float = star1.mass + star2.mass
) : CelestialBody(name, size, mass, axisTilt, position), GravityCenter {

    override fun update(deltaTime: Float) {
    }

    override fun toNbt(): CompoundTag {
        val nbt = super.toNbt()
        nbt.putString("type", "binary_system")
        nbt.put("star1", star1.toNbt())
        nbt.put("star2", star2.toNbt())
        return nbt
    }

    companion object {
        fun fromNbt(tag: CompoundTag): BinarySystem {
            val name = tag.getString("name")
            val size = tag.getDouble("size")
            val axisTilt = tag.getDouble("axisTilt")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val star1 = Star.fromNbt(tag.getCompound("star1"))
            val star2 = Star.fromNbt(tag.getCompound("star2"))
            return BinarySystem(name, size, axisTilt, position, star1, star2)
        }

        fun generateBinarySystem(): BinarySystem {
            val star1 = Star.generateStar()
            val star2 = Star.generateStar()
            val systemSize = star1.size + star2.size
            val axisTilt = RandomSource.createNewThreadLocalInstance().nextDouble() * 180.0
            return BinarySystem(generateBinarySystemName(), systemSize, axisTilt, Vector3d(0.0, 0.0, 0.0), star1, star2)
        }
        fun generateBinarySystemName(): String {
            val rand = RandomSource.createNewThreadLocalInstance()
            val greekLetters = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega")
            val constellationNames = listOf("Andromeda", "Aquila", "Aries", "Auriga", "Boötes", "Cancer", "Canis Major", "Canis Minor", "Capricornus", "Cassiopeia", "Centaurus", "Cepheus", "Cetus", "Corona Borealis", "Corvus", "Crater", "Cygnus", "Delphinus", "Draco", "Equuleus", "Eridanus", "Gemini", "Hercules", "Hydra", "Leo", "Libra", "Lupus", "Lyra", "Ophiuchus", "Orion", "Pegasus", "Perseus", "Pisces", "Sagittarius", "Scorpius", "Taurus", "Ursa Major", "Ursa Minor", "Virgo")
            val descriptors = listOf("Twin", "Double", "Binary", "Paired", "Entangled", "Dancing", "Echoing", "Mirrored")
            val numericSuffixes = (1..999).toList()

            val nameStructure = rand.nextInt(1, 5) // 4 different structures

            val name = when (nameStructure) {
                1 -> "${constellationNames.random()} ${descriptors.random()} ${greekLetters.random()}" // "Orion Twin Alpha"
                2 -> "${descriptors.random()} ${constellationNames.random()} ${greekLetters.random()}" // "Double Cygnus Zeta"
                3 -> "${constellationNames.random()} ${greekLetters.random()} ${descriptors.random()}" // "Andromeda Beta Paired"
                4 -> "${greekLetters.random()} ${constellationNames.random()} ${descriptors.random()} ${numericSuffixes.random()}" // "Sigma Draco Entangled 142"
                else -> "Unknown Binary"
            }

            return name
        }
    }
}