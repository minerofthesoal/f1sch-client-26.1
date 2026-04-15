package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Paged item browser. Search + click to give items via server addon or datapack trigger.
 * Includes all potion variants, enchanted books, and tipped arrows.
 */
public class ItemGiveScreen extends Screen {

    private final Screen parent;
    private EditBox searchField;
    private List<ItemEntry> allItems;
    private List<ItemEntry> filteredItems;
    private int page = 0;
    private String lastQuery = "";
    private static final int ROWS = 8;
    private static final int COLS = 2;
    private static final int PER_PAGE = ROWS * COLS;

    // 47 potion effects (alphabetical) - note: 46 listed, "water" is included
    private static final String[] POTION_EFFECTS = {
            "awkward", "fire_resistance", "harming", "healing", "infested",
            "invisibility", "leaping", "long_fire_resistance", "long_invisibility",
            "long_leaping", "long_night_vision", "long_poison", "long_regeneration",
            "long_slow_falling", "long_slowness", "long_strength", "long_swiftness",
            "long_turtle_master", "long_water_breathing", "long_weakness", "luck",
            "mundane", "night_vision", "oozing", "poison", "regeneration",
            "slow_falling", "slowness", "strength", "strong_harming", "strong_healing",
            "strong_leaping", "strong_poison", "strong_regeneration", "strong_slowness",
            "strong_strength", "strong_swiftness", "strong_turtle_master", "swiftness",
            "thick", "turtle_master", "water", "water_breathing", "weakness",
            "weaving", "wind_charged"
    };

    // 43 enchantments (alphabetical) - note: 42 listed
    private static final String[] ENCHANTMENTS = {
            "aqua_affinity", "bane_of_arthropods", "binding_curse", "blast_protection",
            "breach", "channeling", "density", "depth_strider", "efficiency",
            "feather_falling", "fire_aspect", "fire_protection", "flame", "fortune",
            "frost_walker", "impaling", "infinity", "knockback", "looting", "loyalty",
            "luck_of_the_sea", "lure", "mending", "multishot", "piercing", "power",
            "projectile_protection", "protection", "punch", "quick_charge", "respiration",
            "riptide", "sharpness", "silk_touch", "smite", "soul_speed", "sweeping_edge",
            "swift_sneak", "thorns", "unbreaking", "vanishing_curse", "wind_burst"
    };

    // Potion container types in order
    private static final String[] POTION_CONTAINERS = {"potion", "splash_potion", "lingering_potion"};

