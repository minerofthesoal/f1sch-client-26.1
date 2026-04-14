package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class FlyHandler {

    private static boolean wasFlying = false;

    public static void tick(Minecraft client) {
        LocalPlayer player = client.player;
        if (player == null) return;
        if (player.getAbilities().instabuild) return;

        if (ModConfig.flyEnabled) {
            wasFlying = true;
            player.getAbilities().mayfly = true;
            player.getAbilities().setFlyingSpeed(0.05f * ModConfig.flySpeed);
            if (player.getAbilities().flying) player.fallDistance = 0.0f;
        } else {
            if (!player.isSpectator()) {
                if (wasFlying) {
                    wasFlying = false;
                    String msg = player.onGround()
                            ? "\u00a7c[f1sch] \u00a7aFly disabled. Safe on the ground."
                            : "\u00a7c[f1sch] \u00a7eFly disabled! You are falling!";
                    player.sendOverlayMessage(net.minecraft.network.chat.Component.literal(msg));
                }
                player.getAbilities().mayfly = false;
                player.getAbilities().flying = false;
            }
        }
    }
}
