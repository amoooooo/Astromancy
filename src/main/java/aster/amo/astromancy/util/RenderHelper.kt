package aster.amo.astromancy.util

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.data.AstromancySavedData.Companion.get
import aster.amo.astromancy.mixin.client.LevelRendererMixin
import aster.amo.astromancy.space.classification.stellarobjects.CelestialBody
import aster.amo.astromancy.space.classification.stellarobjects.Planet
import aster.amo.astromancy.space.classification.stellarobjects.Star
import aster.amo.astromancy.space.classification.stellarobjects.star.StarColors
import aster.amo.astromancy.space.classification.stellarobjects.star.StarType
import aster.amo.astromancy.space.render.planet.PlanetRenderer.renderPlanet
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.*
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.client.renderer.*
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.commands.arguments.EntityAnchorArgument
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import java.awt.Color
import java.util.*
import kotlin.math.*

object RenderHelper {
    fun renderInvertedCube(ps: PoseStack, buffer: MultiBufferSource, size: Float, renderType: RenderType?) {
        ps.pushPose()
        ps.translate(-(floor((size / 2).toDouble())), 0.0, -(floor((size / 2).toDouble())))
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        val buff = buffer.getBuffer(renderType)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(270f))
        ps.translate(-size, 0f, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(180f))
        ps.translate(-size, 0f, size)
        renderQuad(ps, size, buff)
        ps.popPose()
    }

    fun renderTexturedCube(ps: PoseStack, buff: VertexConsumer, size: Float) {
        ps.pushPose()
        renderTexturedQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(90f))
        ps.translate(0f, 0f, size)
        renderTexturedQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(90f))
        ps.translate(0f, 0f, size)
        renderTexturedQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(90f))
        ps.translate(0f, 0f, size)
        renderTexturedQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, 0f, size)
        renderTexturedQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(180f))
        ps.translate(0f, size, size)
        renderTexturedQuad(ps, size, buff)

        ps.popPose()
    }

    fun renderTexturedQuad(ps: PoseStack, size: Float, buff: VertexConsumer) {
        ps.pushPose()
        ps.translate(0f, 0f, 0f)
        ps.mulPose(Axis.YP.rotationDegrees(180f))
        ps.mulPose(Axis.ZP.rotationDegrees(180f))
        ps.scale(size, size, size)
        val matrix: PoseStack.Pose = ps.last()
        buff.addVertex(matrix, 0f, 0f, 0f).setColor(1f, 1f, 1f, 1f).setUv(0f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        buff.addVertex(matrix, 0f, 1f, 0f).setColor(1f, 1f, 1f, 1f).setUv(0f, 1f).setOverlay(OverlayTexture.NO_OVERLAY)
        buff.addVertex(matrix, 1f, 1f, 0f).setColor(1f, 1f, 1f, 1f).setUv(1f, 1f).setOverlay(OverlayTexture.NO_OVERLAY)
        buff.addVertex(matrix, 1f, 0f, 0f).setColor(1f, 1f, 1f, 1f).setUv(1f, 0f).setOverlay(OverlayTexture.NO_OVERLAY)
        ps.popPose()
    }

    fun renderNormalCuboid(ps: PoseStack, buffer: MultiBufferSource, size: Float, renderType: RenderType?) {
        ps.pushPose()
        ps.translate((floor((size / 2).toDouble())), 0.0, (floor((size / 2).toDouble())))
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        val buff = buffer.getBuffer(renderType)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, size, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, size, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, size, 0f)
        renderQuad(ps, -size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(270f))
        ps.translate(size, 0f, 0f)
        renderQuad(ps, size, buff)
        ps.mulPose(Axis.YP.rotationDegrees(180f))
        ps.translate(size, 0f, -size)
        renderQuad(ps, size, buff)
        ps.popPose()
    }

    fun renderInvertedCube(
        ps: PoseStack,
        buffer: MultiBufferSource,
        size: Float,
        renderType: RenderType?,
        color: Color
    ) {
        ps.pushPose()
        ps.translate(-(floor((size / 2).toDouble())), 0.0, -(floor((size / 2).toDouble())))
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        val buff = buffer.getBuffer(renderType)
        renderQuad(ps, size, buff, color)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff, color)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff, color)
        ps.mulPose(Axis.XP.rotationDegrees(90f))
        ps.translate(0f, -size, 0f)
        renderQuad(ps, size, buff, color)
        ps.mulPose(Axis.YP.rotationDegrees(270f))
        ps.translate(-size, 0f, 0f)
        renderQuad(ps, size, buff, color)
        ps.mulPose(Axis.YP.rotationDegrees(180f))
        ps.translate(-size, 0f, size)
        renderQuad(ps, size, buff, color)
        ps.popPose()
    }


    private fun renderQuad(ps: PoseStack, size: Float, buff: VertexConsumer) {
        buff.addVertex(ps.last().pose(), 0f, size, 0f).setColor(-0xaa01).setUv(0f, 1f).setLight(0).setNormal(0f, 1f, 0f)
        buff.addVertex(ps.last().pose(), size, size, 0f).setColor(-0xaa01).setUv(1f, 1f).setLight(0).setNormal(1f, 1f, 0f)
        buff.addVertex(ps.last().pose(), size, 0f, 0f).setColor(-0xaa01).setUv(1f, 0f).setLight(0).setNormal(1f, 0f, 0f)
        buff.addVertex(ps.last().pose(), 0f, 0f, 0f).setColor(-0xaa01).setUv(0f, 0f).setLight(0).setNormal(0f, 0f, 0f)
    }

    fun renderQuad(ps: PoseStack, size: Float, buff: VertexConsumer, color: Color) {
        buff.addVertex(ps.last().pose(), 0f, size, 0f).setColor(color.red, color.green, color.blue, color.alpha).setUv(0f, 1f).setLight(0).setNormal(0f, 1f, 0f)
        buff.addVertex(ps.last().pose(), size, size, 0f).setColor(color.red, color.green, color.blue, color.alpha).setUv(1f, 1f).setLight(0).setNormal(1f, 1f, 0f)
        buff.addVertex(ps.last().pose(), size, 0f, 0f).setColor(color.red, color.green, color.blue, color.alpha).setUv(1f, 0f).setLight(0).setNormal(1f, 0f, 0f)
        buff.addVertex(ps.last().pose(), 0f, 0f, 0f).setColor(color.red, color.green, color.blue, color.alpha).setUv(0f, 0f).setLight(0).setNormal(0f, 0f, 0f)
    }

    fun renderQuad(ps: PoseStack, xSize: Float, ySize: Float, buff: VertexConsumer, color: Int) {
        buff.addVertex(ps.last().pose(), 0f, ySize, 0f).setColor(color).setUv(0f, 1f).setLight(0).setNormal(0f, 1f, 0f)
        buff.addVertex(ps.last().pose(), xSize, ySize, 0f).setColor(color).setUv(1f, 1f).setLight(0).setNormal(1f, 1f, 0f)
        buff.addVertex(ps.last().pose(), xSize, 0f, 0f).setColor(color).setUv(1f, 0f).setLight(0).setNormal(1f, 0f, 0f)
        buff.addVertex(ps.last().pose(), 0f, 0f, 0f).setColor(color).setUv(0f, 0f).setLight(0).setNormal(0f, 0f, 0f)
    }

    var nullStarLookPower: Double = 0.0
    var nullStarLookTime: Double = 0.0

    fun renderStar(
        ps: PoseStack,
        size: Float,
        buff: MultiBufferSource,
        star: Star,
        blockEntity: BlockEntity,
        pPartialTick: Float,
        font: Font?,
        offsets: Boolean
    ) {
        // if rendering a null star, get how directly the player is looking at it
        if (star.starType == StarType.EMPTY) {
            val player = Minecraft.getInstance().player
            if (player != null) {
                val playerLook = player.lookAngle
                val starPos = Vec3(blockEntity.blockPos.x.toDouble() + 0.5, blockEntity.blockPos.y.toDouble() + 0.5, blockEntity.blockPos.z.toDouble() + 0.5)
                val playerPos = Vec3(player.x, player.y, player.z)
                val lookVec = starPos.subtract(playerPos).normalize()
                val dot = lookVec.dot(Vec3(playerLook.x, playerLook.y, playerLook.z))
                nullStarLookPower = dot
                nullStarLookTime = 1.0.coerceAtMost(nullStarLookTime + (nullStarLookPower * 0.02))
//                player.lookAt(EntityAnchorArgument.Anchor.EYES, starPos)
            }
        }

        val massMult = min(star.mass / 275f, 1.75f)
        val multiplier = max(massMult.toDouble(), 0.5).toFloat()
        val fac = (blockEntity.level!!.gameTime + pPartialTick) * multiplier
        ps.pushPose()
        //        if (offsets) {
//            Vec3 offset = StarUtils.generatePosition(star, blockEntity.getLevel());
//            ps.translate(offset.x / 2, (offset.y + (star.getRandomOffset() * 200)) / 1.75f + 0.05f, offset.z / 2);
//        }
        ps.translate(0.5f, 0.5f, 0.5f)
        ps.mulPose(Axis.YP.rotationDegrees(fac))
        ps.mulPose(Axis.ZP.rotationDegrees(fac))
        ps.translate(-(size / 2) * multiplier, -(size / 2) * multiplier, -(size / 2) * multiplier)
        renderInvertedCube(
            ps,
            buff,
            size * multiplier,
            RenderTypeRegistry.additiveTexture(Astromancy.astromancy("textures/vfx/white.png")),
            getStarColor(star)
        )
        ps.translate((size / 5) * multiplier, (size / 5) * multiplier, (size / 5) * multiplier)
        renderInvertedCube(
            ps,
            buff,
            ((size / 7) * 4) * multiplier,
            RenderTypeRegistry.additiveTexture(Astromancy.astromancy("textures/vfx/white.png")),
            getStarColor(star).brighter().brighter()
        )
        ps.popPose()
    }

    fun renderStar(ps: PoseStack, size: Float, buff: MultiBufferSource, star: Star, time: Int, pPartialTick: Float) {
        val massMult = min(star.mass / 275f, 1.75f)
        val multiplier = max(massMult.toDouble(), 0.5).toFloat()
        val fac = (time + pPartialTick) * multiplier
        ps.pushPose()
        ps.translate(0.5f, 0.5f, 0.5f)
        ps.mulPose(Axis.YP.rotationDegrees(fac))
        ps.mulPose(Axis.ZP.rotationDegrees(fac))
        ps.translate(-(size / 2), -(size / 2), -(size / 2))
        renderInvertedCube(ps, buff, size, RenderType.lightning(), getStarColor(star))
        ps.translate((size / 5), (size / 5), (size / 5))
        renderInvertedCube(
            ps, buff,
            ((size / 7) * 4), RenderType.lightning(), getStarColor(star).brighter().brighter()
        )
        ps.popPose()
    }


    //    fun renderText(ps: PoseStack, text: String?, buffer: MultiBufferSource?, font: Font) {
