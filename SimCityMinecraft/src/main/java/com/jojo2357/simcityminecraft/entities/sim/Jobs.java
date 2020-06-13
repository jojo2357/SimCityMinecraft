package com.jojo2357.simcityminecraft.entities.sim;

import java.util.ArrayList;

public final class Jobs{
	
	public static ArrayList<Jobs> JobList = new ArrayList<Jobs>();
	public static final Jobs UNEMPLOYED = new Jobs(0, 0F);
	public static final Jobs SHEPARD = new Jobs(1, 0.5F);
	public static final Jobs BUILDER = new Jobs(2, 0.1F);
	
	private final int ID;
	private final float WAGE;
	
	private Jobs(int IdIn, float wage) {
		this.ID = IdIn;
		this.WAGE = wage;
	}
	
	public static Jobs getJobFromId(int findID) {
		for (int i = 0; i < JobList.size(); i++) {
			if (JobList.get(i).ID == findID) return JobList.get(i);
		}
		return null;
	}
	
}
