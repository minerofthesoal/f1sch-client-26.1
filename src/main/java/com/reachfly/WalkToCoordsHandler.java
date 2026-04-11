package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.KeyMapping;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.network.chat.Component;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class WalkToCoordsHandler {

    private static boolean isNavigating = false;
    private static final double ARRIVAL_DISTANCE = 2.0;
    private static int tickCounter = 0;
    private static Vec3 lastPos = null;
    private static int stuckTicks = 0;
    private static float detourYawOffset = 0;
    private static int detourTicks = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.walkToCoordsEnabled) return;
        if (client.player == null || client.level == null || client.screen != null) return;
        LocalPlayer player = client.player;
        double targetX = ModConfig.walkToX, targetY = ModConfig.walkToY, targetZ = ModConfig.walkToZ;
        Vec3 target = new Vec3(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3 pos = player.position();
        double horizDist = Math.sqrt((pos.x - target.x) * (pos.x - target.x) + (pos.z - target.z) * (pos.z - target.z));

        if (!isNavigating) {
            isNavigating = true; lastPos = pos; stuckTicks = 0; detourYawOffset = 0; detourTicks = 0; tickCounter = 0;
            player.displayClientMessage(Component.literal("\u00a7b[f1sch] \u00a7eWalking to X:%.0f Y:%.0f Z:%.0f (%.0f blocks)".formatted(targetX, targetY, targetZ, horizDist)), true);
        }
        if (horizDist < ARRIVAL_DISTANCE && Math.abs(pos.y - target.y) < 4) {
            ModConfig.walkToCoordsEnabled = false; isNavigating = false; releaseAllKeys(client);
            player.displayClientMessage(Component.literal("\u00a7b[f1sch] \u00a7aArrived at destination!"), true); ModConfig.save(); return;
        }
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            if (lastPos != null) { double moved = Math.sqrt((pos.x - lastPos.x) * (pos.x - lastPos.x) + (pos.z - lastPos.z) * (pos.z - lastPos.z)); if (moved < 0.5) stuckTicks += 20; else { stuckTicks = Math.max(0, stuckTicks - 10); if (detourTicks > 0 && moved > 1.0) { detourTicks = Math.max(0, detourTicks - 20); if (detourTicks <= 0) detourYawOffset = 0; } } }
            lastPos = pos;
            player.displayClientMessage(Component.literal(String.format("\u00a7b[WalkTo] \u00a7f%.0f blocks remaining%s", horizDist, stuckTicks > 40 ? " \u00a7e(rerouting...)" : "")), true);
        }
        if (stuckTicks > 40 && detourTicks <= 0) { detourYawOffset = (detourYawOffset == 0) ? 70 : -detourYawOffset; if (Math.abs(detourYawOffset) < 50) detourYawOffset = 70; detourTicks = 60; stuckTicks = 0; }
        if (stuckTicks > 100) { detourYawOffset = 180; detourTicks = 40; stuckTicks = 0; }
        if (detourTicks > 0) { detourTicks--; if (detourTicks <= 0) detourYawOffset = 0; }

        float targetYaw = (float) (Math.atan2(-(target.x - pos.x), (target.z - pos.z)) * (180.0 / Math.PI)) + detourYawOffset;
        float yawDiff = targetYaw - player.getYRot();
        while (yawDiff > 180) yawDiff -= 360; while (yawDiff < -180) yawDiff += 360;
        player.setYRot(player.getYRot() + yawDiff * 0.25f);
        KeyMapping.set(client.options.keyUp.getDefaultKey(), true); client.options.keyUp.setDown(true);

        float facingYaw = player.getYRot();
        double faceDx = -Math.sin(Math.toRadians(facingYaw)), faceDz = Math.cos(Math.toRadians(facingYaw));
        BlockPos feetAhead = new BlockPos((int) Math.floor(pos.x + faceDx), (int) Math.floor(pos.y), (int) Math.floor(pos.z + faceDz));
        BlockPos headAhead = new BlockPos((int) Math.floor(pos.x + faceDx), (int) Math.floor(pos.y + 1), (int) Math.floor(pos.z + faceDz));
        boolean solidAtFeet = isSolid(client.level.getBlockState(feetAhead));
        boolean solidAtHead = isSolid(client.level.getBlockState(headAhead));
        boolean clearAboveFeet = !isSolid(client.level.getBlockState(feetAhead.above())) && !isSolid(client.level.getBlockState(feetAhead.above().above()));
        BlockPos groundAhead = new BlockPos((int) Math.floor(pos.x + faceDx * 1.5), (int) Math.floor(pos.y - 1), (int) Math.floor(pos.z + faceDz * 1.5));
        boolean gapAhead = !isSolid(client.level.getBlockState(groundAhead)) && !isSolid(client.level.getBlockState(groundAhead.below())) && !client.level.getBlockState(groundAhead).liquid();
        boolean inLiquid = player.isInWater() || player.isInLava();
        boolean shouldJump = false;
        if (solidAtFeet && !solidAtHead && clearAboveFeet) shouldJump = true;
        if (target.y > pos.y + 0.5) shouldJump = true;
        if (inLiquid) shouldJump = true;
        if (gapAhead && !inLiquid && horizDist > 3) shouldJump = true;
        if (solidAtFeet && solidAtHead) { if (stuckTicks > 30) BlockBreaker.tryBreak(client, feetAhead); else if (detourTicks <= 0) { detourYawOffset = (detourYawOffset >= 0) ? 90 : -90; if (detourYawOffset == 0) detourYawOffset = 90; detourTicks = 40; } shouldJump = false; }
        if (stuckTicks > 80 && solidAtFeet) BlockBreaker.tryBreak(client, feetAhead);
        KeyMapping.set(client.options.keyJump.getDefaultKey(), shouldJump); client.options.keyJump.setDown(shouldJump);
        boolean canSprint = player.getFoodData().getFoodLevel() > 6 && !inLiquid;
        KeyMapping.set(client.options.keySprint.getDefaultKey(), canSprint); client.options.keySprint.setDown(canSprint);
    }

    private static boolean isSolid(BlockState state) { return !state.isAir() && !state.liquid() && state.isSolid(); }

    public static void onDisable() {
        if (isNavigating) { Minecraft client = Minecraft.getInstance(); releaseAllKeys(client); }
        isNavigating = false; lastPos = null; stuckTicks = 0; detourYawOffset = 0; detourTicks = 0; tickCounter = 0;
    }

    private static void releaseAllKeys(Minecraft client) {
        KeyMapping.set(client.options.keyUp.getDefaultKey(), false); client.options.keyUp.setDown(false);
        KeyMapping.set(client.options.keyJump.getDefaultKey(), false); client.options.keyJump.setDown(false);
        KeyMapping.set(client.options.keySprint.getDefaultKey(), false); client.options.keySprint.setDown(false);
    }
}
