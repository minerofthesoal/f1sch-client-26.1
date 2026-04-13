package com.reachfly.mixin;

import com.reachfly.ModConfig;
import com.reachfly.XrayHandler;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BlockRenderDispatcher.class)
public abstract class XrayBlockRenderMixin {

    // TODO: Verify renderBatched method signature for MC 26.1.
    // The method name and parameters may have changed. If this mixin fails to apply,
    // check BlockRenderDispatcher for the correct render method.
    @Inject(method = "renderBatched", at = @At("HEAD"), cancellable = true, require = 0)
    private void onRenderBatched(BlockState state, BlockPos pos, BlockAndTintGetter level,
                                 PoseStack poseStack, VertexConsumer consumer,
                                 boolean checkSides, RandomSource random, CallbackInfo ci) {
        if (ModConfig.xrayEnabled && !XrayHandler.shouldRenderBlock(state.getBlock())) {
            ci.cancel();
        }
    }
}
