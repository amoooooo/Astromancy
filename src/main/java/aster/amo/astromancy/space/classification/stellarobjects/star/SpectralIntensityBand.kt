package aster.amo.astromancy.space.classification.stellarobjects.star

import net.minecraft.util.random.Weight
import net.minecraft.util.random.WeightedEntry
import net.minecraft.util.random.WeightedRandomList

enum class SpectralIntensityBand(val lowerBound: Int, val upperBound: Int, private val chance: Int) :
        WeightedEntry {
    O(30000, 50000, 5),
    B(10000, 29999, 10),
    A(7500, 9999, 20),
    F(6000, 7499, 20),
    G(5200, 5999, 40),
    K(3700, 5199, 40),
    M(2400, 3699, 60);

    override fun getWeight(): Weight {
        return Weight.of(chance)
    }

    companion object {
        var bands: WeightedRandomList<SpectralIntensityBand> = WeightedRandomList.create(
            O,
            B,
            A,
            F,
            G,
            K,
            M
        )
    }
}
