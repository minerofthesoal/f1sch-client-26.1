package com.reachfly.mixin;

import com.reachfly.ModConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.entity.player.Input;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class InvMoveMixin {

    @Shadow
    public LocalPlayer player;

    @Shadow
    public Screen screen;

    @Shadow
    public Options options;

    @Inject(method = "handleKeybinds", at = @At("HEAD"))
    private void onHandleKeybinds(CallbackInfo ci) {
        if (!ModConfig.invMoveEnabled) return;
        if (screen == null || screen instanceof ChatScreen) return;
        if (player == null) return;

        boolean forward = options.keyUp.isDown();
        boolean backward = options.keyDown.isDown();
        boolean left = options.keyLeft.isDown();
        boolean right = options.keyRight.isDown();
        boolean jump = options.keyJump.isDown();
        boolean sneak = options.keyShift.isDown();
        boolean sprint = options.keySprint.isDown();

        player.input.keyPresses = new Input(
                forward, backward, left, right, jump, sneak, sprint
        );
    }
}
