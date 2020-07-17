package com.jojo2357.simcityminecraft.tileentity;

import java.util.ArrayList;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.buildings.BuildingType;
import com.jojo2357.simcityminecraft.buildings.ResidentialBuilding;
import com.jojo2357.simcityminecraft.container.SimCommercialBuildingContainer;
import com.jojo2357.simcityminecraft.container.SimResidentialBuildingContainer;
import com.jojo2357.simcityminecraft.entities.sim.Jobs;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.entities.sim.SimWealthClass;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.util.handler.Area;
import com.jojo2357.simcityminecraft.util.handler.managers.FoodsMetaData;
import com.jojo2357.simcityminecraft.util.handler.managers.Houses;
import com.jojo2357.simcityminecraft.util.handler.managers.Shops;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SimCommercialBuildingBlockTileEntity extends LockableLootTileEntity implements ITickableTileEntity {

	private BuildingType building = BuildingType.COMMERCIAL;
	private Jobs availableJob = Jobs.GROCER;
	private FoodsMetaData[] foodsHere;
	private int[] foodsHereById;
	private int numFoodsSold;
	private int jobId = 0;
	private int myKreds = 0;

	private String storeName = "";

	// private ResidentialBuilding bilding;

	private int maxWorkers;
	private int workers;

	private ArrayList<Sim> myWorkers = new ArrayList<Sim>();

	private NonNullList<ItemStack> stock = NonNullList.withSize(5, ItemStack.EMPTY);
	private boolean servesFood;

	public SimCommercialBuildingBlockTileEntity() {
		super(ModTileEntityTypes.SIM_COMMERCIAL_BUILDING_BLOCK.get());
		// this.bilding = new ResidentialBuilding(this.building, 0,
		// Main.buildingLoader.allTemplates.get(0).get(0), BlockPos.ZERO);
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.stock = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.stock);
		}
		this.storeName = compound.getString("Name");
		this.servesFood = compound.getBoolean("FoodStore");
		this.jobId = compound.getInt("JobId");
		// this.rent = compound.getDouble("Rent");
		// this.maxWealthResidents = compound.getIntArray("WealthMaxes");
		// if (this.maxWealthResidents == null) this.maxWealthResidents = new
		// int[SimWealthClass.classes];
		// this.residents = compound.getInt("Residents");
		// this.maxResidents = compound.getInt("MaxResidents");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.stock);
		}
		compound.putString("Name", this.storeName);
		compound.putBoolean("FoodStore", this.servesFood);
		compound.putInt("JobId", this.jobId);
		// compound.putDouble("Rent", this.rent);
		// compound.putIntArray("WealthMaxes", this.maxWealthResidents);
		// compound.putInt("Residents", this.residents);
		// compound.putInt("MaxResidents", this.maxResidents);
		return compound;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.read(packet.getNbtCompound());
	}

	@Override
	public int getSizeInventory() {
		return 5;
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.stock;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) {
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Main.MOD_ID + ".sim_commercial_building_block");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new SimCommercialBuildingContainer(id, player, this);
	}

	public void handleUpdateTag(CompoundNBT tag) {
		setInventorySlotContents(2, ItemStack.read(tag));
		updateThis();
	}

	public void updateThis() {
		this.markDirty();
		if (this.getWorld() != null) {
			this.getWorld().notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	public void tick() {
		if (!this.world.isRemote) {
			if (this.foodsHere == null || this.foodsHere.length != this.numFoodsSold) {
				this.foodsHere = new FoodsMetaData[this.numFoodsSold];
				this.foodsHereById = new int[this.numFoodsSold];
				for (int i = 0; i < this.numFoodsSold; i++)
					this.foodsHere[i] = FoodsMetaData.getFoodById(this.foodsHereById[i]);
			}
			this.availableJob = Jobs.getJobFromId(this.jobId);
			handleUpdateTag(this.write(new CompoundNBT()));
			if (this.building == BuildingType.COMMERCIAL)
				if (!Shops.stores.contains(this))
					Shops.stores.add(this);
			if (this.availableJob == Jobs.GROCER) {
				int totalWheat = 0;
				for (ItemStack stack : this.stock) {
					if (stack.getItem() == Items.WHEAT)
						totalWheat += stack.getCount();
				}
				if (totalWheat < 64) {
					for (Sim worker : this.myWorkers) {
						worker.collectResource(Items.WHEAT, 64 - totalWheat);
					}
				}
			}
			if (this.world.getDayTime() % 24000 == 12000)
				for (Sim worker : this.myWorkers) {
					worker.payTheSim(this.availableJob.WAGE);
					Main.KredsManager.addKreds(-this.availableJob.WAGE);
				}
		}
		// this.getUpdatePacket();
	}

	public BuildingType getBuilding() {
		return building;
	}

	public void setBuilding(BuildingType building) {
		this.building = building;
	}

	public void buttonClicked(int setting, int mode) {
		switch (mode) {
		case 0:
			this.myWorkers.get(setting);
			this.myWorkers.remove(setting);
			break;
		case 1:
			this.myWorkers.add(Main.simRegistry.getSims().get(setting));
			this.workers++;
			Main.simRegistry.getSims().get(setting).setJob(this.availableJob, this.pos);
			break;
		}
		this.markDirty();
	}

	public boolean isFull() {
		return this.workers >= this.maxWorkers;
	}

	public int getMaxWorkers() {
		return this.maxWorkers;
	}

	public int getWorkers() {
		return this.workers;
	}

	public double getWage() {
		return this.availableJob.WAGE;
	}

	public boolean simMoveIn(Sim simIn) {
		if (!roomInWealth(simIn.getWealthClass().id))
			return false;
		if (this.isFull())
			return false;
		this.workers++;
		this.myWorkers.add(simIn);
		// this.wealthResidents[simIn.getWealthClass().id]++;
		return true;
	}

	public void simAffirm(Sim simIn) {
		if (!this.myWorkers.contains(simIn)) {
			this.workers++;
			this.myWorkers.add(simIn);
			// this.wealthResidents[simIn.getWealthClass().id]++;
		}
	}

	public boolean roomInWealth(int wealthIndex) {
		return false;
		/*
		 * if (this.maxWealthResidents == null || this.maxWealthResidents.length !=
		 * SimWealthClass.classes) this.maxWealthResidents = new
		 * int[SimWealthClass.classes]; if (this.wealthResidents == null ||
		 * this.wealthResidents.length != SimWealthClass.classes) this.wealthResidents =
		 * new int[SimWealthClass.classes]; return this.maxWealthResidents[wealthIndex]
		 * > this.wealthResidents[wealthIndex];
		 */
	}

	public boolean roomInWealth(SimWealthClass wealthClass) {
		return this.roomInWealth(wealthClass.id);
	}

	public void moveOut() {

	}

	public String getStoreName() {
		return this.storeName;
	}

	public ArrayList<Sim> workers() {
		return this.myWorkers;
	}

	public NonNullList<ItemStack> dropOff(NonNullList<ItemStack> inventory) {
		int waterline = 0;
		for (int i = 0; i < inventory.size(); i++) {
			if (inventory.get(i).getItem() == Items.WHEAT) {
				for (int j = waterline; j < this.stock.size(); j++) {
					waterline = j;
					if (this.stock.get(j).getItem() == Items.WHEAT) {
						if (inventory.get(i).getCount() + this.stock.get(j).getCount() > 64) {
							inventory.get(i).shrink(64 - this.stock.get(j).getCount());
							this.stock.get(j).setCount(64);
						} else {
							this.stock.get(j).grow(inventory.get(i).getCount());
							inventory.get(i).setCount(0);
							break;
						}
					} else {
						this.stock.set(j, inventory.get(i));
						inventory.set(i, ItemStack.EMPTY);
						break;
					}
				}
			}
		}
		this.markDirty();
		return inventory;
	}

	public ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, ItemStack stack,
			@Nullable Direction direction) {
		IInventory destination = this;
		if (destination instanceof ISidedInventory && direction != null) {
			ISidedInventory isidedinventory = (ISidedInventory) destination;
			int[] aint = isidedinventory.getSlotsForFace(direction);

			for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
				stack = insertStack(source, destination, stack, aint[k], direction);
			}
		} else {
			int i = destination.getSizeInventory();

			for (int j = 0; j < i && !stack.isEmpty(); ++j) {
				stack = insertStack(source, destination, stack, j, direction);
			}
		}

		return stack;
	}

	private ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack, int index,
			@Nullable Direction direction) {
		ItemStack itemstack = destination.getStackInSlot(index);
		if (canInsertItemInSlot(destination, stack, index, direction)) {
			boolean flag = false;
			if (itemstack.isEmpty()) {
				destination.setInventorySlotContents(index, stack);
				stack = ItemStack.EMPTY;
				flag = true;
			} else if (canCombine(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.shrink(j);
				itemstack.grow(j);
				flag = j > 0;
			}

			if (flag) {
				destination.markDirty();
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index,
			@Nullable Direction side) {
		if (!inventoryIn.isItemValidForSlot(index, stack)) {
			return false;
		} else {
			return !(inventoryIn instanceof ISidedInventory)
					|| ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
		}
	}

	private boolean canCombine(ItemStack stack1, ItemStack stack2) {
		if (stack1.getItem() != stack2.getItem()) {
			return false;
		} else if (stack1.getDamage() != stack2.getDamage()) {
			return false;
		} else if (stack1.getCount() > stack1.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.areItemStackTagsEqual(stack1, stack2);
		}
	}

	public boolean canBuyFood() {
		return this.servesFood;
	}

	public void buyFood(Sim sim) {
		int neededAmount = sim.getHungerAmount();
		for (int i = 0; i < this.stock.size(); i++) {
			ItemStack stack = this.stock.get(i);
			if (FoodsMetaData.isFood(stack.getItem())) {
				FoodsMetaData food = FoodsMetaData.getFoodByItem(stack.getItem());
				if (stack.getCount() * food.fillingness >= neededAmount) {
					if (sim.getKreds() >= (int) (neededAmount / food.fillingness) * food.cost) {
						sim.feed((int) (neededAmount / food.fillingness) * food.fillingness);
						sim.pay(food.cost * (int) (neededAmount / food.fillingness));
						this.myKreds += food.cost * (double)((int) (neededAmount / food.fillingness));
						stack.setCount(stack.getCount() - (int)(neededAmount / food.fillingness ));
						neededAmount -= (int) (neededAmount / food.fillingness) * food.fillingness;
					} else {
						stack.setCount(stack.getCount() - (int) (sim.getKreds() / food.cost));
						sim.feed((int) (sim.getKreds() / food.cost) * food.fillingness);
						sim.pay(food.cost * (int) (sim.getKreds() / food.cost));
						this.myKreds += food.cost * (int) (sim.getKreds() / food.cost);
						neededAmount -= (int) (sim.getKreds() / food.cost);
						
					}
				}
			}
			this.stock.set(i, stack);
		}
		this.markDirty();
	}

	/*
	 * public boolean simChangedClass(int last, int current) {
	 * this.wealthResidents[last]--; if (roomInWealth(current)) {
	 * this.wealthResidents[current]++; }else { return false; } return true; }
	 */
}
