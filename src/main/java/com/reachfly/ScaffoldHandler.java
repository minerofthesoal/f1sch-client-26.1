package com.reachfly;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class ScaffoldHandler {

    private static int cooldown = 0;

    public static void tick(Minecraft client) {
        if (!ModConfig.scaffoldEnabled) return;
        if (client.player == null || client.level == null || client.gameMode == null || client.screen != null) return;
        if (cooldown > 0) { cooldown--; return; }
        LocalPlayer player = client.player;
        BlockPos below = new BlockPos((int) Math.floor(player.getX()), (int) Math.floor(player.getY() - 1), (int) Math.floor(player.getZ()));
        BlockState belowState = client.level.getBlockState(below);
        if (!belowState.isAir() && !belowState.liquid()) return;
        int origSlot = player.getInventory().getSelectedSlot();
        int blockSlot = -1;
        for (int i = 0; i < 9; i++) { if (player.getInventory().getItem(i).getItem() instanceof BlockItem) { blockSlot = i; break; } }
        if (blockSlot < 0) return;
        player.getInventory().setSelectedSlot(blockSlot);
        Direction[] dirs = {Direction.DOWN, Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST, Direction.UP};
        for (Direction dir : dirs) {
            BlockPos neighbor = below.relative(dir);
            BlockState neighborState = client.level.getBlockState(neighbor);
            if (!neighborState.isAir() && !neighborState.liquid()) {
                BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(neighbor), dir.getOpposite(), below, false);
                client.gameMode.useItemOn(player, InteractionHand.MAIN_HAND, hit);
                cooldown = 2; break;
            }
        }
        player.getInventory().setSelectedSlot(origSlot);
    }
}