    public ItemGiveScreen(Screen parent) {
        super(Component.literal("Item Give"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        allItems = new ArrayList<>();

        // Collect all regular items sorted alphabetically by registry ID
        List<String> sortedItemIds = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            sortedItemIds.add(id.toString());
        }
        Collections.sort(sortedItemIds);
        int regularCount = sortedItemIds.size();

        // Add regular items with trigger codes (1-based index in sorted order)
        for (int i = 0; i < sortedItemIds.size(); i++) {
            String fullId = sortedItemIds.get(i);
            String namespace;
            String path;
            if (fullId.contains(":")) {
                namespace = fullId.substring(0, fullId.indexOf(':'));
                path = fullId.substring(fullId.indexOf(':') + 1);
            } else {
                namespace = "minecraft";
                path = fullId;
            }
            // Get display name from registry
            Identifier rid = Identifier.fromNamespaceAndPath(namespace, path);
            Item item = BuiltInRegistries.ITEM.getValue(rid);
            String name;
            try {
                ItemStack stack = new ItemStack(item);
                name = stack.isEmpty() ? formatName(path) : stack.getHoverName().getString();
            } catch (Exception e) {
                name = formatName(path);
            }
            int triggerCode = i + 1;
            allItems.add(new ItemEntry(fullId, name, triggerCode, false));
        }

        // Special items start after regular items
        int specialStart = regularCount + 1;
        int code = specialStart;

        // Potions (normal)
        for (String effect : POTION_EFFECTS) {
            String displayName = "Potion of " + formatName(effect);
            allItems.add(new ItemEntry("minecraft:potion{potion:" + effect + "}", displayName, code++, true));
        }

        // Splash potions
        for (String effect : POTION_EFFECTS) {
            String displayName = "Splash Potion of " + formatName(effect);
            allItems.add(new ItemEntry("minecraft:splash_potion{potion:" + effect + "}", displayName, code++, true));
        }

        // Lingering potions
        for (String effect : POTION_EFFECTS) {
            String displayName = "Lingering Potion of " + formatName(effect);
            allItems.add(new ItemEntry("minecraft:lingering_potion{potion:" + effect + "}", displayName, code++, true));
        }

        // Tipped arrows
        for (String effect : POTION_EFFECTS) {
            String displayName = "Tipped Arrow of " + formatName(effect);
            allItems.add(new ItemEntry("minecraft:tipped_arrow{potion:" + effect + "}", displayName, code++, true));
        }

        // Enchanted books
        for (String enchantment : ENCHANTMENTS) {
            String displayName = "Enchanted Book: " + formatName(enchantment);
            allItems.add(new ItemEntry("minecraft:enchanted_book{enchantment:" + enchantment + "}", displayName, code++, true));
        }

        filterItems();
        buildPage();
    }

    private static String formatName(String path) {
        StringBuilder sb = new StringBuilder();
        for (String word : path.split("_")) {
            if (sb.length() > 0) sb.append(' ');
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0)));
                if (word.length() > 1) sb.append(word.substring(1));
            }
        }
        return sb.toString();
    }

    private void filterItems() {
        String q = lastQuery.toLowerCase(Locale.ROOT).trim();
        if (q.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(e -> e.name.toLowerCase(Locale.ROOT).contains(q)
                            || e.itemId.toLowerCase(Locale.ROOT).contains(q))
                    .collect(Collectors.toList());
        }
    }

    private void buildPage() {
        clearWidgets();
        int cx = width / 2;
        int colW = 190;
        int totalPages = Math.max(1, (filteredItems.size() + PER_PAGE - 1) / PER_PAGE);
        if (page >= totalPages) page = totalPages - 1;
        if (page < 0) page = 0;

        // Search field
        searchField = new EditBox(font, cx - 95, 8, 190, 16, Component.literal("Search"));
        searchField.setMaxLength(40);
        searchField.setValue(lastQuery);
        searchField.setResponder(q -> {
            if (!q.equals(lastQuery)) { lastQuery = q; filterItems(); page = 0; buildPage(); }
        });
        addRenderableWidget(searchField);

        // Item buttons in 2 columns
        int startIdx = page * PER_PAGE;
        for (int i = 0; i < PER_PAGE && startIdx + i < filteredItems.size(); i++) {
            int col = i % COLS;
            int row = i / COLS;
            int x = cx - colW - 2 + col * (colW + 4);
            int y = 30 + row * 20;
            ItemEntry entry = filteredItems.get(startIdx + i);
            String label = entry.isSpecial ? "\u00a7b" + entry.name : "\u00a7f" + entry.name;
            addRenderableWidget(Button.builder(
                    Component.literal(label),
                    b -> giveItem(entry)
            ).bounds(x, y, colW, 18).build());
        }

        // Navigation
        String pageLabel = "Page " + (page + 1) + "/" + totalPages + " (" + filteredItems.size() + " items)";
        if (page > 0)
            addRenderableWidget(Button.builder(Component.literal("<< Prev"), b -> { page--; buildPage(); })
                    .bounds(cx - 154, height - 28, 70, 20).build());
        addRenderableWidget(Button.builder(Component.literal(pageLabel), b -> {})
                .bounds(cx - 60, height - 28, 120, 20).build());
        if (startIdx + PER_PAGE < filteredItems.size())
            addRenderableWidget(Button.builder(Component.literal("Next >>"), b -> { page++; buildPage(); })
                    .bounds(cx + 84, height - 28, 70, 20).build());

        // Back button
        addRenderableWidget(Button.builder(Component.literal("Back"), b -> onClose())
                .bounds(cx - 40, height - 52, 80, 20).build());
    }

    private void giveItem(ItemEntry entry) {
        Minecraft client = Minecraft.getInstance();
        if (client == null || client.getConnection() == null) return;

        // Try server addon first
        if (!entry.isSpecial && ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                ClientPlayNetworking.send(new ItemGivePayload(entry.itemId, 64));
                return;
            } catch (Exception ignored) {}
        }

        // Use datapack trigger system (works without op)
        client.getConnection().sendCommand("trigger f1sch.give set " + entry.triggerCode);
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    static class ItemEntry {
        final String itemId;
        final String name;
        final int triggerCode;
        final boolean isSpecial;

        ItemEntry(String itemId, String name, int triggerCode, boolean isSpecial) {
            this.itemId = itemId;
            this.name = name;
            this.triggerCode = triggerCode;
            this.isSpecial = isSpecial;
        }
    }
}
