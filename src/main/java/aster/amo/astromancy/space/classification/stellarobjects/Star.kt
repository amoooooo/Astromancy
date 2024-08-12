package aster.amo.astromancy.space.classification.stellarobjects

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.lumen.Lumen
import aster.amo.astromancy.space.classification.constellation.Constellations
import aster.amo.astromancy.space.classification.stellarobjects.star.LuminosityClass
import aster.amo.astromancy.space.classification.stellarobjects.star.SpectralIntensityBand
import aster.amo.astromancy.space.classification.stellarobjects.star.StarClass
import aster.amo.astromancy.space.classification.stellarobjects.star.StarType
import aster.amo.astromancy.space.classification.stellarobjects.star.StarType.Companion.starTypeFromHighestLumen
import aster.amo.astromancy.util.RomanNumeralHelper
import aster.amo.astromancy.util.humanize
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import org.joml.Vector3d
import java.util.*
import kotlin.math.abs

class Star(
    name: String,
    size: Double,
    axisTilt: Double,
    position: Vector3d,
    mass: Float,
    val starClass: StarClass?,
    var starType: StarType,
    var luminosity: Pair<Int, LuminosityClass>,
    var spectralClass: Pair<Int, Char>,
    var orbitSpeed: Float,
    var lumen: MutableMap<Lumen, Float> = mutableMapOf()
) : CelestialBody(name, size, mass, axisTilt, position), GravityCenter {

    companion object {
        val RANDOM = Random()

        fun fromNbt(tag: CompoundTag): Star {
            val name = tag.getString("name")
            val size = tag.getDouble("size")
            val axisTilt = tag.getDouble("axisTilt")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val starClass = StarClass.getStarClassFromIntensity(tag.getInt("spectralIntensity"))
            val starType = StarType.valueOf(tag.getString("starType"))
            val luminosity = Pair(tag.getInt("luminosity"), LuminosityClass.getLuminosityClassFromString(tag.getString("luminosityClass"))!!)
            val mass = tag.getFloat("mass")
            val spectralClass = Pair(tag.getInt("spectralIntensity"), tag.getString("spectralClass")[0])
            val orbitSpeed = tag.getFloat("orbitSpeed")
            val lumenTag = tag.getCompound("lumen")
            val lumen = mutableMapOf<Lumen, Float>()
            for (key in lumenTag.allKeys) {
                lumen[Lumen.getLumenTypeFromString(key)!!] = lumenTag.getFloat(key)
            }
            return Star(name, size, axisTilt, position, mass, starClass, starType, luminosity, spectralClass, orbitSpeed, lumen)
        }

        fun generateStar(): Star {
            val random = RandomSource.createNewThreadLocalInstance()
            val spectralIntBand: SpectralIntensityBand = SpectralIntensityBand.bands.getRandom(random).get()
            val spectralIntensity: Int =
                random.nextInt(spectralIntBand.lowerBound + 1, spectralIntBand.upperBound)
            val star = Star(spectralIntensity)
            val constellation = Constellations.entries.random()
            star.name = run {
                val name = StringBuilder()
                name.append(constellation.humanize())
                name.append(" ")
                name.append(star.starClass?.luminosityClass)
                name.append(star.starClass?.spectralClass)
                name.append(" ")
                name.append(RomanNumeralHelper.toRoman((star.starClass?.spectralIntensity ?: 1)/100))
                name.append(" ")
                name.append(star.starClass?.humanize())
                name.toString()
            }
            return star
        }
    }

    constructor(spectralIntensity: Int) : this(
        name = "Star",
        size = 1.0,
        axisTilt = 0.0,
        position = Vector3d(0.0,0.0,0.0),
        starClass = StarClass.getStarClassFromIntensity(spectralIntensity),
        starType = StarType.NORMAL,
        luminosity = Pair(0, LuminosityClass.O),
        mass = 0f,
        spectralClass = Pair(spectralIntensity, 'A'),
        orbitSpeed = 0f,
        lumen = mutableMapOf()
    ) {
        if(starClass == null) {
            Astromancy.LOGGER.error("Failed to create star: $this")
            return
        }
        val luminosityClass = starClass.luminosityClass
        val luminosityRange = luminosityClass.luminosityRange
        this.luminosity = Pair(
            (RANDOM.nextFloat(luminosityRange.first, luminosityRange.second) + luminosityRange.first).toInt(), // Assuming luminosity is Int
            luminosityClass
        )

        this.mass = 1000 * RANDOM.nextFloat(starClass.massRange.first, starClass.massRange.second)
//        this.renderOffset = RANDOM.nextFloat(0.01f) - 0.005f

        val lumenCount: Int = RANDOM.nextInt(3) + 1
        var lumenStrength = 1f
        for (i in 0 until lumenCount - 1) {
            val lumenStrengthRandom: Float = RANDOM.nextFloat(abs(lumenStrength.toDouble()).toFloat()) + 0.1f
            lumen[Lumen.LIST.getRandom(RandomSource.create()).get()] = lumenStrengthRandom
            lumenStrength -= lumenStrengthRandom
        }
        lumen[Lumen.LIST.getRandom(RandomSource.create()).get()] = lumenStrength

        this.starType = starTypeFromHighestLumen(this.lumen)
    }

    override fun toString(): String {
        return "Star(name='$name', size=$size, axisTilt=$axisTilt, position=$position, starClass=$starClass, starType=$starType, luminosity=$luminosity, mass=$mass, spectralClass=$spectralClass, orbitSpeed=$orbitSpeed, lumen=$lumen)"
    }

    override fun toNbt(): CompoundTag {
        val tag = super.toNbt()
        tag.putString("type", "star")
        tag.putString("starType", starType.name)
        tag.putInt("luminosity", luminosity.first)
        tag.putString("luminosityClass", luminosity.second.className)
        tag.putFloat("mass", mass)
        tag.putInt("spectralIntensity", spectralClass.first)
        tag.putString("spectralClass", spectralClass.second.toString())
        tag.putFloat("orbitSpeed", orbitSpeed)
        val lumenTag = CompoundTag()
        for ((lumen, strength) in lumen) {
            lumenTag.putFloat(lumen.type, strength)
        }
        tag.put("lumen", lumenTag)
        return tag
    }

    override fun update(deltaTime: Float) {
    }
}