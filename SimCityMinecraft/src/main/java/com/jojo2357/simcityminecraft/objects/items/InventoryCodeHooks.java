package com.jojo2357.simcityminecraft.objects.items;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import com.jojo2357.simcityminecraft.objects.blocks.SimFarmBlockBlock;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.block.DropperBlock;
import net.minecraft.block.HopperBlock;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.DispenserTileEntity;
import net.minecraft.tileentity.HopperTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class InventoryCodeHooks
{
    /**
     * Copied from TileEntityHopper#captureDroppedItems and added capability support
     * @return Null if we did nothing {no IItemHandler}, True if we moved an item, False if we moved no items
     */
    @Nullable
    public static Boolean extractHook(IHopper dest, Direction directionIn, Item itemRequested)
    {
        return getItemHandler(dest, directionIn)
                .map(itemHandlerResult -> {
                    IItemHandler handler = itemHandlerResult.getKey();

                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        ItemStack extractItem = handler.extractItem(i, 1, true);
                        if (!extractItem.isEmpty() && extractItem.getItem() == itemRequested)
                        {
                            for (int j = 0; j < dest.getSizeInventory(); j++)
                            {
                                ItemStack destStack = dest.getStackInSlot(j);
                                if (dest.isItemValidForSlot(j, extractItem) && (destStack.isEmpty() || destStack.getCount() < destStack.getMaxStackSize() && destStack.getCount() < dest.getInventoryStackLimit() && ItemHandlerHelper.canItemStacksStack(extractItem, destStack)))
                                {
                                    extractItem = handler.extractItem(i, 1, false);
                                    if (destStack.isEmpty())
                                        dest.setInventorySlotContents(j, extractItem);
                                    else
                                    {
                                        destStack.grow(1);
                                        dest.setInventorySlotContents(j, destStack);
                                    }
                                    dest.markDirty();
                                    return true;
                                }
                            }
                        }
                    }

                    return false;
                })
                .orElse(null); // TODO bad null
    }

    /**
     * Copied from BlockDropper#dispense and added capability support
     */
    public static boolean dropperInsertHook(World world, BlockPos pos, DispenserTileEntity dropper, int slot, @Nonnull ItemStack stack)
    {
        Direction enumfacing = world.getBlockState(pos).get(DropperBlock.FACING);
        BlockPos blockpos = pos.offset(enumfacing);
        return getItemHandler(world, (double) blockpos.getX(), (double) blockpos.getY(), (double) blockpos.getZ(), enumfacing.getOpposite())
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    ItemStack dispensedStack = stack.copy().split(1);
                    ItemStack remainder = putStackInInventoryAllSlots(dropper, destination, itemHandler, dispensedStack);

                    if (remainder.isEmpty())
                    {
                        remainder = stack.copy();
                        remainder.shrink(1);
                    }
                    else
                    {
                        remainder = stack.copy();
                    }

                    dropper.setInventorySlotContents(slot, remainder);
                    return false;
                })
                .orElse(true);
    }

    /**
     * Copied from TileEntityHopper#transferItemsOut and added capability support
     */
    public static boolean insertHook(SimFarmBlockTileEntity simFarmBlockTileEntity)
    {
        Direction hopperFacing = simFarmBlockTileEntity.getBlockState().get(SimFarmBlockBlock.FACING);
        return getItemHandler(simFarmBlockTileEntity, hopperFacing)
                .map(destinationResult -> {
                    IItemHandler itemHandler = destinationResult.getKey();
                    Object destination = destinationResult.getValue();
                    if (isFull(itemHandler))
                    {
                        return false;
                    }
                    else
                    {
                        for (int i = 0; i < simFarmBlockTileEntity.getSizeInventory(); ++i)
                        {
                            if (!simFarmBlockTileEntity.getStackInSlot(i).isEmpty())
                            {
                                ItemStack originalSlotContents = simFarmBlockTileEntity.getStackInSlot(i).copy();
                                ItemStack insertStack = simFarmBlockTileEntity.decrStackSize(i, 1);
                                ItemStack remainder = putStackInInventoryAllSlots(simFarmBlockTileEntity, destination, itemHandler, insertStack);

                                if (remainder.isEmpty())
                                {
                                    return true;
                                }

                                simFarmBlockTileEntity.setInventorySlotContents(i, originalSlotContents);
                            }
                        }

                        return false;
                    }
                })
                .orElse(false);
    }

    private static ItemStack putStackInInventoryAllSlots(TileEntity source, Object destination, IItemHandler destInventory, ItemStack stack)
    {
        for (int slot = 0; slot < destInventory.getSlots() && !stack.isEmpty(); slot++)
        {
            stack = insertStack(source, destination, destInventory, stack, slot);
        }
        return stack;
    }

    /**
     * Copied from TileEntityHopper#insertStack and added capability support
     */
    private static ItemStack insertStack(TileEntity source, Object destination, IItemHandler destInventory, ItemStack stack, int slot)
    {
        ItemStack itemstack = destInventory.getStackInSlot(slot);

        if (destInventory.insertItem(slot, stack, true).isEmpty())
        {
            boolean insertedItem = false;
            boolean inventoryWasEmpty = isEmpty(destInventory);

            if (itemstack.isEmpty())
            {
                destInventory.insertItem(slot, stack, false);
                stack = ItemStack.EMPTY;
                insertedItem = true;
            }
            else if (ItemHandlerHelper.canItemStacksStack(itemstack, stack))
            {
                int originalSize = stack.getCount();
                stack = destInventory.insertItem(slot, stack, false);
                insertedItem = originalSize < stack.getCount();
            }

            if (insertedItem)
            {
                if (inventoryWasEmpty && destination instanceof SimFarmBlockTileEntity)
                {
                	SimFarmBlockTileEntity destinationHopper = (SimFarmBlockTileEntity)destination;

                    if (!destinationHopper.mayTransfer())
                    {
                        int k = 0;
                        if (source instanceof SimFarmBlockTileEntity)
                        {
                            if (destinationHopper.getLastUpdateTime() >= ((SimFarmBlockTileEntity) source).getLastUpdateTime())
                            {
                                k = 1;
                            }
                        }
                        destinationHopper.setTransferCooldown(8 - k);
                    }
                }
            }
        }

        return stack;
    }

    private static LazyOptional<Pair<IItemHandler, Object>> getItemHandler(IHopper hopper, Direction hopperFacing)
    {
        double x = hopper.getXPos() + (double) hopperFacing.getXOffset();
        double y = hopper.getYPos() + (double) hopperFacing.getYOffset();
        double z = hopper.getZPos() + (double) hopperFacing.getZOffset();
        return getItemHandler(hopper.getWorld(), x, y, z, hopperFacing.getOpposite());
    }

    private static boolean isFull(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.isEmpty() || stackInSlot.getCount() < itemHandler.getSlotLimit(slot))
            {
                return false;
            }
        }
        return true;
    }

    private static boolean isEmpty(IItemHandler itemHandler)
    {
        for (int slot = 0; slot < itemHandler.getSlots(); slot++)
        {
            ItemStack stackInSlot = itemHandler.getStackInSlot(slot);
            if (stackInSlot.getCount() > 0)
            {
                return false;
            }
        }
        return true;
    }

    public static LazyOptional<Pair<IItemHandler, Object>> getItemHandler(World worldIn, double x, double y, double z, final Direction side)
    {
        int i = MathHelper.floor(x);
        int j = MathHelper.floor(y);
        int k = MathHelper.floor(z);
        BlockPos blockpos = new BlockPos(i, j, k);
        net.minecraft.block.BlockState state = worldIn.getBlockState(blockpos);

        if (state.hasTileEntity())
        {
            TileEntity tileentity = worldIn.getTileEntity(blockpos);
            if (tileentity != null)
            {
                return tileentity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side)
                    .map(capability -> ImmutablePair.<IItemHandler, Object>of(capability, tileentity));
            }
        }

        return LazyOptional.empty();
    }
}