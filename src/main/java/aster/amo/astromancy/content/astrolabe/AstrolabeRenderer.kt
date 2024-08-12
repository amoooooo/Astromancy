package aster.amo.astromancy.content.astrolabe

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.data.AstromancySavedData
import aster.amo.astromancy.mixin.client.LevelRendererAccessor
import aster.amo.astromancy.space.classification.stellarobjects.*
import aster.amo.astromancy.space.render.planet.PlanetRenderer
import aster.amo.astromancy.space.render.planet.PlanetRenderer.calculateEllipticalOrbitPosition
import aster.amo.astromancy.util.DebugRenderHelper
import aster.amo.astromancy.util.RenderHelper
import aster.amo.astromancy.util.RenderTypeRegistry
import aster.amo.astromancy.util.UniversalConstants
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.util.RandomSource
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.AxisAngle4f
import org.joml.Quaternionf
import org.joml.Vector3d
import org.joml.Vector3f
import java.awt.Color
import java.util.*
import kotlin.math.atan2
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.sqrt
data class NebulaCube(val position: Vector3d, val scale: Float, val color: Int)
class AstrolabeRenderer(context: BlockEntityRendererProvider.Context) : BlockEntityRenderer<AstrolabeBlockEntity> {
    private val font: Font = context.font
    private val planetModel = PlanetRenderer.PlanetModelLand()
    private val planetModelOcean = PlanetRenderer.PlanetModelOcean()
    val random = RandomSource.createNewThreadLocalInstance()
    val nebulaModel = PlanetRenderer.PlanetModelAtmosphere()

    private val nebulaCubes: MutableList<NebulaCube> = mutableListOf()

    init {
        generateNebulaStructure()
    }

    private fun generateNebulaStructure() {
        // Configure nebula properties:
        val nebulaCenter = Vector3d(0.5, 1.5, 0.5)
        val baseNebulaRadius = 10
        val nebulaDensity = 0.2
        val overlapFactor = 1.2f

        // Iterate over the spherical volume
        for (x in -baseNebulaRadius.toInt()..baseNebulaRadius.toInt()) {
            for (y in -baseNebulaRadius.toInt()..baseNebulaRadius.toInt()) {
                for (z in -baseNebulaRadius.toInt()..baseNebulaRadius.toInt()) {
                    val pos = Vector3d(x.toDouble(), y.toDouble(), z.toDouble())
                    val distanceFromCenter = pos.distance(nebulaCenter)

                    if (distanceFromCenter <= baseNebulaRadius) {
                        val densityFactor = perlinNoise3D(
                            (pos.x + nebulaCenter.x) * 0.1,
                            (pos.y + nebulaCenter.y) * 0.1,
                            (pos.z + nebulaCenter.z) * 0.1
                        )

                        val normalizedDensity = (densityFactor * 0.5 + 0.5).coerceIn(0.0, 1.0)
                        if (normalizedDensity > 1.0 - nebulaDensity) {
                            val scaleFactor = 1.0f - (distanceFromCenter / baseNebulaRadius).toFloat() * 0.8f
                            val overlappedScale = scaleFactor * overlapFactor
                            val color = generateNebulaColor(pos)

                            nebulaCubes.add(NebulaCube(pos.add(nebulaCenter), overlappedScale, color))
                        }
                    }
                }
            }
        }
    }

    private fun generateNebulaColor(pos: Vector3d): Int {
        val noiseR = perlinNoise3D(pos.x * 0.05, pos.y * 0.05, pos.z * 0.05)
        val noiseG = perlinNoise3D(pos.x * 0.05 + 100.0, pos.y * 0.05 + 100.0, pos.z * 0.05 + 100.0)

        val color1 = Color(128, 0, 128, 128).rgb // Purple
        val color2 = Color(255, 105, 180, 128).rgb // Pink
        val color3 = Color(0, 0, 128, 128).rgb // Deep blue

        val r = interpolateColor(color1, color2, noiseR).and(0xFF0000).shr(16)
        val g = interpolateColor(color2, color3, noiseG).and(0x00FF00).shr(8)
        val b = interpolateColor(color3, color1, (noiseR + noiseG) / 2.0).and(0x0000FF)

        return 0x11000000.toInt() or (r shl 16) or (g shl 8) or b
    }

