package aster.amo.astromancy.content.astrolabe

import net.minecraft.world.level.block.entity.BlockEntity

interface AstrolabeDelegate {
    fun tick(astrolabe: AstrolabeBlockEntity)
    fun onInitialized(astrolabe: AstrolabeBlockEntity)
}