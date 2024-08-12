package aster.amo.astromancy.space.classification.stellarobjects

import aster.amo.astromancy.util.RomanNumeralHelper
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.Attributes
import org.joml.Vector3d
import java.awt.Color
import java.util.*

class Planet(
    name: String,
    size: Double,
    mass: Float,
    axisTilt: Double,
    position: Vector3d,
    var skyColor: Color = Color.BLACK,
    var landColor: Color = Color.BLACK,
    var oceanColor: Color = Color.BLACK,
    var hasOcean: Boolean = false,
    var hasLand: Boolean = false,
    var hasAtmosphere: Boolean = false,
    var hasRings: Boolean = false,
    var hasClouds: Boolean = false,
    var ringCount: Int = 0,
    var atmosphereThickness: Int = 0,
    var atmosphereDensity: Int = 0,
    var rotationSpeed: Float = 0f,
    var orbitSpeed: Float = 0f,
    var orbitRadius: Float = 0f,
    var orbitAngle: Float = 0f,
    var attribute: Attribute = Attributes.MAX_HEALTH.value(),
    val orbitingBodies: MutableList<CelestialBody> = mutableListOf()
) : CelestialBody(name, size, mass, axisTilt, position) {


    constructor(name: String) : this(name, 1.0, 1.0f, 0.0, Vector3d(0.0,0.0,0.0))
    constructor(name: String, uuid: UUID, size: Int) : this(name, size.toDouble(), 0.0f, 0.0, Vector3d(0.0,0.0,0.0))

    override fun toNbt(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString("type", "planet")
        nbt.putString("name", name)
        nbt.putUUID("uuid", uuid)
        nbt.putFloat("mass", mass)
        nbt.putDouble("axisTilt", axisTilt)
        nbt.putFloat("rotationSpeed", rotationSpeed)
        nbt.putFloat("orbitSpeed", orbitSpeed)
        nbt.putFloat("orbitRadius", orbitRadius)
        nbt.putFloat("orbitAngle", orbitAngle)
        nbt.putBoolean("hasAtmosphere", hasAtmosphere)
        nbt.putBoolean("hasRings", hasRings)
        nbt.putBoolean("hasClouds", hasClouds)
        nbt.putInt("ringCount", ringCount)
        nbt.putInt("atmosphereThickness", atmosphereThickness)
        nbt.putInt("atmosphereDensity", atmosphereDensity)
        nbt.putInt("skyColor", skyColor.rgb)
        nbt.putInt("landColor", landColor.rgb)
        nbt.putInt("oceanColor", oceanColor.rgb)
        nbt.putBoolean("hasOcean", hasOcean)
        nbt.putBoolean("hasLand", hasLand)
        nbt.putString("attribute", BuiltInRegistries.ATTRIBUTE.getKey(attribute).toString())

        val orbitingBodiesNbt = ListTag()
        orbitingBodies.forEach { orbitingBody ->
            val bodyNbt = orbitingBody.toNbt()
            orbitingBodiesNbt.add(bodyNbt)
        }
        nbt.put("orbitingBodies", orbitingBodiesNbt)

        return nbt
    }



    override fun update(deltaTime: Float) {
    }

    companion object {
        fun fromNbt(nbt: CompoundTag): Planet {
            val planet = Planet(
                nbt.getString("name"),
                nbt.getDouble("size"),
                nbt.getFloat("mass"),
                nbt.getDouble("axisTilt"),
                Vector3d(0.0, 0.0, 0.0)
            )

            with(planet) {
                uuid = nbt.getUUID("uuid")
                mass = nbt.getFloat("mass")
                axisTilt = nbt.getDouble("axisTilt")
                rotationSpeed = nbt.getFloat("rotationSpeed")
                orbitSpeed = nbt.getFloat("orbitSpeed")
                orbitRadius = nbt.getFloat("orbitRadius")
                orbitAngle = nbt.getFloat("orbitAngle")
                hasAtmosphere = nbt.getBoolean("hasAtmosphere")
                hasRings = nbt.getBoolean("hasRings")
                hasClouds = nbt.getBoolean("hasClouds")
                ringCount = nbt.getInt("ringCount")
                atmosphereThickness = nbt.getInt("atmosphereThickness")
                atmosphereDensity = nbt.getInt("atmosphereDensity")
                skyColor = Color(nbt.getInt("skyColor"))
                landColor = Color(nbt.getInt("landColor"))
                oceanColor = Color(nbt.getInt("oceanColor"))
                hasOcean = nbt.getBoolean("hasOcean")
                hasLand = nbt.getBoolean("hasLand")

                attribute = BuiltInRegistries.ATTRIBUTE.get(ResourceLocation.tryParse(nbt.getString("attribute")))!!

                orbitingBodies.clear()
                nbt.getList("orbitingBodies", CompoundTag.TAG_COMPOUND.toInt())?.let { bodyList ->
                    for (i in 0 until bodyList.size) {
                        val bodyNbt = bodyList[i] as CompoundTag
                        CelestialBody.fromNbt(bodyNbt)?.let { orbitingBodies.add(it) }
                    }
                }
            }

            return planet
        }

        fun generatePlanet(): Planet {
            val random = RandomSource.createNewThreadLocalInstance()
            val planet = Planet("Planet", UUID.randomUUID(), 1)
            planet.position = Vector3d(random.nextDouble(), random.nextDouble(), random.nextDouble())
            planet.mass = random.nextFloat() * 10000
            planet.axisTilt = random.nextDouble() * 360
            planet.rotationSpeed = random.nextFloat() * 100
            planet.orbitSpeed = random.nextFloat() * 100
            planet.orbitRadius = random.nextFloat() * 100
            planet.orbitAngle = random.nextFloat() * 360
            planet.hasAtmosphere = random.nextBoolean()
            planet.hasRings = random.nextBoolean()
            planet.hasClouds = random.nextBoolean()
            planet.ringCount = random.nextInt(10)
            planet.atmosphereThickness = random.nextInt(10)
            planet.atmosphereDensity = random.nextInt(10)
            planet.skyColor = generateColor()
            planet.landColor = generateColor()
            planet.oceanColor = generateColor()
            planet.hasOcean = random.nextBoolean()
            planet.hasLand = random.nextBoolean()
            planet.attribute = Attributes.MAX_HEALTH.value()
            val orbitingBodies = generateMoons()
            planet.orbitingBodies.addAll(orbitingBodies)
            planet.name = generatePlanetName(planet)
            return planet

        }
        fun generateColor(): Color {
            val rand = Random()
            val r = rand.nextFloat() / 2f + 0.5
            val g = rand.nextFloat() / 2f + 0.5
            val b = rand.nextFloat() / 2f + 0.5
            return Color(r.toFloat(), g.toFloat(), b.toFloat())
        }

        fun generateMoons(): List<Moon> {
            val moons = mutableListOf<Moon>()
            for (i in 0 until RandomSource.createNewThreadLocalInstance().nextInt(10)) {
                moons.add(Moon.generateMoon())
            }
            return moons
        }
        fun generatePlanetName(planet: Planet): String {
            val rand = Random()
            val base = arrayOf(
                "Agamemnon",
                "Agenor",
                "Aigisthos",
                "Alkmene",
                "Amazonen",
                "Amphitryon",
                "Andromeda",
                "Arachne",
                "Ariadne",
                "Atalanta",
                "Bellerophon",
                "Daidalos",
                "Danaiden",
                "Deukalion",
                "Dioskuren",
                "Elektra",
                "Europa",
                "Eurystheus",
                "Hekabe",
                "Hektor",
                "Helena",
                "Hero",
                "Iason",
                "Ikaros",
                "Ixion",
                "Kassandra",
                "Laokoon",
                "Leander",
                "Melampous",
                "Menelaos",
                "Minos",
                "Nestor",
                "Odysseus",
                "Oknos",
                "Orestes",
                "Orpheus",
                "Paris",
                "Pasiphae",
                "Penelope",
                "Perseus",
                "Priamos",
                "Pyrrha",
                "Sisyphos",
                "Tantalos",
                "Telemachos",
                "Theseus",
                "Acheloos",
                "Aletheia",
                "Anemoi",
                "Asklepios",
                "Charon",
                "Hekate",
                "Hesperiden",
                "Horen",
                "Hypnos",
                "Iris",
                "Moiren",
                "Musen",
                "Morpheus",
                "Musen",
                "Nymphen",
                "Oneiroi",
                "Pan",
                "Plutos",
                "Prometheus",
                "Thanatos",
                "Tyche",
                "Zelos"
            )
            val greekLetters = arrayOf(
                "Alpha",
                "Beta",
                "Gamma",
                "Delta",
                "Epsilon",
                "Zeta",
                "Eta",
                "Theta",
                "Iota",
                "Kappa",
                "Lambda",
                "Mu",
                "Nu",
                "Xi",
                "Omicron",
                "Pi",
                "Rho",
                "Sigma",
                "Tau",
                "Upsilon",
                "Phi",
                "Chi",
                "Psi",
                "Omega"
            )
            val toRoman: String = RomanNumeralHelper.toRoman(planet.mass.toInt() / 100).toString()
            val name =
                base[rand.nextInt(base.size)] + " " + greekLetters[rand.nextInt(greekLetters.size)] + " " + toRoman
            return name
        }
    }
}