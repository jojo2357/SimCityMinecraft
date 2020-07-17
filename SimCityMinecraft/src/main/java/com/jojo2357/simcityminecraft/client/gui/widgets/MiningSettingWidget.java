package com.jojo2357.simcityminecraft.client.gui.widgets;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.screens.SimMineBlockScreen;
import com.jojo2357.simcityminecraft.util.handler.messages.MiningSettingMessager;

import net.minecraft.client.gui.widget.Widget;

public class MiningSettingWidget extends Widget {

	private int myIndex;
	private SimMineBlockScreen owner;
	private int ownerID;

	public MiningSettingWidget(int xIn, int yIn, int widthIn, int heightIn, int myIndex, SimMineBlockScreen owner) {
		super(xIn, yIn, widthIn, heightIn, "");
		this.myIndex = myIndex;
		this.owner = owner;
		this.findMessage();
	}

	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		MiningSettingMessager message = (new MiningSettingMessager(this.myIndex,
				owner.getContainer().tileEntity.getPos()));
		Main.INSTANCE.sendToServer(message);
		this.owner.getContainer().clickHappened(this.myIndex);
		this.updateMessage();
	}

	private void updateMessage() {
		this.findMessage();
		switch (this.myIndex) {
		case 0:
			this.myIndex = 1;
			break;
		case 1:
			this.myIndex = 2;
			break;
		case 2:
			this.myIndex = 3;
			break;
		case 3:
			this.myIndex = 0;
			break;
		}
	}

	private void findMessage() {
		switch (this.myIndex) {
		case 0:
			this.setMessage("Discard Dirt");
			break;
		case 1:
			this.setMessage("Discard Stone");
			break;
		case 2:
			this.setMessage("Discard Dirt and Stone");
			break;
		case 3:
			this.setMessage("Keep All");
			break;
		}
	}

}
