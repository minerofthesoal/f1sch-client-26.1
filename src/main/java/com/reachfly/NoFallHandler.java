package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public class NoFallHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.noFallEnabled) return;
        if (client.player == null || client.level == null || client.getConnection() == null) return;
        LocalPlayer player = client.player;
        player.fallDistance = 0.0f;
        if (!player.onGround()) {
            client.getConnection().send(new ServerboundMovePlayerPacket.PosRot(
                    player.getX(), player.getY(), player.getZ(),
                    player.getYRot(), player.getXRot(), true, player.horizontalCollision, false));
        }
        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) {
            ServerPlayer sp = server.getPlayerList().getPlayer(player.getUUID());
            if (sp != null) sp.fallDistance = 0.0f;
        }
    }
}
