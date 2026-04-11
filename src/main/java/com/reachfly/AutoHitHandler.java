package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import java.util.ArrayList;
import java.util.List;

public class AutoHitHandler {

    private static long lastAuraPlusAttack = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoHitEnabled) return;
        if (client.player == null || client.level == null || client.screen != null || client.gameMode == null) return;
        LocalPlayer player = client.player;

        if (ModConfig.killAuraPlusEnabled && ModConfig.killAuraEnabled) { tickKillAuraPlus(client, player); return; }
        if (player.getAttackStrengthScale(0.0f) < 1.0f) return;

        if (ModConfig.killAuraEnabled) {
            List<Entity> targets = new ArrayList<>();
            for (Entity e : client.level.entitiesForRendering()) {
                if (e == player || !(e instanceof LivingEntity l) || !l.isAlive()) continue;
                if (ModConfig.autoHitPlayersOnly && !(e instanceof Player)) continue;
                if (player.distanceTo(e) <= ModConfig.autoHitRange) targets.add(e);
            }
            boolean first = true;
            for (Entity t : targets) { client.gameMode.attack(player, t); if (first) { player.swing(InteractionHand.MAIN_HAND); first = false; } }
            return;
        }

        Entity nearest = null; double nearestDist = ModConfig.autoHitRange;
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == player || !(e instanceof LivingEntity l) || !l.isAlive()) continue;
            if (ModConfig.autoHitPlayersOnly && !(e instanceof Player)) continue;
            double dist = player.distanceTo(e);
            if (dist < nearestDist) { nearestDist = dist; nearest = e; }
        }
        if (nearest != null) { client.gameMode.attack(player, nearest); player.swing(InteractionHand.MAIN_HAND); }
    }

    private static void tickKillAuraPlus(Minecraft client, LocalPlayer player) {
        long now = System.currentTimeMillis();
        long interval = 1000L / ModConfig.killAuraPlusCps;
        if (now - lastAuraPlusAttack < interval) return;
        lastAuraPlusAttack = now;
        player.resetAttackStrengthTicker();
        List<Entity> targets = new ArrayList<>();
        for (Entity e : client.level.entitiesForRendering()) {
            if (e == player || !(e instanceof LivingEntity l) || !l.isAlive()) continue;
            if (ModConfig.autoHitPlayersOnly && !(e instanceof Player)) continue;
            if (player.distanceTo(e) <= ModConfig.autoHitRange) targets.add(e);
        }
        boolean first = true;
        for (Entity t : targets) { player.resetAttackStrengthTicker(); client.gameMode.attack(player, t); if (first) { player.swing(InteractionHand.MAIN_HAND); first = false; } }
    }
}
