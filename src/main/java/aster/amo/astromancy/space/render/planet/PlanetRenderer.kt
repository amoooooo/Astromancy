package aster.amo.astromancy.space.render.planet

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.content.astrolabe.AstrolabeBlockEntity
import aster.amo.astromancy.content.astrolabe.AstrolabeClientDelegate
import aster.amo.astromancy.content.astrolabe.AstrolabeRenderer.Companion.ALL_VISIBLE
import aster.amo.astromancy.space.classification.stellarobjects.Moon
import aster.amo.astromancy.space.classification.stellarobjects.Planet
import aster.amo.astromancy.util.DebugRenderHelper
import aster.amo.astromancy.util.UniversalConstants
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.phys.Vec3
import org.joml.Vector3d
import java.awt.Color
import java.util.List
import java.util.Map
import java.util.function.Function
import kotlin.math.pow

object PlanetRenderer {
    fun renderPlanet(
        ps: PoseStack,
        bufferSource: MultiBufferSource,
        packedLight: Int,
        packedOverlay: Int,
        font: Font,
        planet: Planet,
        pPartialTick: Float,
        astrolabe: AstrolabeBlockEntity?,
        opacity: Float = 1.0f
    ) {
        ps.pushPose()
        val planetModel = PlanetModelLand()
        val planetModelAtmosphere = PlanetModelAtmosphere()
        val planetModelClouds = PlanetModelClouds()
        val planetModelOcean = PlanetModelOcean()
        val planetModelRing = PlanetModelRing()
        planetModel.landColor = planet.landColor
        planetModelOcean.waterColor = planet.oceanColor
        planetModelAtmosphere.atmosphereColor = planet.skyColor
        planetModelClouds.cloudColor = Color.WHITE
        planetModelRing.ringColor = planet.skyColor
        ps.translate(0.25, -0.5, -0.75)
        ps.translate(0.25, 0.25, 0.25)
        ps.mulPose(Axis.ZP.rotationDegrees(planet.axisTilt.toFloat()))
        ps.mulPose(Axis.YP.rotationDegrees(Minecraft.getInstance().level!!.gameTime + pPartialTick))
        ps.translate(-0.25, -0.25, -0.25)
        ps.scale(4f, 4f, 4f)
        if(astrolabe != null) {
            ps.pushPose()
            ps.translate(0.0, 0.0, 0.0)
            (astrolabe.delegate as AstrolabeClientDelegate).OBBList[planet]?.applyPose(ps)
            ps.popPose()
            astrolabe.delegate?.let { it ->
                (it as AstrolabeClientDelegate).OBBList[planet]?.let { it ->
                    val width = it.width
                    val height = it.height
                    val depth = it.depth
                    val minX = 0 - width / 2
                    val minY = 0 - height / 2
                    val minZ = 0 - depth / 2
                    val maxX = 0 + width / 2
                    val maxY = 0 + height / 2
                    val maxZ = 0 + depth / 2
                    if(Minecraft.getInstance().entityRenderDispatcher.shouldRenderHitBoxes()) {
                        ps.pushPose()
                        ps.scale(0.1f, 0.1f, 0.1f)
                        ps.translate(-0.35, -0.35, -0.35)
                        DebugRenderHelper.renderBox(ps, bufferSource.getBuffer(RenderType.lines()),
                            minX, minY, minZ, maxX, maxY, maxZ, 1f, 0f, 1f, 1f)
                        ps.popPose()
                    }
                }
            }
        }
        var water =
            bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/water.png")))
        if (planet.hasOcean) {
            val oceanColor = planet.oceanColor
            // ARGB
            val oceanColorARGB: Int = ((255 * opacity).toInt() shl 24) or (oceanColor.red shl 16) or (oceanColor.green shl 8) or oceanColor.blue
            planetModelOcean.renderToBuffer(
                ps,
                water,
                packedLight,
                packedOverlay,
                oceanColorARGB
            )
        }
        else {
            val planetColor = planet.landColor
            // ARGB
            val planetColorARGB: Int = ((255 * opacity).toInt() shl 24) or (planetColor.red shl 16) or (planetColor.green shl 8) or planetColor.blue
            planetModel.renderToBuffer(
                ps,
                water,
                packedLight,
                packedOverlay,
                planetColorARGB
            )
        }
        var land =
            bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/land.png")))
        if (planet.hasLand) {
            val planetColor = planet.landColor
            // ARGB
            val planetColorARGB: Int = ((255 * opacity).toInt() shl 24) or (planetColor.red shl 16) or (planetColor.green shl 8) or planetColor.blue
            planetModel.renderToBuffer(ps, land, packedLight, packedOverlay, planetColorARGB)
        }
        val clouds =
            bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/clouds.png")))
        if (planet.hasClouds) {
            val cloudColor = planet.skyColor
            // ARGB
            val cloudColorARGB: Int = ((255 * opacity).toInt() shl 24) or (cloudColor.red shl 16) or (cloudColor.green shl 8) or cloudColor.blue
            planetModelClouds.renderToBuffer(
                ps,
                clouds,
                packedLight,
                packedOverlay,
                cloudColorARGB
            )
        }
        val atmosphere =
            bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/vfx/white.png")))
        if (planet.hasAtmosphere) {
            val atmosphereColor = planet.skyColor
            // ARGB
            val atmosphereColorARGB: Int = ((255 * opacity * 0.25).toInt() shl 24) or (atmosphereColor.red shl 16) or (atmosphereColor.green shl 8) or atmosphereColor.blue
            planetModelAtmosphere.renderToBuffer(
                ps,
                atmosphere,
                packedLight,
                packedOverlay,
                atmosphereColorARGB
            )
        }
        val ring =
            bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/rings.png")))
        if (planet.ringCount > 0) {
            val ringColor = planet.landColor
            // ARGB
            val ringColorARGB: Int = ((255 * opacity).toInt() shl 24) or (ringColor.red shl 16) or (ringColor.green shl 8) or ringColor.blue
            planetModelRing.renderToBuffer(
                ps,
                ring,
                packedLight,
                packedOverlay,
                ringColorARGB
            )
        }

        if (planet.orbitingBodies.isNotEmpty()) {
            ps.pushPose()
            ps.scale(0.1f, 0.1f, 0.1f)
            for (i in 0 until planet.orbitingBodies.size) {
                val moon: Moon = planet.orbitingBodies[i] as Moon
                ps.pushPose()
                ps.translate(0.5, 0.5, 0.5)
                ps.mulPose(Axis.ZP.rotationDegrees(moon.axisTilt.toFloat()))
                ps.mulPose(
                    Axis.YP.rotationDegrees(
                        ((UniversalConstants.calculateOrbitSpeed(
                            moon.mass.toDouble() * 1000,
                            10000.toDouble()
                        ) * 10f.pow(7.5f)) * ((Minecraft.getInstance().level!!.gameTime + pPartialTick) / 10f) % 360).toFloat()
                    )
                )
                ps.translate(2.5f + (i / 10f), 0f, 0f)
                ps.scale(moon.mass, moon.mass, moon.mass)
                ps.translate(-moon.mass, 0.0f, -moon.mass)
                water =
                    bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/water.png")))
                val moonColor = ((255 * opacity).toInt() shl 24) or (127 shl 16) or (127 shl 8) or 127
                planetModelOcean.renderToBuffer(ps, water, packedLight, packedOverlay, moonColor)
                land =
                    bufferSource.getBuffer(RenderType.entityTranslucent(Astromancy.astromancy("textures/planet/land.png")))
                val moonColor2 = ((255 * opacity).toInt() shl 24) or (127 shl 16) or (127 shl 8) or 127
                planetModel.renderToBuffer(ps, land, packedLight, packedOverlay, moonColor2)
                ps.popPose()
            }

            ps.popPose()
        }


        ps.popPose()

    }

