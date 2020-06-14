package com.jojo2357.simcityminecraft.objects.blocks;

import com.jojo2357.simcityminecraft.util.handler.Area;

import net.minecraft.block.Block;

public class SimFarmBlock extends Block{
	
	public Area area;

	public SimFarmBlock(Properties properties) {
		super(properties);
		// TODO Auto-generated constructor stub
	}
	
	public void setFarmingArea(Area area) {
		this.area = area;
	}

}
