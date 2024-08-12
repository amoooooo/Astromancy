package aster.amo.astromancy;

import aster.amo.astromancy.data.AstromancySavedData;
import aster.amo.astromancy.registry.BlockEntityRegistry;
import aster.amo.astromancy.registry.BlockRegistry;
import aster.amo.astromancy.registry.ItemRegistry;
import aster.amo.astromancy.space.classification.systems.Universe;
import coffee.amo.astromancy.client.renderer.item.StoneTabletRenderer;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModLoader;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.common.NeoForgeMod;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import org.slf4j.Logger;

@Mod(Astromancy.MODID)
public class Astromancy {
    public static final String MODID = "astromancy";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static AstromancySavedData DATA = null;

    public Astromancy(IEventBus modEventBus, ModContainer modContainer) {
        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.register(StoneTabletRenderer.INSTANCE);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
        ItemRegistry.ITEMS.register(modEventBus);
        BlockRegistry.REGISTER.register(modEventBus);
        BlockEntityRegistry.REGISTER.register(modEventBus);
//        Universe universe = Universe.Companion.generateUniverse();
//        CompoundTag tag = universe.toNbt();
//        try {
//            NbtIo.writeCompressed(tag, FMLPaths.CONFIGDIR.relative());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        DATA = AstromancySavedData.Companion.get(event.getServer());
        DATA.getUniverse().writeUniverseToFile(FMLPaths.GAMEDIR.get().toString() + "/universe.txt");
    }

    public static ResourceLocation astromancy(String s) {
        return ResourceLocation.fromNamespaceAndPath(MODID, s);
    }
}
