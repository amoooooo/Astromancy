package aster.amo.astromancy.content.astrolabe

import aster.amo.astromancy.util.OrientedBoundingBox
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31

class AstrolabeClientDelegate : AstrolabeDelegate {
    override fun tick(astrolabe: AstrolabeBlockEntity) {

    }

    val OBBList: MutableList<OrientedBoundingBox> = mutableListOf()
    override fun onInitialized(astrolabe: AstrolabeBlockEntity) {
        // create OBBs for each celestial body
        astrolabe.starSystem?.celestialBodies?.forEach { _ ->
            OBBList.add(OrientedBoundingBox(Vec3(0.0, 0.0, 0.0), Vec3(2.0, 2.0, 2.0)))
        }
    }
}