package com.jojo2357.simcityminecraft.client.gui.widgets;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.screens.SimWorkBenchScreen;
import com.jojo2357.simcityminecraft.util.handler.messages.BuildingMessager;

import net.minecraft.client.gui.widget.Widget;

public class BuildButtonWidget extends Widget{
	
	private int myIndex;
	private SimWorkBenchScreen owner;
	private int ownerID;
	private int mode;	
	
	public BuildButtonWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex, SimWorkBenchScreen owner, int mode) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
		this.owner = owner;
		this.mode = mode;
	}
	
	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		if (this.mode == 5) {
			this.owner.selectionMade(this.myIndex);
		}
		this.owner.changeMode(this.myIndex);
		BuildingMessager message = (new BuildingMessager(this.myIndex, owner.getContainer().tileEntity.getPos(), this.mode));
		Main.INSTANCE.sendToServer(message);
		this.owner.getContainer().clickHappened(this.myIndex);
	}
}
