package com.jojo2357.simcityminecraft.objects.items;

import javax.annotation.Nonnull;

import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

public class HopperItemHandler extends InvWrapper
{
    private final SimFarmBlockTileEntity simFarmBlock;

    public HopperItemHandler(SimFarmBlockTileEntity simFarmBlockTileEntity)
    {
        super(simFarmBlockTileEntity);
        this.simFarmBlock = simFarmBlockTileEntity;
    }

    @Override
    @Nonnull
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
    {
        if (simulate)
        {
            return super.insertItem(slot, stack, simulate);
        }
        else
        {
            boolean wasEmpty = getInv().isEmpty();

            int originalStackSize = stack.getCount();
            stack = super.insertItem(slot, stack, simulate);


            return stack;
        }
    }
}