    private fun interpolateColor(color1: Int, color2: Int, value: Double): Int {
        val r1 = color1.and(0xFF0000).shr(16)
        val g1 = color1.and(0x00FF00).shr(8)
        val b1 = color1.and(0x0000FF)
        val r2 = color2.and(0xFF0000).shr(16)
        val g2 = color2.and(0x00FF00).shr(8)
        val b2 = color2.and(0x0000FF)

        val r = (r1 + (r2 - r1) * value).toInt()
        val g = (g1 + (g2 - g1) * value).toInt()
        val b = (b1 + (b2 - b1) * value).toInt()

        return (r shl 16) or (g shl 8) or b
    }

    override fun render(
        pBlockEntity: AstrolabeBlockEntity,
        pPartialTick: Float,
        ps: PoseStack,
        pBufferSource: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        ps.pushPose()
        val actualDistance =
            Minecraft.getInstance().player!!.position().distanceTo(Vec3.atCenterOf(pBlockEntity.blockPos))
        val distance = if (actualDistance > 10) 0.0 else actualDistance
        val distanceFactor = max(0.0, max(0.0, distance / 10.0))
        drawRequirements(ps, pBufferSource, pBlockEntity, distanceFactor, 0xFFFFFF, pPartialTick)
        pBlockEntity.delegate?.let { it ->
            (it as AstrolabeClientDelegate).OBBList.values.forEach { it ->
                if(Minecraft.getInstance().entityRenderDispatcher.shouldRenderHitBoxes()) {
                    ps.pushPose()
                    ps.scale(0.1f, 0.1f, 0.1f)
                    ps.translate(-0.35, -0.35, -0.35)
                    DebugRenderHelper.renderBox(ps, pBufferSource.getBuffer(RenderType.lines()),
                        it.min().x, it.min().y, it.min().z, it.max().x, it.max().y, it.max().z, 1f, 1f, 1f, 1f)
                    ps.popPose()
                }
            }
        }
        if (pBlockEntity.starSystem?.gravityCenter is BinarySystem) {
            val binarySystem = pBlockEntity.starSystem?.gravityCenter as BinarySystem
            ps.pushPose()
            ps.translate(0.5, 0.5, 0.5)
            val a1 = UniversalConstants.binaryStarCenterOfMass(
                binarySystem.star1.mass.toDouble(),
                binarySystem.star2.mass.toDouble(),
                0.5
            ).first
            val a2 = UniversalConstants.binaryStarCenterOfMass(
                binarySystem.star1.mass.toDouble(),
                binarySystem.star2.mass.toDouble(),
                0.5
            ).second
            val orbitSpeed: Double = UniversalConstants.binaryStarOrbitSpeed(
                binarySystem.star1.mass.toDouble(),
                binarySystem.star2.mass.toDouble(),
                a1 + a2
            ) * 10f.pow(3.5f)
            ps.mulPose(Axis.YP.rotationDegrees(((Minecraft.getInstance().level!!.gameTime + pPartialTick) * orbitSpeed.toFloat())))
            ps.translate(-0.5, -0.5, -0.5)
            ps.pushPose()
            ps.translate(0.0, 0.0, a1)
            RenderHelper.renderStar(
                ps,
                Math.max(0.2f, binarySystem.star1.mass / 100000f),
                pBufferSource,
                binarySystem.star1,
                pBlockEntity,
                pPartialTick,
                font,
                true
            )
            ps.popPose()

            ps.pushPose()
            ps.translate(0.0, 0.0, -a2)
            RenderHelper.renderStar(
                ps,
                Math.max(0.2f, binarySystem.star2.mass / 100000f),
                pBufferSource,
                binarySystem.star2,
                pBlockEntity,
                pPartialTick,
                font,
                true
            )
            ps.popPose()
            ps.popPose()
        } else {
            ps.pushPose()
            ps.translate(0.0, 0.1, 0.0)
            if (pBlockEntity.starSystem?.gravityCenter != null) {
                val star = pBlockEntity.starSystem?.gravityCenter as CelestialBody
                RenderHelper.renderStar(
                    ps,
                    Math.min(0.3f, star.mass / 1000f),
                    pBufferSource,
                    star as Star,
                    pBlockEntity,
                    pPartialTick,
                    font,
                    true
                )
            }
            ps.popPose()
        }
        ps.translate(0.0, 0.8, 1.0)
        for (i in 0 until pBlockEntity.starSystem?.celestialBodies?.size!!) {
            val body: CelestialBody = pBlockEntity.starSystem?.celestialBodies?.get(i) as CelestialBody
            if(body is Planet) {
                renderPlanet(ps, pBlockEntity, i, pPartialTick, body, pPackedLight, pPackedOverlay, pBufferSource)
            } else if (body is Meteorite) {
                renderMeteorite(ps, body, pBufferSource, pPackedLight, pPackedOverlay)
            }
        }

        run {
            val pulseSpeed = 0.002f
            val pulseMagnitude = 1.5
            val pulseOffset = (Math.sin(((Minecraft.getInstance().level!!.gameTime + pPartialTick) * pulseSpeed).toDouble()) + 1) / 2.0
            ps.pushPose()
            ps.translate(3.5, 0.0, 3.5)
            for (nebulaCube in nebulaCubes) {
                ps.pushPose()
                val pulsatingScale = nebulaCube.scale * (1f + (pulseOffset * pulseMagnitude).toFloat()) * 10f
                ps.scale(0.2f, 0.2f, 0.2f)
                ps.translate(nebulaCube.position.x, nebulaCube.position.y, nebulaCube.position.z)
                ps.scale(pulsatingScale, pulsatingScale, pulsatingScale)

                // ... (Apply additional transformations if needed) ...

                // Render the nebula cube
                nebulaModel.renderToBuffer(
                    ps,
                    pBufferSource.getBuffer(RenderType.lightning()),
                    LightTexture.FULL_BRIGHT,
                    pPackedOverlay,
                    nebulaCube.color
                )

                ps.popPose()
            }
            ps.popPose()
        }

        ps.pushPose()

//        for (i in 0 until pBlockEntity.star.getSolarSystemObjects().size()) {
//            ps.pushPose()
//            ps.translate(0.5, 0.0, -0.5)
//            val celestialBody: CelestialBody = pBlockEntity.star.getSolarSystemObjects().get(i)
//            val starMass: Float = (pBlockEntity.starSystem?.gravityCenter as CelestialBody).mass
//            val a1 = UniversalConstants.binaryStarCenterOfMass(
//                starMass.toDouble(), celestialBody.getSize(),
//                (i + 1).toDouble()
//            ).first
//            val a2 = UniversalConstants.binaryStarCenterOfMass(
//                starMass.toDouble(), celestialBody.getSize(),
//                (i + 1).toDouble()
//            ).second
//            val orbitSpeed: Double =
//                UniversalConstants.binaryStarOrbitSpeed(starMass.toDouble(), celestialBody.getSize(), a1 + a2) * 10f.pow(4.0)
//            ps.mulPose(Axis.ZP.rotationDegrees(celestialBody.getAxisTilt()))
//            ps.mulPose(Axis.YP.rotationDegrees(((Minecraft.getInstance().level!!.gameTime + pPartialTick) * orbitSpeed.toFloat())))
//            ps.translate(0.0, 0.0, a1 * 2500 * (i / 2f))
//            ps.scale(celestialBody.getSize() / 10f, celestialBody.getSize() / 10f, celestialBody.getSize() / 10f)
//            ps.translate(-celestialBody.getSize() / 10f, 0, -celestialBody.getSize() / 10f)
//            renderAstralObject(ps, pBufferSource, pPackedLight, pPackedOverlay, font, celestialBody, pPartialTick)
//            ps.popPose()
//        }

        ps.popPose()


        ps.popPose()
    }

