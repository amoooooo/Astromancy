package aster.amo.astromancy.util

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.renderer.LightTexture
import net.minecraft.network.chat.Component
import net.minecraft.util.FastColor.ARGB32
import net.minecraft.util.Mth
import java.awt.Color

object TextUtil {
    fun renderWrappingText(mStack: PoseStack, text: String, x: Int, y: Int, w: Int) {
        var text = text
        val font = Minecraft.getInstance().font
        text = Component.translatable(text).string
        val lines: MutableList<String> = ArrayList()
        val words = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var line = ""
        for (s in words) {
            if (font.width(line) + font.width(s) > w) {
                lines.add(line)
                line = "$s "
            } else line += "$s "
        }
        if (!line.isEmpty()) lines.add(line)
        for (i in lines.indices) {
            val currentLine = lines[i]
            renderRawText(mStack, currentLine, x, y + i * (font.lineHeight + 1), getTextGlow(i / 4f))
        }
    }

    fun renderWrappingText(mStack: PoseStack, text: String, x: Int, y: Int, w: Int, shadow: Boolean, color: Color) {
        var text = text
        val font = Minecraft.getInstance().font
        text = Component.translatable(text).string
        val lines: MutableList<String> = ArrayList()
        val words = text.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        var line = ""
        for (s in words) {
            if (font.width(line) + font.width(s) > w) {
                lines.add(line)
                line = "$s "
            } else line += "$s "
        }
        if (!line.isEmpty()) lines.add(line)
        for (i in lines.indices) {
            val currentLine = lines[i]
            renderRawText(mStack, currentLine, x, y + i * (font.lineHeight + 1), getTextGlow(i / 4f), shadow, color)
        }
    }

    fun renderText(stack: PoseStack, text: String?, x: Int, y: Int) {
        renderText(stack, Component.translatable(text), x, y, getTextGlow(0f))
    }

    fun renderText(stack: PoseStack, component: Component, x: Int, y: Int) {
        val text = component.string
        renderRawText(stack, text, x, y, getTextGlow(0f))
    }

    fun renderText(stack: PoseStack, text: String?, x: Int, y: Int, glow: Float) {
        renderText(stack, Component.translatable(text), x, y, glow)
    }

    fun renderText(stack: PoseStack, component: Component, x: Int, y: Int, glow: Float) {
        val text = component.string
        renderRawText(stack, text, x, y, glow)
    }

    private fun renderRawText(stack: PoseStack, text: String, x: Int, y: Int, glow: Float) {
        val font = Minecraft.getInstance().font
        //182, 61, 183  227, 39, 228
        val r = Mth.lerp(glow, 2f, 16f).toInt()
        val g = Mth.lerp(glow, 2f, 16f).toInt()
        val b = Mth.lerp(glow, 2f, 16f).toInt()

        font.drawInBatch(text, x.toFloat(), y.toFloat(), ARGB32.color(255, r, g, b), true, stack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0x00FFFFFF, LightTexture.FULL_BRIGHT)
    }

    private fun renderRawText(
        stack: PoseStack,
        text: String,
        x: Int,
        y: Int,
        glow: Float,
        shadow: Boolean,
        color: Color
    ) {
        val font = Minecraft.getInstance().font
        //182, 61, 183  227, 39, 228
        val r = Mth.lerp(glow, 2f, 16f).toInt()
        val g = Mth.lerp(glow, 2f, 16f).toInt()
        val b = Mth.lerp(glow, 2f, 16f).toInt()
        val shade = Color("333333".toInt(16))
        stack.translate(0f, 0f, -0.01f)

        font.drawInBatch(text, x.toFloat(), y.toFloat(), 0xFFFFFFFF.toInt(), false, stack.last().pose(), Minecraft.getInstance().renderBuffers().bufferSource(), Font.DisplayMode.NORMAL, 0x00FFFFFF, LightTexture.FULL_BRIGHT)
    }

    fun getTextGlow(offset: Float): Float {
        return Mth.sin(offset + Minecraft.getInstance().player!!.level().gameTime / 40f) / 2f + 0.5f
    }
}