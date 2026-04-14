package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Item browser screen with search. Click an item to give it via server addon or datapack trigger.
 * TODO: Rendering stubbed for MC 26.1 GuiGraphicsExtractor API migration.
 */
public class ItemGiveScreen extends Screen {

    private final Screen parent;
    private EditBox searchField;
    private List<ItemEntry> allItems;
    private List<ItemEntry> filteredItems;
    private double scrollOffset = 0;
    private String lastQuery = "";
    private String toastMessage = null;
    private int toastTimer = 0;

    private static final int ITEM_SIZE = 20;
    private static final int GRID_PAD = 2;
    private static final int CELL = ITEM_SIZE + GRID_PAD;
    private static final int SPECIAL_START = 1481;

    private static final String[][] POTION_EFFECTS = {
        {"awkward", "Awkward"}, {"fire_resistance", "Fire Resistance"}, {"harming", "Harming"},
        {"healing", "Healing"}, {"infested", "Infested"}, {"invisibility", "Invisibility"},
        {"leaping", "Leaping"}, {"long_fire_resistance", "Fire Resistance (Long)"},
        {"long_invisibility", "Invisibility (Long)"}, {"long_leaping", "Leaping (Long)"},
        {"long_night_vision", "Night Vision (Long)"}, {"long_poison", "Poison (Long)"},
        {"long_regeneration", "Regeneration (Long)"}, {"long_slow_falling", "Slow Falling (Long)"},
        {"long_slowness", "Slowness (Long)"}, {"long_strength", "Strength (Long)"},
        {"long_swiftness", "Swiftness (Long)"}, {"long_turtle_master", "Turtle Master (Long)"},
        {"long_water_breathing", "Water Breathing (Long)"}, {"long_weakness", "Weakness (Long)"},
        {"luck", "Luck"}, {"mundane", "Mundane"}, {"night_vision", "Night Vision"},
        {"oozing", "Oozing"}, {"poison", "Poison"}, {"regeneration", "Regeneration"},
        {"slow_falling", "Slow Falling"}, {"slowness", "Slowness"}, {"strength", "Strength"},
        {"strong_harming", "Harming II"}, {"strong_healing", "Healing II"},
        {"strong_leaping", "Leaping II"}, {"strong_poison", "Poison II"},
        {"strong_regeneration", "Regeneration II"}, {"strong_slowness", "Slowness IV"},
        {"strong_strength", "Strength II"}, {"strong_swiftness", "Swiftness II"},
        {"strong_turtle_master", "Turtle Master II"}, {"swiftness", "Swiftness"},
        {"thick", "Thick"}, {"turtle_master", "Turtle Master"}, {"water", "Water Bottle"},
        {"water_breathing", "Water Breathing"}, {"weakness", "Weakness"},
        {"weaving", "Weaving"}, {"wind_charged", "Wind Charged"},
    };

    private static final Object[][] ENCHANTMENTS = {
        {"aqua_affinity", 1, "Aqua Affinity"}, {"bane_of_arthropods", 5, "Bane of Arthropods V"},
        {"binding_curse", 1, "Curse of Binding"}, {"blast_protection", 4, "Blast Protection IV"},
        {"breach", 4, "Breach IV"}, {"channeling", 1, "Channeling"},
        {"density", 5, "Density V"}, {"depth_strider", 3, "Depth Strider III"},
        {"efficiency", 5, "Efficiency V"}, {"feather_falling", 4, "Feather Falling IV"},
        {"fire_aspect", 2, "Fire Aspect II"}, {"fire_protection", 4, "Fire Protection IV"},
        {"flame", 1, "Flame"}, {"fortune", 3, "Fortune III"},
        {"frost_walker", 2, "Frost Walker II"}, {"impaling", 5, "Impaling V"},
        {"infinity", 1, "Infinity"}, {"knockback", 2, "Knockback II"},
        {"looting", 3, "Looting III"}, {"loyalty", 3, "Loyalty III"},
        {"luck_of_the_sea", 3, "Luck of the Sea III"}, {"lure", 3, "Lure III"},
        {"mending", 1, "Mending"}, {"multishot", 1, "Multishot"},
        {"piercing", 4, "Piercing IV"}, {"power", 5, "Power V"},
        {"projectile_protection", 4, "Projectile Protection IV"},
        {"protection", 4, "Protection IV"}, {"punch", 2, "Punch II"},
        {"quick_charge", 3, "Quick Charge III"}, {"respiration", 3, "Respiration III"},
        {"riptide", 3, "Riptide III"}, {"sharpness", 5, "Sharpness V"},
        {"silk_touch", 1, "Silk Touch"}, {"smite", 5, "Smite V"},
        {"soul_speed", 3, "Soul Speed III"}, {"sweeping_edge", 3, "Sweeping Edge III"},
        {"swift_sneak", 3, "Swift Sneak III"}, {"thorns", 3, "Thorns III"},
        {"unbreaking", 3, "Unbreaking III"}, {"vanishing_curse", 1, "Curse of Vanishing"},
        {"wind_burst", 3, "Wind Burst III"},
    };

    private static Map<String, Integer> triggerCodes = null;

