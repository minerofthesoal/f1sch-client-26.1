package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Baritone-style pathfinding / automation handler.
 * Modes: idle, goto, mine, follow, farm, build
 */
public class BaritoneHandler {

    private static boolean wasActive = false;
    private static int tickCounter = 0;
    private static Vec3 lastPos = null;
    private static int stuckTicks = 0;
    private static float detourYawOffset = 0;
    private static int detourTicks = 0;
    private static int mineBreakTimer = 0;
    private static BlockPos currentMineTarget = null;

    public static void tick(Minecraft client) {
        if (!ModConfig.baritoneEnabled) {
            if (wasActive) { releaseAllKeys(client); wasActive = false; reset(); }
            return;
        }
        if (client.player == null || client.level == null || client.screen != null) return;

        String mode = ModConfig.baritoneMode;
        if (mode == null) mode = "idle";

        switch (mode) {
            case "goto" -> tickGoto(client);
            case "mine" -> tickMine(client);
            case "follow" -> tickFollow(client);
            case "farm" -> tickFarm(client);
            case "build" -> tickBuild(client);
            default -> { /* idle - do nothing */ }
        }

        wasActive = true;
    }

    // ======================== GOTO ========================

    private static void tickGoto(Minecraft client) {
        LocalPlayer player = client.player;
        double targetX = ModConfig.baritoneGotoX;
        double targetY = ModConfig.baritoneGotoY;
        double targetZ = ModConfig.baritoneGotoZ;
        Vec3 target = new Vec3(targetX + 0.5, targetY, targetZ + 0.5);
        Vec3 pos = player.position();
        double horizDist = Math.sqrt((pos.x - target.x) * (pos.x - target.x) + (pos.z - target.z) * (pos.z - target.z));

        if (!wasActive || lastPos == null) {
            lastPos = pos; stuckTicks = 0; detourYawOffset = 0; detourTicks = 0; tickCounter = 0;
            player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a7eGoing to X:%.0f Y:%.0f Z:%.0f (%.0f blocks)".formatted(targetX, targetY, targetZ, horizDist)));
        }

        if (horizDist < 2.0 && Math.abs(pos.y - target.y) < 4) {
            ModConfig.baritoneMode = "idle";
            releaseAllKeys(client);
            player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a7aArrived at destination!"));
            ModConfig.save();
            return;
        }

        tickNavigation(client, player, target, horizDist);
    }

    // ======================== MINE ========================

    private static void tickMine(Minecraft client) {
        LocalPlayer player = client.player;
        String blockName = ModConfig.baritoneMineBlock;
        if (blockName == null || blockName.isEmpty()) return;

        Identifier blockId;
        if (blockName.contains(":")) {
            String[] parts = blockName.split(":", 2);
            blockId = Identifier.fromNamespaceAndPath(parts[0], parts[1]);
        } else {
            blockId = Identifier.fromNamespaceAndPath("minecraft", blockName);
        }

        Block targetBlock = BuiltInRegistries.BLOCK.getValue(blockId);
        if (targetBlock == null) return;

        int radius = (int) ModConfig.baritoneMineRadius;
        BlockPos playerPos = player.blockPosition();

        // Find nearest matching block
        if (currentMineTarget != null) {
            BlockState state = client.level.getBlockState(currentMineTarget);
            if (!state.is(targetBlock)) currentMineTarget = null;
        }

        if (currentMineTarget == null) {
            double bestDist = Double.MAX_VALUE;
            for (int x = -radius; x <= radius; x++) {
                for (int y = -radius; y <= radius; y++) {
                    for (int z = -radius; z <= radius; z++) {
                        BlockPos pos = playerPos.offset(x, y, z);
                        BlockState state = client.level.getBlockState(pos);
                        if (state.is(targetBlock)) {
                            double dist = playerPos.distSqr(pos);
                            if (dist < bestDist) { bestDist = dist; currentMineTarget = pos; }
                        }
                    }
                }
            }
        }

        if (currentMineTarget == null) {
            if (tickCounter % 40 == 0)
                player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a77No %s found within %d blocks".formatted(blockName, radius)));
            tickCounter++;
            return;
        }

        // Navigate to block if far away
        Vec3 pos = player.position();
        Vec3 target = Vec3.atCenterOf(currentMineTarget);
        double dist = pos.distanceTo(target);

        if (dist > 4.5) {
            tickNavigation(client, player, target, dist);
        } else {
            releaseAllKeys(client);
            // Auto-select best tool
            if (ModConfig.baritoneAutoTool) selectBestTool(client, client.level.getBlockState(currentMineTarget));
            // Face block and break
            faceBlock(player, target);
            if (client.gameMode != null) {
                client.gameMode.startDestroyBlock(currentMineTarget, Direction.UP);
                client.gameMode.continueDestroyBlock(currentMineTarget, Direction.UP);
                player.swing(InteractionHand.MAIN_HAND);
            }
            if (client.level.getBlockState(currentMineTarget).isAir()) currentMineTarget = null;
        }
        tickCounter++;
    }

