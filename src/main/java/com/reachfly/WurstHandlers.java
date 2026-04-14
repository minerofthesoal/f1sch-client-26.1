package com.reachfly;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundMovePlayerPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TridentItem;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

public class WurstHandlers {

    private static boolean criticalJumped = false;
    private static int bunnyHopDelay = 0;
    private static int airJumpCooldown = 0;
    private static int longJumpTimer = 0;
    private static boolean wasOnGround = true;
    private static int autoMLGCooldown = 0;
    private static int blinkTickCounter = 0;
    private static Vec3 blinkStartPos = null;
    private static boolean blinkActive = false;

    public static void tick(Minecraft client) {
        if (client.player == null || client.level == null) return;
        tickCriticals(client);
        tickBunnyHop(client);
        tickSpider(client);
        tickGlide(client);
        tickHighJump(client);
        tickDolphin(client);
        tickAutoSword(client);
        tickSneak(client);
        tickPanic(client);
        tickAntiHunger(client);
        tickTriggerBot(client);
        tickFastPlace(client);
        tickParkour(client);
        tickNoSlowdown(client);
        tickAntiBlind(client);
        tickAutoWalk(client);
        tickAirJump(client);
        tickNoWeb(client);
        tickFlightPlus(client);
        tickLongJump(client);
        tickAutoMLG(client);
        tickBlink(client);
    }

    private static void tickCriticals(Minecraft client) {
        if (!ModConfig.criticalsEnabled) {
            criticalJumped = false;
            return;
        }
        LocalPlayer p = client.player;
        if (p == null || !p.onGround()) return;
        if (p.getAttackStrengthScale(0.0f) >= 0.95f && !criticalJumped) {
            // Mini-jump high enough for critical hit detection (must be falling when hitting)
            p.setDeltaMovement(p.getDeltaMovement().add(0, 0.25, 0));
            p.hurtMarked = true;
            criticalJumped = true;
        }
        if (p.getAttackStrengthScale(0.0f) < 0.5f) criticalJumped = false;
    }

    private static void tickBunnyHop(Minecraft client) {
        if (!ModConfig.bunnyHopEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null || !p.isSprinting() || !p.onGround()) return;
        bunnyHopDelay++;
        if (bunnyHopDelay < 1) return;
        bunnyHopDelay = 0;
        p.jumpFromGround();
    }

    private static void tickSpider(Minecraft client) {
        if (!ModConfig.spiderEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        if (p.horizontalCollision && !p.onGround()) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, 0.2, vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickGlide(Minecraft client) {
        if (!ModConfig.glideEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || p.onGround()) return;
        if (p.getDeltaMovement().y < 0) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, Math.max(vel.y, -0.06), vel.z);
            p.fallDistance = 0.0f;
        }
    }

    private static void tickHighJump(Minecraft client) {
        if (!ModConfig.highJumpEnabled) return;
        LocalPlayer p = client.player;
        if (p == null) return;
        Vec3 vel = p.getDeltaMovement();
        if (!p.onGround() && vel.y > 0.38 && vel.y < 0.45) {
            double boost = (ModConfig.highJumpHeight - 1.0) * 0.2;
            p.setDeltaMovement(vel.x, vel.y + boost, vel.z);
        }
    }

