package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.phys.Vec3;

public class SpeedHandler {

    public static void tick(Minecraft client) {
        if (!ModConfig.speedEnabled) return;
        if (client.player == null || client.level == null || client.screen != null) return;
        LocalPlayer player = client.player;
        if (!player.onGround()) return;
        Vec3 velocity = player.getDeltaMovement();
        double currentSpeed = Math.sqrt(velocity.x * velocity.x + velocity.z * velocity.z);
        if (currentSpeed < 0.001) return;
        float forward = 0, strafe = 0;
        if (client.options.keyUp.isDown()) forward += 1;
        if (client.options.keyDown.isDown()) forward -= 1;
        if (client.options.keyLeft.isDown()) strafe += 1;
        if (client.options.keyRight.isDown()) strafe -= 1;
        if (forward == 0 && strafe == 0) return;
        float yaw = player.getYRot();
        double yawRad = Math.toRadians(yaw);
        double moveAngle = yawRad - Math.atan2(strafe, forward);
        double dirX = -Math.sin(moveAngle);
        double dirZ = Math.cos(moveAngle);
        double baseSpeed = player.isSprinting() ? 0.14 : 0.108;
        double targetSpeed = baseSpeed * ModConfig.speedMultiplier;
        player.setDeltaMovement(dirX * targetSpeed, velocity.y, dirZ * targetSpeed);
    }
}
