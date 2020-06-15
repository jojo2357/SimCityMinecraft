package com.jojo2357.simcityminecraft.objects.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.util.handler.AreaHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.TorchBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IProperty;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SimMarker extends TorchBlock{
	
	public static final IntegerProperty COLORSTATE = IntegerProperty.create("colorstate", 1, 3);
	
	public SimMarker(Properties properties, int blockState) {
		super(properties);
		this.setDefaultState(this.stateContainer.getBaseState().with(COLORSTATE, blockState));
	}
	
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(COLORSTATE);
    }
	
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext)
    {
        BlockState blockState = blockItemUseContext.getWorld().getBlockState(blockItemUseContext.getPos());
        if (blockState.getBlock() == this) {
            return blockState.with(COLORSTATE, Math.min(3, blockState.get(COLORSTATE) + 1));
        } else {
            return super.getStateForPlacement(blockItemUseContext);
        }
    }
    
    public static IntegerProperty getColorState() {
    	return COLORSTATE;
    }
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!worldIn.isRemote) {
			placer.sendMessage(new TranslationTextComponent(AreaHandler.addMarker(pos, worldIn)));
			//if (!AreaHandler.inLine(pos)) worldIn.setBlockState(pos, ModBlocks.SIM_MARKER.get().getDefaultState().with(SimMarker.getColorState(), 1));
		}
	}
	
	
	
}
