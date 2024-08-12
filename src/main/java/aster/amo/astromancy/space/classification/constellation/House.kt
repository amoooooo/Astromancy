package aster.amo.astromancy.space.classification.constellation

import net.minecraft.nbt.CompoundTag
import java.util.UUID

class House(
    val house: Houses,
    val name: String,
    val archetype: String,
    val supercluster: UUID
) {

    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        tag.putString("house", house.name)
        tag.putString("name", name)
        tag.putString("archetype", archetype)
        tag.putUUID("supercluster", supercluster)
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): House {
            val house = Houses.valueOf(tag.getString("house"))
            val name = tag.getString("name")
            val archetype = tag.getString("archetype")
            val supercluster = tag.getUUID("supercluster")
            return House(house, name, archetype, supercluster)
        }
    }
}