package com.jojo2357.simcityminecraft.container;

import java.util.ArrayList;
import java.util.Objects;

import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.init.ModItems;
import com.jojo2357.simcityminecraft.tileentity.SimCommercialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimResidentialBuildingBlockTileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.math.BlockPos;

public class SimCommercialBuildingContainer extends Container {
	
	public BlockPos ownerSpot;
	private IWorldPosCallable canInteractWithCallable;
	private Boolean configureMode;
	private SimCommercialBuildingBlockTileEntity owner;

	public SimCommercialBuildingContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public SimCommercialBuildingContainer(int windowId, PlayerInventory p_i50079_2_, SimCommercialBuildingBlockTileEntity p_i50079_3_) {
		super(ModContainers.SIM_COMMERCIAL_BUILDING_BLOCK.get(), windowId);
		this.owner = p_i50079_3_;
		this.configureMode = true;
				//p_i50079_2_.player.getHeldItemMainhand() == new ItemStack(ModItems.BUILDING_CONFIGURE.get(), 1);
		this.ownerSpot = p_i50079_3_.getPos();
		this.canInteractWithCallable = IWorldPosCallable.of(p_i50079_3_.getWorld(), p_i50079_3_.getPos());
		/*this.specialData = new IntArray(5);
		this.specialData.set(0, this.xP);
		this.specialData.set(1, this.lvl);
		this.specialData.set(2, this.speed);
		this.specialData.set(3, this.area);
		this.specialData.set(4, this.tileEntity.isFarming() ? 1 : 0);
		this.trackIntArray(specialData);*/
		assertInventorySize(p_i50079_3_, 5);
		p_i50079_3_.openInventory(p_i50079_2_.player);
		// this.xP = key.getInt("Esperience");
		// int i = 51;

	}

	private static SimCommercialBuildingBlockTileEntity getTileEntity(final PlayerInventory playerInventory,
			final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof SimCommercialBuildingBlockTileEntity) {
			return (SimCommercialBuildingBlockTileEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.SIM_COMMERCIAL_BUILDING_BLOCK.get());
	}


	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
	}

	public BlockPos getPos() {
		return this.ownerSpot;
	}
	
	public Boolean getMode() {
		return this.configureMode;
	}

	public int getWorkerCount() {
		return this.owner.getMaxWorkers();
	}

	public double getWage() {
		return this.owner.getWage();
	}
	
	public String getStoreName() {
		return this.owner.getStoreName();
	}
	
	public ArrayList<Sim> getWorkers(){
		return this.owner.workers();
	}

}
