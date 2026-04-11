package com.reachfly;

import com.mojang.math.Axis;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import org.joml.Matrix4f;
import org.joml.Vector4f;

public class EspRenderer {

    private static final ResourceLocation ESP_HUD_ID =
            ResourceLocation.fromNamespaceAndPath("reachfly", "esp_renderer");

    public static void register() {
        HudElementRegistry.addLast(ESP_HUD_ID, EspRenderer::renderEsp);
    }

    public static void renderEsp(GuiGraphics ctx, DeltaTracker tickCounter) {
        Minecraft client = Minecraft.getInstance();
        if (!ModConfig.espEnabled || client.player == null || client.level == null) return;
        if (client.options.hideGui) return;

        LocalPlayer p = client.player;
        Camera camera = client.gameRenderer.getMainCamera();
        float partialTick = tickCounter.getGameTimeDeltaPartialTick(true);

        int screenWidth = ctx.guiWidth();
        int screenHeight = ctx.guiHeight();
        int centerX = screenWidth / 2;
        int centerY = screenHeight / 2;

        // Build view-projection matrix
        Matrix4f projMatrix = client.gameRenderer.getProjectionMatrix(client.options.fov().get().floatValue());
        Matrix4f viewMatrix = new Matrix4f();
        viewMatrix.identity();
        viewMatrix.rotate(Axis.XP.rotationDegrees(camera.getXRot()));
        viewMatrix.rotate(Axis.YP.rotationDegrees(camera.getYRot() + 180.0f));

        Matrix4f mvpMatrix = new Matrix4f(projMatrix).mul(viewMatrix);

        Vec3 cameraPos = camera.getPosition();

        for (Entity entity : client.level.entitiesForRendering()) {
            if (entity == p) continue;
            if (!(entity instanceof LivingEntity living)) continue;
            if (!living.isAlive()) continue;

            // Filter by type
            if (entity instanceof Player) {
                if (!ModConfig.espPlayers) continue;
            } else if (entity instanceof Monster) {
                if (!ModConfig.espHostile) continue;
            } else if (entity instanceof Animal) {
                if (!ModConfig.espPassive) continue;
            } else {
                if (!ModConfig.espPassive) continue;
            }

            // Interpolated entity position
            double ex = entity.xOld + (entity.getX() - entity.xOld) * partialTick;
            double ey = entity.yOld + (entity.getY() - entity.yOld) * partialTick;
            double ez = entity.zOld + (entity.getZ() - entity.zOld) * partialTick;

            // Relative to camera
            double rx = ex - cameraPos.x;
            double ry = ey - cameraPos.y + entity.getBbHeight() * 0.5;
            double rz = ez - cameraPos.z;

            // Project to screen
            int[] screenPos = worldToScreen(mvpMatrix, rx, ry, rz, screenWidth, screenHeight);
            if (screenPos == null) continue; // Behind camera

            // Choose color by entity type
            int color;
            if (entity instanceof Player) {
                color = 0xFFFF5555; // Red for players
            } else if (entity instanceof Monster) {
                color = 0xFFFF8800; // Orange for hostile
            } else {
                color = 0xFF55FF55; // Green for passive
            }

            // Draw tracer line from crosshair to entity
            if (ModConfig.espLines) {
                drawLine(ctx, centerX, centerY, screenPos[0], screenPos[1], color);
            }

            // Draw path trace dots along ground between player and entity
            if (ModConfig.espPathTrace) {
                drawPathTrace(ctx, mvpMatrix, cameraPos, p, ex, ey, ez,
                        screenWidth, screenHeight, color);
            }
        }

        // Also render server ESP entities if available
        if (ServerSyncHandler.serverEspActive) {
            for (EspDataPayload.EntityEntry entry : ServerSyncHandler.serverEspEntities) {
                double rx = entry.x() - cameraPos.x;
                double ry = entry.y() - cameraPos.y + 1.0;
                double rz = entry.z() - cameraPos.z;

                int[] screenPos = worldToScreen(mvpMatrix, rx, ry, rz, screenWidth, screenHeight);
                if (screenPos == null) continue;

                int color = entry.type().contains("player") ? 0xFFFF5555 : 0xFFFFAA00;

                if (ModConfig.espLines) {
                    drawLine(ctx, centerX, centerY, screenPos[0], screenPos[1], color);
                }
            }
        }
    }

    private static int[] worldToScreen(Matrix4f mvp, double x, double y, double z,
                                        int screenWidth, int screenHeight) {
        Vector4f vec = new Vector4f((float) x, (float) y, (float) z, 1.0f);
        mvp.transform(vec);

        if (vec.w <= 0.0f) return null; // Behind camera

        float ndcX = vec.x / vec.w;
        float ndcY = vec.y / vec.w;

        int sx = (int) ((1.0f + ndcX) * 0.5f * screenWidth);
        int sy = (int) ((1.0f - ndcY) * 0.5f * screenHeight);

        return new int[]{sx, sy};
    }

    private static void drawLine(GuiGraphics ctx, int x1, int y1, int x2, int y2, int color) {
        // Bresenham line drawing using fill rectangles
        int dx = Math.abs(x2 - x1);
        int dy = Math.abs(y2 - y1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int err = dx - dy;
        int steps = 0;
        int maxSteps = Math.max(dx, dy);
        int step = Math.max(1, maxSteps / 200); // limit pixel count for performance

        int cx = x1, cy = y1;
        while (steps < maxSteps) {
            ctx.fill(cx, cy, cx + 1, cy + 1, color);
            if (cx == x2 && cy == y2) break;
            int e2 = 2 * err;
            if (e2 > -dy) { err -= dy; cx += sx; }
            if (e2 < dx) { err += dx; cy += sy; }
            steps += step;
            if (step > 1) {
                // Skip intermediate pixels for performance
                for (int s = 1; s < step && steps < maxSteps; s++) {
                    if (cx == x2 && cy == y2) break;
                    e2 = 2 * err;
                    if (e2 > -dy) { err -= dy; cx += sx; }
                    if (e2 < dx) { err += dx; cy += sy; }
                }
            }
        }
    }

    private static void drawPathTrace(GuiGraphics ctx, Matrix4f mvp, Vec3 cameraPos,
                                       LocalPlayer player, double ex, double ey, double ez,
                                       int screenWidth, int screenHeight, int color) {
        Vec3 start = player.position();
        int dotCount = 10;
        for (int i = 1; i <= dotCount; i++) {
            double t = (double) i / (dotCount + 1);
            double dx = start.x + (ex - start.x) * t;
            double dz = start.z + (ez - start.z) * t;
            // Ground-level Y: use player Y for simplicity
            double dy = start.y + (ey - start.y) * t;

            double rx = dx - cameraPos.x;
            double ry = dy - cameraPos.y;
            double rz = dz - cameraPos.z;

            int[] dotScreen = worldToScreen(mvp, rx, ry, rz, screenWidth, screenHeight);
            if (dotScreen == null) continue;

            // Draw a small dot
            int dotSize = 2;
            ctx.fill(dotScreen[0] - dotSize, dotScreen[1] - dotSize,
                    dotScreen[0] + dotSize, dotScreen[1] + dotSize,
                    (color & 0x00FFFFFF) | 0xAA000000);
        }
    }
}