    fun calculateEllipticalOrbitPosition(
        time: Float, // Current time or progress (0.0 to 1.0 for a full orbit)
        orbitCenter: Vector3d, // Center point of the ellipse
        semiMajorAxis: Double, // Distance from center to furthest point on the ellipse
        semiMinorAxis: Double, // Distance from center to closest point on the ellipse
        orbitalInclination: Double = 0.0, // Tilt of the orbit plane (in degrees)
        orbitalRotation: Double = 0.0 // Rotation of the ellipse around its center (in degrees)
    ): Vec3 {

        // Calculate the angle along the ellipse at the given time
        val angle = 2 * Math.PI * time

        // Calculate the position on the ellipse without rotation or inclination
        var x = semiMajorAxis * Math.cos(angle)
        var y = 0.0 // For a simple 2D ellipse, y is initially 0
        var z = semiMinorAxis * Math.sin(angle)

        // Apply orbital rotation
        val rotatedX = x * Math.cos(Math.toRadians(orbitalRotation)) - z * Math.sin(Math.toRadians(orbitalRotation))
        val rotatedZ = x * Math.sin(Math.toRadians(orbitalRotation)) + z * Math.cos(Math.toRadians(orbitalRotation))
        x = rotatedX
        z = rotatedZ

        // Apply orbital inclination
        val inclinedY = y * Math.cos(Math.toRadians(orbitalInclination)) - z * Math.sin(Math.toRadians(orbitalInclination))
        val inclinedZ = y * Math.sin(Math.toRadians(orbitalInclination)) + z * Math.cos(Math.toRadians(orbitalInclination))
        y = inclinedY
        z = inclinedZ

        // Offset the position from the origin to the orbit center
        return Vec3(x + orbitCenter.x, y + orbitCenter.y, z + orbitCenter.z)
    }

