package aster.amo.astromancy.space.classification.stellarobjects.star

import com.mojang.datafixers.util.Pair
import net.minecraft.util.random.Weight
import net.minecraft.util.random.WeightedEntry

enum class LuminosityClass(val className: String, private val chance: Int, val luminosityRange: Pair<Float, Float>) :
        WeightedEntry {
    O("O", 10, Pair.of(25.0f, 50000.0f)),
    I("I", 45, Pair.of(5.0f, 24.9f)),
    II("II", 35, Pair.of(1.5f, 4.9f)),
    III("III", 10, Pair.of(1.0f, 1.49f)),
    IV("IV", 10, Pair.of(0f, 0f)),
    V("V", 5, Pair.of(0.6f, 1.0f)),
    VI("VI", 5, Pair.of(0.08f, 0.6f)),
    VII("VII", 5, Pair.of(0.0f, 0.08f));

    override fun getWeight(): Weight {
        return Weight.of(chance)
    }

    companion object {
        fun getLuminosityClassFromString(luminosityClass: String): LuminosityClass? {
            for (luminosityClass1 in entries) {
                if (luminosityClass1.className == luminosityClass) {
                    return luminosityClass1
                }
            }
            return null
        }
    }
}
