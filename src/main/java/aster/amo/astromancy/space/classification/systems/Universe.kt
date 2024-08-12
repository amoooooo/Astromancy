package aster.amo.astromancy.space.classification.systems

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.Astromancy.DATA
import aster.amo.astromancy.space.classification.constellation.Constellation
import aster.amo.astromancy.space.classification.constellation.House
import aster.amo.astromancy.space.classification.constellation.Houses
import aster.amo.astromancy.space.classification.matter.glyph.GlyphSequence
import aster.amo.astromancy.space.classification.stellarobjects.CelestialBody
import aster.amo.astromancy.space.classification.stellarobjects.GravityCenter
import aster.amo.astromancy.space.classification.stellarobjects.Planet
import aster.amo.astromancy.space.classification.stellarobjects.Star
import aster.amo.astromancy.util.humanize
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.mojang.serialization.Dynamic
import com.mojang.serialization.JsonOps
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.NbtIo
import net.minecraft.nbt.NbtOps
import net.minecraft.nbt.NbtUtils
import net.minecraft.util.RandomSource
import net.neoforged.neoforge.common.IOUtilities
import net.neoforged.neoforge.common.util.JsonUtils
import java.awt.Color
import java.io.File
import java.util.UUID

class Universe(
    val superclusters: MutableList<Supercluster> = mutableListOf(),
    var earth: UUID? = null
) {
    var earthPlanets: List<Planet> = listOf()
        get() {
            if(field.isEmpty()) {
                field = earthStarSystem!!.getPlanets().map { it as Planet }
            }
            return field
        }
    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        val listTag = ListTag()
        superclusters.forEach {
            listTag.add(it.toNbt())
        }
        tag.put("superclusters", listTag)
        tag.putUUID("earth", earth)
        return tag
    }

    fun prettyPrintUniverse(): String {
        // formulate a string that represents the universe, including all superclusters, houses, galaxies, star systems, and stars names
        val sb = StringBuilder()
        superclusters.forEach { supercluster ->
            sb.append("Supercluster: ${supercluster.name}\n")
            supercluster.house?.let { house ->
                sb.append("\tHouse: ${house.name}\n")
            }
            supercluster.galaxies.forEach { galaxy ->
                sb.append("\tGalaxy: ${galaxy.name}\n")
                galaxy.starSystems.forEach { starSystem ->
                    sb.append("\t\tStar System: ${starSystem.name}\n")
                    starSystem.celestialBodies.forEach { stellarObject ->
                        if(stellarObject is Planet) {
                            sb.append("\t\t\t\tPlanet: ${stellarObject.name}\n")
                            stellarObject.orbitingBodies.forEach { moon ->
                                sb.append("\t\t\t\t\tMoon: ${moon.name}\n")
                            }
                        } else if(stellarObject is Star) {
                            sb.append("\t\t\t\tStar: ${stellarObject.name}\n")
                        }
                    }
                    (starSystem.gravityCenter as CelestialBody).let { star ->
                        sb.append("\t\t\tStar: ${star.name}\n")
                    }
                }
                galaxy.constellations.forEach { constellation ->
                    sb.append("\t\tConstellation: ${constellation.name}\n")
                    constellation.stars.forEach { star ->
                        sb.append("\t\t\tStar: ${getStarByUUID(star)?.name}\n")
                    }
                }
            }
        }
        return sb.toString()
    }


    fun writeUniverseToFile(filePath: String) {
        val universeString = DATA.universe.prettyPrintUniverse()
//        val universeJson: JsonElement = Dynamic.convert(NbtOps.INSTANCE, JsonOps.INSTANCE, toNbt())
        val file = File(filePath)

        try {
            file.parentFile?.mkdirs()
            file.createNewFile()
//            Gson().toJson(universeJson, file.writer())
            file.writeText(universeString)
            println("Universe data written to: $filePath")
        } catch (e: Exception) {
            println("Error writing universe data to file: ${e.message}")
        }
    }

    fun getStarByUUID(uuid: UUID): Star? {
        // find the star with the given UUID and return its name
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.getStars().forEach { star ->
                    if (star.uuid == uuid) {
                        return star
                    }
                }
            }
        }
        return null
    }

    fun getStars(): List<Star> {
        val stars = mutableListOf<Star>()
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.getStars().forEach { star ->
                    stars.add(star)
                }
            }
        }
        return stars
    }

    fun getRandomStarSystem(): StarSystem {
        // get a random star system from the universe
        val random = RandomSource.createNewThreadLocalInstance()
        val supercluster = superclusters[random.nextInt(superclusters.size)]
        val galaxy = supercluster.galaxies[random.nextInt(supercluster.galaxies.size)]
        return galaxy.starSystems[random.nextInt(galaxy.starSystems.size)]
    }

    var earthStarSystem: StarSystem? = null
        get() {
            if(field == null) {
                field = getEarthStarSystemInternal()
            }
            return field
        }
    private fun getEarthStarSystemInternal(): StarSystem? {
        // get the star system that contains Earth
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.starSystems.forEach { starSystem ->
                    starSystem.celestialBodies.forEach { stellarObject ->
                        if(stellarObject is Planet) {
                            if(stellarObject.uuid == earth) {
                                return starSystem
                            }
                        }
                    }
                }
            }
        }
        return null
    }

    fun getStarSystem(starSystemId: UUID): StarSystem? {
        // get a star system by its UUID
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.starSystems.forEach { starSystem ->
                    if (starSystem.uuid == starSystemId) {
                        return starSystem
                    }
                }
            }
        }
        return null
    }

    fun getConstellations(): List<Constellation> {
        val constellations = mutableListOf<Constellation>()
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.constellations.forEach { constellation ->
                    constellations.add(constellation)
                }
            }
        }
        return constellations
    }

    fun getStarForPlanet(planet: Planet): GravityCenter? {
        // get the star that the planet orbits
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.starSystems.forEach { starSystem ->
                    starSystem.celestialBodies.forEach { stellarObject ->
                        if(stellarObject == planet) {
                            return starSystem.gravityCenter
                        }
                    }
                }
            }
        }
        return null
    }

    fun getPlanet(planet: UUID): Planet {
        // get the planet by its UUID
        superclusters.forEach { supercluster ->
            supercluster.galaxies.forEach { galaxy ->
                galaxy.starSystems.forEach { starSystem ->
                    starSystem.celestialBodies.forEach { stellarObject ->
                        if(stellarObject is Planet) {
                            if(stellarObject.uuid == planet) {
                                return stellarObject
                            }
                        }
                    }
                }
            }
        }
        return Planet("Unknown", UUID.randomUUID(), 0)

    }

    companion object {
        fun fromNbt(tag: CompoundTag): Universe {
            val superclusters = mutableListOf<Supercluster>()
            val superclustersTag = tag.getList("superclusters", 10)
            superclustersTag.forEach {
                superclusters.add(Supercluster.fromNbt(it as CompoundTag))
            }
            val earth = tag.getUUID("earth")
            return Universe(superclusters, earth)
        }
        val earthSkyColorRange = Color(135, 206, 250) to Color(0, 191, 255) // Light blue to deep blue
        val earthLandColorRange = Color(34, 139, 34) to Color(139, 69, 19) // Forest green to brown
        val earthOceanColorRange = Color(0, 0, 139) to Color(65, 105, 225) // Dark blue to royal blue

        fun isColorInRange(color: Color, range: Pair<Color, Color>): Boolean {
            return (color.red in range.first.red..range.second.red) &&
                    (color.green in range.first.green..range.second.green) &&
                    (color.blue in range.first.blue..range.second.blue)
        }
        fun generateUniverse(): Universe {
            Astromancy.LOGGER.info("Generating universe...")
            val superclusters = mutableListOf<Supercluster>()
            for (i in 0 until 10) {
                superclusters.add(Supercluster.generateSupercluster())
            }
            val universe = Universe(superclusters)
            universe.earth = searchForEarthlikePlanet(universe)?.uuid ?: run {
                val earth = Planet.generatePlanet()
                earth.name = "Earth"
                earth.uuid = UUID.randomUUID()
                earth.hasAtmosphere = true
                earth.hasLand = true
                earth.hasOcean = true
                earth.hasClouds = true
                earth.atmosphereThickness = 100
                earth.atmosphereDensity = 100
                earth.skyColor = Color(135, 206, 250)
                earth.landColor = Color(34, 139, 34)
                earth.oceanColor = Color(0, 0, 139)
                universe.superclusters.random().galaxies.random().starSystems.random().celestialBodies.add(earth)
                Astromancy.LOGGER.info("Earth-like planet not found, generating new Earth-like planet")
                earth.uuid
            }
            populateHouses(universe)
            universe.superclusters.forEach { cluster: Supercluster ->
                cluster.galaxies.forEach { galaxy: Galaxy ->
                    galaxy.starSystems.forEach { starSystem: StarSystem ->
                        starSystem.glyphSequence = generateUniqueGlyphSequenceForStarSystem(starSystem, 7, universe)
                    }

                }
            }
            return universe

        }

        private fun searchForEarthlikePlanet(universe: Universe): Planet? {
            // search the universe for an earth-like planet
            val earthlikePlanets = mutableListOf<Planet>()
            universe.superclusters.forEach { supercluster ->
                supercluster.galaxies.forEach { galaxy ->
                    galaxy.starSystems.forEach { starSystem ->
                        starSystem.celestialBodies.forEach { stellarObject ->
                            if (stellarObject is Planet) {
                                if (stellarObject.hasAtmosphere && stellarObject.hasLand && stellarObject.hasOcean && stellarObject.hasClouds) {
                                    if (stellarObject.atmosphereThickness in 50..150 && stellarObject.atmosphereDensity in 50..150) {
                                        isColorInRange(stellarObject.skyColor, earthSkyColorRange) &&
                                                isColorInRange(stellarObject.landColor, earthLandColorRange) &&
                                                isColorInRange(stellarObject.oceanColor, earthOceanColorRange)
                                        earthlikePlanets.add(stellarObject)
                                    }
                                }
                            }
                        }
                    }
                }
            }
            return earthlikePlanets.randomOrNull()
        }

        private fun populateHouses(universe: Universe) {
            val random = RandomSource.createNewThreadLocalInstance()
            val housesToAssign = Houses.values().toMutableList()
            // pick 4 random superclusters
            val superclusters = universe.superclusters.shuffled().take(4)
            superclusters.forEach { supercluster ->
                // pick a random house and remove it from the list
                val house = housesToAssign.random()
                housesToAssign.remove(house)
                supercluster.house = House(house, house.humanize(), "House of ${house.humanize()}", supercluster.uuid)
                val constellationsToAssign = house.constellations.toMutableList()
                // pick x random galaxies where x is the number of constellations in the house
                val galaxies = supercluster.galaxies.shuffled().take(constellationsToAssign.size)
                galaxies.forEach { galaxy ->
                    // pick a random constellation and remove it from the list
                    val constellation = constellationsToAssign.random()
                    constellationsToAssign.remove(constellation)
                    val const = Constellation(constellation, constellation.prettyName, galaxy.uuid, galaxy.getStars().shuffled().take(random.nextInt(10)).map { it.uuid }.toMutableList())
                    galaxy.constellations.add(const)
                }
            }
        }
        fun generateUniqueGlyphSequenceForStarSystem(starSystem: StarSystem, length: Int, universe: Universe): GlyphSequence {
            var newSequence: GlyphSequence
            do {
                newSequence = GlyphSequence.generateGlyphSequence(length)
            } while (isGlyphSequenceInUse(newSequence, universe))

            starSystem.glyphSequence = newSequence
            return newSequence
        }

        private fun isGlyphSequenceInUse(sequence: GlyphSequence, universe: Universe): Boolean {
            return universe.superclusters.flatMap { it.galaxies }.flatMap { it.starSystems }
                .any { it.glyphSequence?.sequence == sequence.sequence }
        }
    }
}