    class PlanetModelLand :
            Model(Function { p_110453_: ResourceLocation ->
                RenderType.entityTranslucent(
                    p_110453_
                )
            }) {
        var landColor: Color = Color(0.5f, 0.5f, 0.5f)
        private val land: ModelPart

        init {
            val land = List.of(
                ModelPart.Cube(0, 0, 0f, 0f, 0f, 2f, 2f, 2f, 0f, 0f, 0f, false, 2f, 2f, ALL_VISIBLE)
            )
            this.land = ModelPart(land, Map.of())
        }

        override fun renderToBuffer(
            pPoseStack: PoseStack,
            pBuffer: VertexConsumer,
            pPackedLight: Int,
            pPackedOverlay: Int,
            color: Int
        ) {
            pPoseStack.pushPose()
            land.render(
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                color
            )
            pPoseStack.popPose()
        }

    }

    class PlanetModelRing :
            Model(Function { p_110453_: ResourceLocation ->
                RenderType.entityTranslucent(
                    p_110453_
                )
            }) {
        var ringColor: Color = Color(0.5f, 0.5f, 0.5f)
        private val ring: ModelPart

        init {
            val ring = List.of(
                ModelPart.Cube(0, 0, 0f, 0f, 0f, 4f, 0.1f, 4f, 0f, 0f, 0f, false, 4f, 4f, ALL_VISIBLE)
            )
            this.ring = ModelPart(ring, Map.of())
        }

        override fun renderToBuffer(
            pPoseStack: PoseStack,
            pBuffer: VertexConsumer,
            pPackedLight: Int,
            pPackedOverlay: Int,
            color: Int
        ) {
            pPoseStack.pushPose()
            pPoseStack.translate(-0.065, 0.0625, -0.065)
            ring.render(
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                color
            )
            pPoseStack.popPose()
        }
    }

    class PlanetModelOcean :
            Model(Function { p_110453_: ResourceLocation ->
                RenderType.entityTranslucent(
                    p_110453_
                )
            }) {
        var waterColor: Color = Color(0.5f, 0.5f, 0.5f)
        private val ocean: ModelPart

        init {
            val ocean = List.of(
                ModelPart.Cube(0, 0, 0f, 0f, 0f, 2f, 2f, 2f, 0f, 0f, 0f, false, 2f, 2f, ALL_VISIBLE)
            )
            this.ocean = ModelPart(ocean, Map.of())
        }

        override fun renderToBuffer(
            pPoseStack: PoseStack,
            pBuffer: VertexConsumer,
            pPackedLight: Int,
            pPackedOverlay: Int,
            color: Int
        ) {
            pPoseStack.pushPose()
            ocean.render(
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                color
            )
            pPoseStack.popPose()
        }
    }

    class PlanetModelAtmosphere :
            Model(Function { p_110474_: ResourceLocation ->
                RenderType.entityTranslucent(
                    p_110474_
                )
            }) {
        private val atmosphere: ModelPart
        var atmosphereColor: Color = Color(0, 0, 0, 0)

        init {
            val atmosphere = List.of(
                ModelPart.Cube(0, 0, 0f, 0f, 0f, 2f, 2f, 2f, 0f, 0f, 0f, false, 2f, 2f, ALL_VISIBLE)
            )
            this.atmosphere = ModelPart(atmosphere, Map.of())
        }

        override fun renderToBuffer(
            pPoseStack: PoseStack,
            pBuffer: VertexConsumer,
            pPackedLight: Int,
            pPackedOverlay: Int,
            color: Int
        ) {
            pPoseStack.pushPose()
            pPoseStack.scale(1.1f, 1.1f, 1.1f)
            pPoseStack.translate(-0.005, -0.005, -0.005)
            atmosphere.render(
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                color
            )
            pPoseStack.popPose()
        }
    }

    class PlanetModelClouds :
            Model(Function { p_110453_: ResourceLocation ->
                RenderType.entityTranslucent(
                    p_110453_
                )
            }) {
        private val cloud: ModelPart
        var cloudColor: Color = Color(0, 0, 0, 0)

        init {
            val cloud = List.of(
                ModelPart.Cube(0, 0, 0f, 0f, 0f, 2f, 2f, 2f, 0f, 0f, 0f, false, 2f, 2f, ALL_VISIBLE)
            )
            this.cloud = ModelPart(cloud, Map.of())
        }

        override fun renderToBuffer(
            pPoseStack: PoseStack,
            pBuffer: VertexConsumer,
            pPackedLight: Int,
            pPackedOverlay: Int,
            color: Int
        ) {
            pPoseStack.pushPose()
            pPoseStack.scale(1.05f, 1.05f, 1.05f)
            pPoseStack.translate(-0.0025, -0.0025, -0.0025)
            cloud.render(
                pPoseStack,
                pBuffer,
                pPackedLight,
                pPackedOverlay,
                color
            )
            pPoseStack.popPose()
        }
    }
}