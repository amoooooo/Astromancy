package aster.amo.astromancy.space.classification.matter.glyph

import java.awt.Color

enum class Glyphs(private val symbol: String, private val color: Color) {
    EMPTY("", Color(0, 0, 0, 0)),
    CONJUNCTION("a", Color(120, 120, 120, 255)),  // Basic
    OPPOSITION("b", Color(127, 252, 124, 255)),  // Complete
    SQUARE("c", Color.YELLOW),  // Formed, but not complete
    SEXTILE("d", Color.MAGENTA),  // Versatile
    TRINE("e", Color.CYAN),  // Tool
    SEMISEXTILE("f", Color.BLUE),  // Half, split
    QUINCUNX("g", Color.PINK),  // Complex
    QUINTILE("h", Color.GREEN),  // Compress, condense
    OCTILE("i", Color.RED),  // Combine
    TRIOCTILE("j", Color.ORANGE),  // Power
    DECILE("k", Color(36, 16, 35, 255)),  // Value
    SOL("l", Color(107, 5, 4, 255)),  // Lumen
    LUNA("m", Color(163, 50, 11, 255)),  // Lumen alteration
    MERCURIA("n", Color(213, 230, 141, 255)),  // Metal
    VENUTIO("o", Color(71, 160, 37, 255)),  // Fire
    MARTUS("p", Color(35, 32, 32, 255)),  // Combat
    JUPILUS("q", Color(85, 55, 57, 255)),  // Air
    SATURIS("r", Color(149, 94, 66, 255)),  // Rock, natural
    URANIA("s", Color(156, 145, 97, 255)),  // Mechanical, magical
    NEPTURA("t", Color(116, 142, 84, 255)),  // Water, fluid
    PLUTUS("u", Color(22, 48, 43, 255)),  // Power source
    CHIROS("v", Color(105, 72, 115, 255)),  // Healing, pain
    LILITHIA("w", Color(0x85B79D)); // Life, death

    fun symbol(): String {
        return symbol
    }

    fun color(): Color {
        return color
    }

    companion object {
        fun get(type: Int): Glyphs {
            return entries[type]
        }
    }
}
