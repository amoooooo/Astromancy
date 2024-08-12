package aster.amo.astromancy.data

import aster.amo.astromancy.space.classification.systems.Universe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.minecraft.core.HolderLookup
import net.minecraft.core.HolderLookup.Provider
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.MinecraftServer
import net.minecraft.world.level.saveddata.SavedData
import net.neoforged.neoforge.server.ServerLifecycleHooks

class AstromancySavedData() : SavedData() {
    var universe: Universe = Universe.generateUniverse()
    override fun save(tag: CompoundTag, provider: HolderLookup.Provider): CompoundTag {
        tag.put("universe", universe.toNbt())
        return tag
    }
    companion object {
        fun fromNbt(tag: CompoundTag, provider: Provider): AstromancySavedData {
            val data = AstromancySavedData()
            data.universe = Universe.fromNbt(tag.getCompound("universe"))
            return data
        }

        fun get(server: MinecraftServer): AstromancySavedData {
            return server.overworld().dataStorage.computeIfAbsent(Factory(::AstromancySavedData, Companion::fromNbt), "astromancy")
        }

        fun get(): AstromancySavedData? {
            return ServerLifecycleHooks.getCurrentServer()?.let { get(it) }
        }
    }

    override fun isDirty(): Boolean {
        return true
    }
}