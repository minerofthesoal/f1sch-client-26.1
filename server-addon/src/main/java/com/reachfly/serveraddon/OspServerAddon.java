package com.reachfly.serveraddon;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.*;

public class OspServerAddon implements DedicatedServerModInitializer {

    public static final Logger LOGGER = LoggerFactory.getLogger("f1sch-server-addon");

    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "knockback_boost");
    private static final ResourceLocation BLOCK_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "block_reach");
    private static final ResourceLocation ENTITY_REACH_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "entity_reach");
    private static final ResourceLocation SPEED_ID = ResourceLocation.fromNamespaceAndPath("reachfly", "speed_boost");

    private static final double DEFAULT_BLOCK_RANGE = 4.5;
    private static final double DEFAULT_ENTITY_RANGE = 3.0;
    private static final Map<UUID, PlayerFeatureState> playerStates = new HashMap<>();
    private int tickCounter = 0;

    @Override
    public void onInitializeServer() {
        LOGGER.info("[f1sch Server Addon v3] Initializing for MC 26.1...");

        PayloadTypeRegistry.playC2S().register(TeleportPayload.ID, TeleportPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);
        PayloadTypeRegistry.playC2S().register(ItemGivePayload.ID, ItemGivePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(EspDataPayload.ID, EspDataPayload.CODEC);

        ServerPlayNetworking.registerGlobalReceiver(TeleportPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            double x = payload.x(), y = Math.max(-64, Math.min(320, payload.y())), z = payload.z();
            double finalY = y;
            context.server().execute(() -> {
                player.teleportTo(x, finalY, z);
                player.sendSystemMessage(Component.literal("\u00a7a[f1sch] Teleported to " + String.format("%.0f, %.0f, %.0f", x, finalY, z)));
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(ItemGivePayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            String itemId = payload.itemId();
            int quantity = Math.max(1, Math.min(6400, payload.quantity()));
            context.server().execute(() -> {
                try {
                    String cmd = "give " + player.getName().getString() + " " + itemId + " " + quantity;
                    context.server().getCommands().getDispatcher().execute(cmd, context.server().createCommandSourceStack());
                } catch (Exception e) {
                    player.sendSystemMessage(Component.literal("\u00a7c[f1sch] Failed to give item: " + e.getMessage()));
                }
            });
        });

        ServerPlayNetworking.registerGlobalReceiver(FeatureSyncPayload.ID, (payload, context) -> {
            ServerPlayer player = context.player();
            context.server().execute(() -> handleFeatureSync(context.server(), player, payload.feature(), payload.enabled(), payload.value()));
        });

        ServerTickEvents.END_SERVER_TICK.register(this::onServerTick);

        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            UUID uuid = handler.player.getUUID();
            PlayerFeatureState state = playerStates.remove(uuid);
            if (state != null) cleanupPlayer(handler.player);
        });

        LOGGER.info("[f1sch Server Addon v3] Ready. Features: Teleport, Knockback, Reach, Speed, NoFall, Fly, ESP");
    }

    private void handleFeatureSync(MinecraftServer server, ServerPlayer player, String feature, boolean enabled, float value) {
        PlayerFeatureState state = playerStates.computeIfAbsent(player.getUUID(), k -> new PlayerFeatureState());
        switch (feature) {
            case "knockback" -> handleKnockback(player, state, enabled, value);
            case "reach" -> handleReach(player, state, enabled, value);
            case "speed" -> handleSpeed(player, state, enabled, value);
            case "nofall" -> { state.noFallEnabled = enabled; }
            case "fly" -> handleFly(player, state, enabled, value);
            case "esp" -> { state.espEnabled = enabled; state.espRange = value; }
            case "op" -> { if (enabled) handleOp(server, player); }
        }
    }

    private void handleKnockback(ServerPlayer player, PlayerFeatureState state, boolean enabled, float strength) {
        state.knockbackEnabled = enabled; state.knockbackStrength = strength;
        AttributeInstance attr = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (attr == null) return;
        if (enabled) { attr.removeModifier(KNOCKBACK_ID); attr.addTemporaryModifier(new AttributeModifier(KNOCKBACK_ID, strength, AttributeModifier.Operation.ADD_VALUE)); }
        else attr.removeModifier(KNOCKBACK_ID);
    }

    private void handleReach(ServerPlayer player, PlayerFeatureState state, boolean enabled, float distance) {
        state.reachEnabled = enabled; state.reachDistance = distance;
        AttributeInstance blockRange = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE);
        AttributeInstance entityRange = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
        if (blockRange == null || entityRange == null) return;
        if (enabled) {
            applyModifier(blockRange, BLOCK_REACH_ID, distance - DEFAULT_BLOCK_RANGE);
            applyModifier(entityRange, ENTITY_REACH_ID, distance - DEFAULT_ENTITY_RANGE);
        } else { blockRange.removeModifier(BLOCK_REACH_ID); entityRange.removeModifier(ENTITY_REACH_ID); }
    }

    private void handleSpeed(ServerPlayer player, PlayerFeatureState state, boolean enabled, float multiplier) {
        state.speedEnabled = enabled; state.speedMultiplier = multiplier;
        AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);
        if (speedAttr == null) return;
        if (enabled) { applyModifier(speedAttr, SPEED_ID, 0.1 * (multiplier - 1.0)); }
        else speedAttr.removeModifier(SPEED_ID);
    }

    private void handleFly(ServerPlayer player, PlayerFeatureState state, boolean enabled, float speed) {
        state.flyEnabled = enabled; state.flySpeed = speed;
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayFly = enabled;
            if (enabled) player.getAbilities().setFlyingSpeed(0.05f * speed);
            else { player.getAbilities().flying = false; player.getAbilities().setFlyingSpeed(0.05f); }
            player.onUpdateAbilities();
        }
    }

    private void handleOp(MinecraftServer server, ServerPlayer player) {
        try {
            server.getCommands().getDispatcher().execute("op " + player.getName().getString(), server.createCommandSourceStack());
            player.sendSystemMessage(Component.literal("\u00a7a[f1sch] Operator status granted."));
        } catch (Exception e) { LOGGER.warn("[f1sch] Failed to grant OP: {}", e.getMessage()); }
    }

    private void onServerTick(MinecraftServer server) {
        tickCounter++;
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PlayerFeatureState state = playerStates.get(player.getUUID());
            if (state == null) continue;
            if (state.noFallEnabled) player.fallDistance = 0.0f;
            if (state.flyEnabled && !player.isCreative() && !player.isSpectator()) {
                if (!player.getAbilities().mayFly) { player.getAbilities().mayFly = true; player.getAbilities().setFlyingSpeed(0.05f * state.flySpeed); player.onUpdateAbilities(); }
                if (player.getAbilities().flying) player.fallDistance = 0.0f;
            }
            if (state.espEnabled && tickCounter % 10 == 0) sendEspData(player, state.espRange);
        }
    }

    private void sendEspData(ServerPlayer player, float range) {
        ServerLevel world = player.serverLevel();
        Vec3 pos = player.position();
        double r = Math.min(range, 500);
        AABB searchBox = new AABB(pos.x - r, pos.y - r, pos.z - r, pos.x + r, pos.y + r, pos.z + r);
        List<EspDataPayload.EntityEntry> entries = new ArrayList<>();
        for (Entity entity : world.getEntities(player, searchBox, e -> e instanceof LivingEntity && e.isAlive())) {
            if (entries.size() >= 200) break;
            String type;
            if (entity instanceof Player) type = "player";
            else if (entity instanceof Monster) type = "hostile";
            else if (entity instanceof Animal) type = "passive";
            else type = "other";
            entries.add(new EspDataPayload.EntityEntry(entity.getId(), entity.getX(), entity.getY(), entity.getZ(), type, ((LivingEntity) entity).getHealth()));
        }
        if (!entries.isEmpty()) { try { ServerPlayNetworking.send(player, new EspDataPayload(entries)); } catch (Exception ignored) {} }
    }

    private void cleanupPlayer(ServerPlayer player) {
        AttributeInstance kb = player.getAttribute(Attributes.ATTACK_KNOCKBACK); if (kb != null) kb.removeModifier(KNOCKBACK_ID);
        AttributeInstance br = player.getAttribute(Attributes.BLOCK_INTERACTION_RANGE); if (br != null) br.removeModifier(BLOCK_REACH_ID);
        AttributeInstance er = player.getAttribute(Attributes.ENTITY_INTERACTION_RANGE); if (er != null) er.removeModifier(ENTITY_REACH_ID);
        AttributeInstance sp = player.getAttribute(Attributes.MOVEMENT_SPEED); if (sp != null) sp.removeModifier(SPEED_ID);
        if (!player.isCreative() && !player.isSpectator()) {
            player.getAbilities().mayFly = false; player.getAbilities().flying = false; player.getAbilities().setFlyingSpeed(0.05f); player.onUpdateAbilities();
        }
    }

    private void applyModifier(AttributeInstance attr, ResourceLocation id, double value) {
        AttributeModifier existing = attr.getModifier(id);
        if (existing == null || existing.value() != value) { attr.removeModifier(id); attr.addTemporaryModifier(new AttributeModifier(id, value, AttributeModifier.Operation.ADD_VALUE)); }
    }

    private static class PlayerFeatureState {
        boolean knockbackEnabled, reachEnabled, speedEnabled, noFallEnabled, flyEnabled, espEnabled;
        float knockbackStrength, reachDistance, speedMultiplier = 1, flySpeed = 1, espRange = 100;
    }
}
