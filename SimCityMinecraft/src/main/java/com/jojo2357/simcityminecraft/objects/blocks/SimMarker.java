package com.jojo2357.simcityminecraft.objects.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;

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
	
	public SimMarker(Properties properties) {
		super(properties);;
		// TODO Auto-generated constructor stub
	}
	
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> p_206840_1_) {
        p_206840_1_.add(new IProperty[]{COLORSTATE});
    }
	
    @Nullable
    public BlockState getStateForPlacement(BlockItemUseContext blockItemUseContext)
    {
        BlockState blockState = blockItemUseContext.getWorld().getBlockState(blockItemUseContext.getPos());
        if (blockState.getBlock() == this) {
        	int num = 3;
        			//Main.rand.nextInt(3)+1;
            return blockState.with(COLORSTATE, Math.min(3, blockState.get(COLORSTATE) + 1));
        } else {
            return super.getStateForPlacement(blockItemUseContext);
        }
    }
	
	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
		if (!worldIn.isRemote) {
			placer.sendMessage(new TranslationTextComponent("You placed me?"));
		}
	}
	
}
