package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class SafeWalkMixin {

    @Inject(method = "isStayingOnGroundSurface", at = @At("HEAD"), cancellable = true)
    private void onIsStayingOnGroundSurface(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.safeWalkEnabled && ((Object) this) instanceof LocalPlayer) {
            cir.setReturnValue(true);
        }
    }
}
