package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class LowHealthKillHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.lowHealthKillEnabled) return;
        if (client.player == null || client.level == null || client.screen != null) return;
        LocalPlayer player = client.player;
        if (player.getAttackStrengthScale(0.0f) < 1.0f) return;
        double range = ModConfig.reachEnabled ? ModConfig.reachDistance : 4.5;
        LivingEntity weakest = null; float lowestHealth = Float.MAX_VALUE;
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == player || !(e instanceof LivingEntity l) || !l.isAlive()) continue;
            if (l.getHealth() > ModConfig.lowHealthThreshold) continue;
            double dist = player.distanceTo(e);
            if (dist > range) continue;
            if (l.getHealth() < lowestHealth) { lowestHealth = l.getHealth(); weakest = l; }
        }
        if (weakest != null) { client.gameMode.attack(player, weakest); player.swing(InteractionHand.MAIN_HAND); }
    }
}
