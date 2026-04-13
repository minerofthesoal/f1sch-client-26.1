package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerInput;

public class AutoElytraSwapHandler {

    private static int swapCooldown = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoElytraSwapEnabled) return;
        if (client.player == null || client.level == null || client.screen != null || client.gameMode == null) return;
        LocalPlayer player = client.player;
        if (swapCooldown > 0) { swapCooldown--; return; }
        ItemStack chestSlot = player.getItemBySlot(EquipmentSlot.CHEST);
        boolean hasElytra = chestSlot.is(Items.ELYTRA);
        if (!player.onGround() && player.fallDistance > 0.5f && !hasElytra) {
            int elytraSlot = findItem(player, Items.ELYTRA);
            if (elytraSlot != -1) { swapToChestSlot(client, player, elytraSlot); swapCooldown = 5; }
        } else if (player.onGround() && hasElytra) {
            int chestplateSlot = findChestplate(player);
            if (chestplateSlot != -1) { swapToChestSlot(client, player, chestplateSlot); swapCooldown = 5; }
        }
    }

    private static int findItem(LocalPlayer player, Item item) {
        for (int i = 0; i < 36; i++) { if (player.getInventory().getItem(i).is(item)) return i; }
        return -1;
    }

    private static int findChestplate(LocalPlayer player) {
        for (int i = 0; i < 36; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.is(Items.NETHERITE_CHESTPLATE) || stack.is(Items.DIAMOND_CHESTPLATE)
                    || stack.is(Items.IRON_CHESTPLATE) || stack.is(Items.GOLDEN_CHESTPLATE)
                    || stack.is(Items.CHAINMAIL_CHESTPLATE) || stack.is(Items.LEATHER_CHESTPLATE)) return i;
        }
        return -1;
    }

    private static void swapToChestSlot(Minecraft client, LocalPlayer player, int inventorySlot) {
        int screenSlot = inventorySlot < 9 ? inventorySlot + 36 : inventorySlot;
        int syncId = player.containerMenu.containerId;
        client.gameMode.handleInventoryMouseClick(syncId, screenSlot, 0, ContainerInput.PICKUP, player);
        client.gameMode.handleInventoryMouseClick(syncId, 6, 0, ContainerInput.PICKUP, player);
        client.gameMode.handleInventoryMouseClick(syncId, screenSlot, 0, ContainerInput.PICKUP, player);
    }
}
