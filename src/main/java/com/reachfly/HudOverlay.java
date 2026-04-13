package com.reachfly;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.gui.GuiGraphicsExtractor;

public class HudOverlay {

    public static void render(GuiGraphicsExtractor ctx, DeltaTracker tickCounter) {
        // TODO: port rendering to MC 26.1 GuiGraphicsExtractor API
        // GuiGraphicsExtractor does not have drawString/drawCenteredString/fill methods.
        // Once the correct rendering API is determined, restore HUD rendering here.
    }
}
