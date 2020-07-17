package com.jojo2357.simcityminecraft.entities.sim;

import java.util.ArrayList;

public enum SimWealthClass {
	BROKE(0.0, 1.0, 0, "broke"),
	LOWER(1.0, 5.0, 1, "lower"),
	LOWER_MIDDLE(5.0, 10.0, 2, "lower middle"),
	MIDDLE(10.0, 20.0, 3, "middle"),
	UPPER_MIDDLE(20.0, 40.0, 4, "upper middle"),
	UPPER(40.0, 100.0, 5, "upper class");
	
	public double lowerMoney;
	public double upperMoney;
	public int id;
	public String name;
	
	public static int classes = 6;
	
	private SimWealthClass(double lower, double upper, int id, String name) {
		this.lowerMoney = lower;
		this.upperMoney = upper;
		this.id = id;
		this.name = name;
	}
	
	public static SimWealthClass getWealthClass(double moneyIn) {
		if (moneyIn >= 40.0) return UPPER;
		else if (moneyIn >= 20.0) return UPPER_MIDDLE;
		else if (moneyIn >= 10.0) return MIDDLE;
		else if (moneyIn >= 5.0) return LOWER_MIDDLE;
		else if (moneyIn >= 1.0) return LOWER;
		else return BROKE;
	}
	
	public static SimWealthClass getWealthClassById(int idIn) {
		switch (idIn) {
		case(0):
			return SimWealthClass.BROKE;
		case(1):
			return SimWealthClass.LOWER;
		case(2):
			return SimWealthClass.LOWER_MIDDLE;
		case(3):
			return SimWealthClass.MIDDLE;
		case(4):
			return SimWealthClass.UPPER_MIDDLE;
		case(5):
			return SimWealthClass.UPPER;
		default:
			return SimWealthClass.BROKE;
		}
	}
	
	public String toString() {
		return this.name;
	}
}