    private fun renderMeteorite(
        ps: PoseStack,
        body: Meteorite,
        pBufferSource: MultiBufferSource,
        pPackedLight: Int,
        pPackedOverlay: Int
    ) {
        ps.pushPose()

        val time = (System.currentTimeMillis() % body.orbitalPeriod) / body.orbitalPeriod
        val orbitCenter = body.orbitCenter
        val semiMajorAxis = body.semiMajorAxis
        val semiMinorAxis = body.semiMinorAxis
        val inclination = body.orbitalInclination
        val rotation = body.orbitalRotation

        val meteorPosition = calculateEllipticalOrbitPosition(
            time.toFloat(),
            orbitCenter,
            semiMajorAxis,
            semiMinorAxis,
            inclination,
            rotation
        )

        ps.translate(meteorPosition.x * 10f, meteorPosition.y * 10f, meteorPosition.z * 10f)
        val rotationAngle = System.currentTimeMillis() % 360f
        ps.mulPose(Axis.YP.rotation(rotationAngle * 10))
        ps.mulPose(Axis.XP.rotation(rotationAngle * 0.5f))
        val moonColor2 = ((255 * 1.0f).toInt() shl 24) or (127 shl 16) or (127 shl 8) or 127
        ps.scale(body.mass / 2f, body.mass / 2f, body.mass / 2f)
        planetModelOcean.renderToBuffer(
            ps,
            pBufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/water.png"))),
            pPackedLight,
            pPackedOverlay,
            moonColor2
        )
        planetModel.renderToBuffer(
            ps,
            pBufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/land.png"))),
            pPackedLight,
            pPackedOverlay,
            moonColor2
        )
        ps.popPose()
    }

