package com.jojo2357.simcityminecraft.container;

import java.util.Objects;

import javax.annotation.Nonnull;

import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntArray;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SimMineBlockContainer extends Container {
	public SimMineBlockTileEntity tileEntity;
	private final IInventory hopperInventory;
	private final IWorldPosCallable canInteractWithCallable;
	private int index = 0;
	private boolean doMining = false;
	private int xP;
	private int lvl;
	private int speed;
	private IIntArray specialData;
	private int area;

	// private final IWorldPosCallable canInteractWithCallable;

	// public SimMineBlockContainer(int p_i50078_1_, PlayerInventory p_i50078_2_) {
	// this(p_i50078_1_, p_i50078_2_, new Inventory(5));
	// }

	public SimMineBlockContainer(final int windowId, final PlayerInventory playerInventory, final PacketBuffer data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data));
	}
	
	public SimMineBlockContainer(int windowId, PlayerInventory p_i50079_2_, SimMineBlockTileEntity p_i50079_3_) {
		super(ModContainers.SIM_MINE_BLOCK.get(), windowId);
		this.tileEntity = p_i50079_3_;
		this.xP = this.tileEntity.getCurrentXp();
		this.lvl = this.tileEntity.getLevel();
		this.speed = this.tileEntity.getTicks();
		this.area = this.tileEntity.getArea();
		this.hopperInventory = p_i50079_3_;
		this.canInteractWithCallable = IWorldPosCallable.of(p_i50079_3_.getWorld(), p_i50079_3_.getPos());

		/*this.specialData = new IntArray(5);
		this.specialData.set(0, this.xP);
		this.specialData.set(1, this.lvl);
		this.specialData.set(2, this.speed);
		this.specialData.set(3, this.area);
		this.specialData.set(4, this.tileEntity.isMineing() ? 1 : 0);
		this.trackIntArray(specialData);*/
		assertInventorySize(p_i50079_3_, 5);
		p_i50079_3_.openInventory(p_i50079_2_.player);
		// this.xP = key.getInt("Esperience");
		// int i = 51;

		/*
		 * for(int j = 0; j < 5; ++j) { this.addSlot(new Slot(p_i50079_3_, j, 44 + j *
		 * 18, 20)); }
		 * 
		 * for(int l = 0; l < 3; ++l) { for(int k = 0; k < 9; ++k) { this.addSlot(new
		 * Slot(p_i50079_2_, k + l * 9 + 9, 8 + k * 18, l * 18 + 51)); } }
		 * 
		 * for(int i1 = 0; i1 < 9; ++i1) { this.addSlot(new Slot(p_i50079_2_, i1, 8 + i1
		 * * 18, 109)); }
		 */

	}
	
	@Nonnull
	public CompoundNBT getUpdateTag() {
		CompoundNBT updateTag = new CompoundNBT();
		this.hopperInventory.getStackInSlot(0).write(updateTag);
		return updateTag;
	}
	
	public void read(CompoundNBT compound) {
		this.xP = compound.getInt("CurrentXp");
		this.lvl = compound.getInt("Level");
	}

	private static SimMineBlockTileEntity getTileEntity(final PlayerInventory playerInventory,
			final PacketBuffer data) {
		Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
		Objects.requireNonNull(data, "data cannot be null");
		final TileEntity tileAtPos = playerInventory.player.world.getTileEntity(data.readBlockPos());
		if (tileAtPos instanceof SimMineBlockTileEntity) {
			return (SimMineBlockTileEntity) tileAtPos;
		}
		throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
	}

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(PlayerEntity playerIn) {
		return isWithinUsableDistance(canInteractWithCallable, playerIn, ModBlocks.SIM_MINE_BLOCK.get());
	}

	/**
	 * Handle when the stack in slot {@code index} is shift-clicked. Normally this
	 * moves the stack between the player inventory and the other inventory(s).
	 */
	public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);
		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			if (index < this.hopperInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.hopperInventory.getSizeInventory(),
						this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (!this.mergeItemStack(itemstack1, 0, this.hopperInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}
		}

		return itemstack;
	}

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		super.onContainerClosed(playerIn);
		this.hopperInventory.closeInventory(playerIn);
	}

	public void clickHappened(int index) {
		this.index = index;
		if (index == 1) {
			doMining = true;
		}
		if (index == 2) {
			doMining = false;
		}
		this.tileEntity.setMining(doMining);
	}

	public boolean getMining() {
		return this.doMining;
	}

	@OnlyIn(Dist.CLIENT)
	public int getXp() {
		return this.tileEntity.getCurrentXp();
	}

	@OnlyIn(Dist.CLIENT)
	public int getLvl() {
		return this.tileEntity.getLevel();
	}
	
	public int getLevel() {
		return this.tileEntity.getLevel();
	}

	@OnlyIn(Dist.CLIENT)
	public int getSpeed() {
		return this.tileEntity.getTicks();
	}

	@OnlyIn(Dist.CLIENT)
	public int getArea() {
		return this.tileEntity.getArea();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean isMining() {
		return this.tileEntity.isMining();
	}

	public int getMode() {
		if (!this.tileEntity.doIHaveSim()) return 1;
		if (this.tileEntity.myState() != 3) return 2;
		return 3;
	}

}
