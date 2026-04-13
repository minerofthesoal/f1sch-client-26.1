package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.Items;
import net.minecraft.world.inventory.ContainerInput;

public class AutoTotemHandler {

    private static int tickCounter = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoTotemEnabled) return;
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        tickCounter++;
        if (tickCounter < 20) return;
        tickCounter = 0;
        LocalPlayer player = client.player;
        if (player.getOffhandItem().is(Items.TOTEM_OF_UNDYING)) return;
        int syncId = player.containerMenu.containerId;
        for (int i = 9; i < 45; i++) {
            if (player.containerMenu.getSlot(i).getItem().is(Items.TOTEM_OF_UNDYING)) {
                client.gameMode.handleContainerInput(syncId, i, 0, ContainerInput.SLOT_PICKUP, player);
                client.gameMode.handleContainerInput(syncId, 45, 0, ContainerInput.SLOT_PICKUP, player);
                if (!player.containerMenu.getCarried().isEmpty()) {
                    client.gameMode.handleContainerInput(syncId, i, 0, ContainerInput.SLOT_PICKUP, player);
                }
                return;
            }
        }
    }
}
