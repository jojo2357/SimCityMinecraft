package com.jojo2357.simcityminecraft.util.handler;

public class SimKredsHandler {

	private double kreds = 0.0D;
	
	public SimKredsHandler() {
		addKreds(10.0D);
	}

	public double getKreds() {
		return kreds;
	}

	public void addKreds(double kreds) {
		this.kreds += kreds;
	}
	

}
