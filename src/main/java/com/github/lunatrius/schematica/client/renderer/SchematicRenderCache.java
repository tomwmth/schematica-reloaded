package com.github.lunatrius.schematica.client.renderer;

import com.github.lunatrius.schematica.config.Configuration;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RegionRenderCache;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class SchematicRenderCache extends RegionRenderCache {
    private final Minecraft minecraft = Minecraft.getMinecraft();

    public SchematicRenderCache(final World world, final BlockPos from, final BlockPos to, final int subtract) {
        super(world, from, to, subtract);
    }

    @Override
    public IBlockState getBlockState(final BlockPos pos) {
        final BlockPos schPos = ClientProxy.schematic.position;
        if (schPos == null) {
            return Blocks.air.getDefaultState();
        }

        final BlockPos realPos = pos.add(schPos);
        final World world = this.minecraft.theWorld;

        if (world == null || !world.isAirBlock(realPos) && !Configuration.general.isExtraAirBlock(world.getBlockState(realPos).getBlock())) {
            return Blocks.air.getDefaultState();
        }

        return super.getBlockState(pos);
    }
}
