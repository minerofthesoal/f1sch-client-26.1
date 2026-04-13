package com.reachfly;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

/**
 * Simple paged config screen. Uses addRenderableWidget for auto-rendering
 * (avoids broken ContainerObjectSelectionList.Entry.extractContent API).
 */
public class ConfigScreen extends Screen {

    private final Screen parent;
    private int page = 0;
    private static final int ROWS = 10;
    private static final int COL_W = 200;
    private final List<OptionDef> options = new ArrayList<>();

    public ConfigScreen(Screen parent) {
        super(Component.literal("f1sch Client Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        options.clear();
        buildOptions();
        rebuildWidgets();
    }

    private void buildOptions() {
        // Combat
        tog("Reach", () -> ModConfig.reachEnabled, v -> { ModConfig.reachEnabled = v; ReachHandler.updateReachAttributes(); });
        sld("Reach Dist", ModConfig.REACH_MIN, ModConfig.REACH_MAX, ModConfig.reachDistance, v -> ModConfig.reachDistance = v);
        tog("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        tog("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v);
        tog("Kill Aura+", () -> ModConfig.killAuraPlusEnabled, v -> ModConfig.killAuraPlusEnabled = v);
        tog("Knockback", () -> ModConfig.knockbackEnabled, v -> { ModConfig.knockbackEnabled = v; KnockbackHandler.updateKnockbackAttributes(); });
        sld("KB Strength", ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, ModConfig.knockbackStrength, v -> ModConfig.knockbackStrength = v);
        tog("Criticals", () -> ModConfig.criticalsEnabled, v -> ModConfig.criticalsEnabled = v);
        tog("Trigger Bot", () -> ModConfig.triggerBotEnabled, v -> ModConfig.triggerBotEnabled = v);
        tog("Auto Sword", () -> ModConfig.autoSwordEnabled, v -> ModConfig.autoSwordEnabled = v);

        // Movement
        tog("Fly", () -> ModConfig.flyEnabled, v -> {
            ModConfig.flyEnabled = v;
            if (minecraft.player != null && !minecraft.player.getAbilities().instabuild) {
                minecraft.player.getAbilities().mayfly = v;
                if (!v) minecraft.player.getAbilities().flying = false;
                minecraft.player.onUpdateAbilities();
            }
        });
        sld("Fly Speed", ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, ModConfig.flySpeed, v -> ModConfig.flySpeed = v);
        tog("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v);
        sld("Speed Mult", ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, ModConfig.speedMultiplier, v -> ModConfig.speedMultiplier = v);
        tog("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v);
        tog("Jesus", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v);
        tog("Scaffold", () -> ModConfig.scaffoldEnabled, v -> ModConfig.scaffoldEnabled = v);
        tog("BunnyHop", () -> ModConfig.bunnyHopEnabled, v -> ModConfig.bunnyHopEnabled = v);
        tog("Spider", () -> ModConfig.spiderEnabled, v -> ModConfig.spiderEnabled = v);
        tog("HighJump", () -> ModConfig.highJumpEnabled, v -> ModConfig.highJumpEnabled = v);
        tog("Dolphin", () -> ModConfig.dolphinEnabled, v -> ModConfig.dolphinEnabled = v);
        tog("Parkour", () -> ModConfig.parkourEnabled, v -> ModConfig.parkourEnabled = v);
        tog("Flight+", () -> ModConfig.flightPlusEnabled, v -> ModConfig.flightPlusEnabled = v);
        tog("LongJump", () -> ModConfig.longJumpEnabled, v -> ModConfig.longJumpEnabled = v);
        tog("AirJump", () -> ModConfig.airJumpEnabled, v -> ModConfig.airJumpEnabled = v);
        tog("NoWeb", () -> ModConfig.noWebEnabled, v -> ModConfig.noWebEnabled = v);
        tog("NoSlowdown", () -> ModConfig.noSlowdownEnabled, v -> ModConfig.noSlowdownEnabled = v);
        tog("AutoWalk", () -> ModConfig.autoWalkEnabled, v -> ModConfig.autoWalkEnabled = v);
        tog("BetterSprint", () -> ModConfig.betterSprintEnabled, v -> ModConfig.betterSprintEnabled = v);
        tog("SafeWalk", () -> ModConfig.safeWalkEnabled, v -> ModConfig.safeWalkEnabled = v);
        tog("Step", () -> ModConfig.stepEnabled, v -> ModConfig.stepEnabled = v);

        // Visual
        tog("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v);
        tog("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v);
        tog("X-Ray", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v);
        tog("HUD", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v);

        // Utility
        tog("AutoTotem", () -> ModConfig.autoTotemEnabled, v -> ModConfig.autoTotemEnabled = v);
        tog("AutoArmor", () -> ModConfig.autoArmorEnabled, v -> ModConfig.autoArmorEnabled = v);
        tog("EatingAssist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        tog("AutoElytra", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v);
        tog("AutoMLG", () -> ModConfig.autoMLGEnabled, v -> ModConfig.autoMLGEnabled = v);
        tog("FastPlace", () -> ModConfig.fastPlaceEnabled, v -> ModConfig.fastPlaceEnabled = v);
        tog("InvMove", () -> ModConfig.invMoveEnabled, v -> ModConfig.invMoveEnabled = v);
        tog("Blink", () -> ModConfig.blinkEnabled, v -> ModConfig.blinkEnabled = v);
        tog("Panic", () -> ModConfig.panicEnabled, v -> ModConfig.panicEnabled = v);
        tog("Sneak", () -> ModConfig.sneakEnabled, v -> ModConfig.sneakEnabled = v);
        tog("AntiHunger", () -> ModConfig.antiHungerEnabled, v -> ModConfig.antiHungerEnabled = v);
        tog("AutoLog", () -> ModConfig.autoLogEnabled, v -> ModConfig.autoLogEnabled = v);
        tog("AutoRespawn", () -> ModConfig.autoRespawnEnabled, v -> ModConfig.autoRespawnEnabled = v);
        tog("Glide", () -> ModConfig.glideEnabled, v -> ModConfig.glideEnabled = v);

        // Elytra / PvP
        tog("ElytraFly", () -> ModConfig.elytraFlyEnabled, v -> ModConfig.elytraFlyEnabled = v);
        sld("ElytraSpeed", ModConfig.ELYTRA_FLY_MIN, ModConfig.ELYTRA_FLY_MAX, ModConfig.elytraFlySpeed, v -> ModConfig.elytraFlySpeed = v);
        tog("CrystalAura", () -> ModConfig.crystalAuraEnabled, v -> ModConfig.crystalAuraEnabled = v);
        tog("AnchorAura", () -> ModConfig.anchorAuraEnabled, v -> ModConfig.anchorAuraEnabled = v);
        tog("Surround", () -> ModConfig.surroundEnabled, v -> ModConfig.surroundEnabled = v);
        tog("HoleFiller", () -> ModConfig.holeFillerEnabled, v -> ModConfig.holeFillerEnabled = v);
        tog("AutoTrap", () -> ModConfig.autoTrapEnabled, v -> ModConfig.autoTrapEnabled = v);
        tog("Reversal", () -> ModConfig.reversalEnabled, v -> ModConfig.reversalEnabled = v);

        // Pro
        tog("AntiKB", () -> ModConfig.antiKnockbackEnabled, v -> ModConfig.antiKnockbackEnabled = v);
        tog("NoSwing", () -> ModConfig.noSwingEnabled, v -> ModConfig.noSwingEnabled = v);
        tog("AntiAFK", () -> ModConfig.antiAfkEnabled, v -> ModConfig.antiAfkEnabled = v);
        tog("FastBreak", () -> ModConfig.fastBreakEnabled, v -> ModConfig.fastBreakEnabled = v);
        tog("Nuker", () -> ModConfig.nukerEnabled, v -> ModConfig.nukerEnabled = v);
        tog("AutoFarm", () -> ModConfig.autoFarmEnabled, v -> ModConfig.autoFarmEnabled = v);
        tog("Phase", () -> ModConfig.phaseEnabled, v -> ModConfig.phaseEnabled = v);
        tog("Freecam", () -> ModConfig.freecamEnabled, v -> ModConfig.freecamEnabled = v);
        tog("Timer", () -> ModConfig.timerEnabled, v -> ModConfig.timerEnabled = v);
        tog("ChestESP", () -> ModConfig.chestEspEnabled, v -> ModConfig.chestEspEnabled = v);
        tog("Trajectories", () -> ModConfig.trajectoriesEnabled, v -> ModConfig.trajectoriesEnabled = v);
        tog("Nametags", () -> ModConfig.nametagsEnabled, v -> ModConfig.nametagsEnabled = v);
        tog("AutoFish", () -> ModConfig.autoFishEnabled, v -> ModConfig.autoFishEnabled = v);
        tog("ChestStealer", () -> ModConfig.chestStealerEnabled, v -> ModConfig.chestStealerEnabled = v);
        tog("AutoTool", () -> ModConfig.autoToolEnabled, v -> ModConfig.autoToolEnabled = v);
        tog("InvSort", () -> ModConfig.invSortEnabled, v -> ModConfig.invSortEnabled = v);
        tog("AutoBridge", () -> ModConfig.autoBridgeEnabled, v -> ModConfig.autoBridgeEnabled = v);
        tog("Tower", () -> ModConfig.towerEnabled, v -> ModConfig.towerEnabled = v);
        tog("Baritone", () -> ModConfig.baritoneEnabled, v -> ModConfig.baritoneEnabled = v);
    }

    private void rebuildWidgets() {
        clearWidgets();
        int cx = width / 2;
        int startY = 30;
        int totalPages = (options.size() + ROWS * 2 - 1) / (ROWS * 2);
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;

        int startIdx = page * ROWS * 2;
        for (int i = 0; i < ROWS * 2 && startIdx + i < options.size(); i++) {
            int col = i < ROWS ? 0 : 1;
            int row = i < ROWS ? i : i - ROWS;
            int x = cx - COL_W - 4 + col * (COL_W + 8);
            int y = startY + row * 22;
            OptionDef opt = options.get(startIdx + i);
            if (opt.isSlider) {
                addRenderableWidget(makeSlider(x, y, opt));
            } else {
                addRenderableWidget(makeToggle(x, y, opt));
            }
        }

        // Nav buttons
        String pageLabel = "Page " + (page + 1) + "/" + totalPages;
        if (page > 0)
            addRenderableWidget(Button.builder(Component.literal("<< Prev"), b -> { page--; rebuildWidgets(); })
                    .bounds(cx - 154, height - 28, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal(pageLabel), b -> {})
                .bounds(cx - 40, height - 28, 80, 20).build());
        if (startIdx + ROWS * 2 < options.size())
            addRenderableWidget(Button.builder(Component.literal("Next >>"), b -> { page++; rebuildWidgets(); })
                    .bounds(cx + 84, height - 28, 70, 20).build());

        addRenderableWidget(Button.builder(Component.literal("Done"), b -> onClose())
                .bounds(cx - 40, height - 52, 80, 20).build());
    }

    private Button makeToggle(int x, int y, OptionDef opt) {
        return Button.builder(Component.literal(opt.label + ": " + (opt.getter.getAsBoolean() ? "\u00a7aON" : "\u00a7cOFF")), btn -> {
            opt.setter.accept(!opt.getter.getAsBoolean());
            btn.setMessage(Component.literal(opt.label + ": " + (opt.getter.getAsBoolean() ? "\u00a7aON" : "\u00a7cOFF")));
        }).bounds(x, y, COL_W, 20).build();
    }

    private AbstractSliderButton makeSlider(int x, int y, OptionDef opt) {
        double initial = Mth.clamp((opt.sliderValue - opt.sliderMin) / (opt.sliderMax - opt.sliderMin), 0.0, 1.0);
        return new AbstractSliderButton(x, y, COL_W, 20,
                Component.literal(opt.label + ": " + String.format("%.1f", opt.sliderValue)), initial) {
            @Override protected void updateMessage() {
                float val = (float)(opt.sliderMin + (opt.sliderMax - opt.sliderMin) * this.value);
                setMessage(Component.literal(opt.label + ": " + String.format("%.1f", val)));
            }
            @Override protected void applyValue() {
                opt.sliderSetter.accept((float)(opt.sliderMin + (opt.sliderMax - opt.sliderMin) * this.value));
            }
        };
    }

    @Override
    public void onClose() {
        ModConfig.save();
        minecraft.setScreen(parent);
    }

    // Helpers
    private void tog(String label, BooleanSupplier getter, Consumer<Boolean> setter) {
        options.add(new OptionDef(label, getter, setter));
    }
    private void sld(String label, float min, float max, float value, Consumer<Float> setter) {
        options.add(new OptionDef(label, min, max, value, setter));
    }

    private static class OptionDef {
        final String label;
        final boolean isSlider;
        // Toggle
        BooleanSupplier getter;
        Consumer<Boolean> setter;
        // Slider
        float sliderMin, sliderMax, sliderValue;
        Consumer<Float> sliderSetter;

        OptionDef(String label, BooleanSupplier getter, Consumer<Boolean> setter) {
            this.label = label; this.isSlider = false; this.getter = getter; this.setter = setter;
        }
        OptionDef(String label, float min, float max, float value, Consumer<Float> setter) {
            this.label = label; this.isSlider = true;
            this.sliderMin = min; this.sliderMax = max; this.sliderValue = value; this.sliderSetter = setter;
        }
    }
}