    private fun renderPlanet(
        ps: PoseStack,
        pBlockEntity: AstrolabeBlockEntity,
        i: Int,
        pPartialTick: Float,
        body: Planet,
        pPackedLight: Int,
        pPackedOverlay: Int,
        pBufferSource: MultiBufferSource
    ) {
        ps.pushPose()
        val starMass: Float = (pBlockEntity.starSystem?.gravityCenter as CelestialBody).mass
        val orbitSpeed: Double =
            UniversalConstants.calculateOrbitSpeed(starMass.toDouble(), (1.5 + i) * 10f.pow(6.0f)) * 10f.pow(7.5f)
        val orbitSpeedRotation =
            ((orbitSpeed / i) * ((Minecraft.getInstance().level!!.gameTime + pPartialTick) / 10f) % 360).toFloat()
        ps.mulPose(Axis.ZP.rotationDegrees(body.orbitAngle))
        ps.mulPose(Axis.YP.rotationDegrees(orbitSpeedRotation))
        ps.translate(0.5 + (i), 0.0, 0.0)
        ps.scale(body.mass / 10000f, body.mass / 10000f, body.mass / 10000f)
        ps.translate(-body.mass / 10000f, -body.mass / 10000f, -body.mass / 10000f)
        if (AstromancySavedData.get()?.universe?.earth == body.uuid) {
            PlanetRenderer.renderPlanet(
                ps,
                Minecraft.getInstance().renderBuffers().outlineBufferSource(),
                pPackedLight,
                pPackedOverlay,
                font,
                body,
                pPartialTick,
                pBlockEntity
            )
            Minecraft.getInstance().renderBuffers().outlineBufferSource().endOutlineBatch();
            (Minecraft.getInstance().levelRenderer as LevelRendererAccessor).entityEffect.process(pPartialTick)
            Minecraft.getInstance().mainRenderTarget.bindWrite(false)
        }
        PlanetRenderer.renderPlanet(
            ps,
            pBufferSource,
            pPackedLight,
            pPackedOverlay,
            font,
            body,
            pPartialTick,
            pBlockEntity
        )
        ps.popPose()
        ps.pushPose()
        ps.mulPose(Axis.ZP.rotationDegrees(body.orbitAngle))
        ps.mulPose(Axis.YP.rotationDegrees(orbitSpeedRotation))
        ps.translate(0.5 + (i), 0.0, 0.0)
        ps.scale(body.mass / 10000f, body.mass / 10000f, body.mass / 10000f)
        ps.translate(-body.mass / 10000f, -body.mass / 10000f, -body.mass / 10000f)
        ps.mulPose(Axis.YP.rotationDegrees(-orbitSpeedRotation))
        ps.mulPose(Axis.ZP.rotationDegrees(-body.orbitAngle))
        ps.mulPose(Minecraft.getInstance().entityRenderDispatcher.cameraOrientation())
        ps.scale(0.009f, 0.009f, 0.009f)
        ps.mulPose(Axis.YN.rotationDegrees(180f))
        ps.mulPose(Axis.ZP.rotationDegrees(180f))
        font.drawInBatch(
            body.name,
            -0.5f,
            0.5f,
            0xFFFFFF,
            true,
            ps.last().pose(),
            pBufferSource,
            Font.DisplayMode.NORMAL,
            0x000000,
            LightTexture.FULL_BRIGHT
        )
        ps.popPose()
    }


