package com.github.lunatrius.schematica.client.printer;

import com.github.lunatrius.core.util.math.BlockPosHelper;
import com.github.lunatrius.core.util.math.MBlockPos;
import com.github.lunatrius.schematica.block.state.BlockStateHelper;
import com.github.lunatrius.schematica.client.printer.nbtsync.NBTSync;
import com.github.lunatrius.schematica.client.printer.nbtsync.SyncRegistry;
import com.github.lunatrius.schematica.client.printer.registry.PlacementData;
import com.github.lunatrius.schematica.client.printer.registry.PlacementRegistry;
import com.github.lunatrius.schematica.client.util.BlockStateToItemStack;
import com.github.lunatrius.schematica.client.world.SchematicWorld;
import com.github.lunatrius.schematica.config.Configuration;
import com.github.lunatrius.schematica.proxy.ClientProxy;
import com.github.lunatrius.schematica.reference.Constants;
import com.github.lunatrius.schematica.reference.Reference;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.*;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemBucket;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.BlockFluidBase;

import java.util.*;

public class SchematicPrinter {
    public static final SchematicPrinter INSTANCE = new SchematicPrinter();

    private static final Map<Class<? extends Block>, IProperty<?>> INTERACT_STATE_MAP = ImmutableMap.<Class<? extends Block>, IProperty<?>>builder()
            .put(BlockRedstoneRepeater.class, BlockRedstoneRepeater.DELAY)
            .put(BlockRedstoneComparator.class, BlockRedstoneComparator.MODE)
            .put(BlockTrapDoor.class, BlockTrapDoor.OPEN)
            .put(BlockLever.class, BlockLever.POWERED)
            .put(BlockDoor.class, BlockDoor.OPEN)
            .put(BlockFenceGate.class, BlockFenceGate.OPEN)
            .build();

    private final Minecraft minecraft = Minecraft.getMinecraft();

    private boolean isEnabled = true;
    private boolean isPrinting = false;

    private SchematicWorld schematic = null;
    private byte[][][] timeout = null;
    private HashMap<BlockPos, Integer> syncBlacklist = new HashMap<BlockPos, Integer>();

    public boolean isEnabled() {
        return this.isEnabled;
    }

    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean togglePrinting() {
        this.isPrinting = !this.isPrinting && this.schematic != null;
        return this.isPrinting;
    }

    public boolean isPrinting() {
        return this.isPrinting;
    }

    public void setPrinting(final boolean isPrinting) {
        this.isPrinting = isPrinting;
    }

    public SchematicWorld getSchematic() {
        return this.schematic;
    }

    public void setSchematic(final SchematicWorld schematic) {
        this.isPrinting = false;
        this.schematic = schematic;
        refresh();
    }

    public void refresh() {
        if (this.schematic != null) {
            this.timeout = new byte[this.schematic.getWidth()][this.schematic.getHeight()][this.schematic.getLength()];
        } else {
            this.timeout = null;
        }
        this.syncBlacklist.clear();
    }

    public boolean print(final WorldClient world, final EntityPlayerSP player) {
        final double dX = ClientProxy.playerPosition.x - this.schematic.position.x;
        final double dY = ClientProxy.playerPosition.y - this.schematic.position.y;
        final double dZ = ClientProxy.playerPosition.z - this.schematic.position.z;
        final int x = (int) Math.floor(dX);
        final int y = (int) Math.floor(dY);
        final int z = (int) Math.floor(dZ);
        final int range = Configuration.printer.placeDistance.getValue();

        final int minX = Math.max(0, x - range);
        final int maxX = Math.min(this.schematic.getWidth() - 1, x + range);
        int minY = Math.max(0, y - range);
        int maxY = Math.min(this.schematic.getHeight() - 1, y + range);
        final int minZ = Math.max(0, z - range);
        final int maxZ = Math.min(this.schematic.getLength() - 1, z + range);

        if (minX > maxX || minY > maxY || minZ > maxZ) {
            return false;
        }

        final int slot = player.inventory.currentItem;
        final boolean isSneaking = player.isSneaking();

        switch (schematic.layerMode) {
        case ALL: break;
        case SINGLE_LAYER:
            if (schematic.renderingLayer > maxY) {
                return false;
            }
            maxY = schematic.renderingLayer;
            //$FALL-THROUGH$
        case ALL_BELOW:
            if (schematic.renderingLayer < minY) {
                return false;
            }
            maxY = schematic.renderingLayer;
            break;
        }

        syncSneaking(player, true);

        final double blockReachDistance = this.minecraft.playerController.getBlockReachDistance() - 0.1;
        final double blockReachDistanceSq = blockReachDistance * blockReachDistance;
        for (final MBlockPos pos : BlockPosHelper.getAllInBoxXZY(minX, minY, minZ, maxX, maxY, maxZ)) {
            if (pos.distanceSqToCenter(dX, dY, dZ) > blockReachDistanceSq) {
                continue;
            }

            try {
                if (placeBlock(world, player, pos)) {
                    return syncSlotAndSneaking(player, slot, isSneaking, true);
                }
            } catch (final Exception e) {
                Reference.logger.error("Could not place block!", e);
                return syncSlotAndSneaking(player, slot, isSneaking, false);
            }
        }

        return syncSlotAndSneaking(player, slot, isSneaking, true);
    }

