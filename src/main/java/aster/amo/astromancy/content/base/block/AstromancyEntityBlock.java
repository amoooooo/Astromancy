package aster.amo.astromancy.content.base.block;

import aster.amo.astromancy.content.base.blockentity.AstromancyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.function.Supplier;

@SuppressWarnings("all")
public class AstromancyEntityBlock<T extends AstromancyBlockEntity> extends Block implements EntityBlock {
    protected Supplier<BlockEntityType<T>> blockEntityType = null;
    protected BlockEntityTicker<T> ticker = null;

    public AstromancyEntityBlock(Properties properties) {
        super(properties);
    }

    public <Y extends AstromancyEntityBlock<T>> Y setBlockEntity(Supplier<BlockEntityType<T>> type) {
        this.blockEntityType = type;
        this.ticker = (l, p, s, t) -> t.tick();
        return (Y) this;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return hasTileEntity(state) ? blockEntityType.get().create(pos, state) : null;
    }

    public boolean hasTileEntity(BlockState state) {
        return this.blockEntityType != null;
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return (BlockEntityTicker<T>) ticker;
    }

    @Override
    public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
        if (hasTileEntity(pState)) {
            if (pLevel.getBlockEntity(pPos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                simpleBlockEntity.onPlace(pPlacer, pStack);
            }
        }
        super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, LevelReader world, BlockPos pos, Player player) {
        if (hasTileEntity(state)) {
            if (world.getBlockEntity(pos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                ItemStack stack = simpleBlockEntity.onClone(state, target, world, pos, player);
                if (!stack.isEmpty()) {
                    return stack;
                }
            }
        }
        return super.getCloneItemStack(state, target, world, pos, player);
    }


    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        onBlockBroken(state, level, pos, player);
        return  super.playerWillDestroy(level, pos, state, player);
    }

    @Override
    public void onBlockExploded(BlockState state, Level level, BlockPos pos, Explosion explosion) {
        onBlockBroken(state, level, pos, null);
        super.onBlockExploded(state, level, pos, explosion);
    }

    public void onBlockBroken(BlockState state, BlockGetter level, BlockPos pos, @Nullable Player player) {
        if (hasTileEntity(state)) {
            if (level.getBlockEntity(pos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                simpleBlockEntity.onBreak(player);
            }
        }
    }

    @Override
    public void entityInside(BlockState pState, Level pLevel, BlockPos pPos, Entity pEntity) {
        if (hasTileEntity(pState)) {
            if (pLevel.getBlockEntity(pPos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                simpleBlockEntity.onEntityInside(pState, pLevel, pPos, pEntity);
            }
        }
        super.entityInside(pState, pLevel, pPos, pEntity);
    }

    @Override
    public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
        if (hasTileEntity(pState)) {
            if (pLevel.getBlockEntity(pPos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                simpleBlockEntity.onNeighborUpdate(pState, pPos, pFromPos);
            }
        }
        super.neighborChanged(pState, pLevel, pPos, pBlock, pFromPos, pIsMoving);
    }

//    @Override
//    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult ray) {
//        if (hasTileEntity(state)) {
//            if (level.getBlockEntity(pos) instanceof AstromancyBlockEntity simpleBlockEntity) {
//                return simpleBlockEntity.onUse(player, hand);
//            }
//        }
//        return super.use(state, level, pos, player, hand, ray);
//    }

    @Override
    protected ItemInteractionResult useItemOn(ItemStack p_316304_, BlockState p_316362_, Level p_316459_, BlockPos p_316366_, Player p_316132_, InteractionHand p_316595_, BlockHitResult p_316140_) {
        if (hasTileEntity(p_316362_)) {
            if (p_316459_.getBlockEntity(p_316366_) instanceof AstromancyBlockEntity simpleBlockEntity) {
                return simpleBlockEntity.onUse(p_316132_, p_316595_);
            }
        }
        return super.useItemOn(p_316304_, p_316362_, p_316459_, p_316366_, p_316132_, p_316595_, p_316140_);
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (hasTileEntity(state)) {
            if (level.getBlockEntity(pos) instanceof AstromancyBlockEntity simpleBlockEntity) {
                return simpleBlockEntity.onUse(player, InteractionHand.MAIN_HAND, hitResult);
            }
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }
}
