package com.jojo2357.simcityminecraft.entities.sim;

import net.minecraft.entity.CreatureEntity;
import net.minecraft.entity.ai.goal.MoveToBlockGoal;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;

public class MoveToPlaceGoal extends MoveToBlockGoal {

	private BlockPos target;

	public MoveToPlaceGoal(CreatureEntity creature, double speedIn, int length, BlockPos target) {
		super(creature, speedIn, length);
		this.target = target;
	}

	@Override
	protected boolean shouldMoveTo(IWorldReader worldIn, BlockPos pos) {
		BlockPos block = pos.up();
		return worldIn.isAirBlock(block) && this.creature.getPosition() != pos;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.target.getX() != this.creature.getPosition().getX()
				&& this.target.getY() != this.creature.getPosition().getY()
				&& this.target.getZ() != this.creature.getPosition().getZ();
	}

}
