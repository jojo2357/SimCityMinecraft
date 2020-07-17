package com.jojo2357.simcityminecraft.util.handler.messages;

import java.util.function.Supplier;

import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimWorkBenchTileEntity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class BuildingMessager{
	private int index;
	private BlockPos pos;
	private int mode;
	
	public BuildingMessager(int index) {
		this.index = index;
	}
	
	public BuildingMessager(int index, BlockPos pos, int mode) {
		this.index = index;
		this.pos = pos;
		this.mode = mode;
	}
	
	public void serialize(PacketBuffer buffer) {
		buffer.writeInt(this.index);
		buffer.writeBlockPos(this.pos);
		buffer.writeInt(this.mode);
	}
	
    public static BuildingMessager deserialize(PacketBuffer buffer) {
        int index = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();
        int mode = buffer.readInt();
        return new BuildingMessager(index, pos, mode);
    }
    
    public static boolean handle(BuildingMessager message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        boolean fail = true;
        //if (client.world.getWorld().isRemote()) fail = false;
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
            	//context;
            	World world = context.getSender().world;
                TileEntity entity = world.getTileEntity(message.pos);
                if (entity instanceof SimWorkBenchTileEntity) {
                	((SimWorkBenchTileEntity)entity).buttonClicked(message.index, message.mode);
                }
            });
        }

        return fail;
    }
}
