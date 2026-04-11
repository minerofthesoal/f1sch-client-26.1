package com.reachfly;

import net.minecraft.client.Minecraft;

public class FullbrightHandler {

    private static double originalGamma = -1;
    private static boolean wasEnabled = false;

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        boolean shouldBeActive = ModConfig.fullbrightEnabled || XrayHandler.isActive();
        if (shouldBeActive) {
            if (!wasEnabled) { originalGamma = client.options.gamma().get(); wasEnabled = true; }
            client.options.gamma().set(16.0);
        } else {
            if (wasEnabled) { client.options.gamma().set(originalGamma >= 0 ? originalGamma : 1.0); wasEnabled = false; }
        }
    }
}
