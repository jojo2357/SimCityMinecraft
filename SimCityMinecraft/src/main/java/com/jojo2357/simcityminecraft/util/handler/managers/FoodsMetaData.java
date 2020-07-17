package com.jojo2357.simcityminecraft.util.handler.managers;

import java.util.ArrayList;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

public enum FoodsMetaData {
	BREAD(0, 0.01D, 6, Items.WHEAT),
	CARROT(1, 0, 3,Items.CARROT),
	POTATO(2, 0, 2,Items.POTATO),
	BAKED_POTATO(3, 0, 5,Items.BAKED_POTATO);
	
	public double cost;
	public int fillingness;
	public Item foodItem;
	private final int ID;
	
	private FoodsMetaData(int id, double cost, int fillingness, Item foodItem) {
		this.cost = cost;
		this.foodItem = foodItem;
		this.fillingness = fillingness;
		this.ID = id;
	}
	
	public static FoodsMetaData getFoodById(int id) {
		for (FoodsMetaData food: FoodsMetaData.getAllowedFoods()) {
			if (food.ID == id) return food;
		}
		return null;
	}
	
	public static ArrayList<FoodsMetaData> getAllowedFoods(){
		ArrayList<FoodsMetaData> out = new ArrayList<FoodsMetaData>();
		out.add(BREAD);
		out.add(CARROT);
		out.add(POTATO);
		out.add(BAKED_POTATO);
		return out;
	}

	public static boolean isFood(Item itemIn) {
		for (FoodsMetaData item : FoodsMetaData.getAllowedFoods()) {
			if (itemIn == item.foodItem) return true;
		}
		return false;
	}

	public static FoodsMetaData getFoodByItem(Item itemIn) {
		for (FoodsMetaData item : FoodsMetaData.getAllowedFoods()) {
			if (itemIn == item.foodItem) return item;
		}
		return null;
	}
}
