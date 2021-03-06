package com.jojo2357.simcityminecraft.util.handler.messages;

import java.util.function.Supplier;

import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MiningMessager{
	private int index;
	private BlockPos pos;
	
	public MiningMessager(int index) {
		this.index = index;
	}
	
	public MiningMessager(int index, BlockPos pos) {
		this.index = index;
		this.pos = pos;
	}
	
	public void serialize(PacketBuffer buffer) {
		buffer.writeInt(this.index);
		buffer.writeBlockPos(this.pos);
	}
	
    public static MiningMessager deserialize(PacketBuffer buffer) {
        int index = buffer.readInt();
        BlockPos pos = buffer.readBlockPos();
        return new MiningMessager(index, pos);
    }
    
    public static boolean handle(MiningMessager message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        System.out.println(context.getDirection());
        boolean fail = true;
        //if (client.world.getWorld().isRemote()) fail = false;
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
            	//context;
            	World world = context.getSender().world;
                TileEntity entity = world.getTileEntity(message.pos);
                if (entity instanceof SimMineBlockTileEntity) {
                	((SimMineBlockTileEntity)entity).buttonClicked(message.index);
                }
            });
        }

        return fail;
    }

    /*@OnlyIn(Dist.CLIENT)
    private static <T> T getEntity(World world, int id, Class<T> type) {
        Entity entity = world.getEntityByID(id);
        if (entity != null && type.isAssignableFrom(entity.getClass())) {
            return type.cast(entity);
        }
        return null;
    }*/
}
