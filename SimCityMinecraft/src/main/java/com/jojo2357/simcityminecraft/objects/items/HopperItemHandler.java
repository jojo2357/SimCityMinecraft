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

            if (wasEmpty && originalStackSize > stack.getCount())
            {
                if (!simFarmBlock.mayTransfer())
                {
                    // This cooldown is always set to 8 in vanilla with one exception:
                    // Hopper -> Hopper transfer sets this cooldown to 7 when this hopper
                    // has not been updated as recently as the one pushing items into it.
                    // This vanilla behavior is preserved by VanillaInventoryCodeHooks#insertStack,
                    // the cooldown is set properly by the hopper that is pushing items into this one.
                	simFarmBlock.setTransferCooldown(8);
                }
            }

            return stack;
        }
    }
}