    // ======================== FOLLOW ========================

    private static void tickFollow(Minecraft client) {
        LocalPlayer player = client.player;
        float followRange = ModConfig.baritoneFollowRange;

        // Find nearest other player
        Player closest = null;
        double closestDist = Double.MAX_VALUE;
        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity instanceof Player other && other != player) {
                double dist = player.distanceTo(other);
                if (dist < closestDist) { closestDist = dist; closest = other; }
            }
        }

        if (closest == null) {
            releaseAllKeys(client);
            if (tickCounter % 40 == 0) player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a77No players nearby to follow"));
            tickCounter++;
            return;
        }

        Vec3 target = closest.position();
        Vec3 pos = player.position();
        double horizDist = Math.sqrt((pos.x - target.x) * (pos.x - target.x) + (pos.z - target.z) * (pos.z - target.z));

        if (horizDist <= followRange) {
            releaseAllKeys(client);
            // Face the followed player
            faceBlock(player, target);
        } else {
            tickNavigation(client, player, target, horizDist);
        }
        tickCounter++;
    }

    // ======================== FARM ========================

    private static void tickFarm(Minecraft client) {
        LocalPlayer player = client.player;
        int radius = (int) ModConfig.baritoneFarmRadius;
        BlockPos playerPos = player.blockPosition();

        // Find nearest mature crop
        BlockPos cropTarget = null;
        double bestDist = Double.MAX_VALUE;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -3; y <= 3; y++) {
                    BlockPos pos = playerPos.offset(x, y, z);
                    BlockState state = client.level.getBlockState(pos);
                    Block block = state.getBlock();
                    if (block instanceof CropBlock crop && crop.isMaxAge(state)) {
                        double dist = playerPos.distSqr(pos);
                        if (dist < bestDist) { bestDist = dist; cropTarget = pos; }
                    }
                }
            }
        }

        if (cropTarget == null) {
            releaseAllKeys(client);
            if (tickCounter % 60 == 0)
                player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a77No mature crops within %d blocks".formatted(radius)));
            tickCounter++;
            return;
        }

        Vec3 pos = player.position();
        Vec3 target = Vec3.atCenterOf(cropTarget);
        double dist = pos.distanceTo(target);

        if (dist > 4.0) {
            tickNavigation(client, player, target, dist);
        } else {
            releaseAllKeys(client);
            faceBlock(player, target);
            if (client.gameMode != null) {
                client.gameMode.startDestroyBlock(cropTarget, Direction.UP);
                player.swing(InteractionHand.MAIN_HAND);
            }
        }
        tickCounter++;
    }

    // ======================== BUILD ========================

    private static void tickBuild(Minecraft client) {
        LocalPlayer player = client.player;
        // Build mode: place blocks below/around player from hotbar
        // Simplified builder - places blocks at feet level ahead of player
        if (client.gameMode == null) return;

        BlockPos below = player.blockPosition().below();
        if (client.level.getBlockState(below).isAir()) {
            int blockSlot = findBlockInHotbar(player);
            if (blockSlot < 0) {
                if (ModConfig.baritoneBuildAutoGrab) {
                    if (tickCounter % 40 == 0)
                        player.sendSystemMessage(Component.literal("\u00a7b[Baritone] \u00a7cNo blocks in hotbar"));
                }
                tickCounter++;
                return;
            }
            int prevSlot = player.getInventory().getSelectedSlot();
            player.getInventory().setSelectedSlot(blockSlot);
            client.gameMode.useItemOn(player, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(below), Direction.UP, below, false));
            player.getInventory().setSelectedSlot(prevSlot);
        }

        // Also place block ahead at feet level
        float yaw = player.getYRot();
        double dx = -Math.sin(Math.toRadians(yaw));
        double dz = Math.cos(Math.toRadians(yaw));
        BlockPos ahead = new BlockPos((int) Math.floor(player.getX() + dx), (int) Math.floor(player.getY() - 1), (int) Math.floor(player.getZ() + dz));
        if (client.level.getBlockState(ahead).isAir()) {
            int blockSlot = findBlockInHotbar(player);
            if (blockSlot >= 0) {
                int prevSlot = player.getInventory().getSelectedSlot();
                player.getInventory().setSelectedSlot(blockSlot);
                client.gameMode.useItemOn(player, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(ahead), Direction.UP, ahead, false));
                player.getInventory().setSelectedSlot(prevSlot);
            }
        }
        tickCounter++;
    }

    // ======================== NAVIGATION ========================

    private static void tickNavigation(Minecraft client, LocalPlayer player, Vec3 target, double horizDist) {
        Vec3 pos = player.position();

        // Stuck detection
        tickCounter++;
        if (tickCounter >= 20) {
            tickCounter = 0;
            if (lastPos != null) {
                double moved = Math.sqrt((pos.x - lastPos.x) * (pos.x - lastPos.x) + (pos.z - lastPos.z) * (pos.z - lastPos.z));
                if (moved < 0.5) stuckTicks += 20;
                else {
                    stuckTicks = Math.max(0, stuckTicks - 10);
                    if (detourTicks > 0 && moved > 1.0) {
                        detourTicks = Math.max(0, detourTicks - 20);
                        if (detourTicks <= 0) detourYawOffset = 0;
                    }
                }
            }
            lastPos = pos;
        }

        // Detour logic
        if (stuckTicks > 40 && detourTicks <= 0) {
            detourYawOffset = (detourYawOffset == 0) ? 70 : -detourYawOffset;
            if (Math.abs(detourYawOffset) < 50) detourYawOffset = 70;
            detourTicks = 60;
            stuckTicks = 0;
        }
        if (stuckTicks > 100) { detourYawOffset = 180; detourTicks = 40; stuckTicks = 0; }
        if (detourTicks > 0) { detourTicks--; if (detourTicks <= 0) detourYawOffset = 0; }

        // Yaw calculation
        float targetYaw = (float) (Math.atan2(-(target.x - pos.x), (target.z - pos.z)) * (180.0 / Math.PI)) + detourYawOffset;
        float yawDiff = targetYaw - player.getYRot();
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        player.setYRot(player.getYRot() + yawDiff * 0.25f);

        // Jump / obstacle detection
        float facingYaw = player.getYRot();
        double faceDx = -Math.sin(Math.toRadians(facingYaw));
        double faceDz = Math.cos(Math.toRadians(facingYaw));
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

        // Break through obstacles if stuck
        if (solidAtFeet && solidAtHead) {
            if (stuckTicks > 30) BlockBreaker.tryBreak(client, feetAhead);
            else if (detourTicks <= 0) {
                detourYawOffset = (detourYawOffset >= 0) ? 90 : -90;
                if (detourYawOffset == 0) detourYawOffset = 90;
                detourTicks = 40;
            }
            shouldJump = false;
        }
        if (stuckTicks > 80 && solidAtFeet) BlockBreaker.tryBreak(client, feetAhead);

        // Avoid danger
        if (ModConfig.baritoneAvoidDanger) {
            BlockPos belowFeet = new BlockPos((int) Math.floor(pos.x + faceDx * 2), (int) Math.floor(pos.y - 1), (int) Math.floor(pos.z + faceDz * 2));
            BlockState belowState = client.level.getBlockState(belowFeet);
            if (belowState.liquid()) {
                // Don't walk into lava/water gaps
                if (detourTicks <= 0) {
                    detourYawOffset = 90;
                    detourTicks = 30;
                }
            }
        }

        // Set player movement directly instead of using KeyMapping
        boolean canSprint = ModConfig.baritoneSprint && player.getFoodData().getFoodLevel() > 6 && !inLiquid;
        player.input.keyPresses = new net.minecraft.world.entity.player.Input(
                true, false, false, false, shouldJump, false, canSprint);
    }

    // ======================== UTILITY ========================

    private static boolean isSolid(BlockState state) {
        return !state.isAir() && !state.liquid() && state.isSolid();
    }

    private static void faceBlock(LocalPlayer player, Vec3 target) {
        Vec3 eyePos = player.getEyePosition();
        double dx = target.x - eyePos.x;
        double dy = target.y - eyePos.y;
        double dz = target.z - eyePos.z;
        double horizDist = Math.sqrt(dx * dx + dz * dz);
        float yaw = (float) (Math.atan2(-dx, dz) * (180.0 / Math.PI));
        float pitch = (float) (-(Math.atan2(dy, horizDist) * (180.0 / Math.PI)));
        float yawDiff = yaw - player.getYRot();
        while (yawDiff > 180) yawDiff -= 360;
        while (yawDiff < -180) yawDiff += 360;
        player.setYRot(player.getYRot() + yawDiff * 0.5f);
        float pitchDiff = pitch - player.getXRot();
        player.setXRot(player.getXRot() + pitchDiff * 0.5f);
    }

    private static void selectBestTool(Minecraft client, BlockState state) {
        if (client.player == null) return;
        var inv = client.player.getInventory();
        int bestSlot = -1;
        float bestSpeed = 1.0f;
        for (int i = 0; i < 9; i++) {
            float speed = inv.getItem(i).getDestroySpeed(state);
            if (speed > bestSpeed) { bestSpeed = speed; bestSlot = i; }
        }
        if (bestSlot >= 0 && bestSlot != inv.getSelectedSlot()) inv.setSelectedSlot(bestSlot);
    }

    private static int findBlockInHotbar(LocalPlayer player) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).getItem() instanceof BlockItem) return i;
        }
        return -1;
    }

    private static void releaseAllKeys(Minecraft client) {
        if (client.player != null) {
            client.player.input.keyPresses = new net.minecraft.world.entity.player.Input(
                    false, false, false, false, false, false, false);
        }
    }

    private static void reset() {
        tickCounter = 0;
        lastPos = null;
        stuckTicks = 0;
        detourYawOffset = 0;
        detourTicks = 0;
        mineBreakTimer = 0;
        currentMineTarget = null;
    }

    public static void onDisable() {
        Minecraft client = Minecraft.getInstance();
        releaseAllKeys(client);
        reset();
    }

    public static boolean parseCommand(String message) {
        if (!message.startsWith("#")) return false;
        String[] parts = message.trim().split("\\s+");
        String cmd = parts[0].toLowerCase();
        switch (cmd) {
            case "#goto" -> { if (parts.length >= 4) { try { ModConfig.baritoneGotoX = Float.parseFloat(parts[1]); ModConfig.baritoneGotoY = Float.parseFloat(parts[2]); ModConfig.baritoneGotoZ = Float.parseFloat(parts[3]); ModConfig.baritoneMode = "goto"; ModConfig.baritoneEnabled = true; ModConfig.save(); } catch (NumberFormatException ignored) {} } return true; }
            case "#mine" -> { if (parts.length >= 2) { ModConfig.baritoneMineBlock = parts[1]; ModConfig.baritoneMode = "mine"; ModConfig.baritoneEnabled = true; ModConfig.save(); } return true; }
            case "#follow" -> { ModConfig.baritoneMode = "follow"; ModConfig.baritoneEnabled = true; ModConfig.save(); return true; }
            case "#farm" -> { ModConfig.baritoneMode = "farm"; ModConfig.baritoneEnabled = true; ModConfig.save(); return true; }
            case "#explore" -> { ModConfig.baritoneMode = "explore"; ModConfig.baritoneEnabled = true; ModConfig.save(); return true; }
            case "#build" -> { ModConfig.baritoneMode = "build"; ModConfig.baritoneEnabled = true; ModConfig.save(); return true; }
            case "#stop" -> { stop(); return true; }
            default -> { return false; }
        }
    }

    public static void stop() {
        ModConfig.baritoneMode = "idle";
        reset();
        Minecraft client = Minecraft.getInstance();
        releaseAllKeys(client);
        ModConfig.save();
    }
}
