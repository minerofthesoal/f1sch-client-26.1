package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public abstract class EntityGlowMixin {

    @Inject(method = "isCurrentlyGlowing", at = @At("RETURN"), cancellable = true)
    private void onIsCurrentlyGlowing(CallbackInfoReturnable<Boolean> cir) {
        if (!ModConfig.espEnabled) return;
        Entity self = (Entity) (Object) this;
        if (self instanceof Player && ModConfig.espPlayers) {
            cir.setReturnValue(true);
        } else if (self instanceof Monster && ModConfig.espHostile) {
            cir.setReturnValue(true);
        } else if (self instanceof AgeableMob && ModConfig.espPassive) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    private void onGetTeamColor(CallbackInfoReturnable<Integer> cir) {
        if (!ModConfig.espEnabled) return;
        Entity self = (Entity) (Object) this;
        if (self instanceof Player && ModConfig.espPlayers) {
            cir.setReturnValue(0xFF5555);
        } else if (self instanceof Monster && ModConfig.espHostile) {
            cir.setReturnValue(0xFF8800);
        } else if (self instanceof AgeableMob && ModConfig.espPassive) {
            cir.setReturnValue(0x55FF55);
        }
    }
}
