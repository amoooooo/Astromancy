package aster.amo.astromancy.space.classification.constellation

import aster.amo.astromancy.Astromancy
import com.mojang.datafixers.util.Pair
import net.minecraft.resources.ResourceLocation

enum class Constellations(
    val height: Int,
    val prettyName: String,
    val icon: ResourceLocation,
    val points: Array<Pair<Float, Float>>
) {
    JUSTICE(
        0, "Justice", Astromancy.astromancy("textures/environment/constellations/justice.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(5f, 1f),
            Pair.of<Float, Float>(-10f, 6f),
            Pair.of<Float, Float>(3f, -8f),
            Pair.of<Float, Float>(5f, 2f)
        )
    ),  //
    THE_HANGED_MAN(
        1,
        "The Hanged Man",
        Astromancy.astromancy("textures/environment/constellations/the_hanged_man.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(6f, -3f),
            Pair.of<Float, Float>(10f, -7f),
            Pair.of<Float, Float>(8f, -8f),
            Pair.of<Float, Float>(5f, 2f),
            Pair.of<Float, Float>(1f, 2f)
        )
    ),  //
    DEATH(
        2,
        "Death",
        Astromancy.astromancy("textures/environment/constellations/death.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(1f, 5f),
            Pair.of<Float, Float>(12f, -10f),
            Pair.of<Float, Float>(4f, -6f),
            Pair.of<Float, Float>(10f, -7f),
            Pair.of<Float, Float>(8f, -8f)
        )
    ),  //
    STRENGTH(
        -1, "Strength", Astromancy.astromancy("textures/environment/constellations/strength.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-5f, -1f), Pair.of<Float, Float>(-12f, -7f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_CHARIOT(
        -2,
        "The Chariot",
        Astromancy.astromancy("textures/environment/constellations/the_chariot.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-6f, 3f), Pair.of<Float, Float>(-10f, 7f), Pair.of<Float, Float>(-8f, -8f)
        )
    ),  //
    JUDGEMENT(
        -3,
        "Judgement",
        Astromancy.astromancy("textures/environment/constellations/judgement.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-1f, -5f),
            Pair.of<Float, Float>(-12f, 10f),
            Pair.of<Float, Float>(-4f, -6f),
            Pair.of<Float, Float>(-6f, 3f),
            Pair.of<Float, Float>(-10f, 7f)
        )
    ),  //
    THE_DEVIL(
        0, "The Devil", Astromancy.astromancy("textures/environment/constellations/the_devil.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(5f, 1f), Pair.of<Float, Float>(-10f, 6f), Pair.of<Float, Float>(3f, -8f)
        )
    ),  //
    THE_TOWER(
        1, "The Tower", Astromancy.astromancy("textures/environment/constellations/the_tower.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(6f, -3f), Pair.of<Float, Float>(10f, -7f), Pair.of<Float, Float>(8f, -8f)
        )
    ),  //
    THE_STAR(
        2, "The Star", Astromancy.astromancy("textures/environment/constellations/the_star.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(1f, 5f), Pair.of<Float, Float>(12f, -10f), Pair.of<Float, Float>(4f, -6f)
        )
    ),  //
    THE_MOON(
        -1, "The Moon", Astromancy.astromancy("textures/environment/constellations/the_moon.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-5f, -1f), Pair.of<Float, Float>(-12f, -7f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_SUN(
        -2, "The Sun", Astromancy.astromancy("textures/environment/constellations/the_sun.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-6f, 3f), Pair.of<Float, Float>(-10f, 7f), Pair.of<Float, Float>(-8f, -8f)
        )
    ),  //
    THE_FOOL(
        0, "The Fool", Astromancy.astromancy("textures/environment/constellations/the_fool.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-1f, -5f), Pair.of<Float, Float>(-12f, 10f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_MAGICIAN(
        1,
        "The Magician",
        Astromancy.astromancy("textures/environment/constellations/the_magician.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(6f, -3f), Pair.of<Float, Float>(10f, -7f), Pair.of<Float, Float>(8f, -8f)
        )
    ),  //
    THE_HERMIT(
        -1,
        "The Hermit",
        Astromancy.astromancy("textures/environment/constellations/the_hermit.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-1f, -5f), Pair.of<Float, Float>(-12f, 10f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    WHEEL_OF_FORTUNE(
        -2,
        "Wheel of Fortune",
        Astromancy.astromancy("textures/environment/constellations/wheel_of_fortune.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(5f, 1f), Pair.of<Float, Float>(-10f, 6f), Pair.of<Float, Float>(3f, -8f)
        )
    ),  //
    TEMPERANCE(
        -3,
        "Temperance",
        Astromancy.astromancy("textures/environment/constellations/temperance.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-5f, -1f), Pair.of<Float, Float>(-12f, -7f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_WORLD(
        4, "The World", Astromancy.astromancy("textures/environment/constellations/the_world.png"), arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-1f, -5f), Pair.of<Float, Float>(-12f, 10f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_EMPRESS(
        0,
        "The Empress",
        Astromancy.astromancy("textures/environment/constellations/the_empress.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(-1f, -5f), Pair.of<Float, Float>(-12f, 10f), Pair.of<Float, Float>(-4f, -6f)
        )
    ),  //
    THE_EMPEROR(
        1,
        "The Emperor",
        Astromancy.astromancy("textures/environment/constellations/the_emperor.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(1f, 5f), Pair.of<Float, Float>(12f, -10f), Pair.of<Float, Float>(4f, -6f)
        )
    ),  //
    THE_HIEROPHANT(
        2,
        "The Hierophant",
        Astromancy.astromancy("textures/environment/constellations/the_hierophant.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(1f, 5f), Pair.of<Float, Float>(12f, -10f), Pair.of<Float, Float>(4f, -6f)
        )
    ),  //
    THE_LOVERS(
        -1,
        "The Lovers",
        Astromancy.astromancy("textures/environment/constellations/the_lovers.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(6f, -3f), Pair.of<Float, Float>(10f, -7f), Pair.of<Float, Float>(8f, -8f)
        )
    ),  //
    THE_HIGH_PRIESTESS(
        -2,
        "The High Priestess",
        Astromancy.astromancy("textures/environment/constellations/the_high_priestess.png"),
        arrayOf<Pair<Float, Float>>(
            Pair.of<Float, Float>(5f, 1f), Pair.of<Float, Float>(-10f, 6f), Pair.of<Float, Float>(3f, -8f)
        )
    )
}
