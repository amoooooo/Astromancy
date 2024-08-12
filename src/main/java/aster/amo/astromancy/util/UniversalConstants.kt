package aster.amo.astromancy.util

import com.mojang.datafixers.util.Pair
import kotlin.math.sqrt

object UniversalConstants {
    const val GRAVITATIONAL_CONSTANT: Double = 6.67408e-11
    fun calculateOrbitSpeed(mass: Double, radius: Double): Double {
        return sqrt((GRAVITATIONAL_CONSTANT * mass) / radius)
    }

    fun binaryStarCenterOfMass(mass1: Double, mass2: Double, distance: Double): Pair<Double, Double> {
        val a2 = mass1 / (mass1 + mass2) * distance
        val a1 = (mass2 * a2) / mass1
        return Pair(a1, a2)
    }

    fun binaryStarOrbitSpeed(mass1: Double, mass2: Double, distance: Double): Double {
        return sqrt((GRAVITATIONAL_CONSTANT * (mass1 + mass2)) / distance)
    }
}
