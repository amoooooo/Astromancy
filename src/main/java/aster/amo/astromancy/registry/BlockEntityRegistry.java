package aster.amo.astromancy.registry;

import aster.amo.astromancy.Astromancy;
import aster.amo.astromancy.content.astrolabe.AstrolabeBlockEntity;
import aster.amo.astromancy.content.astrolabe.AstrolabeRenderer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockEntityRegistry {
    public static final DeferredRegister<BlockEntityType<?>> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Astromancy.MODID);

    public static final Supplier<BlockEntityType<AstrolabeBlockEntity>> ASTROLABE = REGISTER.register("astrolabe", () -> BlockEntityType.Builder.of(AstrolabeBlockEntity::new, BlockRegistry.ASTROLABE.get()).build(null));

    @EventBusSubscriber(modid = Astromancy.MODID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
    public static class ClientOnly {
        @SubscribeEvent
        public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event){
            event.registerBlockEntityRenderer(ASTROLABE.get(), AstrolabeRenderer::new);
        }
    }
}
