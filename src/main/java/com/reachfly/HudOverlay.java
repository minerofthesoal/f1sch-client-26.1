package com.reachfly;

import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Direction;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class HudOverlay {

    private static final int COLOR_WATERMARK = 0xFF5555FF;
    private static final int COLOR_FPS = 0xFFAAAAAA;
    private static final int COLOR_COORDS = 0xFFFFFFFF;
    private static final int BAR_COLOR_START = 0xFF00AAFF;
    private static final int BAR_COLOR_END = 0xFFFF55FF;

    public static void render(DrawContext ctx, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (!ModConfig.hudVisible || client.player == null || client.options.hideGui) return;

        Font font = client.font;
        int screenWidth = ctx.guiWidth();
        int screenHeight = ctx.guiHeight();

        // --- Watermark top-left ---
        String watermark = ModConfig.proUnlocked ? "f1sch PRO" : "f1sch v3.0";
        ctx.fill(2, 2, 6 + font.width(watermark), 14, 0x88000000);
        ctx.drawString(font, watermark, 4, 4, COLOR_WATERMARK, true);

        // --- Module array list top-right, sorted by text width, right-aligned with color bars ---
        List<String> activeModules = getActiveModules();
        activeModules.sort(Comparator.comparingInt((String s) -> font.width(s)).reversed());

        int yOffset = 2;
        int moduleCount = activeModules.size();
        for (int i = 0; i < moduleCount; i++) {
            String name = activeModules.get(i);
            int textWidth = font.width(name);
            int x = screenWidth - textWidth - 6;

            // Background
            ctx.fill(x - 2, yOffset, screenWidth, yOffset + 11, 0x88000000);

            // Color bar on right edge
            int barColor = lerpColor(BAR_COLOR_START, BAR_COLOR_END, moduleCount > 1 ? (float) i / (moduleCount - 1) : 0f);
            ctx.fill(screenWidth - 2, yOffset, screenWidth, yOffset + 11, barColor);

            // Module name
            ctx.drawString(font, name, x, yOffset + 1, barColor, false);
            yOffset += 11;
        }

        // --- Info bar bottom-left: coords, FPS, direction ---
        LocalPlayer p = client.player;
        String coords = String.format("XYZ: %.1f / %.1f / %.1f", p.getX(), p.getY(), p.getZ());
        String fps = String.valueOf(client.getFps());
        String direction = getCardinalDirection(p.getYRot());

        int infoY = screenHeight - 12;
        ctx.fill(0, infoY - 2, font.width(coords) + 6, screenHeight, 0x88000000);
        ctx.drawString(font, coords, 4, infoY, COLOR_COORDS, false);

        infoY -= 12;
        ctx.fill(0, infoY - 2, font.width(direction) + 6, infoY + 10, 0x88000000);
        ctx.drawString(font, direction, 4, infoY, COLOR_COORDS, false);

        infoY -= 12;
        String fpsLabel = "FPS: " + fps;
        ctx.fill(0, infoY - 2, font.width(fpsLabel) + 6, infoY + 10, 0x88000000);
        ctx.drawString(font, fpsLabel, 4, infoY, COLOR_FPS, false);
    }

    private static List<String> getActiveModules() {
        List<String> modules = new ArrayList<>();
        if (ModConfig.reachEnabled) modules.add("Reach");
        if (ModConfig.flyEnabled) modules.add("Fly");
        if (ModConfig.espEnabled) modules.add("ESP");
        if (ModConfig.autoHitEnabled) modules.add("AutoHit");
        if (ModConfig.killAuraEnabled) modules.add("KillAura");
        if (ModConfig.killAuraPlusEnabled) modules.add("KillAura+");
        if (ModConfig.noFallEnabled) modules.add("NoFall");
        if (ModConfig.fullbrightEnabled) modules.add("Fullbright");
        if (ModConfig.speedEnabled) modules.add("Speed");
        if (ModConfig.xrayEnabled) modules.add("X-Ray");
        if (ModConfig.knockbackEnabled) modules.add("Knockback");
        if (ModConfig.jesusEnabled) modules.add("Jesus");
        if (ModConfig.scaffoldEnabled) modules.add("Scaffold");
        if (ModConfig.autoTotemEnabled) modules.add("AutoTotem");
        if (ModConfig.autoArmorEnabled) modules.add("AutoArmor");
        if (ModConfig.lowHealthKillEnabled) modules.add("LowHealthKill");
        if (ModConfig.autoKillWhenLowEnabled) modules.add("AutoKillLow");
        if (ModConfig.eatingAssistEnabled) modules.add("EatingAssist");
        if (ModConfig.autoElytraSwapEnabled) modules.add("AutoElytra");
        if (ModConfig.flyToCoordsEnabled) modules.add("FlyToCoords");
        if (ModConfig.walkToCoordsEnabled) modules.add("WalkToCoords");
        // Meteor
        if (ModConfig.autoLogEnabled) modules.add("AutoLog");
        if (ModConfig.autoRespawnEnabled) modules.add("AutoRespawn");
        if (ModConfig.betterSprintEnabled) modules.add("BetterSprint");
        if (ModConfig.safeWalkEnabled) modules.add("SafeWalk");
        if (ModConfig.stepEnabled) modules.add("Step");
        // Meteor v2
        if (ModConfig.elytraFlyEnabled) modules.add("ElytraFly");
        if (ModConfig.surroundEnabled) modules.add("Surround");
        if (ModConfig.crystalAuraEnabled) modules.add("CrystalAura");
        if (ModConfig.anchorAuraEnabled) modules.add("AnchorAura");
        if (ModConfig.holeFillerEnabled) modules.add("HoleFiller");
        if (ModConfig.autoTrapEnabled) modules.add("AutoTrap");
        if (ModConfig.reversalEnabled) modules.add("Reversal");
        // Wurst
        if (ModConfig.criticalsEnabled) modules.add("Criticals");
        if (ModConfig.bunnyHopEnabled) modules.add("BunnyHop");
        if (ModConfig.spiderEnabled) modules.add("Spider");
        if (ModConfig.glideEnabled) modules.add("Glide");
        if (ModConfig.highJumpEnabled) modules.add("HighJump");
        if (ModConfig.dolphinEnabled) modules.add("Dolphin");
        if (ModConfig.autoSwordEnabled) modules.add("AutoSword");
        if (ModConfig.sneakEnabled) modules.add("Sneak");
        if (ModConfig.antiHungerEnabled) modules.add("AntiHunger");
        if (ModConfig.triggerBotEnabled) modules.add("TriggerBot");
        if (ModConfig.fastPlaceEnabled) modules.add("FastPlace");
        if (ModConfig.parkourEnabled) modules.add("Parkour");
        if (ModConfig.noSlowdownEnabled) modules.add("NoSlowdown");
        if (ModConfig.antiBlindEnabled) modules.add("AntiBlind");
        if (ModConfig.autoWalkEnabled) modules.add("AutoWalk");
        if (ModConfig.airJumpEnabled) modules.add("AirJump");
        if (ModConfig.noWebEnabled) modules.add("NoWeb");
        if (ModConfig.flightPlusEnabled) modules.add("Flight+");
        if (ModConfig.longJumpEnabled) modules.add("LongJump");
        if (ModConfig.autoMLGEnabled) modules.add("AutoMLG");
        if (ModConfig.blinkEnabled) modules.add("Blink");
        // Pro
        if (ModConfig.proUnlocked) {
            if (ModConfig.antiKnockbackEnabled) modules.add("AntiKB");
            if (ModConfig.noSwingEnabled) modules.add("NoSwing");
            if (ModConfig.antiAfkEnabled) modules.add("AntiAFK");
            if (ModConfig.fastBreakEnabled) modules.add("FastBreak");
            if (ModConfig.nukerEnabled) modules.add("Nuker");
            if (ModConfig.autoFarmEnabled) modules.add("AutoFarm");
            if (ModConfig.phaseEnabled) modules.add("Phase");
            if (ModConfig.freecamEnabled) modules.add("Freecam");
            if (ModConfig.timerEnabled) modules.add("Timer");
            if (ModConfig.chestEspEnabled) modules.add("ChestESP");
            if (ModConfig.trajectoriesEnabled) modules.add("Trajectories");
            if (ModConfig.nametagsEnabled) modules.add("Nametags");
            if (ModConfig.autoFishEnabled) modules.add("AutoFish");
            if (ModConfig.chestStealerEnabled) modules.add("ChestStealer");
            if (ModConfig.autoToolEnabled) modules.add("AutoTool");
            if (ModConfig.invSortEnabled) modules.add("InvSort");
            if (ModConfig.chatSpamEnabled) modules.add("ChatSpam");
            if (ModConfig.autoReplyEnabled) modules.add("AutoReply");
            if (ModConfig.announcerEnabled) modules.add("Announcer");
            if (ModConfig.autoBridgeEnabled) modules.add("AutoBridge");
            if (ModConfig.towerEnabled) modules.add("Tower");
            if (ModConfig.printerEnabled) modules.add("Printer");
        }
        if (ModConfig.invMoveEnabled) modules.add("InvMove");
        return modules;
    }

    private static int lerpColor(int from, int to, float t) {
        int fA = (from >> 24) & 0xFF, fR = (from >> 16) & 0xFF, fG = (from >> 8) & 0xFF, fB = from & 0xFF;
        int tA = (to >> 24) & 0xFF, tR = (to >> 16) & 0xFF, tG = (to >> 8) & 0xFF, tB = to & 0xFF;
        int a = (int) (fA + (tA - fA) * t);
        int r = (int) (fR + (tR - fR) * t);
        int g = (int) (fG + (tG - fG) * t);
        int b = (int) (fB + (tB - fB) * t);
        return (a << 24) | (r << 16) | (g << 8) | b;
    }

    private static String getCardinalDirection(float yaw) {
        float normalized = ((yaw % 360) + 360) % 360;
        if (normalized >= 315 || normalized < 45) return "Facing: South (+Z)";
        if (normalized >= 45 && normalized < 135) return "Facing: West (-X)";
        if (normalized >= 135 && normalized < 225) return "Facing: North (-Z)";
        return "Facing: East (+X)";
    }
}
