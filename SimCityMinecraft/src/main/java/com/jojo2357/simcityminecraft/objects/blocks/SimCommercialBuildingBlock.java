package com.jojo2357.simcityminecraft.objects.blocks;

import com.jojo2357.simcityminecraft.init.ModItems;
import com.jojo2357.simcityminecraft.tileentity.SimCommercialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimResidentialBuildingBlockTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SimCommercialBuildingBlock extends ContainerBlock {

	public SimCommercialBuildingBlock(Block.Properties builder) {
		super(builder);
	}

	@Override
	public SimCommercialBuildingBlockTileEntity createNewTileEntity(IBlockReader worldIn) {
		return new SimCommercialBuildingBlockTileEntity();
	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SimCommercialBuildingBlockTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (SimCommercialBuildingBlockTileEntity) tileentity, pos);
				// player.openContainer((SimFarmBlockTileEntity)tileentity);
				player.addStat(Stats.INSPECT_HOPPER);
			}

			return ActionResultType.SUCCESS;
		}
	}

}
