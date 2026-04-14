package com.reachfly;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudOverlay {

    public static void render(GuiGraphicsExtractor ctx, DeltaTracker tickCounter) {
        // HUD rendering requires GuiGraphicsExtractor text methods which differ from
        // both Mojmap (drawString) and Yarn (drawTextWithShadow). The fill() method
        // works but text rendering needs the actual MC 26.1 client API which can only
        // be determined with access to the unobfuscated client jar.
        // TODO: Determine correct text rendering method on GuiGraphicsExtractor
    }
}
