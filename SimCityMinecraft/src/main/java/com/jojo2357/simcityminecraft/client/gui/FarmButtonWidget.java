package com.jojo2357.simcityminecraft.client.gui;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.objects.blocks.SimWorkBenchBlock;
import com.jojo2357.simcityminecraft.util.handler.ModPacketHandler;
import com.jojo2357.simcityminecraft.util.handler.MyMessage;

import net.minecraft.client.gui.widget.Widget;
import net.minecraftforge.fml.network.PacketDistributor;

public class FarmButtonWidget extends Widget{
	
	private int myIndex;
	private SimFarmBlockScreen owner;
	
	
	public FarmButtonWidget(int xIn, int yIn, int widthIn, int heightIn, String msg, int myIndex, SimFarmBlockScreen owner) {
		super(xIn, yIn, widthIn, heightIn, msg);
		this.myIndex = myIndex;
		this.owner = owner;
	}
	
	@Override
	public void onClick(double p_onClick_1_, double p_onClick_3_) {
		MyMessage message = (new MyMessage(this.myIndex, owner.getContainer().tileEntity.getPos()));
		Main.INSTANCE.sendToServer(message);
	}
}
