package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

public class ReachHandler {

    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "entity_reach");
    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;
    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastDistance = 0;

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        boolean needsUpdate = (ModConfig.reachEnabled != lastEnabled)
                || (ModConfig.reachEnabled && ModConfig.reachDistance != lastDistance);
        tickCounter++;
        if (tickCounter >= 40) { tickCounter = 0; if (ModConfig.reachEnabled) needsUpdate = true; }
        if (needsUpdate) { lastEnabled = ModConfig.reachEnabled; lastDistance = ModConfig.reachDistance; updateReachAttributes(); }
    }

    public static void updateReachAttributes() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        LocalPlayer player = client.player;
        applyToPlayer(player);
        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) {
            ServerPlayer sp = server.getPlayerList().getPlayer(player.getUUID());
            if (sp != null) applyToPlayer(sp);
        }
    }

    private static void applyToPlayer(net.minecraft.world.entity.LivingEntity player) {
        AttributeInstance blockRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (blockRange == null || entityRange == null) return;
        if (ModConfig.reachEnabled) {
            applyMod(blockRange, BLOCK_REACH_ID, ModConfig.reachDistance - DEFAULT_BLOCK_RANGE);
            applyMod(entityRange, ENTITY_REACH_ID, ModConfig.reachDistance - DEFAULT_ENTITY_RANGE);
        } else {
            blockRange.removeModifier(BLOCK_REACH_ID); entityRange.removeModifier(ENTITY_REACH_ID);
        }
    }

    private static void applyMod(AttributeInstance attr, ResourceLocation id, double value) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing == null || existing.value() != value) {
            attr.removeModifier(id);
            attr.addTemporaryModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE));
        }
    }

    public static void clearReachModifiers() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        clearFor(client.player);
        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) { ServerPlayer sp = server.getPlayerList().getPlayer(client.player.getUUID()); if (sp != null) clearFor(sp); }
    }

    private static void clearFor(net.minecraft.world.entity.LivingEntity p) {
        AttributeInstance br = p.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance er = p.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (br != null) br.removeModifier(BLOCK_REACH_ID);
        if (er != null) er.removeModifier(ENTITY_REACH_ID);
    }
}
