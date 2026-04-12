package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;

public class KnockbackHandler {

    private static final ResourceLocation KNOCKBACK_ID = ResourceLocation.of("reachfly", "knockback_boost");
    private static int tickCounter = 0;
    private static boolean lastEnabled = false;
    private static float lastStrength = 0;

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        boolean needsUpdate = (ModConfig.knockbackEnabled != lastEnabled)
                || (ModConfig.knockbackEnabled && ModConfig.knockbackStrength != lastStrength);
        tickCounter++;
        if (tickCounter >= 40) { tickCounter = 0; if (ModConfig.knockbackEnabled) needsUpdate = true; }
        if (needsUpdate) { lastEnabled = ModConfig.knockbackEnabled; lastStrength = ModConfig.knockbackStrength; updateKnockbackAttributes(); }
    }

    public static void updateKnockbackAttributes() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        LocalPlayer player = client.player;
        applyToPlayer(player);
        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) { ServerPlayer sp = server.getPlayerList().getPlayer(player.getUUID()); if (sp != null) applyToPlayer(sp); }
    }

    private static void applyToPlayer(net.minecraft.world.entity.LivingEntity player) {
        AttributeInstance kb = player.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (kb == null) return;
        if (ModConfig.knockbackEnabled) {
            double boost = ModConfig.knockbackStrength;
            AttributeModifier existing = kb.getModifier(KNOCKBACK_ID);
            if (existing == null || existing.amount() != boost) {
                kb.removeModifier(KNOCKBACK_ID);
                kb.addTransientModifier(new AttributeModifier(KNOCKBACK_ID, boost, AttributeModifier.Operation.ADD_VALUE));
            }
        } else { kb.removeModifier(KNOCKBACK_ID); }
    }

    public static void clearKnockbackModifiers() {
        Minecraft client = Minecraft.getInstance();
        if (client.player == null) return;
        clearFor(client.player);
        MinecraftServer server = client.getSingleplayerServer();
        if (server != null) { ServerPlayer sp = server.getPlayerList().getPlayer(client.player.getUUID()); if (sp != null) clearFor(sp); }
    }

    private static void clearFor(net.minecraft.world.entity.LivingEntity p) {
        AttributeInstance kb = p.getAttribute(Attributes.ATTACK_KNOCKBACK);
        if (kb != null) kb.removeModifier(KNOCKBACK_ID);
    }
}
