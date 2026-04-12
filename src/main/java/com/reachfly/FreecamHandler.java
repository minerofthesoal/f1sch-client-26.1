package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class FreecamHandler {

    private static boolean active = false;
    private static double savedX, savedY, savedZ;
    private static float savedYaw, savedPitch;
    private static boolean wasFlying = false;

    public static boolean isActive() { return active && ModConfig.freecamEnabled && ModConfig.proUnlocked; }

    public static void tick(Minecraft client) {
        if (!ModConfig.proUnlocked || client.player == null) return;
        LocalPlayer p = client.player;
        if (ModConfig.freecamEnabled && !active) {
            active = true; savedX = p.getX(); savedY = p.getY(); savedZ = p.getZ();
            savedYaw = p.getYRot(); savedPitch = p.getXRot(); wasFlying = p.getAbilities().flying;
            p.noPhysics = true; p.getAbilities().mayfly = true; p.getAbilities().flying = true;
            p.getAbilities().setFlyingSpeed(0.1f); p.onUpdateAbilities();
            p.displayClientMessage(net.minecraft.network.chat.Component.literal("\u00a7b[Freecam] Enabled"), true);
        } else if (!ModConfig.freecamEnabled && active) {
            active = false; p.setPosition(savedX, savedY, savedZ);
            p.setYRot(savedYaw); p.setXRot(savedPitch); p.noPhysics = false; p.fallDistance = 0;
            if (!p.isCreative() && !p.isSpectator()) {
                p.getAbilities().mayfly = ModConfig.flyEnabled;
                p.getAbilities().flying = wasFlying && ModConfig.flyEnabled;
                p.getAbilities().setFlyingSpeed(ModConfig.flyEnabled ? 0.05f * ModConfig.flySpeed : 0.05f);
                p.onUpdateAbilities();
            }
            p.displayClientMessage(net.minecraft.network.chat.Component.literal("\u00a7b[Freecam] Disabled"), true);
        }
        if (active && ModConfig.freecamEnabled) { p.noPhysics = true; p.fallDistance = 0; if (!p.getAbilities().flying) { p.getAbilities().flying = true; p.onUpdateAbilities(); } }
    }

    public static Vec3 getSavedPosition() { return new Vec3(savedX, savedY, savedZ); }
}
