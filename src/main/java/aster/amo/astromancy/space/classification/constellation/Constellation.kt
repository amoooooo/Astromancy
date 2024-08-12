package aster.amo.astromancy.space.classification.constellation

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag
import net.minecraft.world.level.levelgen.XoroshiroRandomSource
import net.minecraft.world.level.levelgen.synth.SimplexNoise
import java.util.*
import kotlin.random.Random

class Constellation(
    val const: Constellations,
    val name: String,
    val galaxy: UUID,
    val stars: MutableList<UUID> = mutableListOf(),
    var offset: Float = Random.nextFloat() * 360,
    var daysVisible: Int = Random.nextInt(1, 7),
    var noise: SimplexNoise = SimplexNoise(XoroshiroRandomSource((offset * 100f).toLong()))
) {

    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("const", const.name)
        tag.putString("name", name)
        tag.putUUID("galaxy", galaxy)
        val starsTag = ListTag()
        stars.forEach {
            starsTag.add(StringTag.valueOf(it.toString()))
        }
        tag.put("stars", starsTag)
        tag.putFloat("offset", offset)
        tag.putInt("daysVisible", daysVisible)
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): Constellation {
            val const = Constellations.valueOf(tag.getString("const"))
            val name = tag.getString("name")
            val galaxy = tag.getUUID("galaxy")
            val stars = mutableListOf<UUID>()
            val starsTag = tag.getList("stars", 8)
            starsTag.forEach {
                stars.add(UUID.fromString(it.asString))
            }
            val offset = tag.getFloat("offset")
            val daysVisible = tag.getInt("daysVisible")
            return Constellation(const, name, galaxy, stars, offset, daysVisible)
        }
    }
}