    static Map<String, Integer> getTriggerCodes() {
        if (triggerCodes == null) {
            triggerCodes = new HashMap<>();
            try (InputStream is = ItemGiveScreen.class.getResourceAsStream(
                    "/data/f1sch/function/features/give_item.mcfunction")) {
                if (is != null) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        int scoreIdx = line.indexOf("f1sch.give=");
                        if (scoreIdx < 0) continue;
                        scoreIdx += "f1sch.give=".length();
                        int scoreEnd = line.indexOf('}', scoreIdx);
                        if (scoreEnd < 0) continue;
                        int itemIdx = line.indexOf("{item:\"");
                        if (itemIdx < 0) continue;
                        itemIdx += "{item:\"".length();
                        int itemEnd = line.indexOf('"', itemIdx);
                        if (itemEnd < 0) continue;
                        try {
                            int code = Integer.parseInt(line.substring(scoreIdx, scoreEnd));
                            String itemId = line.substring(itemIdx, itemEnd);
                            triggerCodes.put(itemId, code);
                        } catch (NumberFormatException ignored) {}
                    }
                }
            } catch (Exception ignored) {}

            if (triggerCodes.isEmpty()) {
                List<String> ids = new ArrayList<>();
                for (Item item : BuiltInRegistries.ITEM) {
                    ItemStack stack = new ItemStack(item);
                    if (stack.isEmpty()) continue;
                    ids.add(BuiltInRegistries.ITEM.getKey(item).toString());
                }
                Collections.sort(ids);
                for (int i = 0; i < ids.size(); i++) {
                    triggerCodes.put(ids.get(i), i + 1);
                }
            }
        }
        return triggerCodes;
    }

    public ItemGiveScreen(Screen parent) {
        super(Component.literal("Item Give"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        allItems = new ArrayList<>();
        Map<String, Integer> codes = getTriggerCodes();

        List<ItemEntry> regularItems = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;
            String displayName;
            try { displayName = stack.getHoverName().getString(); } catch (Exception e) { displayName = id.getPath(); }
            Integer triggerCode = codes.get(id.toString());
            regularItems.add(new ItemEntry(item, id, displayName, triggerCode != null ? triggerCode : 0, id.getPath()));
        }
        regularItems.sort((a, b) -> a.id.toString().compareTo(b.id.toString()));
        allItems.addAll(regularItems);

        int code = SPECIAL_START;
        code = addPotionEntries(Items.POTION, "Potion of ", code);
        code = addPotionEntries(Items.SPLASH_POTION, "Splash P. of ", code);
        code = addPotionEntries(Items.LINGERING_POTION, "Lingering P. of ", code);
        code = addPotionEntries(Items.TIPPED_ARROW, "Arrow of ", code);
        addEnchantedBookEntries(code);

        int panelW = Math.min(400, this.width - 40);
        int panelX = (this.width - panelW) / 2;
        searchField = new EditBox(this.font, panelX + 6, 28, panelW - 50, 16, Component.literal("Search"));
        searchField.setMaxLength(50);
        searchField.setResponder(q -> { if (!q.equals(lastQuery)) { lastQuery = q; filterItems(); scrollOffset = 0; } });
        addWidget(searchField);

        addRenderableWidget(Button.builder(Component.literal("Done"), btn -> onClose())
                .bounds(width / 2 - 50, height - 28, 100, 20).build());

        filterItems();
    }

    private int addPotionEntries(Item containerItem, String prefix, int startCode) {
        Identifier containerId = BuiltInRegistries.ITEM.getKey(containerItem);
        for (int i = 0; i < POTION_EFFECTS.length; i++) {
            allItems.add(new ItemEntry(containerItem, containerId, prefix + POTION_EFFECTS[i][1],
                    startCode + i, containerId.getPath() + " [" + POTION_EFFECTS[i][0] + "]"));
        }
        return startCode + POTION_EFFECTS.length;
    }

    private void addEnchantedBookEntries(int startCode) {
        Identifier bookId = BuiltInRegistries.ITEM.getKey(Items.ENCHANTED_BOOK);
        for (int i = 0; i < ENCHANTMENTS.length; i++) {
            allItems.add(new ItemEntry(Items.ENCHANTED_BOOK, bookId, "Book: " + ENCHANTMENTS[i][2],
                    startCode + i, "enchanted_book [" + ENCHANTMENTS[i][0] + "]"));
        }
    }

    private void filterItems() {
        String query = lastQuery.toLowerCase(Locale.ROOT).trim();
        if (query.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(e -> e.name.toLowerCase(Locale.ROOT).contains(query)
                            || e.id.getPath().contains(query)
                            || e.subtitle.toLowerCase(Locale.ROOT).contains(query))
                    .collect(Collectors.toList());
        }
    }

    // TODO: render() / extractContent stubbed - GuiGraphicsExtractor rendering API needs porting

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    public void giveItem(ItemEntry entry) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getConnection() == null) return;
        String itemId = entry.id.toString();

        if (ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                ClientPlayNetworking.send(new ItemGivePayload(itemId, 64));
                return;
            } catch (Exception ignored) {}
        }

        int code = entry.triggerCode;
        if (code <= 0) { Integer c = getTriggerCodes().get(itemId); if (c != null) code = c; }
        if (code > 0 && client.getConnection() != null) {
            client.getConnection().sendCommand("trigger f1sch.give set " + code);
        }
    }

    static class ItemEntry {
        final Item item;
        final Identifier id;
        final String name;
        final int triggerCode;
        final String subtitle;

        ItemEntry(Item item, Identifier id, String name, int triggerCode, String subtitle) {
            this.item = item; this.id = id; this.name = name;
            this.triggerCode = triggerCode; this.subtitle = subtitle;
        }
    }
}
