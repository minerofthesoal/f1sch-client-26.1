package com.reachfly;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * f1sch 2.3 style config screen with category sidebar, expandable modules,
 * pro code entry, and item give. Uses addRenderableWidget for MC 26.1 compat.
 */
public class ConfigScreen extends Screen {

    private final Screen parent;
    private String activeCategory = "Combat";
    private String expandedModule = null;
    private int scrollOffset = 0;

    // Edit modal state
    private EditBox editField = null;
    private String editLabel = null;
    private Consumer<Float> editSetter = null;
    private float editMin, editMax;

    // Code entry state
    private EditBox codeField = null;
    private boolean showCodeEntry = false;

    private final Map<String, List<Module>> categories = new LinkedHashMap<>();

    public ConfigScreen(Screen parent) {
        super(Component.literal("f1sch"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        categories.clear();
        buildCategories();
        buildPage();
    }

    private void buildCategories() {
        // === COMBAT ===
        List<Module> combat = new ArrayList<>();
        combat.add(new Module("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v)
            .num("Range", () -> ModConfig.autoHitRange, ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, v -> ModConfig.autoHitRange = v)
            .tog("Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v)
            .tog("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v));
        combat.add(new Module("KillAura+", () -> ModConfig.killAuraPlusEnabled, v -> ModConfig.killAuraPlusEnabled = v)
            .num("CPS", () -> (float) ModConfig.killAuraPlusCps, ModConfig.KA_PLUS_CPS_MIN, ModConfig.KA_PLUS_CPS_MAX, v -> ModConfig.killAuraPlusCps = Math.round(v)));
        combat.add(new Module("Low HP Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v)
            .num("Threshold", () -> ModConfig.lowHealthThreshold, ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, v -> ModConfig.lowHealthThreshold = v));
        combat.add(new Module("Auto Kill Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v)
            .num("Self HP", () -> ModConfig.autoKillSelfHpThreshold, ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, v -> ModConfig.autoKillSelfHpThreshold = v)
            .num("Range", () -> ModConfig.autoKillWhenLowRange, ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, v -> ModConfig.autoKillWhenLowRange = v));
        combat.add(new Module("Reach", () -> ModConfig.reachEnabled, v -> { ModConfig.reachEnabled = v; ReachHandler.updateReachAttributes(); })
            .num("Distance", () -> ModConfig.reachDistance, ModConfig.REACH_MIN, ModConfig.REACH_MAX, v -> ModConfig.reachDistance = v));
        combat.add(new Module("Knockback", () -> ModConfig.knockbackEnabled, v -> { ModConfig.knockbackEnabled = v; KnockbackHandler.updateKnockbackAttributes(); })
            .num("Strength", () -> ModConfig.knockbackStrength, ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, v -> ModConfig.knockbackStrength = v));
        combat.add(new Module("Criticals", () -> ModConfig.criticalsEnabled, v -> ModConfig.criticalsEnabled = v));
        combat.add(new Module("TriggerBot", () -> ModConfig.triggerBotEnabled, v -> ModConfig.triggerBotEnabled = v));
        combat.add(new Module("AutoSword", () -> ModConfig.autoSwordEnabled, v -> ModConfig.autoSwordEnabled = v));
        categories.put("Combat", combat);

        // === MOVEMENT ===
        List<Module> movement = new ArrayList<>();
        movement.add(new Module("Fly", () -> ModConfig.flyEnabled, v -> {
            ModConfig.flyEnabled = v;
            if (minecraft.player != null && !minecraft.player.getAbilities().instabuild) {
                minecraft.player.getAbilities().mayfly = v;
                if (!v) minecraft.player.getAbilities().flying = false;
                minecraft.player.onUpdateAbilities();
            }
        }).num("Speed", () -> ModConfig.flySpeed, ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, v -> ModConfig.flySpeed = v));
        movement.add(new Module("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v)
            .num("Multiplier", () -> ModConfig.speedMultiplier, ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, v -> ModConfig.speedMultiplier = v));
        movement.add(new Module("Jesus", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v));
        movement.add(new Module("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v));
        movement.add(new Module("Scaffold", () -> ModConfig.scaffoldEnabled, v -> ModConfig.scaffoldEnabled = v));
        movement.add(new Module("BetterSprint", () -> ModConfig.betterSprintEnabled, v -> ModConfig.betterSprintEnabled = v));
        movement.add(new Module("SafeWalk", () -> ModConfig.safeWalkEnabled, v -> ModConfig.safeWalkEnabled = v));
        movement.add(new Module("Step", () -> ModConfig.stepEnabled, v -> ModConfig.stepEnabled = v)
            .num("Height", () -> ModConfig.stepHeight, ModConfig.STEP_MIN, ModConfig.STEP_MAX, v -> ModConfig.stepHeight = v));
        movement.add(new Module("FlyToCoords", () -> ModConfig.flyToCoordsEnabled, v -> { ModConfig.flyToCoordsEnabled = v; if (!v) FlyToCoordsHandler.onDisable(); })
            .num("X", () -> ModConfig.flyToX, -30000000, 30000000, v -> ModConfig.flyToX = v)
            .num("Y", () -> ModConfig.flyToY, -64, 320, v -> ModConfig.flyToY = v)
            .num("Z", () -> ModConfig.flyToZ, -30000000, 30000000, v -> ModConfig.flyToZ = v)
            .num("Speed", () -> ModConfig.flyToCoordsSpeed, ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, v -> ModConfig.flyToCoordsSpeed = v));
        movement.add(new Module("WalkToCoords", () -> ModConfig.walkToCoordsEnabled, v -> { ModConfig.walkToCoordsEnabled = v; if (!v) WalkToCoordsHandler.onDisable(); })
            .num("X", () -> ModConfig.walkToX, -30000000, 30000000, v -> ModConfig.walkToX = v)
            .num("Y", () -> ModConfig.walkToY, -64, 320, v -> ModConfig.walkToY = v)
            .num("Z", () -> ModConfig.walkToZ, -30000000, 30000000, v -> ModConfig.walkToZ = v));
        categories.put("Movement", movement);

        // === RENDER ===
        List<Module> render = new ArrayList<>();
        render.add(new Module("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v)
            .tog("Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v)
            .tog("Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v)
            .tog("Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v)
            .tog("Tracers", () -> ModConfig.espLines, v -> ModConfig.espLines = v)
            .tog("Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v));
        render.add(new Module("X-Ray", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v));
        render.add(new Module("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v));
        render.add(new Module("HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v));
        categories.put("Render", render);

        // === PLAYER ===
        List<Module> player = new ArrayList<>();
        player.add(new Module("Auto Totem", () -> ModConfig.autoTotemEnabled, v -> ModConfig.autoTotemEnabled = v));
        player.add(new Module("Auto Armor", () -> ModConfig.autoArmorEnabled, v -> ModConfig.autoArmorEnabled = v));
        player.add(new Module("Auto Elytra", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v));
        player.add(new Module("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v)
            .num("Hunger Thresh", () -> (float) ModConfig.eatingHungerThreshold, ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, v -> ModConfig.eatingHungerThreshold = Math.round(v)));
        player.add(new Module("Auto Log", () -> ModConfig.autoLogEnabled, v -> ModConfig.autoLogEnabled = v)
            .num("HP Threshold", () -> ModConfig.autoLogHealth, ModConfig.AUTO_LOG_HP_MIN, ModConfig.AUTO_LOG_HP_MAX, v -> ModConfig.autoLogHealth = v));
        player.add(new Module("Auto Respawn", () -> ModConfig.autoRespawnEnabled, v -> ModConfig.autoRespawnEnabled = v));
        player.add(new Module("Teleport", () -> ModConfig.tpUseServerAddon, v -> ModConfig.tpUseServerAddon = v)
            .num("X", () -> ModConfig.tpX, -30000000, 30000000, v -> ModConfig.tpX = v)
            .num("Y", () -> ModConfig.tpY, -64, 320, v -> ModConfig.tpY = v)
            .num("Z", () -> ModConfig.tpZ, -30000000, 30000000, v -> ModConfig.tpZ = v));
        categories.put("Player", player);

        // === WURST ===
        List<Module> wurst = new ArrayList<>();
        wurst.add(new Module("BunnyHop", () -> ModConfig.bunnyHopEnabled, v -> ModConfig.bunnyHopEnabled = v));
        wurst.add(new Module("Spider", () -> ModConfig.spiderEnabled, v -> ModConfig.spiderEnabled = v));
        wurst.add(new Module("Glide", () -> ModConfig.glideEnabled, v -> ModConfig.glideEnabled = v));
        wurst.add(new Module("HighJump", () -> ModConfig.highJumpEnabled, v -> ModConfig.highJumpEnabled = v)
            .num("Height", () -> ModConfig.highJumpHeight, ModConfig.HIGH_JUMP_MIN, ModConfig.HIGH_JUMP_MAX, v -> ModConfig.highJumpHeight = v));
        wurst.add(new Module("Dolphin", () -> ModConfig.dolphinEnabled, v -> ModConfig.dolphinEnabled = v));
        wurst.add(new Module("Sneak", () -> ModConfig.sneakEnabled, v -> ModConfig.sneakEnabled = v));
        wurst.add(new Module("Panic", () -> ModConfig.panicEnabled, v -> ModConfig.panicEnabled = v));
        wurst.add(new Module("AntiHunger", () -> ModConfig.antiHungerEnabled, v -> ModConfig.antiHungerEnabled = v));
        wurst.add(new Module("InvMove", () -> ModConfig.invMoveEnabled, v -> ModConfig.invMoveEnabled = v));
        wurst.add(new Module("FastPlace", () -> ModConfig.fastPlaceEnabled, v -> ModConfig.fastPlaceEnabled = v));
        wurst.add(new Module("Parkour", () -> ModConfig.parkourEnabled, v -> ModConfig.parkourEnabled = v));
        wurst.add(new Module("NoSlowdown", () -> ModConfig.noSlowdownEnabled, v -> ModConfig.noSlowdownEnabled = v));
        wurst.add(new Module("AntiBlind", () -> ModConfig.antiBlindEnabled, v -> ModConfig.antiBlindEnabled = v));
        wurst.add(new Module("AutoWalk", () -> ModConfig.autoWalkEnabled, v -> ModConfig.autoWalkEnabled = v));
        wurst.add(new Module("AirJump", () -> ModConfig.airJumpEnabled, v -> ModConfig.airJumpEnabled = v));
        wurst.add(new Module("NoWeb", () -> ModConfig.noWebEnabled, v -> ModConfig.noWebEnabled = v));
        wurst.add(new Module("Flight+", () -> ModConfig.flightPlusEnabled, v -> ModConfig.flightPlusEnabled = v)
            .num("Speed", () -> ModConfig.flightPlusSpeed, ModConfig.FLIGHT_PLUS_MIN, ModConfig.FLIGHT_PLUS_MAX, v -> ModConfig.flightPlusSpeed = v));
        wurst.add(new Module("LongJump", () -> ModConfig.longJumpEnabled, v -> ModConfig.longJumpEnabled = v)
            .num("Boost", () -> ModConfig.longJumpBoost, ModConfig.LONG_JUMP_MIN, ModConfig.LONG_JUMP_MAX, v -> ModConfig.longJumpBoost = v));
        wurst.add(new Module("AutoMLG", () -> ModConfig.autoMLGEnabled, v -> ModConfig.autoMLGEnabled = v));
        wurst.add(new Module("Blink", () -> ModConfig.blinkEnabled, v -> ModConfig.blinkEnabled = v));
        categories.put("Wurst", wurst);

        // === METEOR+ ===
        List<Module> meteor = new ArrayList<>();
        meteor.add(new Module("ElytraFly", () -> ModConfig.elytraFlyEnabled, v -> ModConfig.elytraFlyEnabled = v)
            .num("Speed", () -> ModConfig.elytraFlySpeed, ModConfig.ELYTRA_FLY_MIN, ModConfig.ELYTRA_FLY_MAX, v -> ModConfig.elytraFlySpeed = v));
        meteor.add(new Module("Surround", () -> ModConfig.surroundEnabled, v -> ModConfig.surroundEnabled = v));
        meteor.add(new Module("CrystalAura", () -> ModConfig.crystalAuraEnabled, v -> ModConfig.crystalAuraEnabled = v));
        meteor.add(new Module("HoleESP", () -> ModConfig.holeEspEnabled, v -> ModConfig.holeEspEnabled = v));
        meteor.add(new Module("AnchorAura", () -> ModConfig.anchorAuraEnabled, v -> ModConfig.anchorAuraEnabled = v));
        meteor.add(new Module("HoleFiller", () -> ModConfig.holeFillerEnabled, v -> ModConfig.holeFillerEnabled = v));
        meteor.add(new Module("AutoTrap", () -> ModConfig.autoTrapEnabled, v -> ModConfig.autoTrapEnabled = v));
        meteor.add(new Module("Reversal", () -> ModConfig.reversalEnabled, v -> ModConfig.reversalEnabled = v));
        categories.put("Meteor+", meteor);

        // === BARITONE ===
        List<Module> baritone = new ArrayList<>();
        baritone.add(new Module("Baritone", () -> ModConfig.baritoneEnabled, v -> ModConfig.baritoneEnabled = v)
            .tog("Sprint", () -> ModConfig.baritoneSprint, v -> ModConfig.baritoneSprint = v)
            .tog("Auto Tool", () -> ModConfig.baritoneAutoTool, v -> ModConfig.baritoneAutoTool = v)
            .tog("Avoid Danger", () -> ModConfig.baritoneAvoidDanger, v -> ModConfig.baritoneAvoidDanger = v));
        baritone.add(new Module("Goto", () -> ModConfig.baritoneMode.equals("goto"), v -> {
            if (v) BaritoneHandler.parseCommand("#goto " + (int) ModConfig.baritoneGotoX + " " + (int) ModConfig.baritoneGotoY + " " + (int) ModConfig.baritoneGotoZ);
            else BaritoneHandler.stop();
        }).num("X", () -> ModConfig.baritoneGotoX, -30000000, 30000000, v -> ModConfig.baritoneGotoX = v)
          .num("Y", () -> ModConfig.baritoneGotoY, -64, 320, v -> ModConfig.baritoneGotoY = v)
          .num("Z", () -> ModConfig.baritoneGotoZ, -30000000, 30000000, v -> ModConfig.baritoneGotoZ = v));
        baritone.add(new Module("Follow", () -> ModConfig.baritoneMode.equals("follow"), v -> {
            if (v) BaritoneHandler.parseCommand("#follow"); else BaritoneHandler.stop();
        }));
        baritone.add(new Module("Farm", () -> ModConfig.baritoneMode.equals("farm"), v -> {
            if (v) BaritoneHandler.parseCommand("#farm"); else BaritoneHandler.stop();
        }));
        baritone.add(new Module("Explore", () -> ModConfig.baritoneMode.equals("explore"), v -> {
            if (v) BaritoneHandler.parseCommand("#explore"); else BaritoneHandler.stop();
        }));
        categories.put("Baritone", baritone);

        // === PRO CATEGORIES ===
        if (ModConfig.proUnlocked) {
            List<Module> pro = new ArrayList<>();
            pro.add(new Module("Anti KB", () -> ModConfig.antiKnockbackEnabled, v -> ModConfig.antiKnockbackEnabled = v)
                .num("Strength%", () -> ModConfig.antiKnockbackStrength, ModConfig.ANTI_KB_MIN, ModConfig.ANTI_KB_MAX, v -> ModConfig.antiKnockbackStrength = v));
            pro.add(new Module("NoSwing", () -> ModConfig.noSwingEnabled, v -> ModConfig.noSwingEnabled = v));
            pro.add(new Module("AntiAFK", () -> ModConfig.antiAfkEnabled, v -> ModConfig.antiAfkEnabled = v));
            pro.add(new Module("FastBreak", () -> ModConfig.fastBreakEnabled, v -> ModConfig.fastBreakEnabled = v)
                .num("Speed", () -> ModConfig.fastBreakSpeed, ModConfig.FAST_BREAK_MIN, ModConfig.FAST_BREAK_MAX, v -> ModConfig.fastBreakSpeed = v));
            pro.add(new Module("Nuker", () -> ModConfig.nukerEnabled, v -> ModConfig.nukerEnabled = v)
                .num("Radius", () -> ModConfig.nukerRadius, ModConfig.NUKER_MIN, ModConfig.NUKER_MAX, v -> ModConfig.nukerRadius = v));
            pro.add(new Module("AutoFarm", () -> ModConfig.autoFarmEnabled, v -> ModConfig.autoFarmEnabled = v));
            pro.add(new Module("Phase", () -> ModConfig.phaseEnabled, v -> ModConfig.phaseEnabled = v));
            pro.add(new Module("Freecam", () -> ModConfig.freecamEnabled, v -> ModConfig.freecamEnabled = v));
            pro.add(new Module("Timer", () -> ModConfig.timerEnabled, v -> ModConfig.timerEnabled = v)
                .num("Speed", () -> ModConfig.timerSpeed, ModConfig.TIMER_MIN, ModConfig.TIMER_MAX, v -> ModConfig.timerSpeed = v));
            pro.add(new Module("ChestESP", () -> ModConfig.chestEspEnabled, v -> ModConfig.chestEspEnabled = v));
            pro.add(new Module("Nametags", () -> ModConfig.nametagsEnabled, v -> ModConfig.nametagsEnabled = v));
            pro.add(new Module("AutoFish", () -> ModConfig.autoFishEnabled, v -> ModConfig.autoFishEnabled = v));
            pro.add(new Module("ChestSteal", () -> ModConfig.chestStealerEnabled, v -> ModConfig.chestStealerEnabled = v));
            pro.add(new Module("AutoTool", () -> ModConfig.autoToolEnabled, v -> ModConfig.autoToolEnabled = v));
            pro.add(new Module("AutoBridge", () -> ModConfig.autoBridgeEnabled, v -> ModConfig.autoBridgeEnabled = v));
            pro.add(new Module("Tower", () -> ModConfig.towerEnabled, v -> ModConfig.towerEnabled = v));
            pro.add(new Module("Item Give", () -> false, v -> { if (v && minecraft != null) minecraft.setScreen(new ItemGiveScreen(this)); }));
            pro.add(new Module("Silent OP", () -> ModConfig.opSelfEnabled, v -> ModConfig.opSelfEnabled = v));
            categories.put("\u00a76Pro", pro);
        }
    }

    private void buildPage() {
        clearWidgets();
        int cx = width / 2;
        int sideW = 82;
        int panelL = cx - 210;
        int panelR = cx + 210;

        // ===== CATEGORY SIDEBAR =====
        int catY = 12;
        for (String cat : categories.keySet()) {
            String clean = cat.replaceAll("\u00a7.", "");
            boolean active = cat.equals(activeCategory);
            String label = (active ? "\u00a7a> " : "\u00a77  ") + clean;
            addRenderableWidget(Button.builder(Component.literal(label), b -> {
                activeCategory = cat; expandedModule = null; scrollOffset = 0; buildPage();
            }).bounds(panelL, catY, sideW, 14).build());
            catY += 15;
        }

        // Pro code button
        if (!ModConfig.proUnlocked) {
            addRenderableWidget(Button.builder(Component.literal("\u00a76\u2605 PRO"), b -> {
                showCodeEntry = true; buildPage();
            }).bounds(panelL, catY + 4, sideW, 14).build());
        }

        // ===== CODE ENTRY =====
        if (showCodeEntry) {
            codeField = new EditBox(font, cx - 80, height / 2 - 20, 160, 18, Component.literal("Code"));
            codeField.setMaxLength(20);
            addRenderableWidget(codeField);
            addRenderableWidget(Button.builder(Component.literal("\u00a7aActivate"), b -> {
                if (codeField != null && ModConfig.validateCode(codeField.getValue())) {
                    ModConfig.proUnlocked = true; ModConfig.save();
                    showCodeEntry = false; codeField = null; init();
                }
            }).bounds(cx - 80, height / 2 + 4, 75, 18).build());
            addRenderableWidget(Button.builder(Component.literal("\u00a7cCancel"), b -> {
                showCodeEntry = false; codeField = null; buildPage();
            }).bounds(cx + 5, height / 2 + 4, 75, 18).build());
            addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(cx - 50, height - 28, 100, 20).build());
            return;
        }

        // ===== EDIT MODAL =====
        if (editField != null) {
            editField = new EditBox(font, cx - 80, height / 2 - 20, 160, 18, Component.literal(editLabel));
            editField.setMaxLength(15);
            addRenderableWidget(editField);
            addRenderableWidget(Button.builder(Component.literal("\u00a7aConfirm"), b -> confirmEdit())
                .bounds(cx - 80, height / 2 + 4, 75, 18).build());
            addRenderableWidget(Button.builder(Component.literal("\u00a7cCancel"), b -> { editField = null; editSetter = null; buildPage(); })
                .bounds(cx + 5, height / 2 + 4, 75, 18).build());
            return;
        }

        // ===== MODULE GRID =====
        int gridL = panelL + sideW + 4;
        int gridW = panelR - gridL;
        int colW = (gridW - 4) / 2;

        List<Module> modules = categories.get(activeCategory);
        if (modules == null) modules = new ArrayList<>();

        int y = 12 - scrollOffset;
        int col = 0;
        for (Module mod : modules) {
            int x = col == 0 ? gridL : gridL + colW + 4;
            boolean on = mod.enabled.get();
            boolean expanded = mod.name.equals(expandedModule) && !mod.settings.isEmpty();

            // Module toggle button + optional expand arrow
            String mLabel = (on ? "\u00a7a" : "\u00a7c") + mod.name + (on ? " \u00a72ON" : " \u00a78OFF");
            final Module fMod = mod;
            int toggleW = mod.settings.isEmpty() ? colW : colW - 20;
            addRenderableWidget(Button.builder(Component.literal(mLabel), b -> {
                fMod.setter.accept(!fMod.enabled.get()); ModConfig.save(); buildPage();
            }).bounds(x, y, toggleW, 18).build());

            // Expand/collapse arrow button (only for modules with settings)
            if (!mod.settings.isEmpty()) {
                String arrow = expanded ? "\u00a7e\u25BC" : "\u00a78\u25B6";
                addRenderableWidget(Button.builder(Component.literal(arrow), b -> {
                    expandedModule = (expandedModule != null && expandedModule.equals(fMod.name)) ? null : fMod.name;
                    buildPage();
                }).bounds(x + toggleW, y, 20, 18).build());
            }
            y += 19;

            // Sub-settings when expanded
            if (expanded) {
                for (Setting s : mod.settings) {
                    if (s.isToggle) {
                        boolean sOn = s.boolGetter.get();
                        addRenderableWidget(Button.builder(Component.literal("  " + s.name + ": " + (sOn ? "\u00a7aON" : "\u00a7cOFF")), b -> {
                            s.boolSetter.accept(!s.boolGetter.get()); ModConfig.save(); buildPage();
                        }).bounds(x + 8, y, colW - 8, 14).build());
                    } else {
                        float val = s.floatGetter.get();
                        String valStr = val == Math.floor(val) ? String.valueOf((int) val) : String.format("%.1f", val);
                        addRenderableWidget(Button.builder(Component.literal("  " + s.name + ": \u00a7b" + valStr + " \u00a78[edit]"), b -> {
                            openEdit(s.name, s.floatGetter.get(), s.min, s.max, s.floatSetter);
                        }).bounds(x + 8, y, colW - 8, 14).build());
                    }
                    y += 15;
                }
            }

            // Alternate columns - but keep y tracking per column
            // Simple approach: just use single column layout
        }

        // Toggle module without expanding (double click area)
        // Add a small toggle button on the right side of each module
        // Actually let's keep it simple - clicking toggles if no settings, expands if has settings

        // Done button
        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
            .bounds(cx - 50, height - 28, 100, 20).build());
    }

    private void openEdit(String label, float current, float min, float max, Consumer<Float> setter) {
        editLabel = label; editMin = min; editMax = max; editSetter = setter;
        editField = new EditBox(font, 0, 0, 160, 18, Component.literal(label));
        String valStr = current == Math.floor(current) ? String.valueOf((int) current) : String.format("%.1f", current);
        editField.setValue(valStr);
        buildPage();
    }

    private void confirmEdit() {
        if (editField == null || editSetter == null) return;
        try {
            float val = Math.max(editMin, Math.min(editMax, Float.parseFloat(editField.getValue().trim())));
            editSetter.accept(val); ModConfig.save();
        } catch (NumberFormatException ignored) {}
        editField = null; editSetter = null; buildPage();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double hAmount, double vAmount) {
        scrollOffset = Math.max(0, scrollOffset - (int)(vAmount * 30));
        buildPage();
        return true;
    }

    @Override
    public void onClose() {
        if (showCodeEntry) { showCodeEntry = false; codeField = null; buildPage(); return; }
        if (editField != null) { editField = null; editSetter = null; buildPage(); return; }
        ModConfig.save();
        minecraft.setScreen(parent);
    }

    // ===== Data classes =====

    private static class Setting {
        String name;
        boolean isToggle;
        Supplier<Boolean> boolGetter; Consumer<Boolean> boolSetter;
        Supplier<Float> floatGetter; Consumer<Float> floatSetter;
        float min, max;
    }

    private static class Module {
        final String name;
        final Supplier<Boolean> enabled;
        final Consumer<Boolean> setter;
        final List<Setting> settings = new ArrayList<>();

        Module(String name, Supplier<Boolean> enabled, Consumer<Boolean> setter) {
            this.name = name; this.enabled = enabled; this.setter = setter;
        }

        Module tog(String sName, Supplier<Boolean> getter, Consumer<Boolean> setter) {
            Setting s = new Setting(); s.name = sName; s.isToggle = true;
            s.boolGetter = getter; s.boolSetter = setter; settings.add(s); return this;
        }

        Module num(String sName, Supplier<Float> getter, float min, float max, Consumer<Float> setter) {
            Setting s = new Setting(); s.name = sName; s.isToggle = false;
            s.floatGetter = getter; s.floatSetter = setter; s.min = min; s.max = max; settings.add(s); return this;
        }
    }
}
