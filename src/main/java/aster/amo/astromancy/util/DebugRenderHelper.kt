package aster.amo.astromancy.util

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.PoseStack.Pose
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.phys.AABB
import org.joml.Matrix3f
import org.joml.Matrix4f
import org.joml.Vector3f


// bunch of static functions for rendering spheres etc
// all pretty self-explanatory
object DebugRenderHelper {
    fun renderCube(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        renderBox(pPoseStack, pConsumer, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, pRed, pGreen, pBlue, pAlpha)
    }

    fun renderBox(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pMinX: Double,
        pMinY: Double,
        pMinZ: Double,
        pMaxX: Double,
        pMaxY: Double,
        pMaxZ: Double,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        val matrix4f = pPoseStack.last().pose()
        val matrix3f = pPoseStack.last()
        val iX = pMinX.toFloat()
        val iY = pMinY.toFloat()
        val iZ = pMinZ.toFloat()
        val aX = pMaxX.toFloat()
        val aY = pMaxY.toFloat()
        val aZ = pMaxZ.toFloat()
        pConsumer.addVertex(matrix4f, iX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
        pConsumer.addVertex(matrix4f, iX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, -1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, -1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, -1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, -1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, iX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, -1.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, -1.0f)
            
        pConsumer.addVertex(matrix4f, iX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 1.0f, 0.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, iY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 1.0f, 0.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, iZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
        pConsumer.addVertex(matrix4f, aX, aY, aZ).setColor(pRed, pGreen, pBlue, pAlpha).setNormal(matrix3f, 0.0f, 0.0f, 1.0f)
            
    }

    fun renderBox(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        box: AABB,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        renderBox(
            pPoseStack,
            pConsumer,
            box.minX,
            box.minY,
            box.minZ,
            box.maxX,
            box.maxY,
            box.maxZ,
            pRed,
            pGreen,
            pBlue,
            pAlpha
        )
    }

    fun renderSphere(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        resolution: Int,
        radius: Float,
        x: Double,
        y: Double,
        z: Double,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        pPoseStack.pushPose()
        pPoseStack.translate(x, y, z)

        for (i in 0..2) {
            pPoseStack.pushPose()
            when (i) {
                1 -> pPoseStack.mulPose(Axis.YP.rotationDegrees(90f))
                2 -> pPoseStack.mulPose(Axis.XP.rotationDegrees(90f))
            }
            val matrix4f = pPoseStack.last().pose()
            val matrix3f = pPoseStack.last()
            pPoseStack.popPose()
            for (segment in 0 until resolution) {
                val angle1 = (segment / resolution.toFloat()) * Mth.TWO_PI
                val angle2 = ((segment + 1) / resolution.toFloat()) * Mth.TWO_PI
                val s1 = Mth.sin(angle1) * radius
                val c1 = Mth.cos(angle1) * radius
                val s2 = Mth.sin(angle2) * radius
                val c2 = Mth.cos(angle2) * radius

                val setNormal = Vector3f(s1, 0f, c1)
                setNormal.sub(Vector3f(s2, 0f, c2))
                setNormal.normalize()

                renderLine(matrix4f, matrix3f, pConsumer, s1, c1, 0f, s2, c2, 0f, pRed, pGreen, pBlue, pAlpha)
            }
        }

        pPoseStack.popPose()
    }

    fun renderCircle(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        resolution: Int,
        radius: Float,
        x: Double,
        y: Double,
        z: Double,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        val mc = Minecraft.getInstance()
        val pRenderInfo = mc.gameRenderer.mainCamera

        val rotation = pRenderInfo.rotation()
        pPoseStack.pushPose()
        pPoseStack.translate(x, y, z)
        pPoseStack.mulPose(rotation)

        val matrix4f = pPoseStack.last().pose()
        val matrix3f = pPoseStack.last()
        for (segment in 0 until resolution) {
            val angle1 = (segment / resolution.toFloat()) * Mth.TWO_PI
            val angle2 = ((segment + 1) / resolution.toFloat()) * Mth.TWO_PI
            val s1 = Mth.sin(angle1) * radius
            val c1 = Mth.cos(angle1) * radius
            val s2 = Mth.sin(angle2) * radius
            val c2 = Mth.cos(angle2) * radius

            val setNormal = Vector3f(s1, 0f, c1)
            setNormal.sub(Vector3f(s2, 0f, c2))
            setNormal.normalize()

            renderLine(matrix4f, matrix3f, pConsumer, s1, c1, 0f, s2, c2, 0f, pRed, pGreen, pBlue, pAlpha)
        }

        pPoseStack.popPose()
    }

    fun renderLine(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pMinX: Double,
        pMinY: Double,
        pMinZ: Double,
        pMaxX: Double,
        pMaxY: Double,
        pMaxZ: Double,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        val matrix4f = pPoseStack.last().pose()
        val matrix3f = pPoseStack.last()
        val minX = pMinX.toFloat()
        val minY = pMinY.toFloat()
        val minZ = pMinZ.toFloat()
        val maxX = pMaxX.toFloat()
        val maxY = pMaxY.toFloat()
        val maxZ = pMaxZ.toFloat()
        val setNormal = Vector3f(minX, minY, minZ)
        setNormal.sub(Vector3f(maxX, maxY, maxZ))
        setNormal.normalize()

        pConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(pRed, pGreen, pBlue, pAlpha)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
        pConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(pRed, pGreen, pBlue, pAlpha)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
    }

    fun renderLine(
        pPoseStack: PoseStack,
        pConsumer: VertexConsumer,
        pMinX: Double,
        pMinY: Double,
        pMinZ: Double,
        pMaxX: Double,
        pMaxY: Double,
        pMaxZ: Double,
        r1: Float,
        g1: Float,
        b1: Float,
        a1: Float,
        r2: Float,
        g2: Float,
        b2: Float,
        a2: Float
    ) {
        val matrix4f = pPoseStack.last().pose()
        val matrix3f = pPoseStack.last()
        val minX = pMinX.toFloat()
        val minY = pMinY.toFloat()
        val minZ = pMinZ.toFloat()
        val maxX = pMaxX.toFloat()
        val maxY = pMaxY.toFloat()
        val maxZ = pMaxZ.toFloat()
        val setNormal = Vector3f(minX, minY, minZ)
        setNormal.sub(Vector3f(maxX, maxY, maxZ))
        setNormal.normalize()

        pConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(r1, g1, b1, a1)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
        pConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(r2, g2, b2, a2)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
    }

    fun renderLine(
        matrix4f: Matrix4f?,
        matrix3f: Pose?,
        pConsumer: VertexConsumer,
        minX: Float,
        minY: Float,
        minZ: Float,
        maxX: Float,
        maxY: Float,
        maxZ: Float,
        pRed: Float,
        pGreen: Float,
        pBlue: Float,
        pAlpha: Float
    ) {
        val setNormal = Vector3f(minX, minY, minZ)
        setNormal.sub(Vector3f(maxX, maxY, maxZ))
        setNormal.normalize()

        pConsumer.addVertex(matrix4f, minX, minY, minZ).setColor(pRed, pGreen, pBlue, pAlpha)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
        pConsumer.addVertex(matrix4f, maxX, maxY, maxZ).setColor(pRed, pGreen, pBlue, pAlpha)
            .setNormal(matrix3f, setNormal.x(), setNormal.y(), setNormal.z())
    }
}