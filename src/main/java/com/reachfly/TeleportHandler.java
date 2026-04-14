package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class TeleportHandler {

    private static boolean pendingTeleport = false;
    private static int cooldownTicks = 0;
    private static long lastTeleportTime = 0;

    public static void triggerTeleport() {
        long now = System.currentTimeMillis();
        if (now - lastTeleportTime < 2000) return;
        pendingTeleport = true;
    }

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        if (cooldownTicks > 0) { cooldownTicks--; return; }
        if (!pendingTeleport) return;
        pendingTeleport = false;
        cooldownTicks = 60;
        lastTeleportTime = System.currentTimeMillis();
        double tx = ModConfig.tpX, ty = ModConfig.tpY, tz = ModConfig.tpZ;
        LocalPlayer player = client.player;

        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) {
            ServerPlayer sp = server.getPlayerList().getPlayer(player.getUUID());
            if (sp != null) {
                sp.teleportTo(tx, ty, tz);
                player.sendOverlayMessage(Component.literal("\u00a7a[TP] Teleported to " + String.format("%.0f, %.0f, %.0f", tx, ty, tz)));
                return;
            }
        }
        if (ModConfig.tpUseServerAddon) { normalTeleport(client, player, tx, ty, tz); }
        else { betaTeleport(client, player, tx, ty, tz); }
    }

    private static void normalTeleport(Minecraft client, LocalPlayer player, double tx, double ty, double tz) {
        if (ClientPlayNetworking.canSend(TeleportPayload.ID)) {
            ClientPlayNetworking.send(new TeleportPayload(tx, ty, tz));
            player.sendOverlayMessage(Component.literal("\u00a7a[TP] Teleported via server addon: " + String.format("%.0f, %.0f, %.0f", tx, ty, tz)));
            return;
        }
        if (client.getConnection() != null) {
            client.getConnection().sendCommand("trigger f1sch.tp_x set " + (int) tx);
            client.getConnection().sendCommand("trigger f1sch.tp_y set " + (int) ty);
            client.getConnection().sendCommand("trigger f1sch.tp_z set " + (int) tz);
            client.getConnection().sendCommand("trigger f1sch.tp set 1");
            player.sendOverlayMessage(Component.literal("\u00a7a[TP] Teleporting to " + String.format("%.0f, %.0f, %.0f", tx, ty, tz)));
        }
    }

    private static void betaTeleport(Minecraft client, LocalPlayer player, double tx, double ty, double tz) {
        if (client.getConnection() == null) return;
        player.setPos(tx, ty, tz); player.fallDistance = 0.0f; player.setDeltaMovement(0, 0, 0);
        for (int i = 0; i < 5; i++) {
            client.getConnection().send(new ServerboundMovePlayerPacket.PosRot(tx, ty, tz, player.getYRot(), player.getXRot(), true, false));
        }
        player.sendOverlayMessage(Component.literal("\u00a7a[TP BETA] Teleported to " + String.format("%.0f, %.0f, %.0f", tx, ty, tz)));
    }

    public static void registerPayload() {
        net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry.serverboundPlay().register(TeleportPayload.ID, TeleportPayload.CODEC);
    }
}
