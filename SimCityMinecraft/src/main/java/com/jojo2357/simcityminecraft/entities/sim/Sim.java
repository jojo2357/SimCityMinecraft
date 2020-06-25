package com.jojo2357.simcityminecraft.entities.sim;

import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimContainer;
import com.jojo2357.simcityminecraft.container.SimContainerProvider;

import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.stats.Stats;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

@SuppressWarnings("unused")
public class Sim extends AnimalEntity implements INamedContainerProvider{

	private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	private boolean hasHome = false;
	private Jobs job = Jobs.UNEMPLOYED;
	private boolean isWorking;
	private boolean hasJob;
	private String name = "Jimbo obmiJ";
	private RandomWalkingGoal walkGoal;
	private PanicGoal panicGoal;
	private int someNum;
	private BlockPos jobBlock;
	private int farmingXp;
	private int farmingLevel;
	private PlayerEntity playerJustClicked;
	private boolean menuOpen;

	public Sim(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
		// TODO Auto-generated constructor stub
	}

	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if (spawnDataIn == null) {
			spawnDataIn = new AgeableEntity.AgeableData();
		}

		AgeableEntity.AgeableData ageableentity$ageabledata = (AgeableEntity.AgeableData) spawnDataIn;
		if (ageableentity$ageabledata.func_226261_c_() && ageableentity$ageabledata.func_226257_a_() > 0
				&& this.rand.nextFloat() <= ageableentity$ageabledata.func_226262_d_()) {
			this.setGrowingAge(-0);
		}

		ageableentity$ageabledata.func_226260_b_();
		Main.simRegistry.addSim(this);
		System.out.println(Main.simRegistry.simCount());
		this.someNum = 420;

		return super.onInitialSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
	}

	@Override
	protected void registerGoals() {
		super.registerGoals();
		// this.canAttack(attackingPlayer);
		this.walkGoal = new RandomWalkingGoal(this, 0.2D, 100);
		this.panicGoal = new PanicGoal(this, 1.5D);
		this.goalSelector.addGoal(0, new SwimGoal(this));
		this.goalSelector.addGoal(3, this.panicGoal);
		this.goalSelector.addGoal(2, this.walkGoal);
		// this.goalSelector.addGoal(4, new );
		// this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
		// this.goalSelector.addGoal(3,
		// new TemptGoal(this, 1.1D, Ingredient.fromItems(ItemInit.DEF_ITEM.get()),
		// false));
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
		this.getAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(2.0D);
		this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(1.0D);
	}

	@Override
	protected int getExperiencePoints(PlayerEntity player) {
		return 0;
	}

	@Override
	public boolean isBreedingItem(ItemStack stack) {
		return false;
	}

	public void teleportTo(BlockPos pos) {
		this.teleportTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, 0.0F, 0.0F);
	}

	public void teleportTo(double x, double y, double z, float yaw, float pitch) {
		this.setLocationAndAngles(x, y, z, yaw, pitch);
	}

	public void forceSetPosition(BlockPos pos) {
		this.teleportTo(pos.getX() + 0.5D, pos.getY() + 1, pos.getZ() + 0.5D, 0, 0);
	}

	public void setWorking(boolean in, BlockPos caller) {
		this.isWorking = in;
		if (this.isWorking) {
			this.goalSelector.removeGoal(this.walkGoal);
			this.goalSelector.removeGoal(this.panicGoal);
			this.jobBlock = caller;
		} else {
			this.goalSelector.addGoal(3, this.panicGoal);
			this.goalSelector.addGoal(2, this.walkGoal);
		}
	}

	public void setJob() {
		this.hasJob = true;
	}

	public void unsetJob() {
		this.hasJob = false;
		this.jobBlock = null;
		this.isWorking = false;
		this.goalSelector.addGoal(3, this.panicGoal);
		this.goalSelector.addGoal(2, this.walkGoal);
	}

	public String getMyName() {
		return this.name;
	}

	public void writeAdditional(CompoundNBT compound) {
		super.writeAdditional(compound);
		// compound.putBoolean("IsWorking", value);
		compound.putInt("MyNum", this.someNum);
		compound.putBoolean("HasJob", this.hasJob);
		compound.putBoolean("Working", this.isWorking);
		if (this.isWorking)
			compound.putLong("WorkPlacePlace", this.jobBlock.toLong());
		compound.putInt("FarmingXp", this.farmingXp);
		compound.putInt("FarmingLevel", this.farmingLevel);
	}

	public void readAdditional(CompoundNBT compound) {
		super.readAdditional(compound);
		this.someNum = compound.getInt("MyNum");
		this.hasJob = compound.getBoolean("HasJob");
		this.isWorking = compound.getBoolean("Working");
		if (this.isWorking)
			this.jobBlock = BlockPos.fromLong(compound.getLong("WorkPlacePlace"));
		this.farmingXp = compound.getInt("FarmingXp");
		this.farmingLevel = compound.getInt("FarmingLevel");
	}

	public int getMyNum() {
		return this.someNum;
	}

	@Override
	public void livingTick() {
		super.livingTick();
		// this.markPotionsDirty();
		if (Main.simRegistry.getSims().contains(this) || this.dead)
			;
		// System.out.println("I am");
		else {
			// System.out.println("I am not");
			Main.simRegistry.addSim(this);
		}
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		Main.simRegistry.remove(this);

	}

	public BlockPos getWorkPlace() {
		return this.jobBlock;
	}

	public void addFarmingXp(int add) {
		this.farmingXp += add;
		if (this.farmingXp >= 2000) {
			this.farmingXp -= 2000;
			this.farmingLevel++;
		}
	}

	public int getFarmingXp() {
		return this.farmingXp;
	}

	public int getFarmingLevel() {
		return this.farmingLevel;
	}

	public int getIndex() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isWorking() {
		return this.hasJob;
	}

	private void displaySimGui(PlayerEntity player) {
		this.openSimContainer(player, this.getDisplayName());
	}

	public void openSimContainer(PlayerEntity player, ITextComponent p_213707_2_) {
		// if (player.world.isRemote()) return;
		OptionalInt optionalint = player
				.openContainer(new SimpleNamedContainerProvider((p_213701_1_, p_213701_2_, p_213701_3_) -> {
					return new SimContainer(p_213701_1_, p_213701_2_, this);
				}, p_213707_2_));
		if (optionalint.isPresent()) {
			NetworkHooks.openGui((ServerPlayerEntity)player, (new SimContainerProvider()));
		}
	}

	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		boolean flag = itemstack.getItem() == Items.NAME_TAG;
		if (flag) {
			itemstack.interactWithEntity(player, this, hand);
			return true;
		} else if (this.isAlive()) {
			if (!this.world.isRemote) {
				this.displaySimGui(player);
			}
			return true;
		} else {
			return super.processInteract(player, hand);
		}
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new SimContainer(p_createMenu_1_, this);
	}
}
