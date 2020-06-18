package com.jojo2357.simcityminecraft.util.handler;

import java.util.function.Supplier;

import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;

public class MyMessage{
	private int index;
	private BlockPos pos;
	
	public MyMessage(int index) {
		this.index = index;
	}
	
	public MyMessage(int index, BlockPos pos) {
		this.index = index;
		this.pos = pos;
	}
	
	public void serialize(PacketBuffer buffer) {
		buffer.writeInt(this.index);
		buffer.writeInt(this.pos.getX());
		buffer.writeInt(this.pos.getY());
		buffer.writeInt(this.pos.getZ());
	}
	
    public static MyMessage deserialize(PacketBuffer buffer) {
        int index = buffer.readInt();
        int getx = buffer.readInt();
        int gety = buffer.readInt();
        int getz = buffer.readInt();
        return new MyMessage(index, new BlockPos(getx, gety, getz));
    }
    
    public static boolean handle(MyMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        System.out.println(context.getDirection());
        boolean fail = true;
        Minecraft client = Minecraft.getInstance();
        //if (client.world.getWorld().isRemote()) fail = false;
        if (context.getDirection().getReceptionSide() == LogicalSide.SERVER) {
            context.enqueueWork(() -> {
                TileEntity entity = client.world.getWorld().getTileEntity(message.pos);
                if (entity instanceof SimFarmBlockTileEntity) {
                	((SimFarmBlockTileEntity)entity).buttonClicked(message.index);
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
