package com.jojo2357.simcityminecraft.container;

import java.util.Objects;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.init.ModEntityTypes;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.NPCMerchant;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

public class SimContainer extends Container{

	private Sim owner;
	private IIntArray data = new IntArray(1);
	
	public SimContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, (Sim)null);
	}
	
	public SimContainer(int id, Sim sim) {
		super(ModContainers.SIM.get(), id);
		if (sim != null) this.owner = sim;
		if (this.owner != null) {
			IntArray dataIn = new IntArray(1);
			dataIn.set(0, this.owner.getFarmingXp());
			this.data = dataIn;
			this.trackIntArray(data);
		}
	}
	
	public SimContainer(int id, PlayerInventory playerInventoryIn) {
		super(ModContainers.SIM.get(), id);
	}

	public SimContainer(int id, PlayerInventory playerInventoryIn, Sim sim) {
		super(ModContainers.SIM.get(), id);

		//assertIntArraySize(dataIn, 1);
		if (sim != null) this.owner = sim;
		if (this.owner != null) {
			IntArray dataIn = new IntArray(1);
			dataIn.set(0, this.owner.getFarmingXp());
			this.data = dataIn;
			this.trackIntArray(data);
		}
	}

	@Override
	public boolean canInteractWith(PlayerEntity playerIn) {
		return true;
	}

	public void onContainerClosed(PlayerEntity playerIn) {
		//System.out.println("WOOO");
	}

	public Sim getOwner() {
		return this.owner;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getXp() {
		try {
			return this.data.get(0);
		}catch (NullPointerException e){
			return 69;
		}
	}

}
