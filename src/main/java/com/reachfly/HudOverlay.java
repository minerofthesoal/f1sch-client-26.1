package com.reachfly;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudOverlay {

    private static final int COLOR_WATERMARK = 0xFF5555FF;
    private static final int COLOR_FPS = 0xFFAAAAAA;
    private static final int COLOR_COORDS = 0xFFFFFFFF;
    private static final int BAR_COLOR_START = 0xFF00AAFF;
    private static final int BAR_COLOR_END = 0xFFFF55FF;

    public static void render(GuiGraphicsExtractor ctx, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (!ModConfig.hudVisible || client.player == null || client.options.hideGui) return;

        Font font = client.font;
        int screenWidth = ctx.guiWidth();
        int screenHeight = ctx.guiHeight();

        // Use Minecraft's own render system for text (bypasses GuiGraphicsExtractor)
        PoseStack pose = new PoseStack();
        MultiBufferSource.BufferSource bufSource = client.renderBuffers().bufferSource();

        // --- Watermark top-left ---
        String watermark = ModConfig.proUnlocked ? "f1sch PRO" : "f1sch v3.0";
        ctx.fill(2, 2, 6 + font.width(watermark), 14, 0x88000000);
        font.drawInBatch(Component.literal(watermark), 4, 4, COLOR_WATERMARK, true,
                pose.last().pose(), bufSource, Font.DisplayMode.NORMAL, 0, 15728880);

        // --- Module array list top-right ---
        List<String> activeModules = getActiveModules();
        activeModules.sort(Comparator.comparingInt((String s) -> font.width(s)).reversed());

        int yOffset = 2;
        int moduleCount = activeModules.size();
        for (int i = 0; i < moduleCount; i++) {
            String name = activeModules.get(i);
            int textWidth = font.width(name);
            int x = screenWidth - textWidth - 6;
            ctx.fill(x - 2, yOffset, screenWidth, yOffset + 11, 0x88000000);
            int barColor = lerpColor(BAR_COLOR_START, BAR_COLOR_END, moduleCount > 1 ? (float) i / (moduleCount - 1) : 0f);
            ctx.fill(screenWidth - 2, yOffset, screenWidth, yOffset + 11, barColor);
            font.drawInBatch(Component.literal(name), x, yOffset + 1, barColor, false,
                    pose.last().pose(), bufSource, Font.DisplayMode.NORMAL, 0, 15728880);
            yOffset += 11;
        }

        // --- Info bar bottom-left ---
        LocalPlayer p = client.player;
        String coords = String.format("XYZ: %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ());
        String fps = String.valueOf(client.getFps());
        String direction = getCardinalDirection(p.getYRot());

        int infoY = screenHeight - 12;
        ctx.fill(0, infoY - 2, font.width(coords) + 6, screenHeight, 0x88000000);
        font.drawInBatch(Component.literal(coords), 4, infoY, COLOR_COORDS, false,
                pose.last().pose(), bufSource, Font.DisplayMode.NORMAL, 0, 15728880);

        infoY -= 12;
        ctx.fill(0, infoY - 2, font.width(direction) + 6, infoY + 10, 0x88000000);
        font.drawInBatch(Component.literal(direction), 4, infoY, COLOR_COORDS, false,
                pose.last().pose(), bufSource, Font.DisplayMode.NORMAL, 0, 15728880);

        infoY -= 12;
        String fpsLabel = "FPS: " + fps;
        ctx.fill(0, infoY - 2, font.width(fpsLabel) + 6, infoY + 10, 0x88000000);
        font.drawInBatch(Component.literal(fpsLabel), 4, infoY, COLOR_FPS, false,
                pose.last().pose(), bufSource, Font.DisplayMode.NORMAL, 0, 15728880);

        // Flush text rendering
        bufSource.endBatch();
    }

    private static List<String> getActiveModules() {
        List<String> m = new ArrayList<>();
        if (ModConfig.reachEnabled) m.add("Reach");
        if (ModConfig.flyEnabled) m.add("Fly");
        if (ModConfig.espEnabled) m.add("ESP");
        if (ModConfig.autoHitEnabled) m.add("AutoHit");
        if (ModConfig.killAuraEnabled) m.add("KillAura");
        if (ModConfig.killAuraPlusEnabled) m.add("KillAura+");
        if (ModConfig.noFallEnabled) m.add("NoFall");
        if (ModConfig.fullbrightEnabled) m.add("Fullbright");
        if (ModConfig.speedEnabled) m.add("Speed");
        if (ModConfig.xrayEnabled) m.add("X-Ray");
        if (ModConfig.knockbackEnabled) m.add("Knockback");
        if (ModConfig.jesusEnabled) m.add("Jesus");
        if (ModConfig.scaffoldEnabled) m.add("Scaffold");
        if (ModConfig.autoTotemEnabled) m.add("AutoTotem");
        if (ModConfig.autoArmorEnabled) m.add("AutoArmor");
        if (ModConfig.criticalsEnabled) m.add("Criticals");
        if (ModConfig.bunnyHopEnabled) m.add("BunnyHop");
        if (ModConfig.spiderEnabled) m.add("Spider");
        if (ModConfig.dolphinEnabled) m.add("Dolphin");
        if (ModConfig.elytraFlyEnabled) m.add("ElytraFly");
        if (ModConfig.surroundEnabled) m.add("Surround");
        if (ModConfig.crystalAuraEnabled) m.add("CrystalAura");
        if (ModConfig.baritoneEnabled) m.add("Baritone");
        if (ModConfig.invMoveEnabled) m.add("InvMove");
        if (ModConfig.proUnlocked) {
            if (ModConfig.freecamEnabled) m.add("Freecam");
            if (ModConfig.nukerEnabled) m.add("Nuker");
            if (ModConfig.timerEnabled) m.add("Timer");
            if (ModConfig.phaseEnabled) m.add("Phase");
        }
        return m;
    }

    private static int lerpColor(int from, int to, float t) {
        int fA = (from >> 24) & 0xFF, fR = (from >> 16) & 0xFF, fG = (from >> 8) & 0xFF, fB = from & 0xFF;
        int tA = (to >> 24) & 0xFF, tR = (to >> 16) & 0xFF, tG = (to >> 8) & 0xFF, tB = to & 0xFF;
        return ((int)(fA + (tA - fA) * t) << 24) | ((int)(fR + (tR - fR) * t) << 16) |
               ((int)(fG + (tG - fG) * t) << 8) | (int)(fB + (tB - fB) * t);
    }

    private static String getCardinalDirection(float yaw) {
        float n = ((yaw % 360) + 360) % 360;
        if (n >= 315 || n < 45) return "South (+Z)";
        if (n < 135) return "West (-X)";
        if (n < 225) return "North (-Z)";
        return "East (+X)";
    }
}
