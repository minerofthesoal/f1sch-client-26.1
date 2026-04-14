package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockRenderMixin {

    @Inject(method = "shouldRenderFace", at = @At("HEAD"), cancellable = true)
    private static void onShouldRenderFace(CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.xrayEnabled) {
            Block block = (Block) (Object) null; // not needed; we check at block-state level
            // When xray is active, always render faces so ores are visible through terrain
            cir.setReturnValue(true);
        }
    }
}
