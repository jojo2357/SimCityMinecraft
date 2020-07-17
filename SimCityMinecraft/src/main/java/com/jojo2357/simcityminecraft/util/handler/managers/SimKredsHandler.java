package com.jojo2357.simcityminecraft.util.handler.managers;

import com.jojo2357.simcityminecraft.Main;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.storage.WorldSavedData;

public class SimKredsHandler{

	//private static final String DATA_NAME = Main.MOD_ID + "KredsData";
	private double kreds = 0.0D;
	
	public SimKredsHandler() {
		//super(DATA_NAME);
		this.kreds = 10.0D;
	}

	public double getKreds() {
		return kreds;
	}

	public void addKreds(double kreds) {
		this.kreds += kreds;
		this.kreds = ((Math.round(this.kreds*100.0D))/100.0D);
		Main.dataSaver.setKredsBackup(this.kreds);
		//this.markDirty();
	}
	
	public void setKreds(double kreds) {
		this.kreds = kreds;
		Main.dataSaver.setKredsBackup(this.kreds);
		//this.markDirty();
	}

	/*@Override
	public void read(CompoundNBT nbt) {
		this.kreds = nbt.getDouble("Kreds");
		
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		compound.putDouble("Kreds", this.kreds);
		return compound;
	}*/

}
