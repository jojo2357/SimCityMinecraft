package com.jojo2357.simcityminecraft.client.gui.widgets;

import com.jojo2357.simcityminecraft.client.gui.screens.SimWorkBenchScreen;
import com.jojo2357.simcityminecraft.objects.blocks.SimWorkBenchBlock;

import net.minecraft.client.gui.widget.Widget;

public class ButtonWidget extends Widget{
	
	private int myIndex;
	private SimWorkBenchScreen owner;
	
	public ButtonWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex, SimWorkBenchScreen owner) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
		this.owner = owner;
	}
	
	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		this.owner.buttonClicked(myIndex);
	}
}
