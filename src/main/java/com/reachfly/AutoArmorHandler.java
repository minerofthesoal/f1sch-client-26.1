package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.core.registries.BuiltInRegistries;

public class AutoArmorHandler {

    private static int tickCounter = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.autoArmorEnabled) return;
        if (client.player == null || client.gameMode == null || client.screen != null) return;
        tickCounter++;
        if (tickCounter < 40) return;
        tickCounter = 0;
        LocalPlayer player = client.player;
        EquipmentSlot[] slots = {EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS, EquipmentSlot.FEET};
        int[] slotIndices = {5, 6, 7, 8};
        for (int s = 0; s < 4; s++) {
            ItemStack current = player.containerMenu.getSlot(slotIndices[s]).getItem();
            int currentTier = getArmorTier(current);
            EquipmentSlot targetSlot = slots[s];
            int bestInvSlot = -1; int bestTier = currentTier;
            for (int i = 9; i < 45; i++) {
                ItemStack stack = player.containerMenu.getSlot(i).getItem();
                Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
                if (equippable != null && equippable.slot() == targetSlot) {
                    int tier = getArmorTier(stack);
                    if (tier > bestTier) { bestTier = tier; bestInvSlot = i; }
                }
            }
            if (bestInvSlot >= 0) {
                client.gameMode.handleContainerInput(player.containerMenu.containerId, bestInvSlot, 0, ContainerInput.SLOT_QUICK_MOVE, player);
                return;
            }
        }
    }

    private static int getArmorTier(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
        if (equippable == null) return -1;
        String id = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        if (id.contains("netherite")) return 6;
        if (id.contains("diamond")) return 5;
        if (id.contains("iron")) return 4;
        if (id.contains("gold")) return 3;
        if (id.contains("chainmail")) return 2;
        if (id.contains("leather")) return 1;
        return 0;
    }
}
