package aster.amo.astromancy.lumen

import aster.amo.astromancy.space.classification.stellarobjects.star.StarType
import net.minecraft.util.random.Weight
import net.minecraft.util.random.WeightedEntry
import net.minecraft.util.random.WeightedRandomList

enum class Lumen(val type: String, val chance: Int, val powerMultiplier: Float, starType: StarType) :
        WeightedEntry {
    NONE("None", 0, 1f, StarType.NORMAL),
    DECONSTRUCTIVE("Deconstructive", 45, 1f, StarType.NORMAL),
    CONSTRUCTIVE("Constructive", 35, 1f, StarType.NORMAL),
    PURE("Pure", 10, 1.3f, StarType.PURE),
    HELL("Hell", 5, 1.5f, StarType.HELL),
    EXOTIC("Exotic", 5, 1.5f, StarType.EXOTIC),
    DENATURED("Denatured", 10, 1.5f, StarType.NORMAL),
    EMPTY("Null", 5, 2.0f, StarType.EMPTY);

    private val starType: StarType = starType

    override fun getWeight(): Weight {
        return Weight.of(chance)
    }

    fun getStarType(): StarType {
        return starType
    }

    companion object {
        fun getLumenTypeFromString(type: String): Lumen? {
            for (lumenType in entries) {
                if (lumenType.type == type) {
                    return lumenType
                }
            }
            return null
        }

        fun get(i: Int): Lumen {
            return entries[i]
        }

        val LIST: WeightedRandomList<Lumen> = WeightedRandomList.create(*entries.filter { it.chance > 0 }.toTypedArray())
    }
}