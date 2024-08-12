package aster.amo.astromancy.space.classification.stellarobjects

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import org.joml.Vector3d
import java.awt.Color
import java.util.*

class Moon(
    name: String,
    size: Double,
    mass: Float,
    axisTilt: Double,
    position: Vector3d
) : CelestialBody(name, size, mass, axisTilt, position) {
    var surfaceColor: Color = Color.GRAY

    constructor(name: String) : this(name, 1.0, 1.0f, 0.0, Vector3d(0.0,0.0,0.0))
    constructor(name: String, uuid: UUID, size: Int) : this(name, size.toDouble(), 1.0f, 0.0, Vector3d(0.0,0.0,0.0))

    override fun toNbt(): CompoundTag {
        val nbt = CompoundTag()
        nbt.putString("type", "moon")
        nbt.putString("name", name)
        nbt.putUUID("uuid", uuid)
        nbt.putInt("surfaceColor", surfaceColor.rgb)
        nbt.putFloat("mass", mass)
        nbt.putDouble("axisTilt", axisTilt)
        nbt.putDouble("x", position.x)
        nbt.putDouble("y", position.y)
        nbt.putDouble("z", position.z)
        return nbt
    }


    override fun update(deltaTime: Float) {
    }


    companion object {
        fun fromNbt(tag: CompoundTag): Moon {
            val name = tag.getString("name")
            val uuid = tag.getUUID("uuid")
            val size = tag.getDouble("size")
            val mass = tag.getFloat("mass")
            val axisTilt = tag.getDouble("axisTilt")
            val x = tag.getDouble("x")
            val y = tag.getDouble("y")
            val z = tag.getDouble("z")
            val position = Vector3d(x, y, z)
            val surfaceColor = Color(tag.getInt("surfaceColor"))
            return Moon(name, size, mass, axisTilt, position).apply {
                this.uuid = uuid
                this.surfaceColor = surfaceColor
            }
        }

        fun generateMoon(): Moon {
            val random = RandomSource.createNewThreadLocalInstance()
            val name = generateMoonName()
            val size = 0.5 + random.nextDouble() * 0.5
            val mass = 0.1f + random.nextFloat() * 0.9f
            val axisTilt = random.nextDouble() * 360.0
            val position = Vector3d(random.nextDouble() * 1000, random.nextDouble() * 1000, random.nextDouble() * 1000)
            return Moon(name, size, mass, axisTilt, position)
        }

        fun generateMoonName(): String {
            val moonNames = listOf(
                "Whisperwind", "Silverveil", "Dragonspire", "Shadowmoon", "Emberglow", "Wyrmrest",
                "Elvenlight", "Sunfall", "Nightsong", "Whisperingwood", "Everdawn", "Gloomhaven",
                "Skyfire", "Stormbreaker", "Frostfell", "Grimfang", "Skulltaker", "Bloodtear",
                "Soulrest", "Whisperfall", "Axiom", "Zenith", "Nexus Prime", "Outpost 42",
                "Binary Star", "Nova Core", "Quantum Leap", "Cybertron", "Cygnus Alpha", "Epsilon Eridani",
                "Kepler-186f", "Proxima b", "Trappist-1e", "Leviathan", "Ragnarok", "Icarus' Folly",
                "Elysium", "Avalon", "New Eden", "Sanctuary", "Luna", "Phobos", "Deimos", "Io",
                "Europa", "Ganymede", "Callisto", "Titan", "Triton", "Charon", "Rhea", "Iapetus",
                "Dione", "Tethys", "Enceladus", "Miranda", "Ariel", "Umbriel", "Oberon", "Titania",
                "New Luna", "Outer Phobos", "Deimos Prime", "Io's Shadow", "Europa's Ocean",
                "Ganymede's Eye", "Callisto's Scar", "Titan's Veil", "Triton's Fury", "Charon's Gate",
                "Azure", "Crimson", "Emerald", "Obsidian", "Diamond", "Silent", "Wanderer", "Sentinel",
                "Cradle", "Sanctuary", "Whispering", "Shrouded", "Forgotten", "Glimmering", "Broken",
                "Crimson Tide", "Azure Expanse", "Emerald Dream", "Obsidian Heart", "Diamond Dust",
                "Silver Whisper", "Azure Fire", "Crimson Tears", "Frozen Whispers", "Shattered Haven",
                "Silent Watch", "Eternal Spring", "Forgotten Hope", "Whispering Tides", "Crimson Dawn"
            )

            return moonNames.random()
        }
    }
}