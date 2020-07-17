package com.jojo2357.simcityminecraft.objects.items;

import javax.annotation.Nonnull;

import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimWorkBenchTileEntity;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.wrapper.InvWrapper;

public class HopperItemHandler extends InvWrapper
{
    public HopperItemHandler(SimFarmBlockTileEntity simFarmBlockTileEntity)
    {
        super(simFarmBlockTileEntity);
    }

    public HopperItemHandler(SimMineBlockTileEntity simMineBlockTileEntity) {
		super(simMineBlockTileEntity);
	}

	public HopperItemHandler(SimWorkBenchTileEntity simWorkBenchTileEntity) {
		super(simWorkBenchTileEntity);
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
