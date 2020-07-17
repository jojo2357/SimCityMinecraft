package com.jojo2357.simcityminecraft.objects.blocks;

import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;

import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.ContainerBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public class SimMineBlockBlock extends ContainerBlock {
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final IntegerProperty COLORSTATE = IntegerProperty.create("colorstate", 1, 3);

	public SimMineBlockBlock(Block.Properties properties) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(FACING, Direction.NORTH).with(COLORSTATE, 1));
	}

	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction direction = context.getFace().getOpposite();
		return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().getOpposite())
				.with(COLORSTATE, 1);
	}

	public TileEntity createNewTileEntity(IBlockReader worldIn) {
		return new SimMineBlockTileEntity();
	}

	/**
	 * Called by ItemBlocks after a block is set in the world, to allow post-place
	 * logic
	 */
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasDisplayName()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SimMineBlockTileEntity) {
				((SimMineBlockTileEntity) tileentity).setCustomName(stack.getDisplayName());
			}
		}

	}

	public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player,
			Hand handIn, BlockRayTraceResult hit) {
		if (worldIn.isRemote) {
			return ActionResultType.SUCCESS;
		} else {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SimMineBlockTileEntity) {
				NetworkHooks.openGui((ServerPlayerEntity) player, (SimMineBlockTileEntity) tileentity, pos);
				// player.openContainer((SimMineBlockTileEntity)tileentity);
				player.addStat(Stats.INSPECT_HOPPER);
			}

			return ActionResultType.SUCCESS;
		}
	}

	public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
		
		if (state.getBlock() != newState.getBlock()) {
			TileEntity tileentity = worldIn.getTileEntity(pos);
			if (tileentity instanceof SimMineBlockTileEntity) {
				((SimMineBlockTileEntity) tileentity).justDied();
				((SimMineBlockTileEntity) tileentity).releaseArea();
				InventoryHelper.dropInventoryItems(worldIn, pos, (SimMineBlockTileEntity) tileentity);
				worldIn.updateComparatorOutputLevel(pos, this);
			}

			super.onReplaced(state, worldIn, pos, newState, isMoving);
		}
	}

	/**
	 * The type of render function called. MODEL for mixed tesr and static model,
	 * MODELBLOCK_ANIMATED for TESR-only, LIQUID for vanilla liquids, INVISIBLE to
	 * skip all rendering
	 * 
	 * @deprecated call via {@link IBlockState#getRenderType()} whenever possible.
	 *             Implementing/overriding is fine.
	 */
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	/**
	 * @deprecated call via {@link IBlockState#hasComparatorInputOverride()}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	public boolean hasComparatorInputOverride(BlockState state) {
		return true;
	}

	/**
	 * @deprecated call via
	 *             {@link IBlockState#getComparatorInputOverride(World,BlockPos)}
	 *             whenever possible. Implementing/overriding is fine.
	 */
	public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
		return Container.calcRedstone(worldIn.getTileEntity(pos));
	}

	/**
	 * Returns the blockstate with the given rotation from the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via {@link IBlockState#withRotation(Rotation)} whenever
	 *             possible. Implementing/overriding is fine.
	 */
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.with(FACING, rot.rotate(state.get(FACING)));
	}

	/**
	 * Returns the blockstate with the given mirror of the passed blockstate. If
	 * inapplicable, returns the passed blockstate.
	 * 
	 * @deprecated call via {@link IBlockState#withMirror(Mirror)} whenever
	 *             possible. Implementing/overriding is fine.
	 */
	public BlockState mirror(BlockState state, Mirror mirrorIn) {
		return state.rotate(mirrorIn.toRotation(state.get(FACING)));
	}

	protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
		builder.add(FACING, COLORSTATE);
	}

	public boolean allowsMovement(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
		return false;
	}

	public static IntegerProperty getColorState() {
		return COLORSTATE;
	}

	public static DirectionProperty getFacing() {
		return FACING;
	}
}