package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;

public class EatingAssistHandler {

    private static int previousSlot = -1;
    private static int eatTicks = 0;
    private static boolean isHoldingUse = false;

    public static void tick(Minecraft client) {
        if (!ModConfig.eatingAssistEnabled) return;
        if (client.player == null || client.level == null) return;
        if (client.screen != null) { reset(client); return; }
        if (client.gameMode == null) return;
        LocalPlayer player = client.player;
        int foodLevel = player.getFoodData().getFoodLevel();
        if (foodLevel >= ModConfig.eatingHungerThreshold) { if (previousSlot >= 0 || isHoldingUse) reset(client); return; }
        if (player.isUsingItem()) { KeyMapping.set(client.options.keyUse.getDefaultKey(), true); isHoldingUse = true; eatTicks++; if (eatTicks > 80) reset(client); return; }
        if (isHoldingUse) { KeyMapping.set(client.options.keyUse.getDefaultKey(), false); isHoldingUse = false; eatTicks = 0; if (player.getFoodData().getFoodLevel() >= ModConfig.eatingHungerThreshold) { reset(client); return; } }
        if (previousSlot >= 0) { ItemStack held = player.getMainHandItem(); if (!isFood(held)) { player.getInventory().selected = previousSlot; previousSlot = -1; } }
        int foodSlot = findBestFoodSlot(player);
        if (foodSlot == -1) return;
        if (previousSlot < 0) previousSlot = player.getInventory().selected;
        player.getInventory().selected = foodSlot;
        eatTicks = 0;
        client.gameMode.useItem(player, net.minecraft.world.InteractionHand.MAIN_HAND);
        KeyMapping.set(client.options.keyUse.getDefaultKey(), true);
        isHoldingUse = true;
    }

    private static void reset(Minecraft client) {
        if (isHoldingUse) { KeyMapping.set(client.options.keyUse.getDefaultKey(), false); isHoldingUse = false; }
        if (previousSlot >= 0 && client.player != null) client.player.getInventory().selected = previousSlot;
        previousSlot = -1; eatTicks = 0;
    }

    private static int findBestFoodSlot(LocalPlayer player) {
        int bestSlot = -1; int bestNutrition = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (!isFood(stack)) continue;
            var foodComp = stack.get(DataComponents.FOOD);
            int nutrition = (foodComp != null) ? foodComp.nutrition() : 1;
            if (nutrition > bestNutrition) { bestNutrition = nutrition; bestSlot = i; }
        }
        return bestSlot;
    }

    private static boolean isFood(ItemStack stack) { return !stack.isEmpty() && stack.has(DataComponents.FOOD); }
}
