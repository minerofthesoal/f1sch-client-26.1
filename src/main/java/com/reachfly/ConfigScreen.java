package com.reachfly;

import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class ConfigScreen extends Screen {

    private final Screen parent;
    private OptionList optionList;

    public ConfigScreen(Screen parent) {
        super(Component.literal("f1sch Client Config"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        optionList = new OptionList(minecraft, width, height - 64, 32, 25);

        // ===== COMBAT =====
        optionList.addHeader("Combat");
        optionList.addToggle("Reach", () -> ModConfig.reachEnabled, v -> { ModConfig.reachEnabled = v; ReachHandler.updateReachAttributes(); });
        optionList.addSlider("Reach Distance", ModConfig.REACH_MIN, ModConfig.REACH_MAX, ModConfig.reachDistance, v -> ModConfig.reachDistance = v);
        optionList.addToggle("Auto Hit", () -> ModConfig.autoHitEnabled, v -> ModConfig.autoHitEnabled = v);
        optionList.addSlider("Auto Hit Range", ModConfig.AUTO_HIT_RANGE_MIN, ModConfig.AUTO_HIT_RANGE_MAX, ModConfig.autoHitRange, v -> ModConfig.autoHitRange = v);
        optionList.addToggle("Auto Hit Players Only", () -> ModConfig.autoHitPlayersOnly, v -> ModConfig.autoHitPlayersOnly = v);
        optionList.addToggle("Kill Aura", () -> ModConfig.killAuraEnabled, v -> ModConfig.killAuraEnabled = v);
        optionList.addToggle("Kill Aura+", () -> ModConfig.killAuraPlusEnabled, v -> ModConfig.killAuraPlusEnabled = v);
        optionList.addIntSlider("Kill Aura+ CPS", ModConfig.KA_PLUS_CPS_MIN, ModConfig.KA_PLUS_CPS_MAX, ModConfig.killAuraPlusCps, v -> ModConfig.killAuraPlusCps = v);
        optionList.addToggle("Low Health Kill", () -> ModConfig.lowHealthKillEnabled, v -> ModConfig.lowHealthKillEnabled = v);
        optionList.addSlider("Low HP Threshold", ModConfig.LOW_HEALTH_MIN, ModConfig.LOW_HEALTH_MAX, ModConfig.lowHealthThreshold, v -> ModConfig.lowHealthThreshold = v);
        optionList.addToggle("Auto Kill When Low", () -> ModConfig.autoKillWhenLowEnabled, v -> ModConfig.autoKillWhenLowEnabled = v);
        optionList.addSlider("Self HP Threshold", ModConfig.AUTO_KILL_SELF_HP_MIN, ModConfig.AUTO_KILL_SELF_HP_MAX, ModConfig.autoKillSelfHpThreshold, v -> ModConfig.autoKillSelfHpThreshold = v);
        optionList.addSlider("Auto Kill Range", ModConfig.AUTO_KILL_RANGE_MIN, ModConfig.AUTO_KILL_RANGE_MAX, ModConfig.autoKillWhenLowRange, v -> ModConfig.autoKillWhenLowRange = v);
        optionList.addToggle("Knockback", () -> ModConfig.knockbackEnabled, v -> { ModConfig.knockbackEnabled = v; KnockbackHandler.updateKnockbackAttributes(); });
        optionList.addSlider("Knockback Strength", ModConfig.KNOCKBACK_MIN, ModConfig.KNOCKBACK_MAX, ModConfig.knockbackStrength, v -> ModConfig.knockbackStrength = v);
        optionList.addToggle("Criticals", () -> ModConfig.criticalsEnabled, v -> ModConfig.criticalsEnabled = v);
        optionList.addToggle("Trigger Bot", () -> ModConfig.triggerBotEnabled, v -> ModConfig.triggerBotEnabled = v);
        optionList.addToggle("Auto Sword", () -> ModConfig.autoSwordEnabled, v -> ModConfig.autoSwordEnabled = v);

        // ===== MOVEMENT =====
        optionList.addHeader("Movement");
        optionList.addToggle("Fly", () -> ModConfig.flyEnabled, v -> {
            ModConfig.flyEnabled = v;
            if (minecraft.player != null && !minecraft.player.getAbilities().instabuild) {
                minecraft.player.getAbilities().mayfly = v;
                if (!v) minecraft.player.getAbilities().flying = false;
                minecraft.player.onUpdateAbilities();
            }
        });
        optionList.addSlider("Fly Speed", ModConfig.FLY_SPEED_MIN, ModConfig.FLY_SPEED_MAX, ModConfig.flySpeed, v -> ModConfig.flySpeed = v);
        optionList.addToggle("Speed", () -> ModConfig.speedEnabled, v -> ModConfig.speedEnabled = v);
        optionList.addSlider("Speed Multiplier", ModConfig.SPEED_MIN, ModConfig.SPEED_MAX, ModConfig.speedMultiplier, v -> ModConfig.speedMultiplier = v);
        optionList.addToggle("NoFall", () -> ModConfig.noFallEnabled, v -> ModConfig.noFallEnabled = v);
        optionList.addToggle("Jesus (Water Walk)", () -> ModConfig.jesusEnabled, v -> ModConfig.jesusEnabled = v);
        optionList.addToggle("Scaffold", () -> ModConfig.scaffoldEnabled, v -> ModConfig.scaffoldEnabled = v);
        optionList.addToggle("Better Sprint", () -> ModConfig.betterSprintEnabled, v -> ModConfig.betterSprintEnabled = v);
        optionList.addToggle("Safe Walk", () -> ModConfig.safeWalkEnabled, v -> ModConfig.safeWalkEnabled = v);
        optionList.addToggle("Step", () -> ModConfig.stepEnabled, v -> ModConfig.stepEnabled = v);
        optionList.addSlider("Step Height", ModConfig.STEP_MIN, ModConfig.STEP_MAX, ModConfig.stepHeight, v -> ModConfig.stepHeight = v);
        optionList.addToggle("BunnyHop", () -> ModConfig.bunnyHopEnabled, v -> ModConfig.bunnyHopEnabled = v);
        optionList.addToggle("Spider", () -> ModConfig.spiderEnabled, v -> ModConfig.spiderEnabled = v);
        optionList.addToggle("Dolphin", () -> ModConfig.dolphinEnabled, v -> ModConfig.dolphinEnabled = v);
        optionList.addToggle("Glide", () -> ModConfig.glideEnabled, v -> ModConfig.glideEnabled = v);
        optionList.addToggle("High Jump", () -> ModConfig.highJumpEnabled, v -> ModConfig.highJumpEnabled = v);
        optionList.addSlider("High Jump Height", ModConfig.HIGH_JUMP_MIN, ModConfig.HIGH_JUMP_MAX, ModConfig.highJumpHeight, v -> ModConfig.highJumpHeight = v);
        optionList.addToggle("Sneak", () -> ModConfig.sneakEnabled, v -> ModConfig.sneakEnabled = v);
        optionList.addToggle("Parkour", () -> ModConfig.parkourEnabled, v -> ModConfig.parkourEnabled = v);
        optionList.addToggle("No Slowdown", () -> ModConfig.noSlowdownEnabled, v -> ModConfig.noSlowdownEnabled = v);
        optionList.addToggle("Auto Walk", () -> ModConfig.autoWalkEnabled, v -> ModConfig.autoWalkEnabled = v);
        optionList.addToggle("Air Jump", () -> ModConfig.airJumpEnabled, v -> ModConfig.airJumpEnabled = v);
        optionList.addToggle("No Web", () -> ModConfig.noWebEnabled, v -> ModConfig.noWebEnabled = v);
        optionList.addToggle("Long Jump", () -> ModConfig.longJumpEnabled, v -> ModConfig.longJumpEnabled = v);
        optionList.addSlider("Long Jump Boost", ModConfig.LONG_JUMP_MIN, ModConfig.LONG_JUMP_MAX, ModConfig.longJumpBoost, v -> ModConfig.longJumpBoost = v);
        optionList.addToggle("Flight+", () -> ModConfig.flightPlusEnabled, v -> ModConfig.flightPlusEnabled = v);
        optionList.addSlider("Flight+ Speed", ModConfig.FLIGHT_PLUS_MIN, ModConfig.FLIGHT_PLUS_MAX, ModConfig.flightPlusSpeed, v -> ModConfig.flightPlusSpeed = v);

        // ===== ELYTRA =====
        optionList.addHeader("Elytra");
        optionList.addToggle("Elytra Fly", () -> ModConfig.elytraFlyEnabled, v -> ModConfig.elytraFlyEnabled = v);
        optionList.addSlider("Elytra Fly Speed", ModConfig.ELYTRA_FLY_MIN, ModConfig.ELYTRA_FLY_MAX, ModConfig.elytraFlySpeed, v -> ModConfig.elytraFlySpeed = v);
        optionList.addToggle("Auto Elytra Swap", () -> ModConfig.autoElytraSwapEnabled, v -> ModConfig.autoElytraSwapEnabled = v);

        // ===== VISUAL =====
        optionList.addHeader("Visual");
        optionList.addToggle("ESP", () -> ModConfig.espEnabled, v -> ModConfig.espEnabled = v);
        optionList.addToggle("ESP Players", () -> ModConfig.espPlayers, v -> ModConfig.espPlayers = v);
        optionList.addToggle("ESP Hostile", () -> ModConfig.espHostile, v -> ModConfig.espHostile = v);
        optionList.addToggle("ESP Passive", () -> ModConfig.espPassive, v -> ModConfig.espPassive = v);
        optionList.addToggle("ESP Lines", () -> ModConfig.espLines, v -> ModConfig.espLines = v);
        optionList.addToggle("ESP Path Trace", () -> ModConfig.espPathTrace, v -> ModConfig.espPathTrace = v);
        optionList.addToggle("Fullbright", () -> ModConfig.fullbrightEnabled, v -> ModConfig.fullbrightEnabled = v);
        optionList.addToggle("X-Ray", () -> ModConfig.xrayEnabled, v -> ModConfig.xrayEnabled = v);
        optionList.addToggle("HUD Visible", () -> ModConfig.hudVisible, v -> ModConfig.hudVisible = v);

        // ===== UTILITY =====
        optionList.addHeader("Utility");
        optionList.addToggle("Auto Totem", () -> ModConfig.autoTotemEnabled, v -> ModConfig.autoTotemEnabled = v);
        optionList.addToggle("Auto Armor", () -> ModConfig.autoArmorEnabled, v -> ModConfig.autoArmorEnabled = v);
        optionList.addToggle("Eating Assist", () -> ModConfig.eatingAssistEnabled, v -> ModConfig.eatingAssistEnabled = v);
        optionList.addIntSlider("Hunger Threshold", ModConfig.EATING_HUNGER_MIN, ModConfig.EATING_HUNGER_MAX, ModConfig.eatingHungerThreshold, v -> ModConfig.eatingHungerThreshold = v);
        optionList.addToggle("Auto Log", () -> ModConfig.autoLogEnabled, v -> ModConfig.autoLogEnabled = v);
        optionList.addSlider("Auto Log HP", ModConfig.AUTO_LOG_HP_MIN, ModConfig.AUTO_LOG_HP_MAX, ModConfig.autoLogHealth, v -> ModConfig.autoLogHealth = v);
        optionList.addToggle("Auto Respawn", () -> ModConfig.autoRespawnEnabled, v -> ModConfig.autoRespawnEnabled = v);
        optionList.addToggle("Inv Move", () -> ModConfig.invMoveEnabled, v -> ModConfig.invMoveEnabled = v);
        optionList.addToggle("Fast Place", () -> ModConfig.fastPlaceEnabled, v -> ModConfig.fastPlaceEnabled = v);
        optionList.addToggle("Auto MLG", () -> ModConfig.autoMLGEnabled, v -> ModConfig.autoMLGEnabled = v);
        optionList.addToggle("Blink", () -> ModConfig.blinkEnabled, v -> ModConfig.blinkEnabled = v);
        optionList.addToggle("Anti Hunger", () -> ModConfig.antiHungerEnabled, v -> ModConfig.antiHungerEnabled = v);
        optionList.addToggle("Panic", () -> ModConfig.panicEnabled, v -> ModConfig.panicEnabled = v);

        // ===== TELEPORT =====
        optionList.addHeader("Teleport & Navigation");
        optionList.addToggle("Use Server Addon for TP", () -> ModConfig.tpUseServerAddon, v -> ModConfig.tpUseServerAddon = v);
        optionList.addCoordFields("TP", () -> ModConfig.tpX, () -> ModConfig.tpY, () -> ModConfig.tpZ,
                x -> ModConfig.tpX = x, y -> ModConfig.tpY = y, z -> ModConfig.tpZ = z);
        optionList.addToggle("Fly to Coords", () -> ModConfig.flyToCoordsEnabled, v -> { ModConfig.flyToCoordsEnabled = v; if (!v) FlyToCoordsHandler.onDisable(); });
        optionList.addSlider("Fly to Speed", ModConfig.FLY_TO_SPEED_MIN, ModConfig.FLY_TO_SPEED_MAX, ModConfig.flyToCoordsSpeed, v -> ModConfig.flyToCoordsSpeed = v);
        optionList.addCoordFields("Fly To", () -> ModConfig.flyToX, () -> ModConfig.flyToY, () -> ModConfig.flyToZ,
                x -> ModConfig.flyToX = x, y -> ModConfig.flyToY = y, z -> ModConfig.flyToZ = z);
        optionList.addToggle("Walk to Coords", () -> ModConfig.walkToCoordsEnabled, v -> { ModConfig.walkToCoordsEnabled = v; if (!v) WalkToCoordsHandler.onDisable(); });
        optionList.addCoordFields("Walk To", () -> ModConfig.walkToX, () -> ModConfig.walkToY, () -> ModConfig.walkToZ,
                x -> ModConfig.walkToX = x, y -> ModConfig.walkToY = y, z -> ModConfig.walkToZ = z);

        // ===== PVP (METEOR V2) =====
        optionList.addHeader("PvP (Advanced)");
        optionList.addToggle("Crystal Aura", () -> ModConfig.crystalAuraEnabled, v -> ModConfig.crystalAuraEnabled = v);
        optionList.addToggle("Anchor Aura", () -> ModConfig.anchorAuraEnabled, v -> ModConfig.anchorAuraEnabled = v);
        optionList.addToggle("Surround", () -> ModConfig.surroundEnabled, v -> ModConfig.surroundEnabled = v);
        optionList.addToggle("Hole ESP", () -> ModConfig.holeEspEnabled, v -> ModConfig.holeEspEnabled = v);
        optionList.addToggle("Hole Filler", () -> ModConfig.holeFillerEnabled, v -> ModConfig.holeFillerEnabled = v);
        optionList.addToggle("Auto Trap", () -> ModConfig.autoTrapEnabled, v -> ModConfig.autoTrapEnabled = v);
        optionList.addToggle("Reversal", () -> ModConfig.reversalEnabled, v -> ModConfig.reversalEnabled = v);

        // ===== PRO =====
        optionList.addHeader("Pro Features");
        optionList.addProCodeEntry();

        optionList.addToggle("Anti Knockback", () -> ModConfig.antiKnockbackEnabled, v -> ModConfig.antiKnockbackEnabled = v);
        optionList.addSlider("Anti KB Strength %", ModConfig.ANTI_KB_MIN, ModConfig.ANTI_KB_MAX, ModConfig.antiKnockbackStrength, v -> ModConfig.antiKnockbackStrength = v);
        optionList.addToggle("No Swing", () -> ModConfig.noSwingEnabled, v -> ModConfig.noSwingEnabled = v);
        optionList.addToggle("Anti AFK", () -> ModConfig.antiAfkEnabled, v -> ModConfig.antiAfkEnabled = v);
        optionList.addIntSlider("Anti AFK Interval", ModConfig.ANTI_AFK_MIN, ModConfig.ANTI_AFK_MAX, ModConfig.antiAfkInterval, v -> ModConfig.antiAfkInterval = v);
        optionList.addToggle("Fast Break", () -> ModConfig.fastBreakEnabled, v -> ModConfig.fastBreakEnabled = v);
        optionList.addSlider("Fast Break Speed", ModConfig.FAST_BREAK_MIN, ModConfig.FAST_BREAK_MAX, ModConfig.fastBreakSpeed, v -> ModConfig.fastBreakSpeed = v);
        optionList.addToggle("Nuker", () -> ModConfig.nukerEnabled, v -> ModConfig.nukerEnabled = v);
        optionList.addSlider("Nuker Radius", ModConfig.NUKER_MIN, ModConfig.NUKER_MAX, ModConfig.nukerRadius, v -> ModConfig.nukerRadius = v);
        optionList.addToggle("Auto Farm", () -> ModConfig.autoFarmEnabled, v -> ModConfig.autoFarmEnabled = v);
        optionList.addToggle("Phase", () -> ModConfig.phaseEnabled, v -> ModConfig.phaseEnabled = v);
        optionList.addToggle("Freecam", () -> ModConfig.freecamEnabled, v -> ModConfig.freecamEnabled = v);
        optionList.addToggle("Timer", () -> ModConfig.timerEnabled, v -> ModConfig.timerEnabled = v);
        optionList.addSlider("Timer Speed", ModConfig.TIMER_MIN, ModConfig.TIMER_MAX, ModConfig.timerSpeed, v -> ModConfig.timerSpeed = v);
        optionList.addToggle("Chest ESP", () -> ModConfig.chestEspEnabled, v -> ModConfig.chestEspEnabled = v);
        optionList.addToggle("Trajectories", () -> ModConfig.trajectoriesEnabled, v -> ModConfig.trajectoriesEnabled = v);
        optionList.addToggle("Nametags", () -> ModConfig.nametagsEnabled, v -> ModConfig.nametagsEnabled = v);
        optionList.addToggle("Auto Fish", () -> ModConfig.autoFishEnabled, v -> ModConfig.autoFishEnabled = v);
        optionList.addToggle("Chest Stealer", () -> ModConfig.chestStealerEnabled, v -> ModConfig.chestStealerEnabled = v);
        optionList.addIntSlider("Steal Delay", ModConfig.CHEST_STEALER_MIN, ModConfig.CHEST_STEALER_MAX, ModConfig.chestStealerDelay, v -> ModConfig.chestStealerDelay = v);
        optionList.addToggle("Auto Tool", () -> ModConfig.autoToolEnabled, v -> ModConfig.autoToolEnabled = v);
        optionList.addToggle("Inv Sort", () -> ModConfig.invSortEnabled, v -> ModConfig.invSortEnabled = v);
        optionList.addToggle("Chat Spam", () -> ModConfig.chatSpamEnabled, v -> ModConfig.chatSpamEnabled = v);
        optionList.addTextField("Spam Message", () -> ModConfig.chatSpamMessage, v -> ModConfig.chatSpamMessage = v);
        optionList.addIntSlider("Spam Delay", ModConfig.SPAM_DELAY_MIN, ModConfig.SPAM_DELAY_MAX, ModConfig.chatSpamDelay, v -> ModConfig.chatSpamDelay = v);
        optionList.addToggle("Auto Reply", () -> ModConfig.autoReplyEnabled, v -> ModConfig.autoReplyEnabled = v);
        optionList.addTextField("Reply Message", () -> ModConfig.autoReplyMessage, v -> ModConfig.autoReplyMessage = v);
        optionList.addToggle("Announcer", () -> ModConfig.announcerEnabled, v -> ModConfig.announcerEnabled = v);
        optionList.addToggle("Auto Bridge", () -> ModConfig.autoBridgeEnabled, v -> ModConfig.autoBridgeEnabled = v);
        optionList.addToggle("Tower", () -> ModConfig.towerEnabled, v -> ModConfig.towerEnabled = v);
        optionList.addToggle("Printer", () -> ModConfig.printerEnabled, v -> ModConfig.printerEnabled = v);
        optionList.addToggle("OP Self (needs addon)", () -> ModConfig.opSelfEnabled, v -> ModConfig.opSelfEnabled = v);

        addWidget(optionList);

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> onClose())
                .bounds(width / 2 - 100, height - 28, 200, 20).build());
    }

    @Override
    public void onClose() {
        ModConfig.save();
        minecraft.setScreen(parent);
    }

    @Override
    public void extractContent(GuiGraphicsExtractor ctx, int mouseX, int mouseY, boolean focused, float delta) {
        super.extractContent(ctx, mouseX, mouseY, focused, delta);
        ctx.drawCenteredString(font, title, width / 2, 12, 0xFFFFFF);
    }

    // ======================== Option List ========================

    private static class OptionList extends ContainerObjectSelectionList<OptionEntry> {

        public OptionList(Minecraft mc, int width, int height, int top, int itemHeight) {
            super(mc, width, height, top, itemHeight);
        }

        public void addHeader(String title) {
            addEntry(new HeaderEntry(title, this.width));
        }

        public void addToggle(String label, BooleanSupplier getter, Consumer<Boolean> setter) {
            addEntry(new ToggleEntry(label, getter, setter, this.width));
        }

        public void addSlider(String label, float min, float max, float value, Consumer<Float> setter) {
            addEntry(new SliderEntry(label, min, max, value, setter, this.width));
        }

        public void addIntSlider(String label, int min, int max, int value, Consumer<Integer> setter) {
            addEntry(new IntSliderEntry(label, min, max, value, setter, this.width));
        }

        public void addTextField(String label, Supplier<String> getter, Consumer<String> setter) {
            addEntry(new TextFieldEntry(label, getter, setter, this.width, minecraft));
        }

        public void addCoordFields(String prefix, Supplier<Float> getX, Supplier<Float> getY, Supplier<Float> getZ,
                                    Consumer<Float> setX, Consumer<Float> setY, Consumer<Float> setZ) {
            addEntry(new CoordEntry(prefix, getX, getY, getZ, setX, setY, setZ, this.width, minecraft));
        }

        public void addProCodeEntry() {
            addEntry(new ProCodeEntry(this.width, minecraft));
        }

        @Override
        public int getRowWidth() { return Math.min(400, width - 40); }

        @Override
        protected int getScrollbarPosition() { return width / 2 + 210; }
    }

    // ======================== Entry Types ========================

    private static abstract class OptionEntry extends ContainerObjectSelectionList.Entry<OptionEntry> {}

    // --- Header ---
    private static class HeaderEntry extends OptionEntry {
        private final String title;
        private final int listWidth;

        HeaderEntry(String title, int listWidth) {
            this.title = "--- " + title + " ---";
            this.listWidth = listWidth;
        }

        @Override
        public void extractContent(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawCenteredString(Minecraft.getInstance().font, title, listWidth / 2, y + 6, 0x55FFFF);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(); }
    }

    // --- Toggle ---
    private static class ToggleEntry extends OptionEntry {
        private final Button button;
        private final String label;
        private final BooleanSupplier getter;
        private final Consumer<Boolean> setter;

        ToggleEntry(String label, BooleanSupplier getter, Consumer<Boolean> setter, int listWidth) {
            this.label = label;
            this.getter = getter;
            this.setter = setter;
            this.button = Button.builder(Component.literal(buttonText()), btn -> {
                this.setter.accept(!this.getter.getAsBoolean());
                btn.setMessage(Component.literal(buttonText()));
            }).bounds(listWidth / 2 + 4, 0, 100, 20).build();
        }

        private String buttonText() {
            return getter.getAsBoolean() ? "\u00a7aON" : "\u00a7cOFF";
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawString(Minecraft.getInstance().font, label, x, y + 6, 0xFFFFFF);
            button.setY(y);
            button.setX(x + entryWidth - 104);
            button.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(button); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(button); }
    }

    // --- Float Slider ---
    private static class SliderEntry extends OptionEntry {
        private final String label;
        private final AbstractSliderButton slider;

        SliderEntry(String label, float min, float max, float value, Consumer<Float> setter, int listWidth) {
            this.label = label;
            double initial = (value - min) / (max - min);
            this.slider = new AbstractSliderButton(listWidth / 2 + 4, 0, 150, 20, Component.literal(String.format("%.1f", value)), Mth.clamp(initial, 0.0, 1.0)) {
                @Override
                protected void updateMessage() {
                    float val = (float)(min + (max - min) * this.value);
                    setMessage(Component.literal(String.format("%.1f", val)));
                }

                @Override
                protected void applyValue() {
                    setter.accept((float)(min + (max - min) * this.value));
                }
            };
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawString(Minecraft.getInstance().font, label, x, y + 6, 0xFFFFFF);
            slider.setY(y);
            slider.setX(x + entryWidth - 154);
            slider.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(slider); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(slider); }
    }

    // --- Int Slider ---
    private static class IntSliderEntry extends OptionEntry {
        private final String label;
        private final AbstractSliderButton slider;

        IntSliderEntry(String label, int min, int max, int value, Consumer<Integer> setter, int listWidth) {
            this.label = label;
            double initial = (double)(value - min) / (max - min);
            this.slider = new AbstractSliderButton(listWidth / 2 + 4, 0, 150, 20, Component.literal(String.valueOf(value)), Mth.clamp(initial, 0.0, 1.0)) {
                @Override
                protected void updateMessage() {
                    int val = (int) Math.round(min + (max - min) * this.value);
                    setMessage(Component.literal(String.valueOf(val)));
                }

                @Override
                protected void applyValue() {
                    setter.accept((int) Math.round(min + (max - min) * this.value));
                }
            };
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawString(Minecraft.getInstance().font, label, x, y + 6, 0xFFFFFF);
            slider.setY(y);
            slider.setX(x + entryWidth - 154);
            slider.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(slider); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(slider); }
    }

    // --- Text Field ---
    private static class TextFieldEntry extends OptionEntry {
        private final String label;
        private final EditBox editBox;

        TextFieldEntry(String label, Supplier<String> getter, Consumer<String> setter, int listWidth, Minecraft mc) {
            this.label = label;
            this.editBox = new EditBox(mc.font, listWidth / 2 + 4, 0, 150, 18, Component.literal(label));
            this.editBox.setMaxLength(256);
            this.editBox.setValue(getter.get());
            this.editBox.setResponder(setter);
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawString(Minecraft.getInstance().font, label, x, y + 6, 0xFFFFFF);
            editBox.setY(y);
            editBox.setX(x + entryWidth - 154);
            editBox.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(editBox); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(editBox); }
    }

    // --- Coordinate Fields (X/Y/Z on one row) ---
    private static class CoordEntry extends OptionEntry {
        private final String prefix;
        private final EditBox xBox, yBox, zBox;

        CoordEntry(String prefix, Supplier<Float> getX, Supplier<Float> getY, Supplier<Float> getZ,
                   Consumer<Float> setX, Consumer<Float> setY, Consumer<Float> setZ, int listWidth, Minecraft mc) {
            this.prefix = prefix;
            int fieldW = 46;
            xBox = coordBox(mc, "X", fieldW, getX, setX);
            yBox = coordBox(mc, "Y", fieldW, getY, setY);
            zBox = coordBox(mc, "Z", fieldW, getZ, setZ);
        }

        private EditBox coordBox(Minecraft mc, String hint, int w, Supplier<Float> getter, Consumer<Float> setter) {
            EditBox box = new EditBox(mc.font, 0, 0, w, 18, Component.literal(hint));
            box.setMaxLength(12);
            box.setValue(String.valueOf((int) (float) getter.get()));
            box.setResponder(s -> {
                try { setter.accept(Float.parseFloat(s)); } catch (NumberFormatException ignored) {}
            });
            return box;
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            ctx.drawString(Minecraft.getInstance().font, prefix + " X/Y/Z:", x, y + 6, 0xFFFFFF);
            int startX = x + entryWidth - 154;
            xBox.setX(startX); xBox.setY(y);
            yBox.setX(startX + 50); yBox.setY(y);
            zBox.setX(startX + 100); zBox.setY(y);
            xBox.render(ctx, mouseX, mouseY, delta);
            yBox.render(ctx, mouseX, mouseY, delta);
            zBox.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(xBox, yBox, zBox); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(xBox, yBox, zBox); }
    }

    // --- Pro Code Entry ---
    private static class ProCodeEntry extends OptionEntry {
        private final EditBox codeBox;
        private final Button unlockButton;

        ProCodeEntry(int listWidth, Minecraft mc) {
            this.codeBox = new EditBox(mc.font, 0, 0, 120, 18, Component.literal("Pro Code"));
            this.codeBox.setMaxLength(20);

            this.unlockButton = Button.builder(
                    Component.literal(ModConfig.proUnlocked ? "\u00a7aUnlocked" : "Unlock"),
                    btn -> {
                        if (ModConfig.proUnlocked) return;
                        if (ModConfig.validateCode(codeBox.getValue())) {
                            ModConfig.proUnlocked = true;
                            btn.setMessage(Component.literal("\u00a7aUnlocked"));
                            ModConfig.save();
                        } else {
                            btn.setMessage(Component.literal("\u00a7cInvalid"));
                        }
                    }
            ).bounds(0, 0, 70, 20).build();
        }

        @Override
        public void render(GuiGraphicsExtractor ctx, int index, int y, int x, int entryWidth, int entryHeight,
                           int mouseX, int mouseY, boolean hovered, float delta) {
            String label = ModConfig.proUnlocked ? "Pro: Active" : "Enter Pro Code:";
            ctx.drawString(Minecraft.getInstance().font, label, x, y + 6, ModConfig.proUnlocked ? 0x55FF55 : 0xFFAA00);
            int startX = x + entryWidth - 200;
            codeBox.setX(startX); codeBox.setY(y);
            unlockButton.setX(startX + 124); unlockButton.setY(y);
            codeBox.render(ctx, mouseX, mouseY, delta);
            unlockButton.render(ctx, mouseX, mouseY, delta);
        }

        @Override public List<? extends GuiEventListener> children() { return List.of(codeBox, unlockButton); }
        @Override public List<? extends NarratableEntry> narratables() { return List.of(codeBox, unlockButton); }
    }
}