    override fun shouldRenderOffScreen(pBlockEntity: AstrolabeBlockEntity): Boolean {
        return true
    }

    override fun shouldRender(blockEntity: AstrolabeBlockEntity, cameraPos: Vec3): Boolean {
        return true
    }

    override fun getViewDistance(): Int {
        return 256
    }

    override fun getRenderBoundingBox(blockEntity: AstrolabeBlockEntity): AABB {
        val pos = blockEntity.blockPos
        return AABB(
            pos.x.toDouble() - 256,
            0.0,
            pos.z.toDouble() - 256,
            pos.x.toDouble() + 256,
            1024.0,
            pos.z.toDouble() + 256
        )
    }

    private fun drawRequirements(
        ps: PoseStack,
        buffer: MultiBufferSource,
        blockEntity: AstrolabeBlockEntity,
        distanceFactor: Double,
        color: Int,
        partialTick: Float = 0.0f
    ) {
        ps.pushPose()
        ps.translate(0.5, 0.35, 0.5)
        ps.mulPose(Axis.XP.rotation(135f))
        val player = Minecraft.getInstance().player
        val eyeVec = player!!.eyePosition
        val oldEyeY = player!!.yOld + player!!.eyeHeight
        val playerVec = Vec3(
            player!!.xOld + (eyeVec.x - player!!.xOld) * partialTick,
            oldEyeY + (eyeVec.y - oldEyeY) * partialTick,
            player!!.zOld + (eyeVec.z - player!!.zOld) * partialTick
        )
        val center = Vec3(
            blockEntity.getBlockPos().getX() + 0.5,
            blockEntity.getBlockPos().getY() + 0.5,
            blockEntity.getBlockPos().getZ() + 0.5
        )

        val startYaw = Vec3(0.0, 0.0, 1.0)
        val endYaw = Vec3(playerVec.x, 0.0, playerVec.z).subtract(Vec3(center.x, 0.0, center.z)).normalize()
        val d = playerVec.subtract(center)

        // Find angle between start & end in yaw
        val yaw = Math.toDegrees(atan2(endYaw.x - startYaw.x, endYaw.z - startYaw.z)).toFloat() + 90

        // Find angle between start & end in pitch
        val pitch = Math.toDegrees(atan2(sqrt(d.z * d.z + d.x * d.x), d.y) + Math.PI).toFloat()

        val Q: Quaternionf = Quaternionf(0.0,0.0,0.0,1.0)

        // doubling to account for how quats work
        Q.mul(Quaternionf(AxisAngle4f(Math.toRadians((-yaw * 2).toDouble()).toFloat(), Vector3f(0.0f, 1.0f, 0.0f))))
        Q.mul(Quaternionf(AxisAngle4f(Math.toRadians(pitch + 90.0).toFloat(), Vector3f(1.0f, 0.0f, 0.0f))))
        //Q.mul(-1);
        ps.mulPose(Q)
        ps.translate(0.05, 0.0, -0.5)
        ps.scale(
            0.015f * max(0.7, distanceFactor).toFloat(),
            0.015f * max(0.7, distanceFactor).toFloat(),
            0.015f * max(0.7, distanceFactor)
                .toFloat()
        )
        //ps.mulPose(Vector3f.ZP.rotationDegrees(180));
        val glyphs = blockEntity.starSystem?.glyphSequence?.sequence?.map { it -> Component.literal(it.symbol()).withStyle { s -> s.withFont(Astromancy.astromancy("glyph")) } } ?: run {
            ps.popPose()
            return
        }
        ps.translate(-font.lineHeight * glyphs.size / 2.0, 0.0, 0.0)
        for (requirement in glyphs) {
            ps.translate(-font.width(requirement) / 2.0f, 0f, 0f)
            font.drawInBatch(requirement, 0f, 0f, color, false, ps.last().pose(), buffer, Font.DisplayMode.NORMAL, 0xAA000000.toInt(), LightTexture.FULL_BRIGHT)
            ps.translate(font.width(requirement) / 2.0f, 0f, 0f)
            ps.translate(font.width(requirement).toFloat(), 0f, 0f)
        }
        ps.popPose()
    }

