package com.jojo2357.simcityminecraft.util.handler;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.objects.blocks.SimMarker;

import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AreaHandler{
	
	public static ArrayList<Area> definedAreas = new ArrayList<Area>();
	public static int liveMarkers = 0;
	
	private static int yPlane = 0;
	private static ArrayList<BlockPos> markers = new ArrayList<BlockPos>();
	
	public static String addMarker(BlockPos pos, World worldIn) {
		String out;
		liveMarkers++;
		if (markers.size() > 0) {
			if (yPlane != pos.getY()) {
				if (yPlane > pos.getY()) out = "This marker is too low";
				else if (yPlane < pos.getY()) out = "This marker is too high";
				else out = "This marker is not in line with the others";
				killMarkers(worldIn);
				markers.clear();
				liveMarkers = 0;
				return out;
			}
		}else {
			if (hasOverLaps(pos)) {
				markers.add(pos);
				killMarkers(worldIn);
				markers.clear();
				liveMarkers = 0;
				return "This marker is in a marked area";
			}
			yPlane = pos.getY();
		}
		markers.add(pos);
		if (liveMarkers == 3) {
			checkValid(worldIn);
			markers.clear();
			return "Created a new area! Place down a farming or mining box to get started";
		}
		return liveMarkers + " valid markers, " + (3 - liveMarkers) + " more";
	}
	
	public static boolean inLine(BlockPos pos) {
		for (BlockPos inLimbo : markers) {
			if (inLimbo.getX() != pos.getX() && inLimbo.getZ() != pos.getZ()) return false;
		}
		return true;
	}
	
	private static void checkValid(World worldIn) {
		liveMarkers = 0;
		BlockPos pos1 = markers.get(0);
		BlockPos pos2 = markers.get(1);
		BlockPos pos3 = markers.get(2);
		if ((pos1.getX() == pos2.getX() && pos3.getX() == pos2.getX()) || 
				(pos1.getZ() == pos2.getZ() && pos3.getZ() == pos2.getZ()) ||
				(pos1.getX() != pos2.getX() && pos3.getX() != pos2.getX() && pos3.getX() != pos1.getX()) ||
				(pos1.getZ() != pos2.getZ() && pos3.getZ() != pos2.getZ() && pos3.getZ() != pos1.getZ())) {
			killMarkers(worldIn);
			return ;
		}
		
		if (pos1.getX() == pos2.getX() ^ pos3.getX() == pos2.getX() ^ pos1.getX() == pos3.getX()) {
			if ((pos1.getZ() == pos2.getZ() ^ pos3.getZ() == pos2.getZ()) ^ pos1.getZ() == pos3.getZ()) {
				if (hasOverLaps()) {
					killMarkers(worldIn);
					return;
				}
				upgradeMarkers(worldIn);
			}
		}
	}
	
	private static boolean hasOverLaps() {
		Area currentPoints = new Area(markers);
		System.out.println("Guessed: " + currentPoints.getGuessedCorner().getX() + ", " + currentPoints.getGuessedCorner().getY() + ", " + currentPoints.getGuessedCorner().getZ());
		System.out.println("Placed: " + currentPoints.getPlacedCorner().getX() + ", " + currentPoints.getPlacedCorner().getY() + ", " + currentPoints.getPlacedCorner().getZ());
		if (definedAreas.size() == 0) {
			definedAreas.add(currentPoints);
			return false;
		}
		for (Area inQuestion : definedAreas) {
			if (isOverlapping(currentPoints, inQuestion)) return true;
		}
		definedAreas.add(currentPoints);
		return false;
	}
	
	private static boolean hasOverLaps(BlockPos pos) {
		if (definedAreas.size() == 0) {
			return false;
		}
		for (Area inQuestion : definedAreas) {
			if (isOverlapping(pos, inQuestion)) return true;
		}
		return false;
	}
	
	private static boolean isOverlapping(BlockPos pos, Area second) {
		if ((pos.getX() <= second.getPlacedCorner().getX() && 
			pos.getX() >= second.getGuessedCorner().getX()) || 
			(pos.getX() >= second.getPlacedCorner().getX() && 
			pos.getX() <= second.getGuessedCorner().getX()))
			if ((pos.getZ() <= second.getPlacedCorner().getZ() && 
				pos.getZ() >= second.getGuessedCorner().getZ()) || 
				(pos.getZ() >= second.getPlacedCorner().getZ() && 
				pos.getZ() <= second.getGuessedCorner().getZ())) return true;
		return false;
	}
	
	private static boolean isOverlapping(Area first, Area second) {
		if (isOverlap(first.getPlacedCorner().getX(), first.getPlacedCorner().getZ(), second.getPlacedCorner().getX(), 
				second.getGuessedCorner().getX(), second.getPlacedCorner().getZ(), second.getGuessedCorner().getZ())) return true;
		
		if (isOverlap(first.getGuessedCorner().getX(), first.getGuessedCorner().getZ(), second.getPlacedCorner().getX(), 
				second.getGuessedCorner().getX(), second.getPlacedCorner().getZ(), second.getGuessedCorner().getZ())) return true;
		
		if (isOverlap(first.getB().getX(), first.getB().getZ(), second.getPlacedCorner().getX(), 
				second.getGuessedCorner().getX(), second.getPlacedCorner().getZ(), second.getGuessedCorner().getZ())) return true;
		
		if (isOverlap(first.getC().getX(), first.getC().getZ(), second.getPlacedCorner().getX(), 
				second.getGuessedCorner().getX(), second.getPlacedCorner().getZ(), second.getGuessedCorner().getZ())) return true;
		
		/*
		if ((first.getPlacedCorner().getX() <= second.getPlacedCorner().getX() && 
				first.getPlacedCorner().getX() >= second.getGuessedCorner().getX()) || 
				(first.getPlacedCorner().getX() >= second.getPlacedCorner().getX() && 
				first.getPlacedCorner().getX() <= second.getGuessedCorner().getX()))
		if ((first.getPlacedCorner().getZ() <= second.getPlacedCorner().getZ() && 
				first.getPlacedCorner().getZ() >= second.getGuessedCorner().getZ()) ||
				(first.getPlacedCorner().getZ() >= second.getPlacedCorner().getZ() && 
				first.getPlacedCorner().getZ() <= second.getGuessedCorner().getZ())) return true;
		
		if ((first.getGuessedCorner().getX() <= second.getPlacedCorner().getX() && 
				first.getGuessedCorner().getX() >= second.getGuessedCorner().getX()) || 
				(first.getGuessedCorner().getX() >= second.getPlacedCorner().getX() && 
				first.getGuessedCorner().getX() <= second.getGuessedCorner().getX()))
		if ((first.getGuessedCorner().getZ() <= second.getPlacedCorner().getZ() && 
				first.getGuessedCorner().getZ() >= second.getGuessedCorner().getZ()) ||
				(first.getGuessedCorner().getZ() >= second.getPlacedCorner().getZ() && 
				first.getGuessedCorner().getZ() <= second.getGuessedCorner().getZ())) return true;
		
		if ((first.getB().getX() <= second.getPlacedCorner().getX() && 
				first.getB().getX() >= second.getGuessedCorner().getX()) || 
				(first.getB().getX() >= second.getPlacedCorner().getX() && 
				first.getB().getX() <= second.getGuessedCorner().getX()))
		if ((first.getB().getZ() <= second.getPlacedCorner().getZ() && 
				first.getB().getZ() >= second.getGuessedCorner().getZ()) ||
				(first.getB().getZ() >= second.getPlacedCorner().getZ() && 
				first.getB().getZ() <= second.getGuessedCorner().getZ())) return true;
		
		if ((first.getC().getX() <= second.getPlacedCorner().getX() && 
				first.getC().getX() >= second.getGuessedCorner().getX()) || 
				(first.getC().getX() >= second.getPlacedCorner().getX() && 
				first.getC().getX() <= second.getGuessedCorner().getX()))
		if ((first.getC().getZ() <= second.getPlacedCorner().getZ() && 
				first.getC().getZ() >= second.getGuessedCorner().getZ()) ||
				(first.getC().getZ() >= second.getPlacedCorner().getZ() && 
				first.getC().getZ() <= second.getGuessedCorner().getZ())) return true;*/
		return false;
	}
	
	private static boolean isOverlap(int x, int z, int xx, int xxx, int zz, int zzz) {
		if ((x <= xx && 
				x >= xxx) || 
				(x >= xx && 
				x <= xxx))
		if ((z <= zz && 
				z >= zzz) ||
				(z >= zz && 
				z <= zzz)) return true;
		return false;
	}
	
	private static void upgradeMarkers(World worldIn) {
		for (BlockPos pos : markers) {
			worldIn.setBlockState(pos, ModBlocks.SIM_MARKER.get().getDefaultState().with(SimMarker.getColorState(), 3));
		}
		
	}
	
	private static void killMarkers(World worldIn) {
		for (BlockPos pos : markers) {
			worldIn.setBlockState(pos, ModBlocks.SIM_MARKER.get().getDefaultState().with(SimMarker.getColorState(), 1));
		}
	}
}
