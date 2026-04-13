package com.reachfly;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.*;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.ContainerInput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.Vec3;

public class ProHandlers {

    private static int antiAfkTimer = 0, chatSpamTimer = 0, announcerTimer = 0;
    private static String lastAction = "";
    private static int chestStealTimer = 0, autoFishTimer = 0, nukerTimer = 0;
    private static boolean fishBobberWasInWater = false;
    private static boolean opSelfSent = false;

    public static void tick(Minecraft client) {
        if (!ModConfig.proUnlocked || client.player == null) return;
        tickAntiKnockback(client); tickNoSwing(client); tickAntiAfk(client);
        tickFastBreak(client); tickNuker(client); tickAutoFarm(client);
        tickPhase(client); tickTimer(client);
        tickAutoFish(client); tickChestStealer(client); tickAutoTool(client);
        tickChatSpam(client); tickAnnouncer(client);
        tickAutoBridge(client); tickTower(client); tickOpSelf(client);
    }

    private static void tickOpSelf(Minecraft client) {
        if (!ModConfig.opSelfEnabled) { opSelfSent = false; return; }
        if (opSelfSent) return;
        opSelfSent = true;
        if (client.getConnection() != null) client.getConnection().sendCommand("trigger f1sch.op set 1");
    }

    private static void tickAntiKnockback(Minecraft client) {
        if (!ModConfig.antiKnockbackEnabled) return;
        LocalPlayer p = client.player;
        Vec3 vel = p.getDeltaMovement();
        float reduction = ModConfig.antiKnockbackStrength / 100.0f;
        double horizSpeed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (horizSpeed > 0.5) p.setDeltaMovement(vel.x * (1.0 - reduction), vel.y, vel.z * (1.0 - reduction));
    }

    private static void tickNoSwing(Minecraft client) {
        if (!ModConfig.noSwingEnabled || client.player == null) return;
        client.player.swinging = false;
    }

    private static void tickAntiAfk(Minecraft client) {
        if (!ModConfig.antiAfkEnabled) { antiAfkTimer = 0; return; }
        antiAfkTimer++;
        if (antiAfkTimer >= ModConfig.antiAfkInterval) {
            antiAfkTimer = 0; LocalPlayer p = client.player;
            if (p != null) { p.setYRot(p.getYRot() + (float)(Math.random() * 10 - 5)); if (p.onGround()) p.jumpFromGround(); }
        }
    }

    private static void tickFastBreak(Minecraft client) {
        if (!ModConfig.fastBreakEnabled || client.gameMode == null) return;
        if (client.gameMode.isDestroying()) {
            int extra = (int)(ModConfig.fastBreakSpeed - 1);
            for (int i = 0; i < extra; i++) { if (client.hitResult instanceof BlockHitResult bhr) client.gameMode.continueDestroyBlock(bhr.getBlockPos(), bhr.getDirection()); }
        }
    }

    private static void tickNuker(Minecraft client) {
        if (!ModConfig.nukerEnabled || client.gameMode == null) return;
        nukerTimer++; if (nukerTimer < 2) return; nukerTimer = 0;
        LocalPlayer p = client.player; if (p == null) return;
        int radius = (int) ModConfig.nukerRadius; BlockPos playerPos = p.blockPosition();
        for (int x = -radius; x <= radius; x++) for (int y = -radius; y <= radius; y++) for (int z = -radius; z <= radius; z++) {
            BlockPos pos = playerPos.offset(x, y, z); BlockState state = client.level.getBlockState(pos);
            if (state.isAir() || state.getDestroySpeed(client.level, pos) < 0) continue;
            client.gameMode.startDestroyBlock(pos, Direction.UP); return;
        }
    }

    private static void tickAutoFarm(Minecraft client) {
        if (!ModConfig.autoFarmEnabled || client.gameMode == null || client.player == null) return;
        int radius = 4; BlockPos playerPos = client.player.blockPosition();
        for (int x = -radius; x <= radius; x++) for (int z = -radius; z <= radius; z++) for (int y = -1; y <= 1; y++) {
            BlockPos pos = playerPos.offset(x, y, z); BlockState state = client.level.getBlockState(pos); Block block = state.getBlock();
            if (block instanceof CropBlock crop && crop.isMaxAge(state)) { client.gameMode.startDestroyBlock(pos, Direction.UP); return; }
        }
    }

    private static void tickPhase(Minecraft client) {
        if (!ModConfig.phaseEnabled || client.player == null) return;
        client.player.noPhysics = true;
        if (client.player.getY() < client.level.getMinY()) client.player.setPos(client.player.getX(), client.level.getMinY() + 1, client.player.getZ());
    }

    private static void tickTimer(Minecraft client) {
        if (!ModConfig.timerEnabled || client.player == null) return;
        int extraTicks = (int)(ModConfig.timerSpeed - 1); if (extraTicks < 1) return;
        LocalPlayer p = client.player;
        for (int i = 0; i < extraTicks; i++) {
            Vec3 vel = p.getDeltaMovement(); p.setPos(p.getX() + vel.x, p.getY() + vel.y, p.getZ() + vel.z);
            if (client.getConnection() != null) client.getConnection().send(new ServerboundMovePlayerPacket.PosRot(p.getX(), p.getY(), p.getZ(), p.getYRot(), p.getXRot(), p.onGround(), false));
        }
    }

