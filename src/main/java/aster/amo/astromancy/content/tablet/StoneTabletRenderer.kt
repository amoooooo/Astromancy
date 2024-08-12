package coffee.amo.astromancy.client.renderer.item

import aster.amo.astromancy.Astromancy
import aster.amo.astromancy.content.astrolabe.AstrolabeRenderer
import aster.amo.astromancy.content.tablet.StoneTabletItem
import aster.amo.astromancy.util.MathHelper
import aster.amo.astromancy.util.TextUtil
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import com.mojang.math.Axis
import net.minecraft.client.Minecraft
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.player.AbstractClientPlayer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.entity.player.PlayerRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.resources.ResourceLocation
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.HumanoidArm
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.Vec3
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RenderHandEvent
import java.awt.Color
import java.util.List
import java.util.Map
import java.util.function.Function

object StoneTabletRenderer {
    val bookModel: StoneTabletModel = StoneTabletModel()
    val bookTexture: ResourceLocation = Astromancy.astromancy("textures/item/stone_tablet_hand.png")
    private val mc: Minecraft = Minecraft.getInstance()

    @SubscribeEvent
    fun onRenderHand(event: RenderHandEvent) {
        if (event.getItemStack().getItem() is StoneTabletItem) {
            event.setCanceled(true)
            val stack: PoseStack = event.getPoseStack()
            val xRot: Float =
                event.getInterpolatedPitch() //Minecraft.getInstance().cameraEntity.getViewXRot(event.getPartialTick());
            val yVRot = Minecraft.getInstance().cameraEntity!!.getViewYRot(event.getPartialTick())
            var yRot = yVRot
            if (Minecraft.getInstance().cameraEntity is LivingEntity) {
                yRot = Mth.lerp(
                    event.partialTick,
                    (Minecraft.getInstance().cameraEntity as LivingEntity?)!!.yBodyRotO,
                    (Minecraft.getInstance().cameraEntity as LivingEntity?)!!.yBodyRot
                )
            }
            val scale = 0.5f
            val distance = 2.0
            val lookAtFactor = Mth.clamp(MathHelper.remap(xRot, 0.0f, 45f, 0.2f, 1f), 0f, 1f).toFloat()

            var position = Vec3.directionFromRotation(xRot, yVRot)
            position = position.multiply(0.0, 1.0, 0.0).add(0.0, 0.0, 1.0).normalize().scale(distance)

            stack.pushPose()
            stack.mulPose(Axis.YP.rotationDegrees(yVRot))
            stack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(0.9f, -yRot, -yVRot)))

            stack.mulPose(Axis.YP.rotationDegrees(180.0f))
            stack.mulPose(Axis.ZP.rotationDegrees(180.0f))
            stack.scale(scale, scale, scale)
            stack.translate(0.0, 1.8 * event.getEquipProgress(), 0.0)

            stack.translate(
                Mth.lerp(lookAtFactor.toDouble(), position.x(), 0.0),
                Mth.lerp(lookAtFactor.toDouble(), position.y() + 2, 0.0),
                Mth.lerp(lookAtFactor.toDouble(), position.z(), distance)
            )

            stack.mulPose(Axis.YP.rotationDegrees(180f))
            stack.mulPose(Axis.XP.rotationDegrees(90f))

            stack.mulPose(Axis.XP.rotationDegrees(Mth.lerp(lookAtFactor, -xRot, -90f)))
            stack.scale(0.45f, 0.45f, 0.45f)

            //Renders the player's hands, some specific stuff to handle cases with the offhand.
            if (!mc.player!!.isInvisible) {
                stack.pushPose()
                val mainHandSide = mc.options.mainHand().get()
                val mainHandItem = mc.player!!.getItemInHand(InteractionHand.MAIN_HAND)
                val offHandItem = mc.player!!.getItemInHand(InteractionHand.OFF_HAND)
                val rightArmItem = if (mainHandSide == HumanoidArm.RIGHT) mainHandItem else offHandItem
                val leftArmItem = if (mainHandSide == HumanoidArm.RIGHT) offHandItem else mainHandItem

                if ((rightArmItem == ItemStack.EMPTY && mainHandSide != HumanoidArm.RIGHT) || rightArmItem.item is StoneTabletItem) {
                    renderHand(stack, event.getMultiBufferSource(), event.getPackedLight(), HumanoidArm.RIGHT)
                }
                if ((leftArmItem == ItemStack.EMPTY && mainHandSide != HumanoidArm.LEFT) || leftArmItem.item is StoneTabletItem) {
                    renderHand(stack, event.getMultiBufferSource(), event.getPackedLight(), HumanoidArm.LEFT)
                }

                stack.popPose()
            }
            stack.mulPose(
                Axis.XP.rotationDegrees(
                    40 * (MathHelper.ease(
                        event.getEquipProgress(),
                        MathHelper.Easing.easeInOutBack
                    ))
                )
            )