    private boolean syncSlotAndSneaking(final EntityPlayerSP player, final int slot, final boolean isSneaking, final boolean success) {
        player.inventory.currentItem = slot;
        syncSneaking(player, isSneaking);
        return success;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos) {
        final int x = pos.getX();
        final int y = pos.getY();
        final int z = pos.getZ();
        if (this.timeout[x][y][z] > 0) {
            this.timeout[x][y][z]--;
            return false;
        }

        final int wx = this.schematic.position.x + x;
        final int wy = this.schematic.position.y + y;
        final int wz = this.schematic.position.z + z;
        final BlockPos realPos = new BlockPos(wx, wy, wz);

        final IBlockState blockState = this.schematic.getBlockState(pos);
        final IBlockState realBlockState = world.getBlockState(realPos);
        final Block realBlock = realBlockState.getBlock();

        if (BlockStateHelper.areBlockStatesEqual(blockState, realBlockState)) {
            // TODO: clean up this mess
            final NBTSync handler = SyncRegistry.INSTANCE.getHandler(realBlock);
            if (handler != null) {
                this.timeout[x][y][z] = Configuration.printer.timeout.getValue().byteValue();

                Integer tries = this.syncBlacklist.get(realPos);
                if (tries == null) {
                    tries = 0;
                } else if (tries >= 10) {
                    return false;
                }

                Reference.logger.trace("Trying to sync block at {} {}", realPos, tries);
                final boolean success = handler.execute(player, this.schematic, pos, world, realPos);
                if (success) {
                    this.syncBlacklist.put(realPos, tries + 1);
                }

                return success;
            }

            return false;
        }

        if (Configuration.printer.changeState.getValue() && this.arePropertiesEqual(BlockDirectional.FACING, blockState, realBlockState)) {
            boolean interactRequired = false;

            for (final Map.Entry<Class<? extends Block>, IProperty<?>> entry : INTERACT_STATE_MAP.entrySet()) {
                final Class<? extends Block> clazz = entry.getKey();
                final IProperty<?> prop = entry.getValue();
                if (this.stateRequiresInteract(clazz, prop, blockState, realBlockState)) {
                    interactRequired = true;
                    break;
                }
            }

            if (interactRequired) {
                this.syncSneaking(player, false);
                final boolean success = this.minecraft.playerController.onPlayerRightClick(player, world, player.getHeldItem(), realPos, EnumFacing.UP, new Vec3(0, 0, 0));
                if (success) {
                    this.timeout[x][y][z] = Configuration.printer.changeStateTimeout.getValue().byteValue();
                    return !Configuration.printer.placeInstantly.getValue();
                }
            }
        }

        if (Configuration.printer.destroyBlocks.getValue() && !world.isAirBlock(realPos) && this.minecraft.playerController.isInCreativeMode()) {
            this.minecraft.playerController.clickBlock(realPos, EnumFacing.DOWN);

            this.timeout[x][y][z] = Configuration.printer.timeout.getValue().byteValue();

            return !Configuration.printer.destroyInstantly.getValue();
        }

        if (this.schematic.isAirBlock(pos)) {
            return false;
        }

        if (!realBlock.isReplaceable(world, realPos)) {
            return false;
        }

        final ItemStack itemStack = BlockStateToItemStack.getItemStack(blockState, new MovingObjectPosition(player), this.schematic, pos, player);
        if (itemStack == null || itemStack.getItem() == null) {
            Reference.logger.debug("{} is missing a mapping!", blockState);
            return false;
        }

        if (placeBlock(world, player, realPos, blockState, itemStack)) {
            this.timeout[x][y][z] = Configuration.printer.timeout.getValue().byteValue();

            return !Configuration.printer.placeInstantly.getValue();
        }

        return false;
    }

