package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.InteractionHand;

public class AutoKillWhenLowHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.autoKillWhenLowEnabled) return;
        if (client.player == null || client.level == null || client.screen != null) return;
        LocalPlayer player = client.player;
        if (player.getHealth() > ModConfig.autoKillSelfHpThreshold) return;
        if (player.getAttackStrengthScale(0.0f) < 1.0f) return;
        double range = ModConfig.autoKillWhenLowRange;
        LivingEntity nearest = null; double nearestDist = Double.MAX_VALUE;
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == player || !(e instanceof LivingEntity l) || !l.isAlive()) continue;
            double dist = player.distanceTo(e);
            if (dist > range) continue;
            if (dist < nearestDist) { nearestDist = dist; nearest = l; }
        }
        if (nearest != null) { client.gameMode.attack(player, nearest); player.swing(InteractionHand.MAIN_HAND); }
    }
}
