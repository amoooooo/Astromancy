package aster.amo.astromancy.space.classification.stellarobjects.star

import aster.amo.astromancy.lumen.Lumen
import net.minecraft.util.random.Weight
import net.minecraft.util.random.WeightedEntry
import net.minecraft.util.random.WeightedRandomList

enum class StarType(val type: String, private val chance: Int) : WeightedEntry {
    NORMAL("Normal", 100),
    EXOTIC("Exotic", 5),
    PURE("Pure", 5),
    EMPTY("Empty", 5),
    HELL("Hell", 5);

    override fun getWeight(): Weight {
        return Weight.of(chance)
    }

    companion object {
        var list: WeightedRandomList<StarType> = WeightedRandomList.create(
            NORMAL,
            EXOTIC,
            PURE,
            EMPTY,
            HELL
        )

        fun starTypeFromHighestLumen(lumenMap: Map<Lumen, Float>): StarType {
            var highestLumen: Lumen? = null
            var highestLumenStrength = 0f
            for ((key, value) in lumenMap) {
                if (value > highestLumenStrength) {
                    highestLumen = key
                    highestLumenStrength = value
                }
            }
            if (highestLumen == null) {
                return NORMAL
            }
            return highestLumen.getStarType()
        }
    }
}
