package aster.amo.astromancy.util

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Vector4f

class OrientedBoundingBox(var center: Vec3, val width: Double, val height: Double, val depth: Double) {
    private val originalCenter = Vec3(center.toVector3f())
    private var pose: PoseStack? = null

    fun applyPose(poseStack: PoseStack) {
        // copy the posestack to a new one
        val newPose = PoseStack()
        newPose.last().pose().set(poseStack.last().pose())
        pose = newPose
        val transformMatrix = pose?.last()?.pose() ?: return

        // Transform the center point
        val transformedCenter = Vector4f(center.x.toFloat(), center.y.toFloat(), center.z.toFloat(), 1.0f)
        transformMatrix.transform(transformedCenter)
        center = Vec3(transformedCenter.x().toDouble(), transformedCenter.y().toDouble(), transformedCenter.z().toDouble())
    }

    fun toAABB(): AABB {
        val halfWidth = width / 2.0
        val halfHeight = height / 2.0
        val halfDepth = depth / 2.0
        return AABB(
            center.x - halfWidth, center.y - halfHeight, center.z - halfDepth,
            center.x + halfWidth, center.y + halfHeight, center.z + halfDepth
        )
    }

    fun add(vec: Vec3): OrientedBoundingBox {
        return OrientedBoundingBox(center.add(vec), width, height, depth)
    }

    fun intersects(rayOrigin: Vec3, rayDirection: Vec3): Boolean {
        val min = Vec3(center.x - width / 2, center.y - height / 2, center.z - depth / 2)
        val max = Vec3(center.x + width / 2, center.y + height / 2, center.z + depth / 2)
        val invDir = Vec3(1.0 / rayDirection.x, 1.0 / rayDirection.y, 1.0 / rayDirection.z)

        val t1 = (min.x - rayOrigin.x) * invDir.x
        val t2 = (max.x - rayOrigin.x) * invDir.x
        val t3 = (min.y - rayOrigin.y) * invDir.y
        val t4 = (max.y - rayOrigin.y) * invDir.y
        val t5 = (min.z - rayOrigin.z) * invDir.z
        val t6 = (max.z - rayOrigin.z) * invDir.z

        val tmin = maxOf(maxOf(minOf(t1, t2), minOf(t3, t4)), minOf(t5, t6))
        val tmax = minOf(minOf(maxOf(t1, t2), maxOf(t3, t4)), maxOf(t5, t6))

        if (tmax < 0) {
            return false // AABB is behind the ray
        }

        if (tmin > tmax) {
            return false // No intersection
        }

        return true
    }

    fun min(): Vec3 {
        return Vec3(center.x - width / 2, center.y - height / 2, center.z - depth / 2)
    }

    fun max(): Vec3 {
        return Vec3(center.x + width / 2, center.y + height / 2, center.z + depth / 2)
    }

    // Reset to the original state (useful for rendering or updates)
    fun reset() {
        this.center = Vec3(originalCenter.toVector3f())
        this.pose = null
    }
}