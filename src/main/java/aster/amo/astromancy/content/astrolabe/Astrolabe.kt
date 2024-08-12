package aster.amo.astromancy.content.astrolabe

import aster.amo.astromancy.content.base.block.AstromancyEntityBlock
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.VoxelShape

class Astrolabe<T : AstrolabeBlockEntity?>(properties: BlockBehaviour.Properties?) : AstromancyEntityBlock<T>(properties) {
    val SHAPE: VoxelShape = Block.box(3.0, 0.0, 3.0, 13.0, 3.0, 13.0)
    override fun getShape(pState: BlockState?, pLevel: BlockGetter?, pPos: BlockPos?, pContext: CollisionContext?): VoxelShape {
        return SHAPE
    }

    override fun getCollisionShape(
        pState: BlockState?,
        pLevel: BlockGetter?,
        pPos: BlockPos?,
        pContext: CollisionContext?
    ): VoxelShape {
        return SHAPE
    }

    override fun getInteractionShape(pState: BlockState?, pLevel: BlockGetter?, pPos: BlockPos?): VoxelShape {
        return SHAPE
    }
}
