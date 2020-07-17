package com.jojo2357.simcityminecraft.entities.sim;

import java.util.List;
import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.buildings.ResidentialBuilding;
import com.jojo2357.simcityminecraft.client.gui.screens.SimScreen;
import com.jojo2357.simcityminecraft.container.SimContainer;
import com.jojo2357.simcityminecraft.tileentity.SimCommercialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimResidentialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.util.handler.managers.Farms;
import com.jojo2357.simcityminecraft.util.handler.managers.FoodsMetaData;
import com.jojo2357.simcityminecraft.util.handler.managers.Houses;
import com.jojo2357.simcityminecraft.util.handler.managers.Shops;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.brain.memory.MemoryModuleType;
import net.minecraft.entity.ai.brain.memory.WalkTarget;
import net.minecraft.entity.ai.brain.task.WalkToTargetTask;
import net.minecraft.entity.ai.brain.task.WalkTowardsPosTask;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.merchant.villager.VillagerEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.stats.Stats;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.Hand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkHooks;

@SuppressWarnings("unused")
public class Sim extends AnimalEntity implements INamedContainerProvider {

	private NonNullList<ItemStack> inventory = NonNullList.withSize(1, ItemStack.EMPTY);
	private boolean hasHome = false;
	private BlockPos homeBlock;
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
	private int miningXp;
	private int miningLevel;
	private int buildingXp;
	private int buildingLvl;
	private PlayerEntity playerJustClicked;
	private boolean menuOpen;
	private SimContainer container;
	private int amountToPickUp;

	private BlockPos lastTryFrom;

	private int cd = 0;

	private SimWealthClass myWealthClass;
	private double myKreds;

	private ResidentialBuilding homeBuilding;
	private boolean onBuisnessTrip = false;
	private int collectMaximum = 0;
	private boolean isAtSite = false;
	private int atSiteTimer;
	private int hungerLevel = 20;
	private int hungerTimer = 1600;


	public Sim(EntityType<? extends AnimalEntity> type, World worldIn) {
		super(type, worldIn);
	}

