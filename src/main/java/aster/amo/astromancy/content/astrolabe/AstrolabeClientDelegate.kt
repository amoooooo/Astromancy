package aster.amo.astromancy.content.astrolabe

import aster.amo.astromancy.space.classification.stellarobjects.CelestialBody
import aster.amo.astromancy.space.classification.stellarobjects.Planet
import aster.amo.astromancy.util.OrientedBoundingBox
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.phys.Vec3
import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL31

class AstrolabeClientDelegate : AstrolabeDelegate {
    override fun tick(astrolabe: AstrolabeBlockEntity) {

    }

    val OBBList: MutableMap<Planet, OrientedBoundingBox> = mutableMapOf()
    override fun onInitialized(astrolabe: AstrolabeBlockEntity) {
        // create OBBs for each celestial body
        astrolabe.starSystem?.celestialBodies?.forEach { planet ->
            if(planet is Planet) {
                OBBList[planet] = OrientedBoundingBox(Vec3(0.0, 0.0, 0.0), 1.0, 1.0, 1.0)
            }
        }
    }
}