package aster.amo.astromancy.events;

import aster.amo.astromancy.util.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ComputeFovModifierEvent;
import net.neoforged.neoforge.client.event.ViewportEvent;

//@EventBusSubscriber(modid = "astromancy", bus = EventBusSubscriber.Bus.GAME, value = Dist.CLIENT)
public class ClientForgeEvents {
//    static int fov = 0;
//    @SubscribeEvent
//    public static void setupFov(ViewportEvent.ComputeFov event) {
//        if(fov == 0 && Minecraft.getInstance().options.fov().get() != 10) {
//            fov = Minecraft.getInstance().options.fov().get();
//        }
//        if(RenderHelper.INSTANCE.getNullStarLookPower() > 0) {
//            event.setFOV(Mth.lerp(RenderHelper.INSTANCE.getNullStarLookTime(), event.getFOV(), 10));
//        } else {
//            event.setFOV(Mth.lerp(RenderHelper.INSTANCE.getNullStarLookTime(), 10, fov));
//        }
//    }
}
