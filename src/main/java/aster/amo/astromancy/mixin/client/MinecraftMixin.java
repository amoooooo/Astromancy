package aster.amo.astromancy.mixin.client;

import aster.amo.astromancy.content.astrolabe.AstrolabeBlockEntity;
import aster.amo.astromancy.content.astrolabe.AstrolabeClientDelegate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public LocalPlayer player;

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void startAttack(CallbackInfoReturnable<Boolean> cir) {
        ((LevelRendererAccessor)Minecraft.getInstance().levelRenderer).getGlobalBlockEntities().forEach(blockEntity -> {
            if (blockEntity instanceof AstrolabeBlockEntity astrolabe) {
                ((AstrolabeClientDelegate)astrolabe.getDelegate()).getOBBList().forEach((planet, obb) -> {
                    if(obb.add(astrolabe.getBlockPos().getBottomCenter()).intersects(player.getEyePosition(), player.getLookAngle())) {
                        player.displayClientMessage(Component.literal("Interacted with planet: " + planet.getName()), true);
                    }
                });
            }
        });
    }
}
