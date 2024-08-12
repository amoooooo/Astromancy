package aster.amo.astromancy.mixin.client;

import aster.amo.astromancy.Astromancy;
import aster.amo.astromancy.data.AstromancySavedData;
import aster.amo.astromancy.space.classification.constellation.Constellation;
import aster.amo.astromancy.space.classification.stellarobjects.BinarySystem;
import aster.amo.astromancy.space.classification.stellarobjects.GravityCenter;
import aster.amo.astromancy.space.classification.stellarobjects.Planet;
import aster.amo.astromancy.space.classification.stellarobjects.Star;
import aster.amo.astromancy.space.classification.systems.StarSystem;
import aster.amo.astromancy.space.classification.systems.Universe;
import aster.amo.astromancy.space.render.planet.PlanetRenderer;
import aster.amo.astromancy.util.RenderHelper;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import kotlin.reflect.jvm.internal.impl.renderer.RenderingUtilsKt;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {


    @Shadow
    @Nullable
    private ClientLevel level;


    @Shadow @Final private Minecraft minecraft;

    @Shadow @Final private static ResourceLocation SUN_LOCATION;
    private static final ResourceLocation SUN = ResourceLocation.withDefaultNamespace("textures/environment/sun.png");

//    private static final Star endStar = new Star(25000).setStarType(StarType.EMPTY, "");


    @ModifyVariable(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"), ordinal = 4)
    public float getStarBrightness(float p_202430_) {
        if(!level.dimension().equals(Level.OVERWORLD)) return p_202430_;
        return 0.0F;
    }


    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0, shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILHARD)
    public void replaceSun(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci, FogType fogtype, PoseStack posestack, Vec3 vec3, float f, float f1, float f2, Tesselator tesselator, ShaderInstance shaderinstance, float[] afloat, float f11, Matrix4f matrix4f1, float f12){
        Universe universe = AstromancySavedData.Companion.get().getUniverse();
        Planet earth = universe.getPlanet(universe.getEarth());
        GravityCenter center = universe.getStarForPlanet(earth);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        Color color = Color.WHITE;
        if(center instanceof Star star) {
            color = RenderHelper.INSTANCE.getStarColor(star).darker();
            RenderSystem.setShaderColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        } else if (center instanceof BinarySystem binary) {
            color = RenderHelper.INSTANCE.getStarColor(binary.getStar1()).darker();
            RenderSystem.setShaderColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        }
        RenderSystem.setShaderTexture(0, SUN_LOCATION);
        BufferBuilder bufferbuilder1 = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, -f12).setUv(0.0F, 0.0F).setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, -f12).setUv(1.0F, 0.0F).setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        bufferbuilder1.addVertex(matrix4f1, f12, 100.0F, f12).setUv(1.0F, 1.0F).setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        bufferbuilder1.addVertex(matrix4f1, -f12, 100.0F, f12).setUv(0.0F, 1.0F).setColor(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, 1.0F);
        BufferUploader.drawWithShader(bufferbuilder1.buildOrThrow());
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/BufferUploader;drawWithShader(Lcom/mojang/blaze3d/vertex/MeshData;)V", ordinal = 1, shift = At.Shift.BEFORE))
    public void resetColors(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci){
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F", shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderNewStars(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci, FogType fogtype, PoseStack poseStack, Vec3 vec3, float f, float f1, float f2, Tesselator tesselator, ShaderInstance shaderinstance, float[] afloat, float f11, Matrix4f matrix4f1, float f12, BufferBuilder bufferbuilder1, int k, int l, int i1, float f13, float f14, float f15, float f16){
        RenderHelper.INSTANCE.renderSky(partialTick, skyFogSetup, poseStack, level, minecraft);
    }



    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F", shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderNightSky(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci, FogType fogtype, PoseStack poseStack, Vec3 vec3, float f, float f1, float f2, Tesselator tesselator, ShaderInstance shaderinstance, float[] afloat){
        float starBrightness = level.getStarBrightness(partialTick) * (1.0F - this.level.getRainLevel(partialTick));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/vfx/white.png"));
        Matrix4f matrix4f = poseStack.last().pose();
        Tesselator tessellator = Tesselator.getInstance();
        BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.addVertex(matrix4f, 300f, 300f, 300f).setUv(0,0).setColor(1,1,1,starBrightness);
        bufferbuilder.addVertex(matrix4f, 300f, 300f, -300f).setUv(1,0).setColor(1,1,1,starBrightness);
        bufferbuilder.addVertex(matrix4f, -300f, 300f, -300f).setUv(1,1).setColor(1,1,1,starBrightness);
        bufferbuilder.addVertex(matrix4f, -300f, 300f, 300f).setUv(0,1).setColor(1,1,1,starBrightness);
        BufferUploader.drawWithShader(bufferbuilder.build());
    }

