package aster.amo.astromancy.content.astrolabe

import aster.amo.astromancy.content.base.blockentity.AstromancyBlockEntity
import aster.amo.astromancy.data.AstromancySavedData
import aster.amo.astromancy.registry.BlockEntityRegistry
import aster.amo.astromancy.space.classification.systems.StarSystem
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.ItemInteractionResult
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult

class AstrolabeBlockEntity(pos: BlockPos?, state: BlockState?) : AstromancyBlockEntity(
    BlockEntityRegistry.ASTROLABE.get(), pos,
    state
) {
    private var _starSystem: StarSystem? = null

    var starSystem: StarSystem? = null
        get() {
            if (_starSystem == null) {
                _starSystem = AstromancySavedData.get()?.universe?.earthStarSystem
            }
            return _starSystem
        }

    private var _delegate: AstrolabeDelegate? = null

    var delegate: AstrolabeDelegate? = null
        get() {
            if (_delegate == null && level?.isClientSide == true) {
                _delegate = AstrolabeClientDelegate()
            }
            return _delegate ?: AstrolabeServerDelegate()
        }

    override fun onPlace(placer: LivingEntity?, stack: ItemStack?) {
        super.onPlace(placer, stack)
        delegate?.onInitialized(this)
        setChanged()
        level?.setBlockAndUpdate(worldPosition, blockState)
    }
    override fun loadAdditional(pTag: CompoundTag, provider: HolderLookup.Provider) {
        super.loadAdditional(pTag, provider)
        _starSystem = StarSystem.fromNbt(pTag.getCompound("starSystem"))
    }

    override fun saveAdditional(p_187471_: CompoundTag, p_323635_: HolderLookup.Provider) {
        super.saveAdditional(p_187471_, p_323635_)
        starSystem?.toNbt()?.let { p_187471_.put("starSystem", it) }
    }

    override fun tick() {
        super.tick()
        needsSync = true
    }

    override fun onUse(player: Player?, hand: InteractionHand?): ItemInteractionResult {
        if(hand == InteractionHand.MAIN_HAND && player?.level() is ServerLevel) {
            _starSystem = AstromancySavedData.get()?.universe?.getRandomStarSystem()
            // re-sync
            setChanged()
            level?.setBlockAndUpdate(worldPosition, blockState)
        }
        return super.onUse(player, hand)
    }

    override fun onUse(player: Player?, hand: InteractionHand?, ray: BlockHitResult?): InteractionResult {
        if(hand == InteractionHand.MAIN_HAND && player?.level() is ServerLevel) {
            _starSystem = AstromancySavedData.get()?.universe?.getRandomStarSystem()
            // re-sync
            setChanged()
            level?.sendBlockUpdated(worldPosition, blockState, blockState, 3)
            return InteractionResult.SUCCESS_NO_ITEM_USED
        }
        return super.onUse(player, hand, ray)
    }
}