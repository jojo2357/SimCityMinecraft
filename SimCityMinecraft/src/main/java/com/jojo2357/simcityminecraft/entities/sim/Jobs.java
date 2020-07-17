package com.jojo2357.simcityminecraft.entities.sim;

import java.util.ArrayList;

public enum Jobs{
	
	UNEMPLOYED(0, 0F),
	SHEPARD(1, 0.5F),
	BUILDER(2, 0.1F),
	GROCER(3, 1F),
	MINER(4, 0.01F), 
	FARMER(5, 0.02F);
	
	public final int ID;
	public final float WAGE;
	
	public final int jobs = 4;
	
	private Jobs(int IdIn, float wage) {
		this.ID = IdIn;
		this.WAGE = wage;
	}
	
	public static Jobs getJobFromId(int findID) {
		ArrayList<Jobs> JobList = new ArrayList<Jobs>();
		JobList.add(UNEMPLOYED);JobList.add(SHEPARD);JobList.add(BUILDER);JobList.add(GROCER);
		for (int i = 0; i < JobList.size(); i++) {
			if (JobList.get(i).ID == findID) return JobList.get(i);
		}
		return null;
	}
	
}
