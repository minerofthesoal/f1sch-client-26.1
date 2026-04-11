package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Block;

public class XrayHandler {

    private static boolean wasEnabled = false;
    private static boolean needsReload = false;
    private static int reloadDelay = 0;

    public static void tick(Minecraft client) {
        if (client.player == null || client.levelRenderer == null) return;
        if (needsReload) { reloadDelay--; if (reloadDelay <= 0) { needsReload = false; client.levelRenderer.allChanged(); } }
        if (ModConfig.xrayEnabled && !wasEnabled) { wasEnabled = true; needsReload = true; reloadDelay = 1; }
        else if (!ModConfig.xrayEnabled && wasEnabled) { wasEnabled = false; needsReload = true; reloadDelay = 1; }
    }

    public static boolean shouldRenderBlock(Block block) {
        if (!ModConfig.xrayEnabled) return true;
        String id = BuiltInRegistries.BLOCK.getKey(block).getPath();
        if (id.contains("ore")) return true;
        if (id.contains("diamond") || id.contains("emerald") || id.contains("ancient_debris") || id.contains("netherite")) return true;
        if (id.contains("chest") || id.contains("barrel") || id.contains("shulker") || id.contains("hopper")) return true;
        if (id.contains("spawner") || id.contains("end_portal") || id.contains("end_gateway")) return true;
        if (id.contains("beacon") || id.contains("enchanting")) return true;
        if (id.contains("amethyst") || id.contains("budding")) return true;
        if (id.equals("lava") || id.equals("water")) return true;
        if (id.equals("tnt")) return true;
        return false;
    }

    public static boolean isActive() { return ModConfig.xrayEnabled; }
}
