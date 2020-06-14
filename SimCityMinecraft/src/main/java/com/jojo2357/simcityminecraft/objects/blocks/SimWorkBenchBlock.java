package com.jojo2357.simcityminecraft.objects.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.tileentity.SimWorkBenchTileEntity;
import com.jojo2357.simcityminecraft.util.handler.AreaHandler;
import com.jojo2357.simcityminecraft.util.handler.TickHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.network.NetworkHooks;

public class SimWorkBenchBlock extends Block{
	
	private BlockPos chestSpot = null;
	private boolean active = false;
	private BlockPos pos;
	private World world;

	public SimWorkBenchBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.SIM_WORK_BENCH.get().create();
	}
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		TickHandler.addBlock(this, pos);
		this.pos = pos;
		this.world = worldIn;
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult result) {
		if (!worldIn.isRemote) {
			chestFind(worldIn, pos);
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof SimWorkBenchTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (SimWorkBenchTileEntity) tile, pos);
				return ActionResultType.SUCCESS;
			}
		}
		return ActionResultType.FAIL;
	}

	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		System.out.println("I died @ " + pos + " " + this.pos);
		TickHandler.removeBlock(pos);
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof SimWorkBenchTileEntity) {
				InventoryHelper.dropItems(worldIn, pos, ((SimWorkBenchTileEntity) te).getItems());
				
			}
		}
	}
	   
	public void tick(World worldIn, BlockPos pos) {
		//System.out.println("tock");
		if (chestSpot == null) {
			if (worldIn.getTileEntity(this.pos.north()) instanceof ChestTileEntity) {
				System.out.println("Chest Detected");
				this.chestSpot = this.pos.north();
			}
			if (worldIn.getTileEntity(this.pos.south()) instanceof ChestTileEntity) {
				System.out.println("Chest Detected");
				this.chestSpot = this.pos.south();
			}
			if (worldIn.getTileEntity(this.pos.east()) instanceof ChestTileEntity) {
				System.out.println("Chest Detected");
				this.chestSpot = this.pos.east();
			}
			if (worldIn.getTileEntity(this.pos.west()) instanceof ChestTileEntity) {
				System.out.println("Chest Detected");
				this.chestSpot = this.pos.west();
			}
		}else {
			if (!(worldIn.getTileEntity(this.chestSpot) instanceof ChestTileEntity)) {
				System.out.println("Chest Lost");
				this.chestSpot = null;
			}
		}
	}
	
	public void tick(BlockPos pos) {
		//System.out.println("tick");
		this.tick(this.world.getWorld(), pos);
	}
	
	public void chestFind(World worldIn, BlockPos pos) {
		System.out.println("tock");
		if (worldIn.getTileEntity(pos.north()) instanceof ChestTileEntity) {
			System.out.println("Chest Detected");
			this.chestSpot = pos.north();
		}
	}
}
