package aster.amo.astromancy.space.classification.stellarobjects.star

import com.mojang.datafixers.util.Pair
import net.minecraft.util.random.Weight
import net.minecraft.util.random.WeightedEntry

enum class StarClass(
    val type: String,
    private val chance: Int,
    val luminosityClass: LuminosityClass,
    val spectralIntensity: Int,
    val massRange: Pair<Float, Float>,
    val spectralClass: Char
) :
        WeightedEntry {
    ULTRAGIANT("Ultragiant", 5, LuminosityClass.O, 30000, Pair.of(16.0f, 50.0f), 'O'),
    HYPERGIANT("Hypergiant", 10, LuminosityClass.O, 10000, Pair.of(2.1f, 15.9f), 'B'),
    SUPERGIANT("Supergiant", 20, LuminosityClass.I, 7500, Pair.of(1.4f, 2.09f), 'A'),
    GIANT("Giant", 40, LuminosityClass.III, 6000, Pair.of(1.04f, 1.39f), 'F'),
    MAIN_SEQUENCE("Main Sequence", 70, LuminosityClass.V, 5200, Pair.of(0.8f, 1.039f), 'G'),
    DWARF("Dwarf", 40, LuminosityClass.VI, 3700, Pair.of(0.45f, 0.79f), 'K'),
    WHITE_DWARF("White Dwarf", 20, LuminosityClass.VII, 2400, Pair.of(0.08f, 0.449f), 'M');

    fun getChance(): Float {
        return chance.toFloat()
    }

    override fun getWeight(): Weight {
        return Weight.of(chance)
    }

    companion object {
        fun getStarClassFromIntensity(spectralIntensity: Int): StarClass? {
            // check if spectral intensity is greater than any of the star classes
            for (starClass in entries) {
                if (spectralIntensity >= starClass.spectralIntensity) {
                    return starClass
                }
            }
            // if not, return null
            return null
        }

        fun fromString(type: String): StarClass? {
            for (starClass in entries) {
                if (starClass.type == type) {
                    return starClass
                }
            }
            return null
        }

        fun fromLuminosityClass(luminosityClass: LuminosityClass?): StarClass? {
            for (starClass in entries) {
                if (starClass.luminosityClass.equals(luminosityClass)) {
                    return starClass
                }
            }
            return null
        }
    }
}
