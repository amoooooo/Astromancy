package aster.amo.astromancy.registry;

import aster.amo.astromancy.Astromancy;
import aster.amo.astromancy.content.astrolabe.Astrolabe;
import aster.amo.astromancy.content.astrolabe.AstrolabeBlockEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class BlockRegistry {
    public static final DeferredRegister<Block> REGISTER = DeferredRegister.create(BuiltInRegistries.BLOCK, Astromancy.MODID);

    public static final Supplier<Block> ASTROLABE =  REGISTER.register("astrolabe", () -> new Astrolabe<AstrolabeBlockEntity>(MAGIC_PROPERTIES()).setBlockEntity(BlockEntityRegistry.ASTROLABE));


    public static BlockBehaviour.Properties MAGIC_PROPERTIES() {
        return BlockBehaviour.Properties.of().sound(SoundType.LODESTONE).dynamicShape().isViewBlocking((var1, var2, var3) -> false).noOcclusion();
    }
}
