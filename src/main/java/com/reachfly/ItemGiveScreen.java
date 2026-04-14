package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Button;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Paged item browser. Search + click to give items via server addon or datapack trigger.
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

    public ItemGiveScreen(Screen parent) {
        super(Component.literal("Item Give"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        allItems = new ArrayList<>();
        for (Item item : BuiltInRegistries.ITEM) {
            Identifier id = BuiltInRegistries.ITEM.getKey(item);
            ItemStack stack = new ItemStack(item);
            if (stack.isEmpty()) continue;
            String name;
            try { name = stack.getHoverName().getString(); } catch (Exception e) { name = id.getPath(); }
            allItems.add(new ItemEntry(item, id, name));
        }
        allItems.sort((a, b) -> a.name.compareToIgnoreCase(b.name));
        filterItems();
        buildPage();
    }

    private void filterItems() {
        String q = lastQuery.toLowerCase(Locale.ROOT).trim();
        if (q.isEmpty()) {
            filteredItems = new ArrayList<>(allItems);
        } else {
            filteredItems = allItems.stream()
                    .filter(e -> e.name.toLowerCase(Locale.ROOT).contains(q) || e.id.getPath().contains(q))
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
            addRenderableWidget(Button.builder(
                    Component.literal("\u00a7f" + entry.name),
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
        String itemId = entry.id.toString();

        if (ClientPlayNetworking.canSend(ItemGivePayload.ID)) {
            try {
                ClientPlayNetworking.send(new ItemGivePayload(itemId, 64));
                return;
            } catch (Exception ignored) {}
        }

        if (client.getConnection() != null) {
            client.getConnection().sendCommand("give @s " + itemId + " 64");
        }
    }

    @Override
    public void onClose() {
        if (minecraft != null) minecraft.setScreen(parent);
    }

    static class ItemEntry {
        final Item item;
        final Identifier id;
        final String name;
        ItemEntry(Item item, Identifier id, String name) {
            this.item = item; this.id = id; this.name = name;
        }
    }
}
