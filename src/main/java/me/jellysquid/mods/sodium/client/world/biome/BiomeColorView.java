package me.jellysquid.mods.sodium.client.world.biome;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;

public interface BiomeColorView {
    int getColor(BiomeColorSource resolver, int blockX, int blockY, int blockZ);

    Holder<Biome> getBiomeFabric(BlockPos pos);

    boolean hasBiomes();

    Object getBlockEntityRenderData(BlockPos pos);
}
