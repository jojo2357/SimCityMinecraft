package com.jojo2357.simcityminecraft.util.handler;

import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

public class MyMessage {
	private int index;
	private SimFarmBlockTileEntity entity;
	public MyMessage(int index, SimFarmBlockTileEntity entity) {
		this.index = index;
		this.entity = entity;
	}
	
	public int getIndex() {
		return this.index;
	}
	
	public SimFarmBlockTileEntity getEntity() {
		return this.entity;
	}
}
