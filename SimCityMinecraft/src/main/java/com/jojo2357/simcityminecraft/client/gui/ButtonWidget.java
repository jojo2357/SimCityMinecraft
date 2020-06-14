package com.jojo2357.simcityminecraft.client.gui;

import com.jojo2357.simcityminecraft.objects.blocks.SimWorkBenchBlock;

import net.minecraft.client.gui.widget.Widget;

public class ButtonWidget extends Widget{
	
	private int myIndex;
	
	public ButtonWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
	}
	
	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		//SimWorkBenchBlock.onSelectionMade(myIndex);
	}
}
