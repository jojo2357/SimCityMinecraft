package com.jojo2357.simcityminecraft.entities.sim;

import java.util.ArrayList;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SimManager {
	
	private ArrayList<Sim> simRegistry = new ArrayList<Sim>();
	private int simCount = 0;
	
	public SimManager() {
		
	}
	
	//@OnlyIn(Dist.DEDICATED_SERVER)
	public void addSim(Sim toAdd) {
		simRegistry.add(toAdd);
		simCount++;
	}
	
	public int simCount() {
		return this.simCount;
	}
	
	public ArrayList<Sim> getSims(){
		return this.simRegistry;
	}

	//@OnlyIn(Dist.DEDICATED_SERVER)
	public void remove(Sim sim) {
		this.simRegistry.remove(sim);
		this.simCount--;		
	}

	
}
