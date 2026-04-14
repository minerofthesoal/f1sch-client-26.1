package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class FlyToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 1.5;
    private static final double SLOWDOWN_DISTANCE = 30.0;
    private static final double MIN_SPEED_FACTOR = 0.15;
    private static int tickCounter = 0;
    private static Vec3 lastPos = null;
    private static int stuckTicks = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.flyToCoordsEnabled) return;
        if (client.player == null || client.level == null) return;
        LocalPlayer player = client.player;
        double targetX = ModConfig.flyToX, targetY = ModConfig.flyToY, targetZ = ModConfig.flyToZ;
        Vec3 target = new Vec3(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3 pos = player.position();
        double distance = pos.distanceTo(target);
        double horizDist = Math.sqrt((pos.x - target.x) * (pos.x - target.x) + (pos.z - target.z) * (pos.z - target.z));

        if (!isNavigating) {
            isNavigating = true; lastPos = pos; stuckTicks = 0; tickCounter = 0;
            player.sendOverlayMessage(Component.literal("\u00a7b[f1sch] \u00a7eFlying to X:%.0f Y:%.0f Z:%.0f (%.0f blocks away)".formatted(targetX, targetY, targetZ, distance)));
        }
        if (distance < ARRIVAL_DISTANCE) {
            ModConfig.flyToCoordsEnabled = false; isNavigating = false; player.setDeltaMovement(Vec3.ZERO);
            player.sendOverlayMessage(Component.literal("\u00a7b[f1sch] \u00a7aArrived at destination!")); ModConfig.save(); return;
        }
        if (!player.getAbilities().instabuild) { player.getAbilities().mayfly = true; player.getAbilities().flying = true; player.fallDistance = 0.0f; }
        if (lastPos != null) { double movedDist = pos.distanceTo(lastPos); if (movedDist < 0.5) stuckTicks++; else stuckTicks = 0; }
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0; lastPos = pos;
            double speed = ModConfig.flyToCoordsSpeed * 0.05 * 20;
            int etaSeconds = (int) (distance / Math.max(speed, 0.1));
            String eta = etaSeconds > 60 ? String.format("%dm %ds", etaSeconds / 60, etaSeconds % 60) : String.format("%ds", etaSeconds);
            player.sendOverlayMessage(Component.literal(String.format("\u00a7b[FlyTo] \u00a7f%.0f blocks | ETA: %s | Speed: %.1fx", distance, eta, ModConfig.flyToCoordsSpeed)));
        }
        Vec3 direction = target.subtract(pos).normalize();
        float baseSpeed = 0.05f * ModConfig.flyToCoordsSpeed;
        double speedFactor = distance < SLOWDOWN_DISTANCE ? MIN_SPEED_FACTOR + (1.0 - MIN_SPEED_FACTOR) * (distance / SLOWDOWN_DISTANCE) : 1.0;
        double extraY = 0;
        if (horizDist > 3) {
            BlockPos ahead1 = new BlockPos((int) Math.floor(pos.x + direction.x * 2), (int) Math.floor(pos.y), (int) Math.floor(pos.z + direction.z * 2));
            BlockPos ahead1Up = ahead1.above();
            boolean blocked = !client.level.getBlockState(ahead1).isAir() || !client.level.getBlockState(ahead1Up).isAir();
            if (blocked || stuckTicks > 20) extraY = 0.3;
            if (stuckTicks > 60 && !client.level.getBlockState(ahead1).isAir()) BlockBreaker.tryBreak(client, ahead1);
        }
        if (stuckTicks > 40) extraY = 0.5;
        float speed = (float) (baseSpeed * speedFactor);
        player.setDeltaMovement(direction.x * speed, direction.y * speed + extraY, direction.z * speed);
        float targetYaw = (float) (Math.atan2(-direction.x, direction.z) * (180.0 / Math.PI));
        float targetPitch = (float) (Math.atan2(-direction.y, Math.sqrt(direction.x * direction.x + direction.z * direction.z)) * (180.0 / Math.PI));
        float yawDiff = targetYaw - player.getYRot();
        while (yawDiff > 180) yawDiff -= 360; while (yawDiff < -180) yawDiff += 360;
        player.setYRot(player.getYRot() + yawDiff * 0.15f);
        player.setXRot(player.getXRot() + (targetPitch - player.getXRot()) * 0.15f);
    }

    public static void onDisable() { isNavigating = false; lastPos = null; stuckTicks = 0; tickCounter = 0; }
}