    private static void tickDolphin(Minecraft client) {
        if (!ModConfig.dolphinEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || !p.isInWater()) return;
        Vec3 vel = p.getDeltaMovement();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed > 0.01) p.setDeltaMovement(vel.x * 1.4, vel.y, vel.z * 1.4);
        if (p.isSwimming() && p.getXRot() < -10)
            p.setDeltaMovement(p.getDeltaMovement().add(0, 0.04, 0));
        if (p.isUnderWater() && !p.isShiftKeyDown())
            p.setDeltaMovement(p.getDeltaMovement().add(0, 0.02, 0));
    }

    private static void tickAutoSword(Minecraft client) {
        if (!ModConfig.autoSwordEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null || client.hitResult == null) return;
        if (!(client.hitResult instanceof EntityHitResult ehr)) return;
        if (!(ehr.getEntity() instanceof LivingEntity)) return;
        int bestSlot = -1;
        float bestDamage = 0;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = p.getInventory().getItem(i);
            Item item = stack.getItem();
            float damage = 0;
            if (item == Items.WOODEN_SWORD || item == Items.STONE_SWORD
                    || item == Items.IRON_SWORD || item == Items.GOLDEN_SWORD
                    || item == Items.DIAMOND_SWORD || item == Items.NETHERITE_SWORD) {
                damage = 7;
            } else if (item instanceof AxeItem) {
                damage = 9;
            } else if (item instanceof TridentItem) {
                damage = 9;
            }
            if (damage > bestDamage) {
                bestDamage = damage;
                bestSlot = i;
            }
        }
        if (bestSlot >= 0 && bestSlot != p.getInventory().getSelectedSlot())
            p.getInventory().setSelectedSlot(bestSlot);
    }

    private static void tickSneak(Minecraft client) {
        if (!ModConfig.sneakEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null) return;
        p.setShiftKeyDown(true);
    }

    private static void tickPanic(Minecraft client) {
        if (!ModConfig.panicEnabled) return;
        ModConfig.reachEnabled = false;
        ModConfig.flyEnabled = false;
        ModConfig.autoHitEnabled = false;
        ModConfig.killAuraEnabled = false;
        ModConfig.killAuraPlusEnabled = false;
        ModConfig.espEnabled = false;
        ModConfig.noFallEnabled = false;
        ModConfig.fullbrightEnabled = false;
        ModConfig.speedEnabled = false;
        ModConfig.xrayEnabled = false;
        ModConfig.knockbackEnabled = false;
        ModConfig.jesusEnabled = false;
        ModConfig.scaffoldEnabled = false;
        ModConfig.criticalsEnabled = false;
        ModConfig.bunnyHopEnabled = false;
        ModConfig.spiderEnabled = false;
        ModConfig.glideEnabled = false;
        ModConfig.highJumpEnabled = false;
        ModConfig.dolphinEnabled = false;
        ModConfig.autoSwordEnabled = false;
        ModConfig.sneakEnabled = false;
        ModConfig.triggerBotEnabled = false;
        ModConfig.phaseEnabled = false;
        ModConfig.timerEnabled = false;
        ModConfig.elytraFlyEnabled = false;
        ModConfig.surroundEnabled = false;
        ModConfig.crystalAuraEnabled = false;
        ModConfig.invMoveEnabled = false;
        ModConfig.fastPlaceEnabled = false;
        ModConfig.parkourEnabled = false;
        ModConfig.noSlowdownEnabled = false;
        ModConfig.antiBlindEnabled = false;
        ModConfig.autoWalkEnabled = false;
        ModConfig.airJumpEnabled = false;
        ModConfig.noWebEnabled = false;
        ModConfig.flightPlusEnabled = false;
        ModConfig.longJumpEnabled = false;
        ModConfig.autoMLGEnabled = false;
        ModConfig.blinkEnabled = false;
        ModConfig.anchorAuraEnabled = false;
        ModConfig.holeFillerEnabled = false;
        ModConfig.autoTrapEnabled = false;
        ModConfig.reversalEnabled = false;
        ModConfig.panicEnabled = false;
        ModConfig.save();
        if (client.player != null)
            client.player.sendOverlayMessage(
                    Component.literal("\u00a7c[f1sch] PANIC - All hacks disabled!"));
    }

    private static void tickAntiHunger(Minecraft client) {
        if (!ModConfig.antiHungerEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.getConnection() == null) return;
        if (!p.onGround() && p.getDeltaMovement().y < 0) {
            client.getConnection().send(
                    new ServerboundMovePlayerPacket.StatusOnly(true, p.horizontalCollision));
        }
    }

    private static void tickTriggerBot(Minecraft client) {
        if (!ModConfig.triggerBotEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null || client.gameMode == null) return;
        if (p.getAttackStrengthScale(0.0f) < 1.0f) return;
        if (client.hitResult instanceof EntityHitResult ehr) {
            Entity target = ehr.getEntity();
            if (target instanceof LivingEntity living && living.isAlive()) {
                if (ModConfig.autoHitPlayersOnly && !(target instanceof Player)) return;
                client.gameMode.attack(p, target);
                p.swing(InteractionHand.MAIN_HAND);
            }
        }
    }

    private static void tickFastPlace(Minecraft client) {
        if (!ModConfig.fastPlaceEnabled || client.player == null) return;
        try {
            java.lang.reflect.Field f = Minecraft.class.getDeclaredField("missTime");
            f.setAccessible(true);
            f.setInt(client, 0);
        } catch (Exception ignored) {
        }
    }

    private static void tickParkour(Minecraft client) {
        if (!ModConfig.parkourEnabled) return;
        LocalPlayer p = client.player;
        if (p == null || client.screen != null || !p.onGround()) return;
        Vec3 vel = p.getDeltaMovement();
        double speed = Math.sqrt(vel.x * vel.x + vel.z * vel.z);
        if (speed < 0.05) return;
        double px = p.getX() + vel.x * 2;
        double pz = p.getZ() + vel.z * 2;
        BlockPos checkPos = new BlockPos(
                (int) Math.floor(px),
                (int) Math.floor(p.getY() - 0.5),
                (int) Math.floor(pz));
        if (client.level.getBlockState(checkPos).isAir()) p.jumpFromGround();
    }

    private static void tickNoSlowdown(Minecraft client) {
        if (!ModConfig.noSlowdownEnabled || client.player == null) return;
        LocalPlayer p = client.player;
        if (p.isUsingItem()) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x * 1.4, vel.y, vel.z * 1.4);
        }
    }

    private static void tickAntiBlind(Minecraft client) {
        if (!ModConfig.antiBlindEnabled || client.player == null) return;
        LocalPlayer p = client.player;
        if (p.hasEffect(MobEffects.BLINDNESS)) p.removeEffect(MobEffects.BLINDNESS);
        if (p.hasEffect(MobEffects.DARKNESS)) p.removeEffect(MobEffects.DARKNESS);
        if (p.hasEffect(MobEffects.NAUSEA)) p.removeEffect(MobEffects.NAUSEA);
    }

    private static void tickAutoWalk(Minecraft client) {
        if (!ModConfig.autoWalkEnabled || client.player == null || client.screen != null) return;
        LocalPlayer p = client.player;
        net.minecraft.world.entity.player.Input current = p.input.keyPresses;
        p.input.keyPresses = new net.minecraft.world.entity.player.Input(
                true, current.backward(), current.left(), current.right(),
                current.jump(), current.shift(), current.sprint());
    }

    private static void tickAirJump(Minecraft client) {
        if (!ModConfig.airJumpEnabled || client.player == null || client.screen != null) return;
        LocalPlayer p = client.player;
        if (airJumpCooldown > 0) airJumpCooldown--;
        if (p.onGround()) {
            wasOnGround = true;
            return;
        }
        if (p.input.keyPresses.jump() && !wasOnGround && airJumpCooldown <= 0) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x, 0.42, vel.z);
            p.fallDistance = 0.0f;
            airJumpCooldown = 10;
        }
        if (!p.input.keyPresses.jump()) wasOnGround = false;
    }

    private static void tickNoWeb(Minecraft client) {
        if (!ModConfig.noWebEnabled || client.player == null) return;
        LocalPlayer p = client.player;
        BlockPos pos = p.blockPosition();
        if (client.level.getBlockState(pos).getBlock() == net.minecraft.world.level.block.Blocks.COBWEB
                || client.level.getBlockState(pos.above()).getBlock() == net.minecraft.world.level.block.Blocks.COBWEB) {
            Vec3 vel = p.getDeltaMovement();
            p.setDeltaMovement(vel.x * 5.0, vel.y, vel.z * 5.0);
        }
    }

    private static void tickFlightPlus(Minecraft client) {
        if (!ModConfig.flightPlusEnabled || client.player == null || client.screen != null) return;
        LocalPlayer p = client.player;
        float speed = ModConfig.flightPlusSpeed;
        float yaw = (float) Math.toRadians(p.getYRot());
        double motionX = 0, motionY = 0, motionZ = 0;
        net.minecraft.world.entity.player.Input input = p.input.keyPresses;
        if (input.forward()) {
            motionX -= Math.sin(yaw) * speed * 0.1;
            motionZ += Math.cos(yaw) * speed * 0.1;
        }
        if (input.backward()) {
            motionX += Math.sin(yaw) * speed * 0.1;
            motionZ -= Math.cos(yaw) * speed * 0.1;
        }
        if (input.left()) {
            motionX += Math.cos(yaw) * speed * 0.1;
            motionZ += Math.sin(yaw) * speed * 0.1;
        }
        if (input.right()) {
            motionX -= Math.cos(yaw) * speed * 0.1;
            motionZ -= Math.sin(yaw) * speed * 0.1;
        }
        if (input.jump()) motionY = speed * 0.1;
        if (input.shift()) motionY = -speed * 0.1;
        if (input.sprint()) {
            motionX *= 2.0;
            motionZ *= 2.0;
        }
        p.setDeltaMovement(motionX, motionY, motionZ);
        p.fallDistance = 0.0f;
    }

    private static void tickLongJump(Minecraft client) {
        if (!ModConfig.longJumpEnabled || client.player == null) return;
        LocalPlayer p = client.player;
        if (longJumpTimer > 0) longJumpTimer--;
        Vec3 vel = p.getDeltaMovement();
        if (!p.onGround() && vel.y > 0.38 && vel.y < 0.45 && longJumpTimer <= 0) {
            float yaw = (float) Math.toRadians(p.getYRot());
            double boost = ModConfig.longJumpBoost * 0.3;
            p.setDeltaMovement(
                    vel.x - Math.sin(yaw) * boost,
                    vel.y + 0.1,
                    vel.z + Math.cos(yaw) * boost);
            longJumpTimer = 20;
        }
    }

    private static void tickAutoMLG(Minecraft client) {
        if (!ModConfig.autoMLGEnabled || client.player == null || client.gameMode == null) return;
        LocalPlayer p = client.player;
        if (autoMLGCooldown > 0) {
            autoMLGCooldown--;
            return;
        }
        if (p.onGround() || p.getDeltaMovement().y > -0.5 || p.fallDistance < 5.0f) return;
        BlockPos below = p.blockPosition();
        for (int i = 0; i < 5; i++) {
            below = below.below();
            if (!client.level.getBlockState(below).isAir()) {
                int waterSlot = -1;
                for (int slot = 0; slot < 9; slot++) {
                    if (p.getInventory().getItem(slot).is(Items.WATER_BUCKET)) {
                        waterSlot = slot;
                        break;
                    }
                }
                if (waterSlot < 0) return;
                int prevSlot = p.getInventory().getSelectedSlot();
                p.getInventory().setSelectedSlot(waterSlot);
                p.setXRot(90.0f);
                client.gameMode.useItem(p, InteractionHand.MAIN_HAND);
                p.swing(InteractionHand.MAIN_HAND);
                p.getInventory().setSelectedSlot(prevSlot);
                autoMLGCooldown = 40;
                return;
            }
        }
    }

    private static void tickBlink(Minecraft client) {
        if (!ModConfig.blinkEnabled) {
            if (blinkActive) {
                blinkActive = false;
                blinkStartPos = null;
            }
            return;
        }
        LocalPlayer p = client.player;
        if (p == null || client.getConnection() == null) return;
        if (!blinkActive) {
            blinkActive = true;
            blinkStartPos = p.position();
            blinkTickCounter = 0;
        }
        blinkTickCounter++;
        if (blinkTickCounter >= 100) {
            ModConfig.blinkEnabled = false;
            blinkActive = false;
            blinkStartPos = null;
            p.sendOverlayMessage(
                    Component.literal("\u00a7e[Blink] Auto-released (5s limit)"));
        }
    }
}
