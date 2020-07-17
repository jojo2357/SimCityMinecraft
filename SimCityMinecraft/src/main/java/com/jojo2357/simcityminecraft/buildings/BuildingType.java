package com.jojo2357.simcityminecraft.buildings;

public enum BuildingType {
	
	RESIDENTIAL(1, "Residential"),
	COMMERCIAL(2, "Commercial"),
	COMMERCIAL_FOOD(3, "Restaurant"),
	COMMERCIAL_OTHER(4, "Commercial Other"),
	INDUSTRIAL(5, "Industrial"),
	INDUSTRIAL_FACTORY(6, "Factory");
	
	private final int id;
	private final String type;
	
	private BuildingType(int id, String name) {
		this.id = id;
		this.type = name;
	}

	public String getType() {
		return type;
	}

	public int getId() {
		return id;
	}
}
