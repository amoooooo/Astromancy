package aster.amo.astromancy.util

import kotlin.math.pow

object MathHelper {
    fun remap(value: Float, from1: Float, to1: Float, from2: Float, to2: Float): Float {
        return (value - from1) / (to1 - from1) * (to2 - from2) + from2
    }

    fun normalize(`val`: Float, min: Float, max: Float): Float {
        return 1 - ((`val` - min) / (max - min))
    }

    fun ease(x: Float, eType: Easing): Float {
        return eType.ease(x)
    }

    enum class Easing : IEasing {
        easeInOutBack {
            override fun ease(x: Float): Float {
                val c1 = 1.70158f
                val c2 = c1 * 1.525f
                return (if (x < 0.5) ((2 * x).pow(2.0f) * ((c2 + 1) * 2 * x - c2)) / 2 else ((2 * x - 2).pow(2.0f) * ((c2 + 1) * (x * 2 - 2) + c2) + 2) / 2).toFloat()
            }
        },
        easeOutCubic {
            override fun ease(x: Float): Float {
                return (1f - (1 - x).pow(3.0f)).toFloat()
            }
        }
    }

    interface IEasing {
        fun ease(x: Float): Float
    }
}
