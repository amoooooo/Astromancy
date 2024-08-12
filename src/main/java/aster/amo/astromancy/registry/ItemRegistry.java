package aster.amo.astromancy.registry;

import aster.amo.astromancy.Astromancy;
import aster.amo.astromancy.content.tablet.StoneTabletItem;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ItemRegistry {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, Astromancy.MODID);

    public static final Supplier<BlockItem> ASTROLABE = ITEMS.register("astrolabe", () -> new BlockItem(BlockRegistry.ASTROLABE.get(), DEFAULT_PROPERTIES()));
    public static final Supplier<Item> STONE_TABLET = ITEMS.register("stone_tablet", () -> new StoneTabletItem(DEFAULT_PROPERTIES()));


    public static Item.Properties DEFAULT_PROPERTIES() {
        return new Item.Properties();
    }
}
