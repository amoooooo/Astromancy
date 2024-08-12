package aster.amo.astromancy.content.tablet

import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.UseAnim
import net.minecraft.world.level.Level

class StoneTabletItem(pProperties: Properties?) : Item(pProperties) {
    override fun getUseAnimation(pStack: ItemStack): UseAnim {
        return UseAnim.NONE
    }

    fun getUseDuration(pStack: ItemStack?): Int {
        return 36000
    }

    override fun use(pLevel: Level, pPlayer: Player, pUsedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        return super.use(pLevel, pPlayer, pUsedHand)
    }
}