//    @Inject(at = @At(value = "TAIL"), method = "renderEndSky", locals = LocalCapture.CAPTURE_FAILHARD)
//    public void renderStarInEndSky(PoseStack pPoseStack, CallbackInfo ci){
//        ClientRenderHelper.renderEndStar(pPoseStack, endStar);
//    }

//    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V"), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
//    public void renderWaypointStars(PoseStack poseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean p_202428_, Runnable pSkyFogSetup, CallbackInfo ci){
//    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V", ordinal = 4, shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderConstellations(Matrix4f frustumMatrix, Matrix4f projectionMatrix, float partialTick, Camera camera, boolean isFoggy, Runnable skyFogSetup, CallbackInfo ci, FogType fogtype, PoseStack posestack, Vec3 vec3, float f, float f1, float f2, Tesselator tesselator, ShaderInstance shaderinstance, float[] afloat, float f11) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;
//        ClientRenderHelper.renderWaypointStars(p_202424_, p_202425_, p_202426_, p_202427_, p_202428_, p_202429_, ci, bufferbuilder);
        float starBrightness = level.getStarBrightness(partialTick) * f11;
//        starBrightness = ClientRenderHelper.isSolarEclipse ? 10F : starBrightness;
        if (starBrightness > 0.0F) {
            posestack.pushPose();
            for (Constellation constellationInstance : AstromancySavedData.Companion.get().getUniverse().getConstellations()) {
                SimplexNoise noise = constellationInstance.getNoise();
                if (Math.round((level.getGameTime() / 24000f) + 1) % constellationInstance.getDaysVisible() == 0) {
                    float dayScale = level.getTimeOfDay(partialTick) * 0.8f;
                    float rotFactorZ = (dayScale * constellationInstance.getOffset()) + 25f % 360;
                    float rotFactorX = (dayScale * -constellationInstance.getOffset()) + 174 % 360;
                    float rotFactorY = (dayScale * constellationInstance.getOffset()) + 248 % 360;
                    posestack.mulPose(Axis.ZN.rotationDegrees(rotFactorZ));
                    posestack.mulPose(Axis.XP.rotationDegrees(rotFactorX));
                    posestack.mulPose(Axis.YN.rotationDegrees(rotFactorY));
                    float offset = 100f;
                    float rotMult = 1f;
                    RenderSystem.setShaderColor(starBrightness * rotMult, starBrightness * rotMult, starBrightness * rotMult, 1.0F);
                    Matrix4f matrix4f = posestack.last().pose();
                    float k = 10f;
                    if(minecraft.player != null){
                        k = k * (minecraft.player.isScoping() ? 0.5F : 1.0F);
                    }
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, constellationInstance.getConst().getIcon());
                    float starVis = Math.max(level.getStarBrightness(partialTick) * (1.0F - level.getRainLevel(partialTick)), 0.0F);
                    RenderSystem.setShaderColor(1.2f,1.2f,1.2f, 1.0F);
                    Tesselator tessellator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tessellator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    bufferbuilder.addVertex(matrix4f, -k, offset, -k).setUv(0.0F, 0.0F).setColor(1f, 1f, 1f, (float) noise.getValue(1 + Minecraft.getInstance().level.getGameTime() / 5000f, 1, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis));
                    bufferbuilder.addVertex(matrix4f, k, offset, -k).setUv(1.0F, 0.0F).setColor(1, 1, 1, (float) noise.getValue(128, 128 + Minecraft.getInstance().level.getGameTime() / 5000f, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis));
                    bufferbuilder.addVertex(matrix4f, k, offset, k).setUv(1.0F, 1.0F).setColor(1, 1, 1, (float) noise.getValue(256 + Minecraft.getInstance().level.getGameTime() / 5000f, 256, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis));
                    bufferbuilder.addVertex(matrix4f, -k, offset, k).setUv(0.0F, 1.0F).setColor(1, 1, 1, (float) noise.getValue(512, 512 + Minecraft.getInstance().level.getGameTime() / 5000f, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis));
                    BufferUploader.drawWithShader(bufferbuilder.build());
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            posestack.popPose();
        }
    }
}
