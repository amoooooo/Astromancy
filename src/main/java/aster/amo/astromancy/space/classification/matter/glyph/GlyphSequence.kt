package aster.amo.astromancy.space.classification.matter.glyph

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.StringTag

class GlyphSequence(
    val sequence: List<Glyph>
){
    fun toNbt(): CompoundTag {
        val tag = CompoundTag()
        val listTag: ListTag = ListTag()
        sequence.forEach {
            listTag.add(StringTag.valueOf(it.name))
        }
        tag.put("sequence", listTag)
        return tag
    }

    companion object {
        fun fromNbt(tag: CompoundTag): GlyphSequence {
            val sequence = mutableListOf<Glyph>()
            val sequenceTag = tag.getList("sequence", 8)
            sequenceTag.forEach {
                sequence.add(Glyph.valueOf(it.asString))
            }
            return GlyphSequence(sequence)
        }

        fun generateGlyphSequence(length: Int): GlyphSequence {
            val sequence = mutableListOf<Glyph>()
            for (i in 0 until length) {
                sequence.add(Glyph.entries.toMutableList().filter { it != Glyph.EMPTY }.toTypedArray().random())
            }
            return GlyphSequence(sequence)
        }
    }
}