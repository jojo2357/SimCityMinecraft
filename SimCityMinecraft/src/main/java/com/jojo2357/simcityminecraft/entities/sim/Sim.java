package com.jojo2357.simcityminecraft.entities.sim;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

@SuppressWarnings("unused")
public class Sim extends AnimalEntity{
	
	private boolean hasHome = false;
	private Jobs job = Jobs.UNEMPLOYED;

	public Sim(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		//this.canAttack(attackingPlayer);
		this.canBreatheUnderwater();
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(3, new PanicGoal(this, 1.5D));
		this.goalSelector.addGoal(2, new RandomWalkingGoal(this, 0.2D, 100));
		//this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		//this.goalSelector.addGoal(3,
				//new TemptGoal(this, 1.1D, Ingredient.fromItems(ItemInit.DEF_ITEM.get()), false));
		this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.addGoal(4, new LookRandomlyGoal(this));
	}

	@Override
	public AgeableEntity createChild(AgeableEntity ageable) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void registerAttributes() {
		super.registerAttributes();
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
	}

}