    companion object {
        val ALL_VISIBLE: Set<Direction> = EnumSet.allOf(
            Direction::class.java
        )
        fun renderAstralObject(
            ps: PoseStack,
            bufferSource: MultiBufferSource,
            packedLight: Int,
            packedOverlay: Int,
            font: Font?,
            `object`: CelestialBody?,
            pTick: Float
        ) {
            ps.pushPose()
            val planetModel = PlanetRenderer.PlanetModelLand()
            val planetModelOcean = PlanetRenderer.PlanetModelOcean()
            val water =
                bufferSource.getBuffer(RenderType.entityCutout(Astromancy.astromancy("textures/planet/water.png")))
            planetModelOcean.renderToBuffer(ps, water, packedLight, packedOverlay, 0xFFAAAAAA.toInt())
            val land =
                bufferSource.getBuffer(RenderType.entityCutout(Astromancy.astromancy("textures/planet/land.png")))
            planetModel.renderToBuffer(ps, land, packedLight, packedOverlay, 0xFFAAAAAA.toInt())
            ps.popPose()
        }


        // --- Simple 3D Perlin Noise Implementation ---
// (You can replace this with a more advanced implementation if needed)

        fun perlinNoise3D(x: Double, y: Double, z: Double): Double {
            // Adjust these values for different noise characteristics
            val persistence = 0.5 // Controls how much detail is added at each octave
            val numOctaves = 4     // Number of layers of noise combined

            var total = 0.0
            var frequency = 1.0
            var amplitude = 1.0
            var maxValue = 0.0  // Used for normalizing result to 0.0 - 1.0

            for (i in 0 until numOctaves) {
                total += interpolatedNoise(x * frequency, y * frequency, z * frequency) * amplitude
                maxValue += amplitude
                amplitude *= persistence
                frequency *= 2.0
            }

            return total / maxValue
        }

        // --- Helper function for interpolation (used by perlinNoise3D) ---
        private fun interpolatedNoise(x: Double, y: Double, z: Double): Double {
            val ix = x.toInt()
            val iy = y.toInt()
            val iz = z.toInt()

            val fx = x - ix
            val fy = y - iy
            val fz = z - iz

            val v1 = smoothNoise(ix, iy, iz)
            val v2 = smoothNoise(ix + 1, iy, iz)
            val v3 = smoothNoise(ix, iy + 1, iz)
            val v4 = smoothNoise(ix + 1, iy + 1, iz)
            val i1 = interpolate(v1, v2, fx)
            val i2 = interpolate(v3, v4, fx)
            val w1 = interpolate(i1, i2, fy)

            val v5 = smoothNoise(ix, iy, iz + 1)
            val v6 = smoothNoise(ix + 1, iy, iz + 1)
            val v7 = smoothNoise(ix, iy + 1, iz + 1)
            val v8 = smoothNoise(ix + 1, iy + 1, iz + 1)
            val i3 = interpolate(v5, v6, fx)
            val i4 = interpolate(v7, v8, fx)
            val w2 = interpolate(i3, i4, fy)

            return interpolate(w1, w2, fz)
        }

        // --- Helper function for smoothing noise (used by interpolatedNoise) ---
        private fun smoothNoise(x: Int, y: Int, z: Int): Double {
            // Simple hash function for demo purposes (can be improved)
            var h = (x * 31 + y * 53 + z * 79) and 0xFFFF
            h = (h shl 13) xor h
            return 1.0 - (h * (h * h * 15731 + 789221 + 1376312589) and 0x7fffffff) / 1073741824.0
        }

        // --- Linear interpolation function ---
        private fun interpolate(a: Double, b: Double, x: Double): Double {
            return a * (1 - x) + b * x
        }
    }
}