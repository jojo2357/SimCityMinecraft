package com.jojo2357.simcityminecraft.objects.blocks;

import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimWorkBenchTileEntity;
import com.jojo2357.simcityminecraft.util.handler.Area;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SimFarmBlockBlock extends Block {
	
	public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
	public static final IntegerProperty COLORSTATE = IntegerProperty.create("colorstate", 1, 3);

	public SimFarmBlockBlock(Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(COLORSTATE, 1));
	}
	
	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING);
		builder.add(COLORSTATE);
	}
	
	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}
	
	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ModTileEntityTypes.SIM_FARM_BLOCK.get().create();
	}
	
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite());
	}

	@Override
	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult result) {
		if (!worldIn.isRemote) {		
			TileEntity tile = worldIn.getTileEntity(pos);
			if (tile instanceof SimFarmBlockTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (SimFarmBlockTileEntity) tile, pos);
				return ActionResultType.SUCCESS;
			}
			return ActionResultType.FAIL;
		}
		return ActionResultType.SUCCESS;
	}
	
	@Override
	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		//TickHandler.removeBlock(pos);
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = worldIn.getTileEntity(pos);
			if (te instanceof SimWorkBenchTileEntity) {
				InventoryHelper.dropItems(worldIn, pos, ((SimWorkBenchTileEntity) te).getItems());
				
			}
		}
	}

	public static IntegerProperty getColorState() {
		return COLORSTATE;
	}
	
	public static DirectionProperty getFacing() {
		return FACING;
	}

}
