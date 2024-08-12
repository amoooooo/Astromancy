package aster.amo.astromancy.space.classification.stellarobjects

import net.minecraft.nbt.CompoundTag
import net.minecraft.util.RandomSource
import org.joml.Vector3d
import java.awt.Color
import java.util.*

class Meteorite(
    name: String,
    size: Double,
    mass: Float,
    axisTilt: Double,
    position: Vector3d, // Starting position, can be randomized or part of the orbit
    val orbitCenter: Vector3d,
    val semiMajorAxis: Double,
    val semiMinorAxis: Double,
    val orbitalInclination: Double,
    val orbitalRotation: Double,
    val orbitalPeriod: Double // Time for one full orbit (in arbitrary units, e.g., seconds)
) : CelestialBody(name, size, mass, axisTilt, position) {
    var surfaceColor: Color = Color.GRAY
    private var orbitalProgress: Double = 0.0 // Internal progress tracker (0.0 to 1.0)

    // Constructors for easier initialization
    constructor(name: String) : this(
        name,
        1.0, 1.0f, 0.0,
        Vector3d(0.0, 0.0, 0.0),
        Vector3d(0.0, 0.0, 0.0), 10.0, 5.0, 30.0, 0.0, 120.0 // Example default values
    )

    constructor(name: String, uuid: UUID, size: Int) : this(
        name,
        size.toDouble(), 1.0f, 0.0,
        Vector3d(0.0, 0.0, 0.0),
        Vector3d(0.0, 0.0, 0.0), 10.0, 5.0, 30.0, 0.0, 120.0 // Example default values
    )

    override fun toNbt(): CompoundTag {
        val nbt = super.toNbt() // Use super to store common CelestialBody properties
        nbt.putString("type", "meteorite")
        nbt.putInt("surfaceColor", surfaceColor.rgb)

        // Store orbit parameters:
        nbt.putDouble("orbitCenterX", orbitCenter.x)
        nbt.putDouble("orbitCenterY", orbitCenter.y)
        nbt.putDouble("orbitCenterZ", orbitCenter.z)
        nbt.putDouble("semiMajorAxis", semiMajorAxis)
        nbt.putDouble("semiMinorAxis", semiMinorAxis)
        nbt.putDouble("orbitalInclination", orbitalInclination)
        nbt.putDouble("orbitalRotation", orbitalRotation)
        nbt.putDouble("orbitalPeriod", orbitalPeriod)

        return nbt
    }

    override fun update(deltaTime: Float) {
    }

    companion object {
        fun fromNbt(tag: CompoundTag): Meteorite {
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

            val orbitCenter = Vector3d(
                tag.getDouble("orbitCenterX"),
                tag.getDouble("orbitCenterY"),
                tag.getDouble("orbitCenterZ")
            )
            val semiMajorAxis = tag.getDouble("semiMajorAxis")
            val semiMinorAxis = tag.getDouble("semiMinorAxis")
            val orbitalInclination = tag.getDouble("orbitalInclination")
            val orbitalRotation = tag.getDouble("orbitalRotation")
            val orbitalPeriod = tag.getDouble("orbitalPeriod")

            return Meteorite(
                name,
                size,
                mass,
                axisTilt,
                position,
                orbitCenter,
                semiMajorAxis,
                semiMinorAxis,
                orbitalInclination,
                orbitalRotation,
                orbitalPeriod
            ).apply {
                this.uuid = uuid
                this.surfaceColor = surfaceColor
            }
        }
        
        fun generateMeteorite(): Meteorite {
            val random = RandomSource.createNewThreadLocalInstance()
            val name = generateMeteoriteName()
            val size = 0.5 + random.nextDouble() * 0.5
            val mass = 0.1f + random.nextFloat() * 0.9f
            val axisTilt = random.nextDouble() * 360.0
            val position = Vector3d(0.0,0.0,0.0)
            val orbitCenter = Vector3d(0.0,0.0,0.0)
            val semiMinorAxis = random.nextDouble()
            val semiMajorAxis = random.nextDouble() + semiMinorAxis
            val orbitalInclination = random.nextDouble() * 360.0
            val orbitalRotation = random.nextDouble() * 360.0
            val orbitalPeriod = 12000.0 + random.nextDouble() * 24000.0 // Example: orbit period between 2 and 6 minutes (in your time units)

            return Meteorite(
                name = name,
                size = size,
                mass = mass,
                axisTilt = axisTilt,
                position = position,
                orbitCenter = orbitCenter,
                semiMajorAxis = semiMajorAxis,
                semiMinorAxis = semiMinorAxis,
                orbitalInclination = orbitalInclination,
                orbitalRotation = orbitalRotation,
                orbitalPeriod = orbitalPeriod
            )
        }

        fun generateMeteoriteName(): String {

            val prefixes = listOf(
                "Burning", "Frozen", "Whispering", "Howling", "Screaming", "Glimmering", "Shattered", "Ancient", "Lost",
                "Celestial", "Cosmic", "Radiant", "Ethereal", "Spectral", "Crimson", "Azure", "Emerald", "Golden", "Silver",
                "Iron", "Nickel", "Cobalt", "Diamond", "Ruby", "Sapphire", "Onyx", "Quartz", "Obsidian", "Cometary",
                "Asteroidan", "Meteoric", "Stellar", "Lunar", "Solar", "Martian", "Venusian", "Jovian", "Saturnine",
                "Uranian", "Neptunian", "Plutonian"
            )

            val suffixes = listOf(
                "Shard", "Fragment", "Comet", "Meteor", "Asteroid", "Stone", "Rock", "Crystal", "Ember", "Tear",
                "Whisper", "Echo", "Remnant", "Dust", "Debris", "Heart", "Soul", "Spirit", "Dream", "Nightmare",
                "Hope", "Despair", "Fury", "Rage", "Peace", "Tranquility", "Chaos", "Order", "Light", "Darkness",
                "Void", "Starfire", "Stardust", "Skyfire", "Moonstone", "Sunstone", "Iron Rain", "Diamond Shard",
                "Cosmic Dust", "Celestial Fire", "Spectral Whisper", "Ethereal Echo", "Radiant Ember",
                "Ancient Artifact", "Lost Relic", "Shattered Memory", "Whispering Wind", "Howling Gale",
                "Screaming Silence", "Glimmering Hope", "Frozen Tear"
            )

            val prefix = prefixes.random()
            val suffix = suffixes.random()

            return "$prefix $suffix"
        }
    }
}