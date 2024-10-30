package com.github.lunatrius.schematica.handler.client;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlay;
import com.github.lunatrius.schematica.config.Configuration;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Thomas Wearmouth
 * Created on 28/10/2024
 */
public final class EspHandler {
    public static final EspHandler INSTANCE = new EspHandler();

    private final Minecraft mc = Minecraft.getMinecraft();
    private final Map<BlockPos, RenderOverlay.BlockType> tracedBlocks = new ConcurrentHashMap<>();

    @SubscribeEvent
    public void onRenderWorld(final RenderWorldLastEvent event) {
        if (Configuration.rendering.esp.getValue() && !this.tracedBlocks.isEmpty()) {
            final boolean wasBlend = GL11.glGetBoolean(GL11.GL_BLEND);
            final boolean wasLineSmooth = GL11.glGetBoolean(GL11.GL_LINE_SMOOTH);
            final boolean wasTexture2D = GL11.glGetBoolean(GL11.GL_TEXTURE_2D);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glDisable(GL11.GL_TEXTURE_2D);

            int amount = 0;
            final int limit = Configuration.rendering.espLimit.getValue();

            for (Map.Entry<BlockPos, RenderOverlay.BlockType> entry : this.tracedBlocks.entrySet()) {
                this.drawTracer(entry.getKey(), entry.getValue(), event.partialTicks);
                if (++amount >= limit) {
                    break;
                }
            }

            if (!wasBlend) GL11.glDisable(GL11.GL_BLEND);
            if (!wasLineSmooth) GL11.glDisable(GL11.GL_LINE_SMOOTH);
            if (wasTexture2D) GL11.glEnable(GL11.GL_TEXTURE_2D);
        }
    }

    public void add(final BlockPos pos, final RenderOverlay.BlockType type) {
        this.tracedBlocks.put(pos, type);
    }

    public void refresh() {
        this.tracedBlocks.clear();
    }

    private void drawTracer(final BlockPos pos, final RenderOverlay.BlockType type, final float partialTicks) {
        final EntityPlayer player = this.mc.thePlayer;
        final RenderManager manager = this.mc.getRenderManager();

        Vec3 center = new Vec3(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D);
        Vec3 eyePos = player.getPositionEyes(partialTicks);
        if (manager.options.thirdPersonView == 0) {
            final double pitch = Math.toRadians(player.rotationPitch + 90.0D);
            final double yaw = Math.toRadians(player.rotationYaw + 90.0D);
            eyePos.addVector(
                    Math.sin(pitch) * Math.cos(yaw),
                    Math.cos(pitch),
                    Math.sin(pitch) * Math.sin(yaw)
            );
        }
        center = relativize(center, manager);
        eyePos = relativize(eyePos, manager);

        final int color = type.color;
        GlStateManager.color(
                (float) (color >> 16 & 255) / 255.0F,
                (float) (color >> 8 & 255) / 255.0F,
                (float) (color & 255) / 255.0F,
                0.65F
        );

        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer renderer = tessellator.getWorldRenderer();
        renderer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        renderer.pos(center.xCoord, center.yCoord, center.zCoord).endVertex();
        renderer.pos(eyePos.xCoord, eyePos.yCoord, eyePos.zCoord).endVertex();
        tessellator.draw();
    }

    private static Vec3 relativize(final Vec3 vec, final RenderManager manager) {
        return vec.subtract(manager.viewerPosX, manager.viewerPosY, manager.viewerPosZ);
    }
}
