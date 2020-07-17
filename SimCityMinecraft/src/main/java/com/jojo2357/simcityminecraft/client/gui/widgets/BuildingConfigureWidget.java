package com.jojo2357.simcityminecraft.client.gui.widgets;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.screens.SimResidentialBuildingBlockScreen;
import com.jojo2357.simcityminecraft.client.gui.screens.SimWorkBenchScreen;
import com.jojo2357.simcityminecraft.util.handler.messages.ConfigureMessager;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.math.BlockPos;

public class BuildingConfigureWidget extends Widget {

	private int myIndex; //What setting am I?
	private int ownerIndex; //Owner # in list
	private int ownerType;	//Owner category
	private int myMode; //What field am I?
	
	private SimResidentialBuildingBlockScreen owner;
	
	private BlockPos ownerPlace;
	/*
	 * xloc, yloc, width, height, msg, setting, owner index in list, owner category, field, owner
	 */
	public BuildingConfigureWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex, int ownerIndex,
			int ownerType, int mode, BlockPos ownerPlace, SimResidentialBuildingBlockScreen owner) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
		this.ownerIndex = ownerIndex;
		this.ownerType = ownerType;
		this.myMode = mode;
		this.ownerPlace = ownerPlace;
		this.owner = owner;
	}

	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		ConfigureMessager message = (new ConfigureMessager(this.myIndex, this.myMode, this.ownerIndex, this.ownerType, this.ownerPlace));
		Main.INSTANCE.sendToServer(message);
	}
}
