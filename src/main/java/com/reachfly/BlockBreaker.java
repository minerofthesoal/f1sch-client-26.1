package com.reachfly;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;

public class BlockBreaker {

    private static BlockPos currentTarget = null;
    private static int breakProgress = 0;

    public static boolean tryBreak(Minecraft client, BlockPos pos) {
        if (client.player == null || client.gameMode == null || client.level == null) return false;
        BlockState state = client.level.getBlockState(pos);
        if (state.isAir() || state.liquid()) return false;
        if (state.getDestroySpeed(client.level, pos) < 0) return false;
        if (!pos.equals(currentTarget)) { currentTarget = pos; breakProgress = 0; }
        client.gameMode.startDestroyBlock(pos, Direction.UP);
        client.gameMode.continueDestroyBlock(pos, Direction.UP);
        client.player.swing(net.minecraft.world.InteractionHand.MAIN_HAND);
        breakProgress++;
        if (client.level.getBlockState(pos).isAir()) { currentTarget = null; breakProgress = 0; return false; }
        return true;
    }

    public static void reset() { currentTarget = null; breakProgress = 0; }
}
