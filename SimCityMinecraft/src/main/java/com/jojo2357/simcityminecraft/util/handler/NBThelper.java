package com.jojo2357.simcityminecraft.util.handler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class NBThelper {

	public static CompoundNBT ToNBT(Object ob) {
		if(ob instanceof ItemStack) {
			return writeItemStack((ItemStack)ob);
		}
		/*if(ob instanceof ReactorTileEntity) {
			return writeReactorTileEntity((ReactorTileEntity)ob); 
		}*/
		
		return null;
	}

	/*private static CompoundNBT writeReactorTileEntity(ReactorTileEntity ob) {
		CompoundNBT compound = new CompoundNBT();
		compound.putInt("tick", ob.tick);
		compound.putInt("mode", ob.mode);
		return compound;
	}*/

	private static CompoundNBT writeItemStack(ItemStack item) {
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("count", item.getCount());
		nbt.putString("item", item.getItem().getRegistryName().toString());
		nbt.putByte("type", (byte)0);
		return nbt;
	}
	
	@Nullable
	public static Object fromNBT(@Nonnull CompoundNBT compound) {
		switch (compound.getByte("type")) {
		case 0:
			return readItemStack(compound);
		default:
			return null;
		}
	}

	private static ItemStack readItemStack(CompoundNBT compound) {
		Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(compound.getString("item")));
		int count = compound.getInt("count");
		return new ItemStack(item, count);
	}

}
