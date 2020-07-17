package com.jojo2357.simcityminecraft.client.gui.widgets;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.screens.SimMineBlockScreen;
import com.jojo2357.simcityminecraft.util.handler.messages.MiningMessager;

import net.minecraft.client.gui.widget.Widget;

public class MineButtonWidget extends Widget{
	
	private int myIndex;
	private SimMineBlockScreen owner;
	   private int ownerID;
	
	
	public MineButtonWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex, SimMineBlockScreen owner) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
		this.owner = owner;
	}
	
	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		MiningMessager message = (new MiningMessager(this.myIndex, owner.getContainer().tileEntity.getPos()));
		Main.INSTANCE.sendToServer(message);
		this.owner.getContainer().clickHappened(this.myIndex);
		if (this.myIndex == 1) {
			this.myIndex = 2;
			this.setMessage("OFF");
		}else if (this.myIndex == 2) {
			this.myIndex = 1;
			this.setMessage("ON");
		}
	}
}
