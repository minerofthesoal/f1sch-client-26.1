package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MultiPlayerGameMode.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @Inject(method = "attack", at = @At("TAIL"))
    private void onAttack(Player player, Entity target, CallbackInfo ci) {
        if (!ModConfig.knockbackEnabled) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.getSingleplayerServer() == null) return;

        for (ServerLevel level : mc.getSingleplayerServer().getAllLevels()) {
            Entity serverEntity = level.getEntity(target.getId());
            if (serverEntity != null) {
                Vec3 look = player.getLookAngle();
                double strength = ModConfig.knockbackStrength;
                serverEntity.push(look.x * strength, 0.4 * strength, look.z * strength);
                serverEntity.hurtMarked = true;
                break;
            }
        }
    }
}
