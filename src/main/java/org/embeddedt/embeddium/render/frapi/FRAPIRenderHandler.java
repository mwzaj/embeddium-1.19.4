package org.embeddedt.embeddium.render.frapi;

import com.mojang.blaze3d.vertex.PoseStack;
import me.jellysquid.mods.sodium.client.render.chunk.compile.ChunkBuildBuffers;
import me.jellysquid.mods.sodium.client.render.chunk.compile.pipeline.BlockRenderContext;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3fc;

public interface FRAPIRenderHandler {
    boolean INDIGO_PRESENT = isIndigoPresent();

    private static boolean isIndigoPresent() {
        boolean indigoPresent = false;
        try {
            Class.forName("net.fabricmc.fabric.impl.client.indigo.renderer.render.BlockRenderContext");
            indigoPresent = true;
        } catch(Throwable ignored) {}
        return indigoPresent;
    }

    boolean isFaceCulled(@Nullable Direction face);

    void reset();

    void renderEmbeddium(BlockRenderContext ctx, PoseStack mStack, RandomSource random);

    void flush(ChunkBuildBuffers buffers, Vector3fc origin);
}
