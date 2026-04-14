package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class MeteorHandlers {

    private static final Identifier STEP_ID = Identifier.fromNamespaceAndPath("reachfly", "step_height");

    public static void tick(Minecraft client) {
        if (client.player == null) return;
        tickAutoLog(client); tickAutoRespawn(client); tickBetterSprint(client); tickStep(client);
    }

    private static void tickAutoLog(Minecraft client) {
        if (!ModConfig.autoLogEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || p.isDeadOrDying()) return;
        if (p.getHealth() <= ModConfig.autoLogHealth) {
            ModConfig.autoLogEnabled = false; ModConfig.save();
            p.sendSystemMessage(Component.literal("\u00a7c[f1sch] Auto Log: disconnecting at " + String.format("%.1f HP", p.getHealth())));
            client.disconnect(null, false); // auto-disconnect on low health
        }
    }

    private static void tickAutoRespawn(Minecraft client) {
        if (!ModConfig.autoRespawnEnabled) return;
        if (client.player != null && client.player.isDeadOrDying()) client.player.respawn();
    }

    private static void tickBetterSprint(Minecraft client) {
        if (!ModConfig.betterSprintEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        if (client.options.keyUp.isDown() && !p.isShiftKeyDown() && !p.isUsingItem() && p.getFoodData().getFoodLevel() > 6)
            p.setSprinting(true);
    }

    private static boolean stepApplied = false;

    private static void tickStep(Minecraft client) {
        LocalPlayer p = client.player;
        if (p == null) return;
        AttributeInstance stepAttr = p.getAttribute(Attributes.STEP_HEIGHT);
        if (stepAttr == null) return;
        if (ModConfig.stepEnabled && !stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepAttr.addTransientModifier(new AttributeModifier(STEP_ID, ModConfig.stepHeight - 0.6, AttributeModifier.Operation.ADD_VALUE));
            stepApplied = true;
        } else if (!ModConfig.stepEnabled && stepApplied) {
            stepAttr.removeModifier(STEP_ID);
            stepApplied = false;
        }
    }
}
