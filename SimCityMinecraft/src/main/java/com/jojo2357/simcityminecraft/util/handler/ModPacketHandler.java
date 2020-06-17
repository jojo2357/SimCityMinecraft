package com.jojo2357.simcityminecraft.util.handler;

import com.google.common.base.Supplier;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class ModPacketHandler {
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
	    new ResourceLocation("simcityminecraft", "main"),
	    () -> PROTOCOL_VERSION,
	    PROTOCOL_VERSION::equals,
	    PROTOCOL_VERSION::equals
	);
	
	
	
	public static void handle(MyMessage msg, Supplier<NetworkEvent.Context> ctx) {
	    ctx.get().enqueueWork(() -> {
	        // Work that needs to be threadsafe (most work)
	        //EntityPlayerMP sender = ctx.get().getSender(); // the client that sent this packet
	        // do stuff
	    	msg.getEntity().clickHappened(msg);
	    });
	    ctx.get().setPacketHandled(true);
	}
}
