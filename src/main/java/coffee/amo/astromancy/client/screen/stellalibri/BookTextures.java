package coffee.amo.astromancy.client.screen.stellalibri;

import coffee.amo.astromancy.Astromancy;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class BookTextures {
    public static final ResourceLocation TAB = Astromancy.astromancy("textures/gui/book/tab.png");
    public static final ResourceLocation OUTSIDE_LOC = Astromancy.astromancy("textures/gui/book/bg-scaled.png");
    public static final ResourceLocation BACKGROUND_TEXTURE = Astromancy.astromancy("textures/gui/book/bg.png");
    public static final ResourceLocation VERTICAL_LINE = Astromancy.astromancy("textures/gui/book/lines/vertical.png");
    public static final ResourceLocation HORIZONTAL_LINE = Astromancy.astromancy("textures/gui/book/lines/horizontal.png");
    public static final ResourceLocation LOCKED_VERTICAL_LINE = Astromancy.astromancy("textures/gui/book/lines/locked_vertical.png");
    public static final ResourceLocation LOCKED_HORIZONTAL_LINE = Astromancy.astromancy("textures/gui/book/lines/locked_horizontal.png");
    public static final ResourceLocation SPLINES = Astromancy.astromancy("textures/gui/book/lines/splines.png");
    public static final ResourceLocation LOCKED_SPLINES = Astromancy.astromancy("textures/gui/book/lines/locked_splines.png");
    public static final ResourceLocation LOCKED_ICONS = Astromancy.astromancy("textures/gui/book/locked_entries.png");
    public static final ResourceLocation LOCKED_CHAINS = Astromancy.astromancy("textures/gui/book/locked_chains.png");
    public static final ResourceLocation ENTRIES = Astromancy.astromancy("textures/gui/book/entries_icons.png");
    public static final ResourceLocation EXCLAMATION_MARK = Astromancy.astromancy("textures/gui/book/exclamation_mark.png");
    public static final ResourceLocation BLINK_FX = Astromancy.astromancy("textures/gui/book/fx/blink.png");

    public static final List<ResourceLocation> TAB1_PARALLAX = List.of(
            Astromancy.astromancy("textures/gui/book/test/parallax-space-background.png"),
            Astromancy.astromancy("textures/gui/book/test/parallax-space-stars.png"),
            Astromancy.astromancy("textures/gui/book/test/parallax-space-far-planets.png"),
            Astromancy.astromancy("textures/gui/book/test/parallax-space-ring-planet.png"),
            Astromancy.astromancy("textures/gui/book/test/parallax-space-big-planet.png")
    );

    public static final List<ResourceLocation> INTRO_BACKGROUND = List.of(
            Astromancy.astromancy("textures/gui/book/backgrounds/introduction/intro_bg.png"),
            Astromancy.astromancy("textures/gui/book/backgrounds/introduction/intro_nebula.png"),
            Astromancy.astromancy("textures/gui/book/backgrounds/introduction/intro_stars_top.png"),
            Astromancy.astromancy("textures/gui/book/backgrounds/introduction/intro_stars_btm.png")
    );
}
