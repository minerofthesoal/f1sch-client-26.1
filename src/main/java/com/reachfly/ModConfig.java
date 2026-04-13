package com.reachfly;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = FabricLoader.getInstance()
            .getConfigDir().resolve("reachfly.json");

    // --- Reach ---
    public static boolean reachEnabled = false;
    public static float reachDistance = 6.0f;
    public static final float REACH_MIN = 3.0f;
    public static final float REACH_MAX = 50.0f;

    // --- Fly ---
    public static boolean flyEnabled = false;
    public static float flySpeed = 1.0f;
    public static final float FLY_SPEED_MIN = 0.1f;
    public static final float FLY_SPEED_MAX = 10.0f;

    // --- ESP ---
    public static boolean espEnabled = false;
    public static boolean espPlayers = true;
    public static boolean espHostile = true;
    public static boolean espPassive = false;
    public static boolean espLines = false;
    public static boolean espPathTrace = false;

    // --- Auto Hit ---
    public static boolean autoHitEnabled = false;
    public static float autoHitRange = 3.0f;
    public static final float AUTO_HIT_RANGE_MIN = 1.0f;
    public static final float AUTO_HIT_RANGE_MAX = 50.0f;
    public static boolean autoHitPlayersOnly = false;
    public static boolean killAuraEnabled = false;
    public static boolean killAuraPlusEnabled = false;
    public static int killAuraPlusCps = 20;
    public static final int KA_PLUS_CPS_MIN = 5;
    public static final int KA_PLUS_CPS_MAX = 40;

    // --- Low Health Kill ---
    public static boolean lowHealthKillEnabled = false;
    public static float lowHealthThreshold = 6.0f;
    public static final float LOW_HEALTH_MIN = 1.0f;
    public static final float LOW_HEALTH_MAX = 20.0f;

    // --- Auto Kill When Low HP ---
    public static boolean autoKillWhenLowEnabled = false;
    public static float autoKillSelfHpThreshold = 6.0f;
    public static float autoKillWhenLowRange = 4.0f;
    public static final float AUTO_KILL_SELF_HP_MIN = 1.0f;
    public static final float AUTO_KILL_SELF_HP_MAX = 20.0f;
    public static final float AUTO_KILL_RANGE_MIN = 1.0f;
    public static final float AUTO_KILL_RANGE_MAX = 50.0f;

    // --- Eating Assist ---
    public static boolean eatingAssistEnabled = false;
    public static int eatingHungerThreshold = 14;
    public static final int EATING_HUNGER_MIN = 1;
    public static final int EATING_HUNGER_MAX = 19;

    // --- Jesus ---
    public static boolean jesusEnabled = false;

    // --- Auto Elytra Swap ---
    public static boolean autoElytraSwapEnabled = false;

    // --- Fly to Coords ---
    public static boolean flyToCoordsEnabled = false;
    public static float flyToX = 0;
    public static float flyToY = 100;
    public static float flyToZ = 0;
    public static float flyToCoordsSpeed = 2.0f;
    public static final float FLY_TO_SPEED_MIN = 0.5f;
    public static final float FLY_TO_SPEED_MAX = 20.0f;

    // --- NoFall ---
    public static boolean noFallEnabled = false;

    // --- Fullbright ---
    public static boolean fullbrightEnabled = false;

    // --- Speed ---
    public static boolean speedEnabled = false;
    public static float speedMultiplier = 2.0f;
    public static final float SPEED_MIN = 1.0f;
    public static final float SPEED_MAX = 10.0f;

    // --- X-Ray ---
    public static boolean xrayEnabled = false;

    // --- Knockback ---
    public static boolean knockbackEnabled = false;
    public static float knockbackStrength = 5.0f;
    public static final float KNOCKBACK_MIN = 1.0f;
    public static final float KNOCKBACK_MAX = 2500.0f;

    // --- Auto Totem ---
    public static boolean autoTotemEnabled = false;

    // --- Auto Armor ---
    public static boolean autoArmorEnabled = false;

    // --- Scaffold ---
    public static boolean scaffoldEnabled = false;

    // --- Meteor-style ---
    public static boolean autoLogEnabled = false;
    public static float autoLogHealth = 4.0f;
    public static final float AUTO_LOG_HP_MIN = 1.0f;
    public static final float AUTO_LOG_HP_MAX = 19.0f;
    public static boolean autoRespawnEnabled = false;
    public static boolean betterSprintEnabled = false;
    public static boolean safeWalkEnabled = false;
    public static boolean stepEnabled = false;

    // --- Meteor-style v2 ---
    public static boolean elytraFlyEnabled = false;
    public static float elytraFlySpeed = 2.0f;
    public static final float ELYTRA_FLY_MIN = 0.5f;
    public static final float ELYTRA_FLY_MAX = 10.0f;
    public static boolean surroundEnabled = false;
    public static boolean holeEspEnabled = false;
    public static boolean crystalAuraEnabled = false;

    // --- Wurst-style ---
    public static boolean criticalsEnabled = false;
    public static boolean bunnyHopEnabled = false;
    public static boolean spiderEnabled = false;
    public static boolean glideEnabled = false;
    public static boolean highJumpEnabled = false;
    public static float highJumpHeight = 2.0f;
    public static final float HIGH_JUMP_MIN = 1.0f;
    public static final float HIGH_JUMP_MAX = 10.0f;
    public static boolean dolphinEnabled = false;
    public static boolean autoSwordEnabled = false;
    public static boolean sneakEnabled = false;
    public static boolean panicEnabled = false;
    public static boolean antiHungerEnabled = false;
    public static boolean triggerBotEnabled = false;

    // --- Inventory Move ---
    public static boolean invMoveEnabled = false;
    public static float stepHeight = 2.0f;
    public static final float STEP_MIN = 1.0f;
    public static final float STEP_MAX = 10.0f;

    // --- New Wurst features ---
    public static boolean fastPlaceEnabled = false;
    public static boolean parkourEnabled = false;
    public static boolean noSlowdownEnabled = false;
    public static boolean antiBlindEnabled = false;
    public static boolean autoWalkEnabled = false;
    public static boolean airJumpEnabled = false;
    public static boolean noWebEnabled = false;
    public static boolean flightPlusEnabled = false;
    public static float flightPlusSpeed = 2.0f;
    public static final float FLIGHT_PLUS_MIN = 0.5f;
    public static final float FLIGHT_PLUS_MAX = 10.0f;
    public static boolean longJumpEnabled = false;
    public static float longJumpBoost = 2.0f;
    public static final float LONG_JUMP_MIN = 0.5f;
    public static final float LONG_JUMP_MAX = 5.0f;
    public static boolean autoMLGEnabled = false;
    public static boolean blinkEnabled = false;

    // --- New Meteor features ---
    public static boolean anchorAuraEnabled = false;
    public static boolean holeFillerEnabled = false;
    public static boolean autoTrapEnabled = false;
    public static boolean reversalEnabled = false;

    // --- Teleport ---
    public static boolean tpUseServerAddon = true;
    public static float tpX = 0;
    public static float tpY = 100;
    public static float tpZ = 0;

    // --- HUD ---
    public static boolean hudVisible = true;

    // --- Walk to Coords ---
    public static boolean walkToCoordsEnabled = false;
    public static float walkToX = 0;
    public static float walkToY = 64;
    public static float walkToZ = 0;

    // --- Pro Unlock ---
    public static boolean proUnlocked = false;

    // ===== PRO: Stealth =====
    public static boolean antiKnockbackEnabled = false;
    public static float antiKnockbackStrength = 100.0f;
    public static final float ANTI_KB_MIN = 0.0f;
    public static final float ANTI_KB_MAX = 100.0f;
    public static boolean noSwingEnabled = false;
    public static boolean antiAfkEnabled = false;
    public static int antiAfkInterval = 200;
    public static final int ANTI_AFK_MIN = 20;
    public static final int ANTI_AFK_MAX = 1200;

    // ===== PRO: World =====
    public static boolean fastBreakEnabled = false;
    public static float fastBreakSpeed = 3.0f;
    public static final float FAST_BREAK_MIN = 1.0f;
    public static final float FAST_BREAK_MAX = 10.0f;
    public static boolean nukerEnabled = false;
    public static float nukerRadius = 3.0f;
    public static final float NUKER_MIN = 1.0f;
    public static final float NUKER_MAX = 6.0f;
    public static boolean autoFarmEnabled = false;

    // ===== PRO: Exploit =====
    public static boolean phaseEnabled = false;
    public static boolean freecamEnabled = false;
    public static boolean timerEnabled = false;
    public static float timerSpeed = 2.0f;
    public static final float TIMER_MIN = 0.1f;
    public static final float TIMER_MAX = 10.0f;

    // ===== PRO: Visual =====
    public static boolean chestEspEnabled = false;
    public static boolean trajectoriesEnabled = false;
    public static boolean nametagsEnabled = false;

    // ===== PRO: Utility =====
    public static boolean autoFishEnabled = false;
    public static boolean chestStealerEnabled = false;
    public static int chestStealerDelay = 3;
    public static final int CHEST_STEALER_MIN = 0;
    public static final int CHEST_STEALER_MAX = 20;
    public static boolean autoToolEnabled = false;
    public static boolean invSortEnabled = false;

    // ===== PRO: Social =====
    public static boolean chatSpamEnabled = false;
    public static String chatSpamMessage = "f1sch Pro";
    public static int chatSpamDelay = 100;
    public static final int SPAM_DELAY_MIN = 20;
    public static final int SPAM_DELAY_MAX = 1200;
    public static boolean autoReplyEnabled = false;
    public static String autoReplyMessage = "I'm AFK";
    public static boolean announcerEnabled = false;

    // ===== PRO: Build =====
    public static boolean autoBridgeEnabled = false;
    public static boolean towerEnabled = false;
    public static boolean printerEnabled = false;

    // ===== PRO: Server (requires addon/datapack) =====
    public static boolean opSelfEnabled = false;

    // --- Baritone ---
    public static boolean baritoneEnabled = false;
    public static String baritoneMode = "idle";
    public static float baritoneGotoX = 0;
    public static float baritoneGotoY = 64;
    public static float baritoneGotoZ = 0;
    public static String baritoneMineBlock = "";
    public static float baritoneMineRadius = 32;
    public static float baritoneFollowRange = 5;
    public static float baritoneFarmRadius = 16;
    public static boolean baritoneAutoTool = true;
    public static boolean baritoneSprint = true;
    public static boolean baritoneAvoidDanger = true;
    public static String baritoneBuildFile = "";
    public static boolean baritoneBuildAutoGrab = true;

    // --- Obfuscated validation ---
    private static final int[] _d = {0x39, 0x7D, 0x62, 0x3F, 0x3D, 0x20, 0x61, 0x29, 0x23, 0x26, 0x24};
    private static final int _x = 0x4F;

    public static boolean validateCode(String input) {
        if (input == null || input.length() != _d.length) return false;
        for (int i = 0; i < _d.length; i++) {
            if ((input.charAt(i) ^ _x) != _d[i]) return false;
        }
        return true;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                String json = Files.readString(CONFIG_PATH);
                ConfigData data = GSON.fromJson(json, ConfigData.class);
                if (data != null) {
                    applyFromData(data);
                }
                ReachFlyClient.LOGGER.info("[f1sch] Config loaded.");
            } catch (IOException e) {
                ReachFlyClient.LOGGER.error("[f1sch] Failed to load config", e);
            }
        } else {
            save();
        }
    }

    private static void applyFromData(ConfigData d) {
        reachEnabled = d.reachEnabled; reachDistance = clamp(d.reachDistance, REACH_MIN, REACH_MAX);
        flyEnabled = d.flyEnabled; flySpeed = clamp(d.flySpeed, FLY_SPEED_MIN, FLY_SPEED_MAX);
        espEnabled = d.espEnabled; espPlayers = d.espPlayers; espHostile = d.espHostile;
        espPassive = d.espPassive; espLines = d.espLines; espPathTrace = d.espPathTrace;
        autoHitEnabled = d.autoHitEnabled; autoHitRange = clamp(d.autoHitRange, AUTO_HIT_RANGE_MIN, AUTO_HIT_RANGE_MAX);
        autoHitPlayersOnly = d.autoHitPlayersOnly; killAuraEnabled = d.killAuraEnabled;
        killAuraPlusEnabled = d.killAuraPlusEnabled;
        killAuraPlusCps = Math.max(KA_PLUS_CPS_MIN, Math.min(KA_PLUS_CPS_MAX, d.killAuraPlusCps));
        lowHealthKillEnabled = d.lowHealthKillEnabled;
        lowHealthThreshold = clamp(d.lowHealthThreshold, LOW_HEALTH_MIN, LOW_HEALTH_MAX);
        autoKillWhenLowEnabled = d.autoKillWhenLowEnabled;
        autoKillSelfHpThreshold = clamp(d.autoKillSelfHpThreshold, AUTO_KILL_SELF_HP_MIN, AUTO_KILL_SELF_HP_MAX);
        autoKillWhenLowRange = clamp(d.autoKillWhenLowRange, AUTO_KILL_RANGE_MIN, AUTO_KILL_RANGE_MAX);
        eatingAssistEnabled = d.eatingAssistEnabled;
        eatingHungerThreshold = (int) clamp(d.eatingHungerThreshold, EATING_HUNGER_MIN, EATING_HUNGER_MAX);
        jesusEnabled = d.jesusEnabled; autoElytraSwapEnabled = d.autoElytraSwapEnabled;
        flyToCoordsEnabled = d.flyToCoordsEnabled; flyToX = d.flyToX; flyToY = d.flyToY; flyToZ = d.flyToZ;
        flyToCoordsSpeed = clamp(d.flyToCoordsSpeed, FLY_TO_SPEED_MIN, FLY_TO_SPEED_MAX);
        noFallEnabled = d.noFallEnabled; fullbrightEnabled = d.fullbrightEnabled;
        speedEnabled = d.speedEnabled; speedMultiplier = clamp(d.speedMultiplier, SPEED_MIN, SPEED_MAX);
        xrayEnabled = d.xrayEnabled; knockbackEnabled = d.knockbackEnabled;
        knockbackStrength = clamp(d.knockbackStrength, KNOCKBACK_MIN, KNOCKBACK_MAX);
        hudVisible = d.hudVisible; autoTotemEnabled = d.autoTotemEnabled;
        autoArmorEnabled = d.autoArmorEnabled; scaffoldEnabled = d.scaffoldEnabled;
        autoLogEnabled = d.autoLogEnabled; autoLogHealth = clamp(d.autoLogHealth, AUTO_LOG_HP_MIN, AUTO_LOG_HP_MAX);
        autoRespawnEnabled = d.autoRespawnEnabled; betterSprintEnabled = d.betterSprintEnabled;
        safeWalkEnabled = d.safeWalkEnabled; stepEnabled = d.stepEnabled;
        stepHeight = clamp(d.stepHeight, STEP_MIN, STEP_MAX);
        elytraFlyEnabled = d.elytraFlyEnabled; elytraFlySpeed = clamp(d.elytraFlySpeed, ELYTRA_FLY_MIN, ELYTRA_FLY_MAX);
        surroundEnabled = d.surroundEnabled; holeEspEnabled = d.holeEspEnabled; crystalAuraEnabled = d.crystalAuraEnabled;
        criticalsEnabled = d.criticalsEnabled; bunnyHopEnabled = d.bunnyHopEnabled;
        spiderEnabled = d.spiderEnabled; glideEnabled = d.glideEnabled;
        highJumpEnabled = d.highJumpEnabled; highJumpHeight = clamp(d.highJumpHeight, HIGH_JUMP_MIN, HIGH_JUMP_MAX);
        dolphinEnabled = d.dolphinEnabled; autoSwordEnabled = d.autoSwordEnabled;
        sneakEnabled = d.sneakEnabled; panicEnabled = d.panicEnabled;
        antiHungerEnabled = d.antiHungerEnabled; triggerBotEnabled = d.triggerBotEnabled;
        invMoveEnabled = d.invMoveEnabled;
        fastPlaceEnabled = d.fastPlaceEnabled; parkourEnabled = d.parkourEnabled;
        noSlowdownEnabled = d.noSlowdownEnabled; antiBlindEnabled = d.antiBlindEnabled;
        autoWalkEnabled = d.autoWalkEnabled; airJumpEnabled = d.airJumpEnabled;
        noWebEnabled = d.noWebEnabled; flightPlusEnabled = d.flightPlusEnabled;
        flightPlusSpeed = clamp(d.flightPlusSpeed, FLIGHT_PLUS_MIN, FLIGHT_PLUS_MAX);
        longJumpEnabled = d.longJumpEnabled; longJumpBoost = clamp(d.longJumpBoost, LONG_JUMP_MIN, LONG_JUMP_MAX);
        autoMLGEnabled = d.autoMLGEnabled; blinkEnabled = d.blinkEnabled;
        anchorAuraEnabled = d.anchorAuraEnabled; holeFillerEnabled = d.holeFillerEnabled;
        autoTrapEnabled = d.autoTrapEnabled; reversalEnabled = d.reversalEnabled;
        tpUseServerAddon = d.tpUseServerAddon; tpX = d.tpX; tpY = d.tpY; tpZ = d.tpZ;
        walkToCoordsEnabled = d.walkToCoordsEnabled; walkToX = d.walkToX; walkToY = d.walkToY; walkToZ = d.walkToZ;
        proUnlocked = d.proUnlocked;
        antiKnockbackEnabled = d.antiKnockbackEnabled;
        antiKnockbackStrength = clamp(d.antiKnockbackStrength, ANTI_KB_MIN, ANTI_KB_MAX);
        noSwingEnabled = d.noSwingEnabled; antiAfkEnabled = d.antiAfkEnabled;
        antiAfkInterval = (int) clamp(d.antiAfkInterval, ANTI_AFK_MIN, ANTI_AFK_MAX);
        fastBreakEnabled = d.fastBreakEnabled; fastBreakSpeed = clamp(d.fastBreakSpeed, FAST_BREAK_MIN, FAST_BREAK_MAX);
        nukerEnabled = d.nukerEnabled; nukerRadius = clamp(d.nukerRadius, NUKER_MIN, NUKER_MAX);
        autoFarmEnabled = d.autoFarmEnabled; phaseEnabled = d.phaseEnabled;
        freecamEnabled = d.freecamEnabled; timerEnabled = d.timerEnabled;
        timerSpeed = clamp(d.timerSpeed, TIMER_MIN, TIMER_MAX);
        chestEspEnabled = d.chestEspEnabled; trajectoriesEnabled = d.trajectoriesEnabled;
        nametagsEnabled = d.nametagsEnabled; autoFishEnabled = d.autoFishEnabled;
        chestStealerEnabled = d.chestStealerEnabled;
        chestStealerDelay = (int) clamp(d.chestStealerDelay, CHEST_STEALER_MIN, CHEST_STEALER_MAX);
        autoToolEnabled = d.autoToolEnabled; invSortEnabled = d.invSortEnabled;
        chatSpamEnabled = d.chatSpamEnabled;
        if (d.chatSpamMessage != null) chatSpamMessage = d.chatSpamMessage;
        chatSpamDelay = (int) clamp(d.chatSpamDelay, SPAM_DELAY_MIN, SPAM_DELAY_MAX);
        autoReplyEnabled = d.autoReplyEnabled;
        if (d.autoReplyMessage != null) autoReplyMessage = d.autoReplyMessage;
        announcerEnabled = d.announcerEnabled; autoBridgeEnabled = d.autoBridgeEnabled;
        towerEnabled = d.towerEnabled; printerEnabled = d.printerEnabled;
        opSelfEnabled = d.opSelfEnabled;
        baritoneEnabled = d.baritoneEnabled;
        if (d.baritoneMode != null) baritoneMode = d.baritoneMode;
        baritoneGotoX = d.baritoneGotoX; baritoneGotoY = d.baritoneGotoY; baritoneGotoZ = d.baritoneGotoZ;
        if (d.baritoneMineBlock != null) baritoneMineBlock = d.baritoneMineBlock;
        baritoneMineRadius = d.baritoneMineRadius; baritoneFollowRange = d.baritoneFollowRange;
        baritoneFarmRadius = d.baritoneFarmRadius; baritoneAutoTool = d.baritoneAutoTool;
        baritoneSprint = d.baritoneSprint; baritoneAvoidDanger = d.baritoneAvoidDanger;
        if (d.baritoneBuildFile != null) baritoneBuildFile = d.baritoneBuildFile;
        baritoneBuildAutoGrab = d.baritoneBuildAutoGrab;
    }

    public static void save() {
        ConfigData d = new ConfigData();
        d.reachEnabled = reachEnabled; d.reachDistance = reachDistance;
        d.flyEnabled = flyEnabled; d.flySpeed = flySpeed;
        d.espEnabled = espEnabled; d.espPlayers = espPlayers; d.espHostile = espHostile;
        d.espPassive = espPassive; d.espLines = espLines; d.espPathTrace = espPathTrace;
        d.autoHitEnabled = autoHitEnabled; d.autoHitRange = autoHitRange;
        d.autoHitPlayersOnly = autoHitPlayersOnly; d.killAuraEnabled = killAuraEnabled;
        d.killAuraPlusEnabled = killAuraPlusEnabled; d.killAuraPlusCps = killAuraPlusCps;
        d.lowHealthKillEnabled = lowHealthKillEnabled; d.lowHealthThreshold = lowHealthThreshold;
        d.autoKillWhenLowEnabled = autoKillWhenLowEnabled;
        d.autoKillSelfHpThreshold = autoKillSelfHpThreshold;
        d.autoKillWhenLowRange = autoKillWhenLowRange;
        d.eatingAssistEnabled = eatingAssistEnabled; d.eatingHungerThreshold = eatingHungerThreshold;
        d.jesusEnabled = jesusEnabled; d.autoElytraSwapEnabled = autoElytraSwapEnabled;
        d.flyToCoordsEnabled = flyToCoordsEnabled; d.flyToX = flyToX; d.flyToY = flyToY; d.flyToZ = flyToZ;
        d.flyToCoordsSpeed = flyToCoordsSpeed;
        d.noFallEnabled = noFallEnabled; d.fullbrightEnabled = fullbrightEnabled;
        d.speedEnabled = speedEnabled; d.speedMultiplier = speedMultiplier;
        d.xrayEnabled = xrayEnabled; d.knockbackEnabled = knockbackEnabled;
        d.knockbackStrength = knockbackStrength; d.hudVisible = hudVisible;
        d.autoTotemEnabled = autoTotemEnabled; d.autoArmorEnabled = autoArmorEnabled;
        d.scaffoldEnabled = scaffoldEnabled; d.autoLogEnabled = autoLogEnabled;
        d.autoLogHealth = autoLogHealth; d.autoRespawnEnabled = autoRespawnEnabled;
        d.betterSprintEnabled = betterSprintEnabled; d.safeWalkEnabled = safeWalkEnabled;
        d.stepEnabled = stepEnabled; d.stepHeight = stepHeight;
        d.elytraFlyEnabled = elytraFlyEnabled; d.elytraFlySpeed = elytraFlySpeed;
        d.surroundEnabled = surroundEnabled; d.holeEspEnabled = holeEspEnabled;
        d.crystalAuraEnabled = crystalAuraEnabled; d.criticalsEnabled = criticalsEnabled;
        d.bunnyHopEnabled = bunnyHopEnabled; d.spiderEnabled = spiderEnabled;
        d.glideEnabled = glideEnabled; d.highJumpEnabled = highJumpEnabled;
        d.highJumpHeight = highJumpHeight; d.dolphinEnabled = dolphinEnabled;
        d.autoSwordEnabled = autoSwordEnabled; d.sneakEnabled = sneakEnabled;
        d.panicEnabled = panicEnabled; d.antiHungerEnabled = antiHungerEnabled;
        d.triggerBotEnabled = triggerBotEnabled; d.invMoveEnabled = invMoveEnabled;
        d.fastPlaceEnabled = fastPlaceEnabled; d.parkourEnabled = parkourEnabled;
        d.noSlowdownEnabled = noSlowdownEnabled; d.antiBlindEnabled = antiBlindEnabled;
        d.autoWalkEnabled = autoWalkEnabled; d.airJumpEnabled = airJumpEnabled;
        d.noWebEnabled = noWebEnabled; d.flightPlusEnabled = flightPlusEnabled;
        d.flightPlusSpeed = flightPlusSpeed; d.longJumpEnabled = longJumpEnabled;
        d.longJumpBoost = longJumpBoost; d.autoMLGEnabled = autoMLGEnabled;
        d.blinkEnabled = blinkEnabled; d.anchorAuraEnabled = anchorAuraEnabled;
        d.holeFillerEnabled = holeFillerEnabled; d.autoTrapEnabled = autoTrapEnabled;
        d.reversalEnabled = reversalEnabled;
        d.tpUseServerAddon = tpUseServerAddon; d.tpX = tpX; d.tpY = tpY; d.tpZ = tpZ;
        d.walkToCoordsEnabled = walkToCoordsEnabled; d.walkToX = walkToX; d.walkToY = walkToY; d.walkToZ = walkToZ;
        d.proUnlocked = proUnlocked;
        d.antiKnockbackEnabled = antiKnockbackEnabled; d.antiKnockbackStrength = antiKnockbackStrength;
        d.noSwingEnabled = noSwingEnabled; d.antiAfkEnabled = antiAfkEnabled;
        d.antiAfkInterval = antiAfkInterval; d.fastBreakEnabled = fastBreakEnabled;
        d.fastBreakSpeed = fastBreakSpeed; d.nukerEnabled = nukerEnabled;
        d.nukerRadius = nukerRadius; d.autoFarmEnabled = autoFarmEnabled;
        d.phaseEnabled = phaseEnabled; d.freecamEnabled = freecamEnabled;
        d.timerEnabled = timerEnabled; d.timerSpeed = timerSpeed;
        d.chestEspEnabled = chestEspEnabled; d.trajectoriesEnabled = trajectoriesEnabled;
        d.nametagsEnabled = nametagsEnabled; d.autoFishEnabled = autoFishEnabled;
        d.chestStealerEnabled = chestStealerEnabled; d.chestStealerDelay = chestStealerDelay;
        d.autoToolEnabled = autoToolEnabled; d.invSortEnabled = invSortEnabled;
        d.chatSpamEnabled = chatSpamEnabled; d.chatSpamMessage = chatSpamMessage;
        d.chatSpamDelay = chatSpamDelay; d.autoReplyEnabled = autoReplyEnabled;
        d.autoReplyMessage = autoReplyMessage; d.announcerEnabled = announcerEnabled;
        d.autoBridgeEnabled = autoBridgeEnabled; d.towerEnabled = towerEnabled;
        d.printerEnabled = printerEnabled; d.opSelfEnabled = opSelfEnabled;
        d.baritoneEnabled = baritoneEnabled; d.baritoneMode = baritoneMode;
        d.baritoneGotoX = baritoneGotoX; d.baritoneGotoY = baritoneGotoY; d.baritoneGotoZ = baritoneGotoZ;
        d.baritoneMineBlock = baritoneMineBlock; d.baritoneMineRadius = baritoneMineRadius;
        d.baritoneFollowRange = baritoneFollowRange; d.baritoneFarmRadius = baritoneFarmRadius;
        d.baritoneAutoTool = baritoneAutoTool; d.baritoneSprint = baritoneSprint;
        d.baritoneAvoidDanger = baritoneAvoidDanger; d.baritoneBuildFile = baritoneBuildFile;
        d.baritoneBuildAutoGrab = baritoneBuildAutoGrab;

        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            Files.writeString(CONFIG_PATH, GSON.toJson(d));
        } catch (IOException e) {
            ReachFlyClient.LOGGER.error("[f1sch] Failed to save config", e);
        }
    }

    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    @SuppressWarnings("unused")
    private static class ConfigData {
        boolean reachEnabled, flyEnabled, espEnabled, espPlayers = true, espHostile = true, espPassive;
        boolean espLines, espPathTrace, autoHitEnabled, autoHitPlayersOnly, killAuraEnabled;
        boolean killAuraPlusEnabled, lowHealthKillEnabled, autoKillWhenLowEnabled;
        boolean eatingAssistEnabled, jesusEnabled, autoElytraSwapEnabled, flyToCoordsEnabled;
        boolean noFallEnabled, fullbrightEnabled, speedEnabled, xrayEnabled, knockbackEnabled;
        boolean autoTotemEnabled, autoArmorEnabled, scaffoldEnabled, autoLogEnabled;
        boolean autoRespawnEnabled, betterSprintEnabled, safeWalkEnabled, stepEnabled;
        boolean elytraFlyEnabled, surroundEnabled, holeEspEnabled, crystalAuraEnabled;
        boolean criticalsEnabled, bunnyHopEnabled, spiderEnabled, glideEnabled, highJumpEnabled;
        boolean dolphinEnabled, autoSwordEnabled, sneakEnabled, panicEnabled;
        boolean antiHungerEnabled, triggerBotEnabled, invMoveEnabled;
        boolean fastPlaceEnabled, parkourEnabled, noSlowdownEnabled, antiBlindEnabled;
        boolean autoWalkEnabled, airJumpEnabled, noWebEnabled, flightPlusEnabled;
        boolean longJumpEnabled, autoMLGEnabled, blinkEnabled;
        boolean anchorAuraEnabled, holeFillerEnabled, autoTrapEnabled, reversalEnabled;
        boolean tpUseServerAddon = true, walkToCoordsEnabled, proUnlocked;
        boolean antiKnockbackEnabled, noSwingEnabled, antiAfkEnabled;
        boolean fastBreakEnabled, nukerEnabled, autoFarmEnabled;
        boolean phaseEnabled, freecamEnabled, timerEnabled;
        boolean chestEspEnabled, trajectoriesEnabled, nametagsEnabled;
        boolean autoFishEnabled, chestStealerEnabled, autoToolEnabled, invSortEnabled;
        boolean chatSpamEnabled, autoReplyEnabled, announcerEnabled;
        boolean autoBridgeEnabled, towerEnabled, printerEnabled, opSelfEnabled;
        boolean hudVisible = true;
        float reachDistance = 6.0f, flySpeed = 1.0f, autoHitRange = 3.0f;
        float lowHealthThreshold = 6.0f, autoKillSelfHpThreshold = 6.0f, autoKillWhenLowRange = 4.0f;
        float flyToX, flyToY = 100, flyToZ, flyToCoordsSpeed = 2.0f;
        float speedMultiplier = 2.0f, knockbackStrength = 5.0f;
        float autoLogHealth = 4.0f, stepHeight = 2.0f;
        float elytraFlySpeed = 2.0f, highJumpHeight = 2.0f;
        float flightPlusSpeed = 2.0f, longJumpBoost = 2.0f;
        float tpX, tpY = 100, tpZ, walkToX, walkToY = 64, walkToZ;
        float antiKnockbackStrength = 100.0f, fastBreakSpeed = 3.0f;
        float nukerRadius = 3.0f, timerSpeed = 2.0f;
        int killAuraPlusCps = 20, eatingHungerThreshold = 14;
        int antiAfkInterval = 200, chestStealerDelay = 3;
        int chatSpamDelay = 100;
        String chatSpamMessage = "f1sch Pro", autoReplyMessage = "I'm AFK";
        boolean baritoneEnabled, baritoneAutoTool = true, baritoneSprint = true;
        boolean baritoneAvoidDanger = true, baritoneBuildAutoGrab = true;
        String baritoneMode = "idle", baritoneMineBlock = "", baritoneBuildFile = "";
        float baritoneGotoX, baritoneGotoY = 64, baritoneGotoZ;
        float baritoneMineRadius = 32, baritoneFollowRange = 5, baritoneFarmRadius = 16;
    }
}
