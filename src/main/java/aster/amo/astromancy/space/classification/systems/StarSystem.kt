package aster.amo.astromancy.space.classification.systems

import aster.amo.astromancy.space.classification.matter.glyph.GlyphSequence
import aster.amo.astromancy.space.classification.stellarobjects.*
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import org.joml.Vector3d
import java.util.UUID
import kotlin.random.Random

class StarSystem(
    val name: String,
    val position: Vector3d,
    val gravityCenter: GravityCenter,
    val celestialBodies: MutableList<CelestialBody> = mutableListOf(),
    val uuid: UUID = UUID.randomUUID(),
    var glyphSequence: GlyphSequence? = null
) {

    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("name", name)
        tag.putDouble("x", position.x)
        tag.putDouble("y", position.y)
        tag.putDouble("z", position.z)
        tag.put("gravityCenter", gravityCenter.toNbt())
        val bodies: ListTag = ListTag()
        celestialBodies.forEach {
            bodies.add(it.toNbt())
        }
        tag.put("bodies", bodies)
        tag.putUUID("uuid", uuid)
        if (glyphSequence != null) {
            tag.put("glyphSequence", glyphSequence!!.toNbt())
        }
        return tag
    }

    fun getPlanets(): List<CelestialBody> {
        return celestialBodies.filterIsInstance<Planet>()
    }

    companion object {
        fun fromNbt(tag: CompoundTag): StarSystem {
            val name = tag.getString("name")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val gravityCenter = GravityCenter.fromNbt(tag.getCompound("gravityCenter"))
                ?: throw IllegalArgumentException("Gravity center is null")
            val bodies = mutableListOf<CelestialBody>()
            val bodiesTag = tag.getList("bodies", 10)
            bodiesTag.forEach {
                bodies.add(CelestialBody.fromNbt(it as CompoundTag)!!)
            }
            val uuid = tag.getUUID("uuid")
            val glyphSequence = if (tag.contains("glyphSequence")) GlyphSequence.fromNbt(tag.getCompound("glyphSequence")) else null
            return StarSystem(name, position, gravityCenter, bodies, uuid, glyphSequence)
        }

        fun generateStarSystem(): StarSystem {
            val bodies = mutableListOf<CelestialBody>()
            for (i in 0 until Random.nextInt(4, 16)) {
                bodies.add(CelestialBody.generateCelestialBody())
            }
            for (i in 0 until Random.nextInt(4, 64)) {
                bodies.add(Meteorite.generateMeteorite())
            }
            val gravityCenter = if(Random.nextFloat() < 0.1) BinarySystem.generateBinarySystem() else Star.generateStar()
            return StarSystem(generateStarSystemName(), Vector3d(), gravityCenter, bodies)
        }
        fun generateStarSystemName(): String {
            val greekLetters = listOf("Alpha", "Beta", "Gamma", "Delta", "Epsilon", "Zeta", "Eta", "Theta", "Iota", "Kappa", "Lambda", "Mu", "Nu", "Xi", "Omicron", "Pi", "Rho", "Sigma", "Tau", "Upsilon", "Phi", "Chi", "Psi", "Omega")
            val constellationNames = listOf("Andromeda", "Antlia", "Apus", "Aquarius", "Aquila", "Ara", "Aries", "Auriga", "Boötes", "Caelum", "Camelopardalis", "Cancer", "Canes Venatici", "Canis Major", "Canis Minor", "Capricornus", "Carina", "Cassiopeia", "Centaurus", "Cepheus", "Cetus", "Chamaeleon", "Circinus", "Columba", "Coma Berenices", "Corona Australis", "Corona Borealis", "Corvus", "Crater", "Crux", "Cygnus", "Delphinus", "Dorado", "Draco", "Equuleus", "Eridanus", "Fornax", "Gemini", "Grus", "Hercules", "Horologium", "Hydra", "Hydrus", "Indus", "Lacerta", "Leo", "Leo Minor", "Lepus", "Libra", "Lupus", "Lynx", "Lyra", "Mensa", "Microscopium", "Monoceros", "Musca", "Norma", "Octans", "Ophiuchus", "Orion", "Pavo", "Pegasus", "Perseus", "Phoenix", "Pictor", "Pisces", "Piscis Austrinus", "Puppis", "Pyxis", "Reticulum", "Sagitta", "Sagittarius", "Scorpius", "Sculptor", "Scutum", "Serpens", "Sextans", "Taurus", "Telescopium", "Triangulum", "Triangulum Australe", "Tucana", "Ursa Major", "Ursa Minor", "Vela", "Virgo", "Volans", "Vulpecula")
            val spectralClasses = listOf("O", "B", "A", "F", "G", "K", "M")
            val numericSuffixes = (1..9999).toList()

            val nameStructure = Random.nextInt(1, 6) // 5 different structures

            val name = when (nameStructure) {
                1 -> "${constellationNames.random()} ${greekLetters.random()}-${numericSuffixes.random()}" // "Orion Zeta-3456"
                2 -> "${greekLetters.random()} ${constellationNames.random()} ${numericSuffixes.random()}" // "Sigma Draco 123"
                3 -> "${constellationNames.random()}-${numericSuffixes.random()} ${greekLetters.random()}" // "Cygnus-8765 Alpha"
                4 -> "${spectralClasses.random()}${greekLetters.random()}-${numericSuffixes.random()}" // "G Beta-7890"
                5 -> "${constellationNames.random()}-${spectralClasses.random()}${numericSuffixes.random()}" // "Andromeda-K1234"
                else -> "Unknown System" // Default (should not happen)
            }

            return name
        }
    }
}