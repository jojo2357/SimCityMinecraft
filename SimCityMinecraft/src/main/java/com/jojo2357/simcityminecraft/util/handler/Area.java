package com.jojo2357.simcityminecraft.util.handler;

import java.util.ArrayList;

import net.minecraft.util.math.BlockPos;

public class Area {
	
	private BlockPos placedCorner;
	private BlockPos b;
	private BlockPos c;
	private BlockPos guessedCorner;
	private boolean inUse = false;
	
	//private ArrayList<Integer> places;
	
	private int y;
	private int cornerIndex;
	private int width;
	private int length;
	private int area;
	
	public boolean taken() {
		return inUse;
	}
	
	public void markTaken() {
		inUse = true;
	}
	
	public Area(ArrayList<BlockPos> places) {
		/*for (BlockPos place : places) {
			this.places.add(place.getX());
			this.places.add(place.getZ());
		}*/

		findCorner(places);
		this.y = this.placedCorner.getY();
		//this.places.add(this.y);
		findMissingCorner(places);
		this.length = Math.abs(this.placedCorner.getX() - this.guessedCorner.getX()) - 1;
		this.width = Math.abs(this.placedCorner.getZ() - this.guessedCorner.getZ()) - 1;
		this.area = this.length * this.width;
	}
	
	/*public Area(int[] intArray) {
		this(translate(intArray));
		for (int i : intArray)
		this.places.add(i);
	}

	private static ArrayList<BlockPos> translate(int[] intArray) {
		ArrayList<BlockPos> out = new ArrayList<BlockPos>();
		int y = intArray[intArray.length - 1];
		for (int i = 0; i < 3; i++)
		out.add(new BlockPos(intArray[2 * i], y, intArray[2 * i + 1]));
		return out;
	}*/

	public Area(int PlacedX, int PlacedZ, int GuessedX, int GuessedZ, int Yplane) {
		this.placedCorner = new BlockPos(PlacedX, Yplane, PlacedZ);
		this.guessedCorner = new BlockPos(GuessedX, Yplane, GuessedZ);
		this.b = new BlockPos(PlacedX, Yplane, GuessedZ);
		this.c = new BlockPos(GuessedX, Yplane, PlacedZ);
		this.length = Math.abs(PlacedX - GuessedX) - 1;
		this.width = Math.abs(PlacedZ - GuessedZ) - 1;
		this.area = this.length * this.width;
	}

	private void findMissingCorner(ArrayList<BlockPos> places) {
		if (this.placedCorner.getX() == b.getX()) this.guessedCorner = new BlockPos(c.getX(), this.y, b.getZ());
		if (this.placedCorner.getX() == c.getX()) this.guessedCorner = new BlockPos(b.getX(), this.y, c.getZ());
	}
	
	private void findCorner(ArrayList<BlockPos> places) {
		int cornerIndex = (findCornerIndex(places));
		System.out.println("cornerIndex: " + cornerIndex);
		System.out.println(places.get(0).getX() + ", " + places.get(0).getY() + ", " + places.get(0).getZ());
		System.out.println(places.get(1).getX() + ", " + places.get(1).getY() + ", " + places.get(1).getZ());
		System.out.println(places.get(2).getX() + ", " + places.get(2).getY() + ", " + places.get(2).getZ());
		switch (cornerIndex) {
			case 0:
				this.b = places.get(1);
				this.c = places.get(2);
				break;
			case 1:
				this.b = places.get(0);
				this.c = places.get(2);
				break;
			case 2:
				this.b = places.get(0);
				this.c = places.get(1);
				break;
		}
		this.placedCorner = places.get(cornerIndex);
	}
	
	private int findCornerIndex(ArrayList<BlockPos> places) {
		BlockPos A = places.get(0);
		BlockPos B = places.get(1);
		BlockPos C = places.get(2);
		if ((A.getX() == B.getX() && A.getZ() == C.getZ()) || (A.getZ() == B.getZ() && A.getX() == C.getX())) return 0;
		if ((B.getX() == A.getX() && B.getZ() == C.getZ()) || (B.getZ() == A.getZ() && B.getX() == C.getX())) return 1;
		if ((C.getX() == B.getX() && A.getZ() == C.getZ()) || (C.getZ() == B.getZ() && A.getX() == C.getX())) return 2;
		return 3;
	}

	public BlockPos getGuessedCorner() {
		return guessedCorner;
	}
	public BlockPos getPlacedCorner() {
		return placedCorner;
	}
	public BlockPos getB() {
		return b;
	}
	public BlockPos getC() {
		return c;
	}

	public int getWidth() {
		return this.width;
	}
	
	public int getLength() {
		return this.length;
	}
	
	public int getArea() {
		return this.area;
	}

	public Area markNotTaken() {
		this.inUse = false;
		return this;
	}
	
	/*public ArrayList<Integer> getPlaces(){
		return this.places;
	}*/
}