    private boolean isSolid(final World world, final BlockPos pos, final EnumFacing side) {
        final BlockPos offset = pos.offset(side);

        final IBlockState blockState = world.getBlockState(offset);
        final Block block = blockState.getBlock();

        if (block == null) {
            return false;
        }

        if (block.isAir(world, offset)) {
            return false;
        }

        if (block instanceof BlockFluidBase) {
            return false;
        }

        return !block.isReplaceable(world, offset);
    }

    private List<EnumFacing> getSolidSides(final World world, final BlockPos pos) {
        if (!Configuration.printer.placeAdjacent.getValue()) {
            return Arrays.asList(EnumFacing.VALUES);
        }

        final List<EnumFacing> list = new ArrayList<EnumFacing>();

        for (final EnumFacing side : EnumFacing.VALUES) {
            if (isSolid(world, pos, side)) {
                list.add(side);
            }
        }

        return list;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos, final IBlockState blockState, final ItemStack itemStack) {
        if (itemStack.getItem() instanceof ItemBucket) {
            return false;
        }

        final PlacementData data = PlacementRegistry.INSTANCE.getPlacementData(blockState, itemStack);
        if (data != null && !data.isValidPlayerFacing(blockState, player, pos, world)) {
            return false;
        }

        final List<EnumFacing> solidSides = getSolidSides(world, pos);

        if (solidSides.isEmpty()) {
            return false;
        }

        final EnumFacing direction;
        final float offsetX;
        final float offsetY;
        final float offsetZ;
        final int extraClicks;

        if (data != null) {
            final List<EnumFacing> validDirections = data.getValidBlockFacings(solidSides, blockState);
            if (validDirections.isEmpty()) {
                return false;
            }

            direction = validDirections.get(0);
            offsetX = data.getOffsetX(blockState);
            offsetY = data.getOffsetY(blockState);
            offsetZ = data.getOffsetZ(blockState);
            extraClicks = data.getExtraClicks(blockState);
        } else {
            direction = solidSides.get(0);
            offsetX = 0.5f;
            offsetY = 0.5f;
            offsetZ = 0.5f;
            extraClicks = 0;
        }

        if (!swapToItem(player.inventory, itemStack)) {
            return false;
        }

        return placeBlock(world, player, pos, direction, offsetX, offsetY, offsetZ, extraClicks);
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final BlockPos pos, final EnumFacing direction, final float offsetX, final float offsetY, final float offsetZ, final int extraClicks) {
        final ItemStack itemStack = player.getCurrentEquippedItem();
        boolean success = false;

        if (!this.minecraft.playerController.isInCreativeMode() && itemStack != null && itemStack.stackSize <= extraClicks) {
            return false;
        }

        final BlockPos offset = pos.offset(direction);
        final EnumFacing side = direction.getOpposite();
        final Vec3 hitVec = new Vec3(offset.getX() + offsetX, offset.getY() + offsetY, offset.getZ() + offsetZ);

        success = placeBlock(world, player, itemStack, offset, side, hitVec);
        for (int i = 0; success && i < extraClicks; i++) {
            success = placeBlock(world, player, itemStack, offset, side, hitVec);
        }

        if (itemStack != null && itemStack.stackSize == 0 && success) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
        }