            bookModel.tablet.yRot = -(Math.PI / 2).toFloat()
            bookModel.tablet.x = 24.5f
            bookModel.renderToBuffer(
                stack, event.getMultiBufferSource().getBuffer(
                    RenderType.entitySolid(
                        bookTexture
                    )
                ), event.getPackedLight(), OverlayTexture.NO_OVERLAY, 0xFFFFFFFF.toInt()
            )
            stack.translate(1f, 0f, 0f)

            stack.pushPose()
            stack.mulPose(Axis.YP.rotationDegrees(90f))
            stack.translate(-0.85f, 0.1575f, 0.4f)
            stack.scale(0.005f, 0.005f, 0.005f)
            val height = Minecraft.getInstance().font.lineHeight / 10.3f
            stack.scale(height, height, height)
            TextUtil.renderWrappingText(
                stack,
                "wa, la wu bovo rexu, ke xi wevy hywapu ge quwu wuji gakede fisy dy? du quma bydi gunywo hy tido vuly tozy? kidi su! lady lu fi dihi cojyqu, sevyme xa, ho sy jyka, kyge tosali lyza du gy tipy wokydo ryfu quhi pe! nesi xa jy ga dikipe se! roqu ri? lemo kawa li wy sapusy vu tu wybi zaca qu ly japu zacu fu jilowe",
                0,
                0,
                107,
                true,
                Color("e6b029".toInt(16))
            )
            stack.popPose()
            stack.popPose()
        }
    }

    fun renderHand(stack: PoseStack, buffer: MultiBufferSource?, light: Int, side: HumanoidArm) {
        RenderSystem.setShaderTexture(0, mc.player?.skin?.texture)
        val playerrenderer = mc.entityRenderDispatcher.getRenderer<AbstractClientPlayer?>(mc.player) as PlayerRenderer
        stack.pushPose()

        val sideSign = if (side == HumanoidArm.RIGHT) 1.0f else -1.0f
        val scale = 5.2f
        stack.scale(scale, scale, scale)
        stack.translate(0.0, 1.1, 0.6)
        //stack.mulPose(Axis.YP.rotationDegrees(180.0F));
        stack.mulPose(Axis.XP.rotationDegrees(45.0f))
        stack.mulPose(Axis.XP.rotationDegrees(180.0f))
        stack.mulPose(Axis.ZP.rotationDegrees(sideSign * -30.0f))
        stack.translate(sideSign * -0.25f, -0.05f, 0f)

        if (side == HumanoidArm.RIGHT) {
            playerrenderer.renderRightHand(stack, buffer, light, mc.player)
        } else {
            playerrenderer.renderLeftHand(stack, buffer, light, mc.player)
        }

        stack.popPose()
    }

    class StoneTabletModel :
            Model(Function<ResourceLocation, RenderType> { location: ResourceLocation? ->
                RenderType.entitySolid(
                    location
                )
            }) {
        val tablet: ModelPart

        init {
            val cubes = List.of(
                ModelPart.Cube(0, 0, 4f, 0f, 0f, 12f, 16f, 1f, 0f, 0f, 0f, false, 32f, 32f, AstrolabeRenderer.ALL_VISIBLE)
            )
            this.tablet = ModelPart(cubes, Map.of())
        }

        override fun renderToBuffer(
            poseStack: PoseStack,
            buffer: VertexConsumer,
            packedLight: Int,
            packedOverlay: Int,
            color: Int
        ) {
            poseStack.mulPose(Axis.YP.rotationDegrees(90f))
            poseStack.translate(-25f, -25f, -31f)
            poseStack.scale(50f, 50f, 50f)
            tablet.render(poseStack, buffer, packedLight, packedOverlay)
        }
    }
}