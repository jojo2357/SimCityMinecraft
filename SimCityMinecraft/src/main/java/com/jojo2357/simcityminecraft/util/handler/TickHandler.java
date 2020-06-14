package com.jojo2357.simcityminecraft.util.handler;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.objects.blocks.SimWorkBenchBlock;

import net.minecraft.util.math.BlockPos;

public class TickHandler {
	
	private static ArrayList<SimWorkBenchBlock> trackedBenches = new ArrayList<SimWorkBenchBlock>();
	private static ArrayList<BlockPos> trackedBenchLocations = new ArrayList<BlockPos>();
	
	public TickHandler() {
		
	}
	
	public void doTick() {
		for(int k = 0; k < trackedBenches.size(); k++) {
			trackedBenches.get(k).tick(trackedBenchLocations.get(k));
		}
	}
	
	public static void addBlock(SimWorkBenchBlock blockIn, BlockPos pos) {
		//System.out.println(trackedBenches.size() + " " + trackedBenchLocations.size());
		if (!trackedBenchLocations.contains(pos)) {
			trackedBenches.add(blockIn);
			trackedBenchLocations.add(pos);
		}
	}
	
	public static void removeBlock(BlockPos pos) {
		int index = trackedBenchLocations.indexOf(pos);
		if (index < 0) return;
		System.out.println(index + " " + trackedBenchLocations.size());
		int i = 0;
		for (BlockPos z : trackedBenchLocations) {
			System.out.println(i + ": " + (z));
			i++;
		}
		trackedBenches.remove(index);
		trackedBenchLocations.remove(index);
		trackedBenchLocations.size();
		i = 0;
		for (BlockPos z : trackedBenchLocations) {
			System.out.println(i + ": " + (z));
			i++;
		}
	}
}