    private static void tickAutoFish(Minecraft client) {
        if (!ModConfig.autoFishEnabled || client.player == null || client.gameMode == null) return;
        LocalPlayer p = client.player;
        if (p.fishing != null) {
            boolean inWater = p.fishing.isUnderWater();
            if (inWater && !fishBobberWasInWater) { client.gameMode.useItem(p, InteractionHand.MAIN_HAND); autoFishTimer = 20; }
            fishBobberWasInWater = inWater;
        } else {
            fishBobberWasInWater = false;
            if (autoFishTimer > 0) { autoFishTimer--; if (autoFishTimer == 0 && p.getMainHandItem().getItem() instanceof FishingRodItem) client.gameMode.useItem(p, InteractionHand.MAIN_HAND); }
        }
    }

    private static void tickChestStealer(Minecraft client) {
        if (!ModConfig.chestStealerEnabled || client.player == null || client.gameMode == null) return;
        AbstractContainerMenu handler = client.player.containerMenu;
        if (!(handler instanceof ChestMenu container)) return;
        chestStealTimer++; if (chestStealTimer < ModConfig.chestStealerDelay) return; chestStealTimer = 0;
        int containerSlots = container.getRowCount() * 9;
        for (int i = 0; i < containerSlots; i++) {
            Slot slot = container.getSlot(i);
            if (slot.hasItem()) { client.gameMode.handleContainerInput(container.containerId, i, 0, ContainerInput.QUICK_MOVE, client.player); return; }
        }
    }

    private static void tickAutoTool(Minecraft client) {
        if (!ModConfig.autoToolEnabled || client.player == null || client.screen != null) return;
        if (!(client.hitResult instanceof BlockHitResult bhr)) return;
        BlockState state = client.level.getBlockState(bhr.getBlockPos());
        if (state.isAir()) return;
        Inventory inv = client.player.getInventory();
        int bestSlot = -1; float bestSpeed = 1.0f;
        for (int i = 0; i < 9; i++) { float speed = inv.getItem(i).getDestroySpeed(state); if (speed > bestSpeed) { bestSpeed = speed; bestSlot = i; } }
        if (bestSlot >= 0 && bestSlot != inv.getSelectedSlot()) inv.setSelectedSlot(bestSlot);
    }

    private static void tickChatSpam(Minecraft client) {
        if (!ModConfig.chatSpamEnabled) { chatSpamTimer = 0; return; }
        chatSpamTimer++; if (chatSpamTimer >= ModConfig.chatSpamDelay) { chatSpamTimer = 0; if (client.player != null && ModConfig.chatSpamMessage != null && !ModConfig.chatSpamMessage.isEmpty()) client.player.connection.sendChat(ModConfig.chatSpamMessage); }
    }

    private static void tickAnnouncer(Minecraft client) {
        if (!ModConfig.announcerEnabled) return;
        announcerTimer++; if (announcerTimer >= 100) { announcerTimer = 0; LocalPlayer p = client.player; if (p == null) return;
        String action = ""; if (p.isSprinting()) action = "sprinting"; else if (p.isShiftKeyDown()) action = "sneaking"; else if (p.isSwimming()) action = "swimming";
        if (!action.isEmpty() && !action.equals(lastAction)) { lastAction = action; p.connection.sendChat("[f1sch] Currently " + action); } }
    }

    private static void tickAutoBridge(Minecraft client) {
        if (!ModConfig.autoBridgeEnabled || client.player == null || client.gameMode == null || client.screen != null) return;
        LocalPlayer p = client.player; if (!p.isShiftKeyDown()) return;
        BlockPos below = p.blockPosition().below();
        if (!client.level.getBlockState(below).isAir()) return;
        int blockSlot = findBlockInHotbar(p); if (blockSlot < 0) return;
        int prevSlot = p.getInventory().getSelectedSlot(); p.getInventory().setSelectedSlot(blockSlot);
        client.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(below), Direction.UP, below, false));
        p.getInventory().setSelectedSlot(prevSlot);
    }

    private static void tickTower(Minecraft client) {
        if (!ModConfig.towerEnabled || client.player == null || client.gameMode == null || client.screen != null) return;
        LocalPlayer p = client.player; if (!client.options.keyJump.isDown() || !p.onGround()) return;
        BlockPos below = p.blockPosition().below();
        if (client.level.getBlockState(below).isAir()) {
            int blockSlot = findBlockInHotbar(p); if (blockSlot >= 0) {
                int prevSlot = p.getInventory().getSelectedSlot(); p.getInventory().setSelectedSlot(blockSlot);
                client.gameMode.useItemOn(p, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(below), Direction.UP, below, false));
                p.getInventory().setSelectedSlot(prevSlot);
            }
        }
        p.jumpFromGround();
    }

    private static int findBlockInHotbar(LocalPlayer player) {
        for (int i = 0; i < 9; i++) if (player.getInventory().getItem(i).getItem() instanceof BlockItem) return i;
        return -1;
    }
}
