package com.github.lunatrius.schematica.client.renderer.chunk;

import com.github.lunatrius.schematica.client.renderer.chunk.overlay.RenderOverlayList;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.client.renderer.chunk.CompiledChunk;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.EnumWorldBlockLayer;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class OverlayRenderDispatcher extends ChunkRenderDispatcher {
    @Override
    public ListenableFuture<Object> uploadChunk(final EnumWorldBlockLayer layer, final WorldRenderer buffer, final RenderChunk renderChunk, final CompiledChunk compiledChunk) {
        if (!Minecraft.getMinecraft().isCallingFromMinecraftThread() || OpenGlHelper.useVbo()) {
            return super.uploadChunk(layer, buffer, renderChunk, compiledChunk);
        }

        uploadDisplayList(buffer, ((RenderOverlayList) renderChunk).getDisplayList(layer, compiledChunk), renderChunk);

        buffer.setTranslation(0.0, 0.0, 0.0);
        return Futures.immediateFuture(null);
    }
}