        return success;
    }

    private boolean placeBlock(final WorldClient world, final EntityPlayerSP player, final ItemStack itemStack, final BlockPos pos, final EnumFacing side, final Vec3 hitVec) {
        if (ForgeEventFactory.onPlayerInteract(player, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, world, pos, side, hitVec).isCanceled()) {
            return false;
        }

        final BlockPos actualPos = Configuration.printer.placeAdjacent.getValue() ? pos : pos.offset(side);
        final boolean result = this.minecraft.playerController.onPlayerRightClick(player, world, itemStack, actualPos, side, hitVec);
        if (result) {
            player.swingItem();
        }
        return result;
    }

    private void syncSneaking(final EntityPlayerSP player, final boolean isSneaking) {
        player.setSneaking(isSneaking);
        player.sendQueue.addToSendQueue(new C0BPacketEntityAction(player, isSneaking ? C0BPacketEntityAction.Action.START_SNEAKING : C0BPacketEntityAction.Action.STOP_SNEAKING));
    }

    private boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack) {
        return swapToItem(inventory, itemStack, true);
    }

    private boolean swapToItem(final InventoryPlayer inventory, final ItemStack itemStack, final boolean swapSlots) {
        final int slot = getInventorySlotWithItem(inventory, itemStack);

        if (this.minecraft.playerController.isInCreativeMode() && (slot < Constants.Inventory.InventoryOffset.HOTBAR || slot >= Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR) && !Configuration.printer.swapSlots.queue.isEmpty()) {
            inventory.currentItem = getNextSlot();
            inventory.setInventorySlotContents(inventory.currentItem, itemStack.copy());
            this.minecraft.playerController.sendSlotPacket(inventory.getStackInSlot(inventory.currentItem), Constants.Inventory.SlotOffset.HOTBAR + inventory.currentItem);
            return true;
        }

        if (slot >= Constants.Inventory.InventoryOffset.HOTBAR && slot < Constants.Inventory.InventoryOffset.HOTBAR + Constants.Inventory.Size.HOTBAR) {
            inventory.currentItem = slot;
            return true;
        } else if (swapSlots && slot >= Constants.Inventory.InventoryOffset.INVENTORY && slot < Constants.Inventory.InventoryOffset.INVENTORY + Constants.Inventory.Size.INVENTORY) {
            if (swapSlots(inventory, slot)) {
                return swapToItem(inventory, itemStack, false);
            }
        }

        return false;
    }

    private int getInventorySlotWithItem(final InventoryPlayer inventory, final ItemStack itemStack) {
        for (int i = 0; i < inventory.mainInventory.length; i++) {
            ItemStack other = inventory.mainInventory[i];
            if (other != null && other.isItemEqual(itemStack)) {
                return i;
            }
        }
        return -1;
    }

    private boolean swapSlots(final InventoryPlayer inventory, final int from) {
        if (!Configuration.printer.swapSlots.queue.isEmpty()) {
            final int slot = getNextSlot();

            swapSlots(from, slot);
            return true;
        }

        return false;
    }

    private int getNextSlot() {
        final int slot = Configuration.printer.swapSlots.queue.poll() % Constants.Inventory.Size.HOTBAR;
        Configuration.printer.swapSlots.queue.offer(slot);
        return slot;
    }

    private boolean swapSlots(final int from, final int to) {
        return this.minecraft.playerController.windowClick(this.minecraft.thePlayer.inventoryContainer.windowId, from, to, 2, this.minecraft.thePlayer) == null;
    }

    private boolean stateRequiresInteract(final Class<? extends Block> clazz, final IProperty<?> property, final IBlockState target, final IBlockState real) {
        if (clazz.isAssignableFrom(real.getBlock().getClass()) && clazz.isAssignableFrom(target.getBlock().getClass())) {
            return this.arePropertiesNotEqual(property, target, real);
        }
        return false;
    }

    private <T extends Comparable<T>> boolean arePropertiesEqual(final IProperty<T> property, final IBlockState main, final IBlockState state) {
        final T mainVal = this.getValue(property, main), stateVal = this.getValue(property, state);
        return mainVal != null && mainVal == stateVal;
    }

    private <T extends Comparable<T>> boolean arePropertiesNotEqual(final IProperty<T> property, final IBlockState main, final IBlockState state) {
        final T mainVal = this.getValue(property, main), stateVal = this.getValue(property, state);
        return mainVal != null && mainVal != stateVal;
    }

    private <T extends Comparable<T>> T getValue(final IProperty<T> property, final IBlockState state) {
        T val = null;
        if (state.getProperties().containsKey(property)) {
            val = state.getValue(property);
        }
        return val;
    }
}
