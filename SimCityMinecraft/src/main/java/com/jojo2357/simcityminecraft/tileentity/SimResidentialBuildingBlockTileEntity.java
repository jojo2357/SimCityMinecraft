package com.jojo2357.simcityminecraft.tileentity;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.buildings.BuildingType;
import com.jojo2357.simcityminecraft.buildings.ResidentialBuilding;
import com.jojo2357.simcityminecraft.container.SimResidentialBuildingContainer;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.entities.sim.SimWealthClass;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.util.handler.Area;
import com.jojo2357.simcityminecraft.util.handler.managers.Houses;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class SimResidentialBuildingBlockTileEntity extends LockableLootTileEntity implements ITickableTileEntity{
	
	private BuildingType building = BuildingType.RESIDENTIAL;
	private int residents = 0;
	private int maxResidents = 0;
	private double rent = 0;
	private ResidentialBuilding bilding;
	
	private int[] maxWealthResidents;
	private int[] wealthResidents;
	
	private ArrayList<Sim> myResidents = new ArrayList<Sim>();
	
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);

	public SimResidentialBuildingBlockTileEntity() {
		super(ModTileEntityTypes.SIM_RESIDENTIAL_BUILDING_BLOCK.get());
		maxWealthResidents = new int[SimWealthClass.classes];
		wealthResidents = new int[SimWealthClass.classes];
		//this.bilding = new ResidentialBuilding(this.building, 0, Main.buildingLoader.allTemplates.get(0).get(0), BlockPos.ZERO);
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.inventory);
		}
		this.rent = compound.getDouble("Rent");
		this.maxWealthResidents = compound.getIntArray("WealthMaxes");
		if (this.maxWealthResidents == null) this.maxWealthResidents = new int[SimWealthClass.classes];
		//this.residents = compound.getInt("Residents");
		this.maxResidents = compound.getInt("MaxResidents");
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.inventory);
		}
		compound.putDouble("Rent", this.rent);
		compound.putIntArray("WealthMaxes", this.maxWealthResidents);
		//compound.putInt("Residents", this.residents);
		compound.putInt("MaxResidents", this.maxResidents);
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
		return this.inventory;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> itemsIn) {
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Main.MOD_ID + ".sim_residential_building_block");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new SimResidentialBuildingContainer(id, player, this);
	}
	
	public void handleUpdateTag(CompoundNBT tag) {
		setInventorySlotContents(0, ItemStack.read(tag));
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
			handleUpdateTag(this.write(new CompoundNBT()));
		}
		if (this.building == BuildingType.RESIDENTIAL)
			if (!Houses.houses.contains(this))
				Houses.houses.add(this);
		//this.getUpdatePacket();
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
			this.maxResidents += setting;
			break;
		case 1:
			this.rent += setting/100.0;
			this.rent *= 100;
			this.rent = ((int)this.rent)/100.0D;
		}
		this.markDirty();
	}

	public boolean isFull() {
		return this.residents >= this.maxResidents;
	}
	
	public int getMaxResidents() {
		return this.maxResidents;
	}
	
	public int getResidents() {
		return this.residents;
	}

	public double getRent() {
		return this.rent;
	}
	
	public boolean simMoveIn(Sim simIn) {
		if (!roomInWealth(simIn.getWealthClass().id)) return false;
		this.residents++;
		this.myResidents.add(simIn);
		this.wealthResidents[simIn.getWealthClass().id]++;
		return true;
	}

	public void simAffirm(Sim simIn) {
		if (!this.myResidents.contains(simIn)) {
			this.residents++;
			this.myResidents.add(simIn);
			this.wealthResidents[simIn.getWealthClass().id]++;
		}			
   	}
	
	public boolean roomInWealth(int wealthIndex) {
		if (this.maxWealthResidents == null || this.maxWealthResidents.length != SimWealthClass.classes) 
			this.maxWealthResidents = new int[SimWealthClass.classes];
		if (this.wealthResidents == null || this.wealthResidents.length != SimWealthClass.classes) 
			this.wealthResidents = new int[SimWealthClass.classes];
		return this.maxWealthResidents[wealthIndex] > this.wealthResidents[wealthIndex];
	}
	
	public boolean roomInWealth(SimWealthClass wealthClass) {
		return this.roomInWealth(wealthClass.id);
	}

	public void moveOut() {
				
	}

	public boolean simChangedClass(int last, int current) {
		this.wealthResidents[last]--;
		if (roomInWealth(current)) {
			this.wealthResidents[current]++;
		}else {
			return false;
		}
		return true;
	}
}
