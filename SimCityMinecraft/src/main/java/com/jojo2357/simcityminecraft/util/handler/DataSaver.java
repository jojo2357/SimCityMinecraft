package com.jojo2357.simcityminecraft.util.handler;

import com.jojo2357.simcityminecraft.Main;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.DimensionSavedDataManager;
import net.minecraft.world.storage.WorldSavedData;

public class DataSaver extends WorldSavedData {
	
	  private static final String DATA_NAME = Main.MOD_ID + "_ExampleData";
	  private static final DataSaver CLIENT_DUMMY = new DataSaver();
	  private double kredsBackup;

	  // Required constructors
	  public DataSaver() {
		  super(DATA_NAME);
	  }
  
	  public DataSaver(String s) {
		  super(s);
	  }
	  
	@Override
	public void read(CompoundNBT compound) {
		Main.KredsManager.setKreds(compound.getDouble("Kreds"));
	}
	
	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putDouble("Kreds", kredsBackup);
				//Main.KredsManager.getKreds());
		//this.markDirty();
		return compound;
	}

	public double getKredsBackup() {
		return kredsBackup;
	}

	public void setKredsBackup(double kreds) {
		this.kredsBackup = kreds;
	}
	
	public static DataSaver get(World world)
	{
		if (!(world instanceof ServerWorld))
		{
			return CLIENT_DUMMY;
		}
		
		ServerWorld wbWorld = ((ServerWorld)world);
		DimensionSavedDataManager storage = wbWorld.getSavedData();
		return storage.getOrCreate(DataSaver::new, DATA_NAME);
	}

}

