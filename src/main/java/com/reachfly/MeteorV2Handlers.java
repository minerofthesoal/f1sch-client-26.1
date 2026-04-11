package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.Comparator;
import java.util.List;

public class MeteorV2Handlers {

    private static int elytraFlyCooldown = 0;
    private static int surroundCooldown = 0;
    private static int crystalAuraCooldown = 0;
    private static int anchorAuraCooldown = 0;
    private static int holeFillerCooldown = 0;
    private static int autoTrapCooldown = 0;
    private static int reversalCooldown = 0;
    private static Vec3 lastHitDirection = null;
    private static double lastHealth = -1;

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        tickElytraFly(client);
        tickSurround(client);
        tickCrystalAura(client);
        tickAnchorAura(client);
        tickHoleFiller(client);
        tickAutoTrap(client);
        tickReversal(client);
    }

    // --- ElytraFly: control elytra flight speed/direction based on look angle ---
    private static void tickElytraFly(Minecraft client) {
        if (!ModConfig.elytraFlyEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || !p.isFallFlying()) return;
        if (elytraFlyCooldown > 0) { elytraFlyCooldown--; return; }

        float speed = ModConfig.elytraFlySpeed;
        float yaw = (float) Math.toRadians(p.getYRot());
        float pitch = (float) Math.toRadians(p.getXRot());

        double motionX = -Math.sin(yaw) * Math.cos(pitch) * speed * 0.05;
        double motionY = -Math.sin(pitch) * speed * 0.05;
        double motionZ = Math.cos(yaw) * Math.cos(pitch) * speed * 0.05;

        Vec3 vel = p.getDeltaMovement();
        p.setDeltaMovement(vel.x + motionX, vel.y + motionY, vel.z + motionZ);

        // Cap velocity to prevent extreme speeds
        Vec3 newVel = p.getDeltaMovement();
        double maxSpeed = speed * 1.5;
        double horizSpeed = Math.sqrt(newVel.x * newVel.x + newVel.z * newVel.z);
        if (horizSpeed > maxSpeed) {
            double scale = maxSpeed / horizSpeed;
            p.setDeltaMovement(newVel.x * scale, newVel.y, newVel.z * scale);
        }
    }

    // --- Surround: place obsidian around feet for crystal PvP ---
    private static void tickSurround(Minecraft client) {
        if (!ModConfig.surroundEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (surroundCooldown > 0) { surroundCooldown--; return; }
        surroundCooldown = 4;

        int obsSlot = findItemInHotbar(p, Items.OBSIDIAN);
        if (obsSlot < 0) return;

        BlockPos center = p.blockPosition();
        BlockPos[] offsets = {
                center.north(), center.south(), center.east(), center.west(),
                center.north().below(), center.south().below(), center.east().below(), center.west().below()
        };

        int prevSlot = p.getInventory().selected;
        p.getInventory().selected = obsSlot;

        for (BlockPos pos : offsets) {
            BlockState state = client.level.getBlockState(pos);
            if (state.isAir()) {
                gm.useItemOn(p, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                p.swing(InteractionHand.MAIN_HAND);
                break; // one block per tick for legit appearance
            }
        }

        p.getInventory().selected = prevSlot;
    }

    // --- CrystalAura: auto place/detonate end crystals near enemy players ---
    private static void tickCrystalAura(Minecraft client) {
        if (!ModConfig.crystalAuraEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (crystalAuraCooldown > 0) { crystalAuraCooldown--; return; }
        crystalAuraCooldown = 2;

        // First, try to detonate existing crystals near enemies
        List<EndCrystal> crystals = client.level.getEntitiesOfClass(
                EndCrystal.class, p.getBoundingBox().inflate(6.0));
        for (EndCrystal crystal : crystals) {
            // Check if any enemy player is nearby the crystal
            List<Player> nearbyPlayers = client.level.getEntitiesOfClass(
                    Player.class, crystal.getBoundingBox().inflate(6.0));
            for (Player target : nearbyPlayers) {
                if (target == p || target.isSpectator()) continue;
                double distToTarget = crystal.distanceTo(target);
                if (distToTarget < 6.0) {
                    gm.attack(p, crystal);
                    p.swing(InteractionHand.MAIN_HAND);
                    return;
                }
            }
        }

        // Then try to place a crystal near enemies
        int crystalSlot = findItemInHotbar(p, Items.END_CRYSTAL);
        if (crystalSlot < 0) return;

        Player nearestEnemy = findNearestEnemy(client, p, 6.0);
        if (nearestEnemy == null) return;

        BlockPos enemyPos = nearestEnemy.blockPosition();
        BlockPos[] placementSpots = {
                enemyPos.below(), enemyPos.north().below(), enemyPos.south().below(),
                enemyPos.east().below(), enemyPos.west().below()
        };

        int prevSlot = p.getInventory().selected;
        p.getInventory().selected = crystalSlot;

        for (BlockPos pos : placementSpots) {
            BlockState state = client.level.getBlockState(pos);
            if (state.getBlock() == Blocks.OBSIDIAN || state.getBlock() == Blocks.BEDROCK) {
                BlockState above = client.level.getBlockState(pos.above());
                if (above.isAir()) {
                    gm.useItemOn(p, InteractionHand.MAIN_HAND,
                            new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                    p.swing(InteractionHand.MAIN_HAND);
                    break;
                }
            }
        }

        p.getInventory().selected = prevSlot;
    }

    // --- AnchorAura: use respawn anchors for PvP ---
    private static void tickAnchorAura(Minecraft client) {
        if (!ModConfig.anchorAuraEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (anchorAuraCooldown > 0) { anchorAuraCooldown--; return; }
        anchorAuraCooldown = 5;

        // Only works in non-nether dimensions
        if (client.level.dimensionType().respawnAnchorWorks()) return;

        Player target = findNearestEnemy(client, p, 5.0);
        if (target == null) return;

        int anchorSlot = findItemInHotbar(p, Items.RESPAWN_ANCHOR);
        int glowstoneSlot = findItemInHotbar(p, Items.GLOWSTONE);
        if (anchorSlot < 0 || glowstoneSlot < 0) return;

        BlockPos placePos = target.blockPosition().below();
        BlockState state = client.level.getBlockState(placePos);

        // Place anchor if not already placed
        if (state.getBlock() != Blocks.RESPAWN_ANCHOR) {
            if (!state.isAir()) return;
            int prevSlot = p.getInventory().selected;
            p.getInventory().selected = anchorSlot;
            gm.useItemOn(p, InteractionHand.MAIN_HAND,
                    new BlockHitResult(Vec3.atCenterOf(placePos), Direction.UP, placePos, false));
            p.swing(InteractionHand.MAIN_HAND);
            p.getInventory().selected = prevSlot;
            return;
        }

        // Charge with glowstone then activate
        int prevSlot = p.getInventory().selected;
        p.getInventory().selected = glowstoneSlot;
        gm.useItemOn(p, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(placePos), Direction.UP, placePos, false));

        // Activate (right-click with empty hand or non-glowstone)
        p.getInventory().selected = anchorSlot >= 0 ? anchorSlot : 0;
        gm.useItemOn(p, InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(placePos), Direction.UP, placePos, false));
        p.swing(InteractionHand.MAIN_HAND);
        p.getInventory().selected = prevSlot;
    }

    // --- HoleFiller: fill 1x1 holes near player with obsidian ---
    private static void tickHoleFiller(Minecraft client) {
        if (!ModConfig.holeFillerEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (holeFillerCooldown > 0) { holeFillerCooldown--; return; }
        holeFillerCooldown = 3;

        int obsSlot = findItemInHotbar(p, Items.OBSIDIAN);
        if (obsSlot < 0) return;

        BlockPos playerPos = p.blockPosition();
        int radius = 4;

        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                BlockPos base = playerPos.offset(x, -1, z);
                // Check if it's a 1x1 hole: solid floor, air at feet, surrounded on 4 sides
                if (!client.level.getBlockState(base).isAir()) continue;
                BlockPos below = base.below();
                if (client.level.getBlockState(below).isAir()) continue; // not a 1-deep hole
                // Check 4 surrounding blocks at feet level
                boolean surrounded = !client.level.getBlockState(base.north()).isAir()
                        && !client.level.getBlockState(base.south()).isAir()
                        && !client.level.getBlockState(base.east()).isAir()
                        && !client.level.getBlockState(base.west()).isAir();
                if (!surrounded) continue;

                int prevSlot = p.getInventory().selected;
                p.getInventory().selected = obsSlot;
                gm.useItemOn(p, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(base), Direction.UP, base, false));
                p.swing(InteractionHand.MAIN_HAND);
                p.getInventory().selected = prevSlot;
                return; // one per tick
            }
        }
    }

    // --- AutoTrap: place obsidian above enemy heads ---
    private static void tickAutoTrap(Minecraft client) {
        if (!ModConfig.autoTrapEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (autoTrapCooldown > 0) { autoTrapCooldown--; return; }
        autoTrapCooldown = 3;

        int obsSlot = findItemInHotbar(p, Items.OBSIDIAN);
        if (obsSlot < 0) return;

        Player target = findNearestEnemy(client, p, 4.0);
        if (target == null) return;

        BlockPos headPos = target.blockPosition().above();
        BlockPos aboveHead = headPos.above();

        // Place blocks around and above head to trap
        BlockPos[] trapPositions = {
                aboveHead,
                headPos.north(), headPos.south(), headPos.east(), headPos.west(),
                aboveHead.north(), aboveHead.south(), aboveHead.east(), aboveHead.west()
        };

        int prevSlot = p.getInventory().selected;
        p.getInventory().selected = obsSlot;

        for (BlockPos pos : trapPositions) {
            if (client.level.getBlockState(pos).isAir()) {
                gm.useItemOn(p, InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                p.swing(InteractionHand.MAIN_HAND);
                p.getInventory().selected = prevSlot;
                return; // one block per tick
            }
        }

        p.getInventory().selected = prevSlot;
    }

    // --- Reversal: counter-attack when hit, boost toward attacker ---
    private static void tickReversal(Minecraft client) {
        if (!ModConfig.reversalEnabled) return;
        LocalPlayer p = client.player;
        MultiPlayerGameMode gm = client.gameMode;
        if (p == null || gm == null) return;
        if (reversalCooldown > 0) { reversalCooldown--; return; }

        double currentHealth = p.getHealth();
        if (lastHealth < 0) { lastHealth = currentHealth; return; }

        // Detect damage taken
        if (currentHealth < lastHealth) {
            // Find the closest player who likely hit us
            Player attacker = findNearestEnemy(client, p, 6.0);
            if (attacker != null) {
                reversalCooldown = 10;

                // Boost toward attacker
                Vec3 dir = attacker.position().subtract(p.position()).normalize();
                double boostStrength = 0.8;
                p.setDeltaMovement(p.getDeltaMovement().add(
                        dir.x * boostStrength, 0.2, dir.z * boostStrength));

                // Counter-attack
                if (p.distanceTo(attacker) < 4.0) {
                    gm.attack(p, attacker);
                    p.swing(InteractionHand.MAIN_HAND);
                }

                lastHitDirection = dir;
            }
        }
        lastHealth = currentHealth;
    }

    // --- Utility methods ---

    private static Player findNearestEnemy(Minecraft client, LocalPlayer self, double range) {
        List<Player> players = client.level.getEntitiesOfClass(
                Player.class, self.getBoundingBox().inflate(range));
        return players.stream()
                .filter(pl -> pl != self && !pl.isSpectator() && pl.isAlive())
                .min(Comparator.comparingDouble(self::distanceTo))
                .orElse(null);
    }

    private static int findItemInHotbar(LocalPlayer player, net.minecraft.world.item.Item item) {
        for (int i = 0; i < 9; i++) {
            if (player.getInventory().getItem(i).is(item)) return i;
        }
        return -1;
    }
}
