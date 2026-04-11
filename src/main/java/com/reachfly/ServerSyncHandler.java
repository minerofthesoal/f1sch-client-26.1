package com.reachfly;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.minecraft.client.Minecraft;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ServerSyncHandler {

    public static final List<EspDataPayload.EntityEntry> serverEspEntities = new CopyOnWriteArrayList<>();
    public static boolean serverEspActive = false;
    private static boolean lastOpSelfEnabled, lastKnockbackEnabled, lastReachEnabled, lastSpeedEnabled, lastNoFallEnabled, lastFlyEnabled, lastEspEnabled;
    private static float lastKnockbackStrength, lastReachDistance, lastSpeedMultiplier, lastFlySpeed;
    private static int syncTicker = 0;

    public static void registerPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(FeatureSyncPayload.ID, FeatureSyncPayload.CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ItemGivePayload.ID, ItemGivePayload.CODEC);
        PayloadTypeRegistry.clientboundPlay().register(EspDataPayload.ID, EspDataPayload.CODEC);
        ClientPlayNetworking.registerGlobalReceiver(EspDataPayload.ID, (payload, context) -> {
            serverEspEntities.clear(); serverEspEntities.addAll(payload.entities()); serverEspActive = true;
        });
    }

    public static void tick(Minecraft client) {
        if (client.player == null || client.getConnection() == null) return;
        syncTicker++;
        if (ModConfig.proUnlocked && ModConfig.opSelfEnabled && !lastOpSelfEnabled) { lastOpSelfEnabled = true; sendSync("op", true, 4); }
        if (!ModConfig.opSelfEnabled) lastOpSelfEnabled = false;
        syncFeature("knockback", ModConfig.knockbackEnabled, ModConfig.knockbackStrength, lastKnockbackEnabled, lastKnockbackStrength, b -> lastKnockbackEnabled = b, v -> lastKnockbackStrength = v);
        syncFeature("reach", ModConfig.reachEnabled, ModConfig.reachDistance, lastReachEnabled, lastReachDistance, b -> lastReachEnabled = b, v -> lastReachDistance = v);
        syncFeature("speed", ModConfig.speedEnabled, ModConfig.speedMultiplier, lastSpeedEnabled, lastSpeedMultiplier, b -> lastSpeedEnabled = b, v -> lastSpeedMultiplier = v);
        if (ModConfig.noFallEnabled != lastNoFallEnabled) { lastNoFallEnabled = ModConfig.noFallEnabled; sendSync("nofall", ModConfig.noFallEnabled, 0); }
        syncFeature("fly", ModConfig.flyEnabled, ModConfig.flySpeed, lastFlyEnabled, lastFlySpeed, b -> lastFlyEnabled = b, v -> lastFlySpeed = v);
        if (ModConfig.espEnabled != lastEspEnabled) { lastEspEnabled = ModConfig.espEnabled; sendSync("esp", ModConfig.espEnabled, 200); }
        if (!ModConfig.espEnabled && serverEspActive) { serverEspEntities.clear(); serverEspActive = false; }
        if (syncTicker >= 100) { syncTicker = 0; forceResync(); }
    }

    private static void syncFeature(String name, boolean enabled, float value, boolean lastEnabled, float lastValue,
                                     java.util.function.Consumer<Boolean> setEnabled, java.util.function.Consumer<Float> setValue) {
        if (enabled != lastEnabled || (enabled && value != lastValue)) {
            setEnabled.accept(enabled); setValue.accept(value); sendSync(name, enabled, value);
        }
    }

    private static void forceResync() {
        if (ModConfig.knockbackEnabled) sendSync("knockback", true, ModConfig.knockbackStrength);
        if (ModConfig.reachEnabled) sendSync("reach", true, ModConfig.reachDistance);
        if (ModConfig.speedEnabled) sendSync("speed", true, ModConfig.speedMultiplier);
        if (ModConfig.noFallEnabled) sendSync("nofall", true, 0);
        if (ModConfig.flyEnabled) sendSync("fly", true, ModConfig.flySpeed);
        if (ModConfig.espEnabled) sendSync("esp", true, 200);
    }

    private static void sendSync(String feature, boolean enabled, float value) {
        try { ClientPlayNetworking.send(new FeatureSyncPayload(feature, enabled, value)); } catch (Exception ignored) {}
    }
}
