package com.reachfly;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class KeybindHandler {

    private static final String CATEGORY = "reachfly";

    public static KeyMapping toggleReach, toggleFly, toggleEsp, toggleAutoHit;
    public static KeyMapping toggleLowHealthKill, toggleEatingAssist, toggleAutoKillWhenLow;
    public static KeyMapping toggleJesus, toggleAutoElytraSwap, toggleFlyToCoords;
    public static KeyMapping toggleNoFall, toggleFullbright, toggleSpeed;
    public static KeyMapping toggleWalkToCoords, toggleXray, toggleKnockback;
    public static KeyMapping toggleAutoTotem, toggleAutoArmor, toggleScaffold;
    public static KeyMapping toggleHud, triggerTeleport, openConfig;

    public static void register() {
        toggleReach = reg("toggle_reach", GLFW.GLFW_KEY_R);
        toggleFly = reg("toggle_fly", GLFW.GLFW_KEY_G);
        toggleEsp = reg("toggle_esp", GLFW.GLFW_KEY_X);
        toggleAutoHit = reg("toggle_autohit", GLFW.GLFW_KEY_V);
        toggleLowHealthKill = reg("toggle_lowhealthkill", GLFW.GLFW_KEY_B);
        toggleEatingAssist = reg("toggle_eating", GLFW.GLFW_KEY_N);
        toggleAutoKillWhenLow = reg("toggle_autokilllow", GLFW.GLFW_KEY_K);
        toggleJesus = reg("toggle_jesus", GLFW.GLFW_KEY_U);
        toggleAutoElytraSwap = reg("toggle_elytraswap", GLFW.GLFW_KEY_Y);
        toggleFlyToCoords = reg("toggle_flytocoords", GLFW.GLFW_KEY_P);
        toggleNoFall = reg("toggle_nofall", GLFW.GLFW_KEY_I);
        toggleFullbright = reg("toggle_fullbright", GLFW.GLFW_KEY_L);
        toggleSpeed = reg("toggle_speed", GLFW.GLFW_KEY_O);
        toggleWalkToCoords = reg("toggle_walktocoords", GLFW.GLFW_KEY_SEMICOLON);
        toggleXray = reg("toggle_xray", GLFW.GLFW_KEY_Z);
        toggleKnockback = reg("toggle_knockback", GLFW.GLFW_KEY_J);
        toggleAutoTotem = reg("toggle_autototem", GLFW.GLFW_KEY_M);
        toggleAutoArmor = reg("toggle_autoarmor", GLFW.GLFW_KEY_COMMA);
        toggleScaffold = reg("toggle_scaffold", GLFW.GLFW_KEY_PERIOD);
        toggleHud = reg("toggle_hud", GLFW.GLFW_KEY_H);
        triggerTeleport = reg("trigger_teleport", GLFW.GLFW_KEY_T);
        openConfig = reg("open_config", GLFW.GLFW_KEY_RIGHT_SHIFT);
    }

    private static KeyMapping reg(String id, int key) {
        return KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.reachfly." + id, InputConstants.Type.KEYSYM, key, CATEGORY));
    }
}
