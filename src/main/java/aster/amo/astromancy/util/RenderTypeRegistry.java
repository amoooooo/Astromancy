package aster.amo.astromancy.util;

import aster.amo.astromancy.Astromancy;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;

import java.io.IOException;
import java.util.function.Function;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.*;

public class RenderTypeRegistry {
    public static RenderType astrolabeStarfield(ResourceLocation texture){
        return AstromancyRenderTypes.ASTROLABE_STARFIELD.apply(texture);
    }

    public static RenderType additiveTexture(ResourceLocation texture){
        return AstromancyRenderTypes.ADDITIVE_TEXTURE.apply(texture);
    }

    public static RenderType cubemap(ResourceLocation texture){
        return AstromancyRenderTypes.CUBEMAP.apply(texture);
    }

    public static RenderType nightSky(ResourceLocation texture){
        return AstromancyRenderTypes.NIGHTSKY.apply(texture);
    }

    public static RenderType solid(ResourceLocation texture) {
        return AstromancyRenderTypes.SOLID.apply(texture);
    }

    @EventBusSubscriber(value = Dist.CLIENT, modid = Astromancy.MODID, bus = EventBusSubscriber.Bus.MOD)
    public static class ShaderRegistryEvent{
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Astromancy.astromancy("rendertype_astrolabe_starfield"), POSITION_TEX_COLOR), shaderInstance -> {
                AstromancyRenderTypes.astrolabeStarfield = shaderInstance;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Astromancy.astromancy("rendertype_additive_texture"), POSITION_TEX_COLOR), shaderInstance -> {
                AstromancyRenderTypes.additiveTexture = shaderInstance;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Astromancy.astromancy("rendertype_cubemap"), POSITION_TEX_COLOR), shaderInstance -> {
                AstromancyRenderTypes.cubemap = shaderInstance;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(), Astromancy.astromancy("rendertype_night_sky"), POSITION_TEX_COLOR), shaderInstance -> {
                AstromancyRenderTypes.nightSky = shaderInstance;
            });
            event.registerShader(new ShaderInstance(event.getResourceProvider(), ResourceLocation.tryParse("minecraft:rendertype_solid"), POSITION_COLOR_TEX_LIGHTMAP), shaderInstance -> {
                AstromancyRenderTypes.solid = shaderInstance;
            });
        }
    }

    public static class AstromancyRenderTypes extends RenderType {

        private static ShaderInstance astrolabeStarfield;
        private static ShaderInstance additiveTexture;
        private static ShaderInstance cubemap;
        private static ShaderInstance nightSky;
        private static ShaderInstance solid;

        public static ShaderInstance getNightSky(){
            return nightSky;
        }

        private static final ShaderStateShard RENDER_TYPE_ADDITIVE_TEXTURE = new ShaderStateShard(() -> additiveTexture);

        private static final ShaderStateShard RENDERTYPE_CUBEMAP = new ShaderStateShard(() -> cubemap);

        private static final ShaderStateShard RENDERTYPE_ASTROLABE_STARFIELD = new ShaderStateShard(() -> astrolabeStarfield);

        private static final ShaderStateShard RENDERTYPE_NIGHTSKY = new ShaderStateShard(() -> nightSky);

        private static final ShaderStateShard RENDERTYPE_SOLID_ASTRO = new ShaderStateShard(() -> solid);

        private AstromancyRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }

        public static Function<ResourceLocation, RenderType> ASTROLABE_STARFIELD = Util.memoize(AstromancyRenderTypes::astrolabeStarfield);
        public static Function<ResourceLocation, RenderType> ADDITIVE_TEXTURE = Util.memoize(AstromancyRenderTypes::additiveTexture);
        public static Function<ResourceLocation, RenderType> CUBEMAP = Util.memoize(AstromancyRenderTypes::cubemap);
        public static Function<ResourceLocation, RenderType> NIGHTSKY = Util.memoize(AstromancyRenderTypes::nightSky);
        public static Function<ResourceLocation, RenderType> SOLID = Util.memoize(AstromancyRenderTypes::astromancySolid);

        private static RenderType astromancySolid(ResourceLocation texture){
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_SOLID_ASTRO)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("astromancy_solid", DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP, VertexFormat.Mode.QUADS, 256, true, true, rendertype$state);
        }

        private static RenderType additiveTexture(ResourceLocation texture){
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDER_TYPE_ADDITIVE_TEXTURE)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("additive_texture", POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        private static RenderType astrolabeStarfield(ResourceLocation texture) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_ASTROLABE_STARFIELD)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("astrolabe_starfield", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        private static RenderType cubemap(ResourceLocation texture) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_CUBEMAP)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("cubemap", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }

        private static RenderType nightSky(ResourceLocation texture) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDERTYPE_NIGHTSKY)
                    .setTextureState(new RenderStateShard.TextureStateShard(texture, false, false))
                    .setTransparencyState(NO_TRANSPARENCY)
                    .setLightmapState(NO_LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("night_sky", DefaultVertexFormat.POSITION_TEX_COLOR, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }
    }
}
