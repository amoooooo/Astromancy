package aster.amo.astromancy.mixin.client;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.PostChain;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Set;

@Mixin(LevelRenderer.class)
public interface LevelRendererAccessor {
    @Accessor("entityEffect")
    PostChain getEntityEffect();

    @Accessor("globalBlockEntities")
    Set<BlockEntity> getGlobalBlockEntities();
}
