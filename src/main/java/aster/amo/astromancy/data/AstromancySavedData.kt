package aster.amo.astromancy.data

import aster.amo.astromancy.space.classification.systems.Universe
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.world.level.saveddata.SavedData

class StarSavedData : SavedData() {
    var universe: Universe = Universe.generateUniverse()
    override fun save(tag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
        tag.put("universe", universe.toNbt())
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): StarSavedData {
            val data = StarSavedData()
            data.universe = Universe.fromNbt(tag.getCompound("universe"))
            return data
        }
    }
}