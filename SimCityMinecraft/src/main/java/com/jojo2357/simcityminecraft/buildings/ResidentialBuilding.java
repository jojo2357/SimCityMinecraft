package com.jojo2357.simcityminecraft.buildings;

import com.jojo2357.simcityminecraft.util.handler.BuildingTemplate;

import net.minecraft.util.math.BlockPos;

public class ResidentialBuilding extends Building{

	private int rent;
	private BlockPos homeSpot = BlockPos.ZERO;
	private BlockPos homeSpots[];
	private String name;
	
	public ResidentialBuilding(BuildingType buildingType, int maxResidents, BuildingTemplate template, BlockPos offset) {
		super(buildingType, maxResidents, template, offset);
		this.homeSpots = new BlockPos[maxResidents];
		this.name = template.getName();
	}

	public int getRent() {
		return this.rent;
	}

	public BlockPos getHomeSpot() {
		return this.homeSpot;
	}

	public void setHomeSpot(BlockPos homeSpot) {
		this.homeSpot = homeSpot;
	}

	
	
}