	@Override
	public ILivingEntityData onInitialSpawn(IWorld worldIn, DifficultyInstance difficultyIn, SpawnReason reason,
			@Nullable ILivingEntityData spawnDataIn, @Nullable CompoundNBT dataTag) {
		if (spawnDataIn == null) {
			spawnDataIn = new AgeableEntity.AgeableData();
		}

		this.myKreds = 0.5;
		this.updateWealthClass();

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
	public VillagerEntity createChild(AgeableEntity ageable) {
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
		try {
			if (this.isWorking) {
				this.goalSelector.removeGoal(this.walkGoal);
				this.goalSelector.removeGoal(this.panicGoal);
				this.jobBlock = caller;
			} else {
				this.goalSelector.addGoal(3, this.panicGoal);
				this.goalSelector.addGoal(2, this.walkGoal);
			}
		} catch (NullPointerException e) {

		}
	}

	public void setJob(Jobs job, BlockPos jobBlock) {
		this.job = job;
		this.hasJob = job != Jobs.UNEMPLOYED;
		this.jobBlock = jobBlock;
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
		compound.putInt("MiningXp", this.miningXp);
		compound.putInt("MiningLevel", this.miningLevel);
		compound.putBoolean("MenuStatus", this.menuOpen);
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
		this.miningXp = compound.getInt("MiningXp");
		this.miningLevel = compound.getInt("MiningLevel");
		this.menuOpen = compound.getBoolean("MenuStatus");
	}

	public int getMyNum() {
		return this.someNum;
	}

	@Override
	public void livingTick() {
		super.livingTick();
		/*
		 * try { if (this.panicGoal.shouldExecute() || this.walkGoal.shouldExecute()) {
		 * System.out.println("PANIC"); }else System.out.println("Don't panic"); } catch
		 * (NullPointerException e) { }
		 */
		// this.markPotionsDirty();
		if (!this.world.isRemote()) {
			this.updateWealthClass();
			if (this.world.getDayTime() % 24000 == 0)
				this.payRent();
			this.hungerTimer--;
			if (this.hungerTimer <= 0) {
				this.hungerTimer = 1600;
				this.hungerLevel--;
				if (this.hungerLevel <= 10)
				for (SimCommercialBuildingBlockTileEntity store : Shops.stores) {
					if (store.canBuyFood()) {
						store.buyFood(this);
					}
				}
			}
			if (Main.simRegistry.getSims().contains(this) || this.dead)
				;
			// System.out.println("I am");
			else {
				// System.out.println("I am not");
				Main.simRegistry.addSim(this);
			}
			if (this.homeBlock != null)
				((SimResidentialBuildingBlockTileEntity) this.world.getTileEntity(this.homeBlock)).simAffirm(this);
			if (!this.hasHome) {
				if (this.rand.nextInt(100) == 99) {
					this.findHouse();
				}
			}
			if (this.hasHome && this.homeBlock == null)
				this.hasHome = false;
			if (this.hasJob && this.jobBlock == null)
				this.hasJob = false;
			if (this.world.isNightTime()) {
				if (this.hasHome) {
					// if (this.getPosition().distanceSq(homeBlock) > 100)
					// this.moveController.setMoveTo(this.homeBlock.getX(), this.homeBlock.getY(),
					// this.homeBlock.getZ(),
					// this.getAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).getValue());

					this.cd--;
					if (this.cd <= 0) {
						if (this.distanceFrom(this.homeBlock) > 5) {
							if (this.lastTryFrom != null && this.lastTryFrom.manhattanDistance(this.getPosition()) < 1)
								this.teleportTo(this.homeBlock);
							else {
								this.getNavigator().tryMoveToXYZ((double) ((float) this.homeBlock.getX()) + 0.5D,
										(double) (this.homeBlock.getY() + 1),
										(double) ((float) this.homeBlock.getZ()) + 0.5D, 0.2D);
								this.lastTryFrom = this.getPosition();
							}

						} else {
							this.registerMovingGoals();
						}
					}
				}
			} else {
				if (this.hasJob) {
					this.cd--;
					if (this.cd <= 0) {
						if (this.distanceFrom(this.jobBlock) > 5) {
							if (this.lastTryFrom != null && !this.onBuisnessTrip &&this.lastTryFrom.manhattanDistance(this.getPosition()) < 1)
								this.teleportTo(this.jobBlock);
							else {
								this.getNavigator().tryMoveToXYZ((double) ((float) this.jobBlock.getX()) + 0.5D,
										(double) (this.jobBlock.getY() + 1),
										(double) ((float) this.jobBlock.getZ()) + 0.5D, 0.2D);
								this.lastTryFrom = this.getPosition();
							}
						}

					}
					if (this.onBuisnessTrip) {
						if (!this.isAtSite) {
							SimFarmBlockTileEntity dest = Farms.farms.get(0);
							if (dest.getChestPos() != null) {
								for (Direction dir : ChestBlock.FACING.getAllowedValues()) {
									if (this.world.getBlockState(dest.getChestPos().offset(dir))
											.getBlock() == Blocks.AIR
											&& this.world
													.getBlockState(
															dest.getChestPos().offset(dir).offset(Direction.DOWN))
													.getBlock() != Blocks.AIR) {
										this.teleportTo(dest.getChestPos().offset(dir));
										this.isAtSite = true;
										this.atSiteTimer = 600;
										this.navigator.clearPath();
									}
								}
							}
						} else
							this.atSiteTimer--;
						if (this.atSiteTimer <= 0) {
							if (this.isAtSite) {
								this.atSiteTimer = 600;
								this.teleportTo(this.jobBlock);
								int inventoryIndex = 0;
								for (ItemStack stack : this.inventory)
									if (stack.isEmpty() || stack.getItem() == Items.WHEAT)
										inventoryIndex = this.inventory.indexOf(stack);
								for (int i = 0; i < (getInventoryAtPosition(this.world,
										Farms.farms.get(0).getChestPos())).getSizeInventory(); i++) {
									if ((getInventoryAtPosition(this.world, Farms.farms.get(0).getChestPos()))
											.getStackInSlot(i).getItem() == Items.WHEAT) {
										int numToGrab = 0;
										if ((getInventoryAtPosition(this.world, Farms.farms.get(0).getChestPos()))
												.getStackInSlot(i).getCount() > amountToPickUp)
											numToGrab = amountToPickUp;
										else
											numToGrab = (getInventoryAtPosition(this.world,
													Farms.farms.get(0).getChestPos())).getStackInSlot(i).getCount();
										this.inventory.set(inventoryIndex, new ItemStack(Items.WHEAT,
												this.inventory.get(inventoryIndex).getCount() + numToGrab));
										(getInventoryAtPosition(this.world, Farms.farms.get(0).getChestPos()))
												.decrStackSize(i, numToGrab);
										this.amountToPickUp -= numToGrab;
									}
								}
								this.inventory = ((SimCommercialBuildingBlockTileEntity) this.world
										.getTileEntity(this.jobBlock)).dropOff(this.inventory);
								this.onBuisnessTrip = false;
							} else {
								this.isAtSite = false;
							}
						}
					}
					this.registerMovingGoals();
				}
			}
		}
		// this.getNavigator().clearPath();
		// this.registerMovingGoals();

		// this.goalSelector.removeGoal(new MoveToPlaceGoal(this, 2, 10,
		// this.homeBlock));

		if (this.hasJob)

		{
		}
	}

	private int distanceFrom(BlockPos blockpos) {
		return (int) Math.sqrt(this.getDistanceSq(blockpos.getX(), blockpos.getY(), blockpos.getZ()));
	}

	private void registerMovingGoals() {
		this.goalSelector.addGoal(2, this.walkGoal);
		this.goalSelector.addGoal(3, this.panicGoal);
	}

	private void unregisterMovingGoals() {
		this.goalSelector.removeGoal(this.walkGoal);
		this.goalSelector.removeGoal(this.panicGoal);
	}

	private void payRent() {
		if (this.hasHome) {
			if (this.myKreds > 0.0) {
				int last = this.myWealthClass.id;
				double rent = ((SimResidentialBuildingBlockTileEntity) this.world.getTileEntity(this.homeBlock))
						.getRent();
				Main.KredsManager.addKreds(Math.min(this.myKreds, rent));
				this.myKreds -= Math.min(this.myKreds, rent);
				if (last != this.myWealthClass.id) {
					Minecraft.getInstance().player
							.sendChatMessage(this.name + " dropped to " + this.myWealthClass + " from " + last);
					if (((SimResidentialBuildingBlockTileEntity) this.world.getTileEntity(this.homeBlock))
							.simChangedClass(last, this.myWealthClass.id)) {
						this.hasHome = false;
						this.homeBlock = null;
						Minecraft.getInstance().player
								.sendChatMessage(" and their home is no longer suitable for their situation");
						Minecraft.getInstance().player.sendChatMessage("(" + this.name + " has moved out)");
					}
				}
			}
		}
	}

	private boolean reduceKreds(double kreds) {
		if (kreds > this.myKreds)
			this.myKreds = 0;
		else
			this.myKreds -= kreds;
		return this.updateWealthClass();
	}

	private boolean updateWealthClass() {
		if (this.myWealthClass == null)
			this.myWealthClass = SimWealthClass.getWealthClass(this.myKreds);
		int before = this.myWealthClass.id;
		this.myWealthClass = SimWealthClass.getWealthClass(this.myKreds);
		return before == this.myWealthClass.id;
	}

	@Override
	public void onDeath(DamageSource damageSource) {
		super.onDeath(damageSource);
		if (!this.world.isRemote())
			Main.simRegistry.remove(this);
		if (this.hasHome) {
			((SimResidentialBuildingBlockTileEntity) this.world.getTileEntity(this.homeBlock)).moveOut();
		}

	}

	public BlockPos getWorkPlace() {
		return this.jobBlock;
	}

	public void addMiningXp(int add) {
		this.miningXp += add;
		if (this.miningXp >= 2000) {
			this.miningXp -= 2000;
			this.miningLevel++;
		}
	}

	public int getMiningXp() {
		return this.miningXp;
	}

	public int getMiningLevel() {
		return this.miningLevel;
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
		/*
		 * OptionalInt optionalint = player .openContainer(new
		 * SimpleNamedContainerProvider((p_213701_1_, p_213701_2_, p_213701_3_) -> {
		 * return new SimContainer(p_213701_1_, p_213701_2_, this); }, p_213707_2_)); if
		 * (optionalint.isPresent()) {
		 * player.openMerchantContainer(optionalint.getAsInt(), this.getOffers(), 1,
		 * this.getXp(), this.func_213705_dZ(), this.func_223340_ej());
		 * System.out.println("Open"); }
		 */
		this.container = new SimContainer(1, player.inventory);
		this.container.setOwner(this);
		Minecraft mc = Minecraft.getInstance();
		mc.displayGuiScreen(new SimScreen((this.container), player.inventory, p_213707_2_));
	}

	public BlockPos whereIAm() {
		return this.getPosition();
	}

	@Override
	public boolean processInteract(PlayerEntity player, Hand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		boolean flag = itemstack.getItem() == Items.NAME_TAG;
		BlockPos pos = this.whereIAm();
		if (flag) {
			itemstack.interactWithEntity(player, this, hand);
			return true;
		} else if (this.isAlive()) {
			if (!this.world.isRemote) {
				this.displaySimGui(player);
				// NetworkHooks.openGui((ServerPlayerEntity) player, containerProvider, pos);
			}
			return true;
		} else {
			return super.processInteract(player, hand);
		}
	}

	@Override
	public Container createMenu(int p_createMenu_1_, PlayerInventory p_createMenu_2_, PlayerEntity p_createMenu_3_) {
		return new SimContainer(p_createMenu_1_, p_createMenu_2_);
	}

	public boolean menuOpen() {
		return this.menuOpen;
	}

	public void closeMenu() {
		this.menuOpen = false;
	}

	public void addBuildingXp(int xp) {
		this.buildingXp += xp;
		if (this.buildingXp >= 2000) {
			this.buildingXp -= 2000;
			this.buildingLvl++;
		}
	}

	public int getBuildingLevel() {
		return this.buildingLvl;
	}

	public int getBuildingXp() {
		return this.buildingXp;
	}

	public SimWealthClass getWealthClass() {
		if (this.myWealthClass == null)
			this.myWealthClass = SimWealthClass.getWealthClass(this.myKreds);
		return this.myWealthClass;
	}

	public boolean hasJob() {
		return this.hasJob;
	}

	public void payTheSim(double kreds) {
		this.myKreds += kreds;
		this.myKreds *= 100;
		this.myKreds = ((int) this.myKreds) / 100D;
		if (this.updateWealthClass())
			this.verifyElideables();
	}

	private void verifyElideables() {
		if (this.hasHome)
			if (!((SimResidentialBuildingBlockTileEntity) this.world.getTileEntity(this.homeBlock))
					.roomInWealth(this.myWealthClass)) {
				this.hasHome = false;
				this.homeBlock = null;
				this.findHouse();
			}
		// if (this.hasJob())
	}

	private void findHouse() {
		for (SimResidentialBuildingBlockTileEntity hoose : Houses.houses) {
			if (!hoose.isFull()) {
				if (!hoose.simMoveIn(this))
					continue;
				this.hasHome = true;
				this.homeBlock = hoose.getPos();
				Minecraft.getInstance().player.sendChatMessage(this.name + " has moved in");
				System.out.println(this.name + " has moved in");
			}
		}
	}

	public boolean collectResource(Item wheat, int maxToGet) {
		if (this.onBuisnessTrip)
			return false;
		this.onBuisnessTrip = true;
		this.amountToPickUp = maxToGet;
		return true;
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World worldIn, BlockPos blockpos) {
		double x = blockpos.getX();
		double y = blockpos.getY();
		double z = blockpos.getZ();
		IInventory iinventory = null;
		BlockState blockstate = worldIn.getBlockState(blockpos);
		Block block = blockstate.getBlock();
		if (block instanceof ISidedInventoryProvider) {
			iinventory = ((ISidedInventoryProvider) block).createInventory(blockstate, worldIn, blockpos);
		} else if (blockstate.hasTileEntity()) {
			TileEntity tileentity = worldIn.getTileEntity(blockpos);
			if (tileentity instanceof IInventory) {
				iinventory = (IInventory) tileentity;
				if (iinventory instanceof ChestTileEntity && block instanceof ChestBlock) {
					iinventory = ChestBlock.func_226916_a_((ChestBlock) block, blockstate, worldIn, blockpos, true);
				}
			}
		}

		if (iinventory == null) {
			List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity) null,
					new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D),
					EntityPredicates.HAS_INVENTORY);
			if (!list.isEmpty()) {
				iinventory = (IInventory) list.get(worldIn.rand.nextInt(list.size()));
			}
		}

		return iinventory;
	}
	
	public int getHungerAmount() {
		return 20 - this.hungerLevel;
	}

	public void feed(int i) {
		this.hungerLevel += i;
	}

	public double getKreds() {
		return this.myKreds;
	}

	public void pay(double d) {
		this.myKreds -= d;
		
	}

}
