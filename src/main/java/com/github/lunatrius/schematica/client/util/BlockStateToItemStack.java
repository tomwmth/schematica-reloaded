package com.github.lunatrius.schematica.client.util;

import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.reference.Reference;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MovingObjectPosition;

public class BlockStateToItemStack {
    public static ItemStack getItemStack(final IBlockState blockState, final MovingObjectPosition rayTraceResult, final SchematicWorld world, final BlockPos pos, final EntityPlayer player) {
        final Block block = blockState.getBlock();

        try {
            final ItemStack itemStack = block.getPickBlock(rayTraceResult, world, pos, player);
            if (itemStack != null && itemStack.getItem() != null) {
                return itemStack;
            }
        } catch (final Exception e) {
            Reference.logger.debug("Could not get the pick block for: {}", blockState, e);
        }

        return null;
    }
}