//        ps.pushPose()
//        ps.mulPose(Axis.XP.rotation(135))
//        val player = Vec3(0.0, 1.5, 0.0)
//        val center =
//            Vec3(ps.last().pose().m03.toDouble(), ps.last().pose().m13.toDouble(), ps.last().pose().m23.toDouble())
//
//        val startYaw = Vec3(0.0, 0.0, 1.0)
//        val endYaw = Vec3(player.x, 0.0, player.z).subtract(Vec3(center.x, 0.0, center.z)).normalize()
//        val d = player.subtract(center)
//
//        // Find angle between start & end in yaw
//        val yaw = Math.toDegrees(atan2(endYaw.x - startYaw.x, endYaw.z - startYaw.z)).toFloat() + 90
//
//        // Find angle between start & end in pitch
//        val pitch = Math.toDegrees(atan2(sqrt(d.z * d.z + d.x * d.x), d.y) + Math.PI).toFloat()
//
//        val Q: Quaternion = Quaternion.ONE.copy()
//
//        // doubling to account for how quats work
//        Q.mul(Quaternion(Axis(0.0f, 1.0f, 0.0f), -yaw * 2, true))
//        Q.mul(Quaternion(Axis(1.0f, 0.0f, 0.0f), pitch + 90, true))
//        //Q.mul(-1);
//        ps.mulPose(Q)
//        ps.scale(0.007f, 0.007f, 0.007f)
//        ps.translate(-60f, 10f, 10f)
//        ps.translate(font.width(text).toFloat(), 0f, 0f)
//        font.draw(ps, text, 0, 0, Color.WHITE.rgb)
//        ps.translate(-font.width(text).toFloat(), 0f, 0f)
//        ps.popPose()
//    }
    fun renderDisc(ps: PoseStack, size: Float, buffer: MultiBufferSource, type: RenderType?, partialTick: Float) {
        ps.pushPose()
        val consumer = buffer.getBuffer(type)
        ps.translate(-size / 2.5, -size / 2.5, 0.01)
        renderQuad(ps, size, consumer, Color(255, 0, 0, 255))
        ps.translate(size / 2.5, size / 2.5, -0.01)
        ps.mulPose(Axis.XP.rotationDegrees(180f))
        ps.translate(-size / 2.5, -size / 2.5, -0.01)
        renderQuad(ps, size, consumer, Color(255, 0, 0, 255))
        ps.popPose()
    }

    fun getStarColor(star: Star): Color {
        var color: Color? = Color(0, 0, 0, 255)
        color = when (star.spectralClass.second) {
            'O' -> StarColors.O.color
            'B' -> StarColors.B.color
            'A' -> StarColors.A.color
            'F' -> StarColors.F.color
            'G' -> StarColors.G.color
            'K' -> StarColors.K.color
            'M' -> StarColors.M.color
            else -> Color(0, 0, 0, 255)
        }
        color = when (star.starType) {
            StarType.EXOTIC -> StarColors.EXOTIC.color
            StarType.EMPTY -> StarColors.EMPTY.color
            StarType.PURE -> StarColors.PURE.color
            StarType.HELL -> StarColors.HELL.color
            else -> color
        }
        return color
    }

    private fun drawHorizontalLineBetween(
        buffer: MultiBufferSource,
        mstack: PoseStack,
        local: Vec3,
        target: Vec3,
        lineWidth: Float,
        r: Int,
        g: Int,
        b: Int,
        a: Int
    ) {
        val builder = buffer.getBuffer(RenderType.leash())

        //Calculate yaw
        val rotY = Mth.atan2(target.x - local.x, target.z - local.z).toFloat()

        //Calculate pitch
        val distX = target.x - local.x
        val distZ = target.z - local.z
        val rotX =
            Mth.atan2(target.y - local.y, Mth.sqrt((distX * distX + distZ * distZ).toFloat()).toDouble()).toFloat()

        mstack.pushPose()

        //Translate to start point
        mstack.translate(local.x, local.y, local.z)
        //Rotate to point towards end point
        mstack.mulPose(Axis.YP.rotation(rotY))
        mstack.mulPose(Axis.XN.rotation(rotX))

        //Calculate distance between points -> length of the line
        val distance = local.distanceTo(target).toFloat()

        val matrix: PoseStack.Pose = mstack.last()
        val halfWidth = lineWidth / 2f

        //Draw horizontal quad
        builder.addVertex(matrix, -halfWidth, 0f, 0f).setColor(r, g, b, a).setLight(0).setNormal(0f, 1f, 0f)
        builder.addVertex(matrix, halfWidth, 0f, 0f).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(1f, 1f, 0f)
        builder.addVertex(matrix, halfWidth, 0f, distance).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(1f, 1f, 0f)
        builder.addVertex(matrix, -halfWidth, 0f, distance).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f)

        //Draw vertical Quad
        builder.addVertex(matrix, 0f, -halfWidth, 0f).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f)
        builder.addVertex(matrix, 0f, halfWidth, 0f).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(1f, 1f, 0f)
        builder.addVertex(matrix, 0f, halfWidth, distance).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(1f, 1f, 0f)
        builder.addVertex(matrix, 0f, -halfWidth, distance).setColor(r, g, b, a).setLight(LightTexture.FULL_BRIGHT).setNormal(0f, 1f, 0f)

        mstack.popPose()
    }

    fun isLookingAt(player: LocalPlayer, target: Vec3, accuracy: Double): Boolean {
        val diff = Vec3(target.x - player.x, target.y - player.eyeY, target.z - player.z)
        val length = diff.length()
        val dot = player.getViewVector(1.0f).normalize().dot(diff.normalize())
        return dot > 1.0 - accuracy / length
    }

    fun renderSkybox(
        pPoseStack: PoseStack,
        texture: ResourceLocation?,
        partialTick: Float,
        gameTime: Float,
        starVis: Float
    ) {
        pPoseStack.pushPose()
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.depthMask(false)
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        RenderSystem.setShaderTexture(0, texture)
        val tesselator = Tesselator.getInstance()
        val bufferbuilder: BufferBuilder = tesselator.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        pPoseStack.mulPose(Axis.YP.rotationDegrees((gameTime * 0.05f)))

        for (i in 0..5) {
            pPoseStack.pushPose()
            if (i == 1) {
                pPoseStack.mulPose(Axis.XP.rotationDegrees(90.0f))
            }

            if (i == 2) {
                pPoseStack.mulPose(Axis.XP.rotationDegrees(-90.0f))
            }

            if (i == 3) {
                pPoseStack.mulPose(Axis.XP.rotationDegrees(180.0f))
            }

            if (i == 4) {
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(90.0f))
            }

            if (i == 5) {
                pPoseStack.mulPose(Axis.ZP.rotationDegrees(-90.0f))
            }

            val alpha = starVis

            val matrix4f: PoseStack.Pose = pPoseStack.last()
            bufferbuilder.addVertex(matrix4f, -90.0f, -90.0f, -90.0f).setUv(0.0f, 0.0f).setColor(0.2f, 0.2f, 0.2f, alpha).setLight(0)
            bufferbuilder.addVertex(matrix4f, -90.0f, -90.0f, 90.0f).setUv(0.0f, 1.0f).setColor(0.2f, 0.2f, 0.2f, alpha).setLight(0)
            bufferbuilder.addVertex(matrix4f, 90.0f, -90.0f, 90.0f).setUv(1.0f, 1.0f).setColor(0.2f, 0.2f, 0.2f, alpha).setLight(0)
            bufferbuilder.addVertex(matrix4f, 90.0f, -90.0f, -90.0f).setUv(1.0f, 0.0f).setColor(0.2f, 0.2f, 0.2f, alpha).setLight(0)
            BufferUploader.drawWithShader(bufferbuilder.build()!!)
            pPoseStack.popPose()
        }

        RenderSystem.depthMask(true)
        RenderSystem.disableBlend()
        pPoseStack.popPose()
    }
    var COLOR_CACHE: List<Color> = mutableListOf()

    fun renderSky(
        partialTick: Float,
        skyFogSetup: Runnable,
        poseStack: PoseStack,
        level: ClientLevel,
        minecraft: Minecraft
    ) {
        FogRenderer.setupNoFog()
        val random = RandomSource.create(10842L)
        val starDistance = 80.0f
        val starRadius = 10f
        val starVariance = 10.0f
        val time = level.gameTime + partialTick
        val starCount = 8192
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        RenderSystem.depthMask(false)
        RenderSystem.setShader { GameRenderer.getPositionTexColorShader() }
        val tess = Tesselator.getInstance()
        val bufferbuilder = tess.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR)
        if (COLOR_CACHE.isEmpty()) {
            for (i in 0 until starCount) {
                val stars = get()!!.universe.getStars()
                val color = getStarColor(stars[i % stars.size])
                COLOR_CACHE += color
            }
        }
        for (star in 0..starCount) {
            poseStack.pushPose()
            val sDist = starDistance + (random.nextGaussian() * starVariance).toFloat()
            var distanceFactor = (starDistance / sDist)
            val size = random.nextFloat()
            var sRad = min((starRadius * size).toDouble(), 0.55).toFloat()

            if (minecraft.player != null) {
                sRad = sRad * (if (minecraft.player!!.isScoping) 0.2f else 1.0f)
            }

            val xRot = (random.nextFloat() * 2) - 1
            val zRot = (random.nextFloat() * 2) - 1
            val scale = (random.nextInt(10) - 5).toFloat()
            poseStack.scale(scale, scale, scale)
            poseStack.mulPose(Axis.YP.rotationDegrees(((time * distanceFactor * 0.01f) * (random.nextGaussian() * random.nextGaussian())).toFloat() + (star * 0.1f)))
            poseStack.mulPose(Axis.XP.rotationDegrees(xRot * 270.0f))
            poseStack.mulPose(Axis.ZP.rotationDegrees(zRot * 270.0f))

            val m2 = poseStack.last().pose()
            distanceFactor = (sDist / starDistance * (1 - abs(xRot.toDouble())) * (1 - abs(zRot.toDouble()))).toFloat()

            //Color.getHSBColor((float) Math.abs(random.nextGaussian()), 0.3F, 1.0F);
            val color = COLOR_CACHE[star % COLOR_CACHE.size]

            if (star % 2 == 0) {
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_med.png"))
            } else if (star % 3 == 0) {
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_big.png"))
            } else {
                RenderSystem.setShaderTexture(0, Astromancy.astromancy("textures/environment/star_small.png"))
            }
            sRad *= max(
                0.05f, Mth.abs(
                    sin((time * (0.1f * random.nextFloat())) * random.nextGaussian())
                        .toFloat()
                )
            )
            distanceFactor *= max(
                ((level.getStarBrightness(partialTick) * 1.4f) * (1.0f - level.getRainLevel(
                    partialTick
                ))), 0.0f
            )
            bufferbuilder.addVertex(m2, -sRad, sDist, -sRad).setUv(0f, 0f)
                .setColor(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, distanceFactor)
            bufferbuilder.addVertex(m2, sRad, sDist, -sRad).setUv(1f, 0f)
                .setColor(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, distanceFactor)
            bufferbuilder.addVertex(m2, sRad, sDist, sRad).setUv(1f, 1f)
                .setColor(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, distanceFactor)
            bufferbuilder.addVertex(m2, -sRad, sDist, sRad).setUv(0f, 1f)
                .setColor(color.red / 255.0f, color.green / 255.0f, color.blue / 255.0f, distanceFactor)

            poseStack.popPose()
        }
        BufferUploader.drawWithShader(bufferbuilder.build())
        val universe = get()!!.universe
        val earth = universe.earth
        val system = universe.earthStarSystem
        var i = 0
        for(planet in universe.earthPlanets) {
            if(planet.uuid == earth) continue
            i++
            poseStack.pushPose()
            val starMass: Float = (system!!.gravityCenter as CelestialBody).mass
            val orbitSpeed: Double =
                UniversalConstants.calculateOrbitSpeed(starMass.toDouble(), (1.5 + i) * 10f.pow(6.0f)) * 10f.pow(7.5f)
            val orbitSpeedRotation = ((orbitSpeed / i) * ((Minecraft.getInstance().level!!.gameTime + partialTick) / 100f) % 360).toFloat()
            poseStack.mulPose(Axis.ZP.rotationDegrees(planet.orbitAngle))
            poseStack.mulPose(Axis.YP.rotationDegrees(orbitSpeedRotation))
            val b = max(0f, (level.getStarBrightness(partialTick) * 1.4f) * (1.0f - level.getRainLevel(
                partialTick
            )) - 0.2f)
            poseStack.translate(10.0, 10.0, 10.0)
//            poseStack.scale(b, b, b)
            renderPlanet(
                poseStack,
                Minecraft.getInstance().renderBuffers().bufferSource(),
                LightTexture.pack(2, Math.ceil(Minecraft.getInstance().level!!.getStarBrightness(partialTick) * 15.0).toInt()),
                OverlayTexture.NO_OVERLAY,
                Minecraft.getInstance().font,
                planet,
                partialTick,
                null,
                b
            )
            poseStack.popPose()
        }
        RenderSystem.depthMask(true)


        skyFogSetup.run()
    }
}