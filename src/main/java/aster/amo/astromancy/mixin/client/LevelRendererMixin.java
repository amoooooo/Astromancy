package coffee.amo.astromancy.mixin.client;

import coffee.amo.astromancy.Astromancy;
import coffee.amo.astromancy.client.helper.ClientRenderHelper;
import coffee.amo.astromancy.client.systems.ClientUniverseHolder;
import coffee.amo.astromancy.core.helpers.RenderHelper;
import coffee.amo.astromancy.core.systems.stars.classification.constellation.ConstellationInstance;
import coffee.amo.astromancy.core.systems.stars.classification.star.StarType;
import coffee.amo.astromancy.core.systems.stars.types.Star;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.synth.SimplexNoise;
import net.minecraft.world.level.material.FogType;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import javax.annotation.Nullable;
import java.awt.*;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {


    @Shadow
    @Nullable
    private ClientLevel level;


    @Shadow @Final private Minecraft minecraft;

    private static final ResourceLocation SUN = Astromancy.astromancy("textures/environment/sun.png");

    private static final Star endStar = new Star(25000).setStarType(StarType.EMPTY, "");


    @ModifyVariable(method = "renderSky", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F"), ordinal = 4)
    public float getStarBrightness(float p_202430_) {
        if(!level.dimension().equals(Level.OVERWORLD)) return p_202430_;
        return 0.0F;
    }

    @Inject(method = "renderSky", at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;setShaderTexture(ILnet/minecraft/resources/ResourceLocation;)V", ordinal = 0, shift = At.Shift.AFTER))
    public void replaceSun(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean p_202428_, Runnable pSkyFogSetup, CallbackInfo ci){
        if(ClientRenderHelper.isSolarEclipse) {
            float mult = ((Minecraft.getInstance().level.getDayTime() / 1000f) / 3)/8;
            RenderSystem.setShaderTexture(0, SUN);
        }
    }


    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getStarBrightness(F)F", shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderNewStars(PoseStack poseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean p_202428_, Runnable pSkyFogSetup, CallbackInfo ci, FogType fogtype, Vec3 vec3, float f, float f1, float f2, BufferBuilder bufferbuilder, ShaderInstance shaderinstance, float[] afloat, float f11, Matrix4f matrix4f1, float f12, int k, int l, int i1, float f13, float f14, float f15, float f16){
        FogRenderer.setupNoFog();
        RandomSource random = RandomSource.create(10842L);
        float starDistance = 80.0F;
        float starRadius = 10F;
        float starVariance = 10.0F;
        float time = level.getGameTime() + pPartialTick;
        int starCount = 4096;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(false);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        for(int star = 0; star <= starCount; ++star) {
            poseStack.pushPose();
            float sDist = starDistance + (float)(random.nextGaussian() * starVariance);
            float distanceFactor = (starDistance / sDist);
            float size = random.nextFloat();
            float sRad = Math.min(starRadius * size, 0.55F);

            if(minecraft.player != null){
                sRad = sRad * (minecraft.player.isScoping() ? 0.2F : 1.0F);
            }

            float xRot = (random.nextFloat() * 2) - 1;
            float zRot = (random.nextFloat() * 2) - 1;
            float scale = random.nextInt(10) - 5;
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Vector3f.YP.rotationDegrees((float) ((time * distanceFactor * 0.01F) * (random.nextGaussian() + random.nextGaussian()))));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(xRot * 270.0F));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(zRot * 270.0F));

            Matrix4f m2 = poseStack.last().pose();
            distanceFactor = (sDist / starDistance) * (1 - Math.abs(xRot)) * (1 - Math.abs(zRot));

            Color color = Color.getHSBColor((float) Math.abs(random.nextGaussian()), 0.3F, 1.0F);

            if(star % 2 == 0){
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_med.png"));
            } else if (star % 3 == 0){
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_big.png"));
            } else {
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_small.png"));
            }
            sRad *= Math.max(0.05f, Mth.abs((float) Math.sin((time * (0.1F * random.nextFloat())) * random.nextGaussian())));
            distanceFactor *= ClientRenderHelper.isSolarEclipse ? 1.0F : Math.max(level.getStarBrightness(pPartialTick) * (1.0F - level.getRainLevel(pPartialTick)), 0.0F);
            bufferbuilder.vertex(m2, -sRad, sDist, -sRad).uv(0,0).color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, distanceFactor).endVertex();
            bufferbuilder.vertex(m2, sRad, sDist, -sRad).uv(1,0).color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, distanceFactor).endVertex();
            bufferbuilder.vertex(m2, sRad, sDist, sRad).uv(1,1).color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, distanceFactor).endVertex();
            bufferbuilder.vertex(m2, -sRad, sDist, sRad).uv(0,1).color(color.getRed() / 255.0F, color.getGreen() / 255.0F, color.getBlue() / 255.0F, distanceFactor).endVertex();

            poseStack.popPose();
        }
        BufferUploader.drawWithShader(bufferbuilder.end());
        RenderSystem.depthMask(true);
        pSkyFogSetup.run();
    }

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/ClientLevel;getRainLevel(F)F", shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderNightSky(PoseStack pPoseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean p_202428_, Runnable pSkyFogSetup, CallbackInfo ci, FogType fogtype, Vec3 vec3, float f, float f1, float f2, BufferBuilder bufferbuilder, ShaderInstance shaderinstance, float[] afloat){
        float starBrightness = level.getStarBrightness(pPartialTick) * (1.0F - this.level.getRainLevel(pPartialTick));
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/vfx/white.png"));
        Matrix4f matrix4f = pPoseStack.last().pose();
        bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferbuilder.vertex(matrix4f, 300f, 300f, 300f).uv(0,0).color(1,1,1,starBrightness).endVertex();
        bufferbuilder.vertex(matrix4f, 300f, 300f, -300f).uv(1,0).color(1,1,1,starBrightness).endVertex();
        bufferbuilder.vertex(matrix4f, -300f, 300f, -300f).uv(1,1).color(1,1,1,starBrightness).endVertex();
        bufferbuilder.vertex(matrix4f, -300f, 300f, 300f).uv(0,1).color(1,1,1,starBrightness).endVertex();
        BufferUploader.drawWithShader(bufferbuilder.end());
    }

    @Inject(at = @At(value = "TAIL"), method = "renderEndSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderStarInEndSky(PoseStack pPoseStack, CallbackInfo ci){
        ClientRenderHelper.renderEndStar(pPoseStack, endStar);
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/systems/RenderSystem;disableTexture()V"), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderWaypointStars(PoseStack poseStack, Matrix4f pProjectionMatrix, float pPartialTick, Camera pCamera, boolean p_202428_, Runnable pSkyFogSetup, CallbackInfo ci){
    }

    @Inject(at = @At(value = "INVOKE", target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lcom/mojang/math/Quaternion;)V", ordinal = 4, shift = At.Shift.AFTER), method = "renderSky", locals = LocalCapture.CAPTURE_FAILHARD)
    public void renderConstellations(PoseStack p_202424_, Matrix4f p_202425_, float p_202426_, Camera p_202427_, boolean p_202428_, Runnable p_202429_, CallbackInfo ci, FogType fogtype, Vec3 vec3, float f, float f1, float f2, BufferBuilder bufferbuilder, ShaderInstance shaderinstance, float[] afloat, float f11) {
        if (!level.dimension().equals(Level.OVERWORLD)) return;
        ClientRenderHelper.renderWaypointStars(p_202424_, p_202425_, p_202426_, p_202427_, p_202428_, p_202429_, ci, bufferbuilder);
        float starBrightness = level.getStarBrightness(p_202426_) * f11;
        starBrightness = ClientRenderHelper.isSolarEclipse ? 10F : starBrightness;
        if (starBrightness > 0.0F) {
            p_202424_.pushPose();
            for (ConstellationInstance constellationInstance : ClientUniverseHolder.getConstellationInstances()) {
                SimplexNoise noise = constellationInstance.getNoise();
                if (Math.round((level.getGameTime() / 24000f) + 1) % constellationInstance.getDaysVisible() == 0) {
                    float dayScale = level.getTimeOfDay(p_202426_) * 0.8f;
                    float rotFactorZ = (dayScale * constellationInstance.getOffset()) + 25f % 360;
                    float rotFactorX = (dayScale * -constellationInstance.getOffset()) + 174 % 360;
                    float rotFactorY = (dayScale * constellationInstance.getOffset()) + 248 % 360;
                    p_202424_.mulPose(Vector3f.ZN.rotationDegrees(rotFactorZ));
                    p_202424_.mulPose(Vector3f.XP.rotationDegrees(rotFactorX));
                    p_202424_.mulPose(Vector3f.YN.rotationDegrees(rotFactorY));
                    float offset = 100f;
                    float rotMult = 1f;
                    RenderSystem.setShaderColor(starBrightness * rotMult, starBrightness * rotMult, starBrightness * rotMult, 1.0F);
                    Matrix4f matrix4f = p_202424_.last().pose();
                    float k = 20f;
                    if(minecraft.player != null){
                        k = k * (minecraft.player.isScoping() ? 0.5F : 1.0F);
                    }
                    RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
                    RenderSystem.setShaderTexture(0, constellationInstance.getConstellation().getIcon());
                    float starVis = Math.max(level.getStarBrightness(p_202426_) * (1.0F - level.getRainLevel(p_202426_)), 0.0F);
                    RenderSystem.setShaderColor(1.2f,1.2f,1.2f, 1.0F);
                    bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
                    bufferbuilder.vertex(matrix4f, -k, offset, -k).uv(0.0F, 0.0F).color(1f, 1f, 1f, (float) noise.getValue(1 + Minecraft.getInstance().level.getGameTime() / 5000f, 1, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis)).endVertex();
                    bufferbuilder.vertex(matrix4f, k, offset, -k).uv(1.0F, 0.0F).color(1, 1, 1, (float) noise.getValue(128, 128 + Minecraft.getInstance().level.getGameTime() / 5000f, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis)).endVertex();
                    bufferbuilder.vertex(matrix4f, k, offset, k).uv(1.0F, 1.0F).color(1, 1, 1, (float) noise.getValue(256 + Minecraft.getInstance().level.getGameTime() / 5000f, 256, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis)).endVertex();
                    bufferbuilder.vertex(matrix4f, -k, offset, k).uv(0.0F, 1.0F).color(1, 1, 1, (float) noise.getValue(512, 512 + Minecraft.getInstance().level.getGameTime() / 5000f, (Minecraft.getInstance().level.getGameTime() / 5000f)*starVis)).endVertex();
                    BufferUploader.drawWithShader(bufferbuilder.end());
                    RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                }
            }
            p_202424_.popPose();
        }
    }
}
