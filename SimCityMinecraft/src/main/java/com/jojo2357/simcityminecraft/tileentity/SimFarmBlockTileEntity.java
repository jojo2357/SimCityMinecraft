package com.jojo2357.simcityminecraft.tileentity;

import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.objects.blocks.SimFarmBlockBlock;
import com.jojo2357.simcityminecraft.objects.items.HopperItemHandler;
import com.jojo2357.simcityminecraft.objects.items.InventoryCodeHooks;
import com.jojo2357.simcityminecraft.util.handler.Area;
import com.jojo2357.simcityminecraft.util.handler.AreaHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.IHopper;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class SimFarmBlockTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;
	private boolean doFarming = true;
	private Direction chestDirection;
	private BlockPos chestPos = null;
	private int shouldState = 1;
	private Area area;
	private Boolean foundArea = false;
	private Boolean dirtPullSuccess = false;
	private final int baseTicks = 40;
	private final int xpPerLevel = 2000;
	private int level = 0;
	private int currentXp = 0;
	private int totalXp = 0;
	private int areaArea;
	private Sim workingSim;
	private boolean hasASim = false;
	private int sendState;
	private int mySimIndex = -1;

	private boolean isDirty;

	private SimFarmBlockContainer container;
	private String mySimName = "";

	public SimFarmBlockTileEntity() {
		super(ModTileEntityTypes.SIM_FARM_BLOCK.get());
	}

	public int getTicks() {
		return this.mySimIndex != -1 ? (int) Math.ceil(
				this.baseTicks * Math.pow(0.85, Main.simRegistry.getSims().get(this.mySimIndex).getFarmingLevel()))
				: 40;
	}

	public boolean foundArea() {
		return this.foundArea;
	}

	public int getWidth() {
		if (area != null)
			return area.getWidth();
		return 0;
	}

	public int getLength() {
		if (area != null)
			return area.getLength();
		return 0;
	}

	public int getArea() {
		return this.areaArea;
	}

	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.inventory = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.inventory);
		}
		this.transferCooldown = compound.getInt("TransferCooldown");
		this.currentXp = compound.getInt("CurrentXp");
		this.level = compound.getInt("Level");
		this.mySimIndex = compound.getInt("Simdex");
		this.areaArea = compound.getInt("Area");
		this.sendState = compound.getInt("State");
		this.hasASim = compound.getBoolean("HasSim");
		if (this.hasASim)
			this.mySimName = compound.getString("SimName");
		this.foundArea = compound.getBoolean("HasArea");
		if (this.foundArea && this.area == null) {
			this.area = new Area(compound.getInt("PlacedX"), compound.getInt("PlacedZ"), compound.getInt("GuessX"),
					compound.getInt("GuessZ"), compound.getInt("Yplane"));
			this.shouldState++;
		}
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.inventory);
		}
		compound.putInt("TransferCooldown", this.transferCooldown);
		compound.putInt("CurrentXp", this.currentXp);
		compound.putInt("Level", this.level);
		compound.putInt("Simdex", Main.simRegistry.getSims().indexOf(this.workingSim));
		compound.putInt("Area", this.areaArea);
		compound.putInt("State", this.shouldState);
		compound.putBoolean("HasSim", this.hasASim);
		if (this.hasASim)
			compound.putString("SimName", this.mySimName());
		compound.putBoolean("HasArea", this.foundArea);
		if (this.foundArea) {
			compound.putInt("PlacedX", this.area.getPlacedCorner().getX());
			compound.putInt("PlacedZ", this.area.getPlacedCorner().getZ());
			compound.putInt("GuessX", this.area.getGuessedCorner().getX());
			compound.putInt("GuessZ", this.area.getGuessedCorner().getZ());
			compound.putInt("Yplane", this.area.getGuessedCorner().getY());
		}
		return compound;
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		this.write(nbt);
		return new SUpdateTileEntityPacket(this.getPos(), 0, nbt);
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket packet) {
		this.read(packet.getNbtCompound());
	}

	public int getSizeInventory() {
		return this.inventory.size();
	}

	public ItemStack decrStackSize(int index, int count) {
		this.fillWithLoot((PlayerEntity) null);
		return ItemStackHelper.getAndSplit(this.getItems(), index, count);
	}

	public void setInventorySlotContents(int index, ItemStack stack) {
		this.fillWithLoot((PlayerEntity) null);
		this.getItems().set(index, stack);
		if (stack.getCount() > this.getInventoryStackLimit()) {
			stack.setCount(this.getInventoryStackLimit());
		}

	}

	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container." + Main.MOD_ID + ".sim_farm_block");
	}

	public void handleUpdateTag(CompoundNBT tag) {
		setInventorySlotContents(0, ItemStack.read(tag));
		updateThis();
	}

	@SuppressWarnings("deprecation")
	public void tick() {
		this.isDirty = false;
		dirtPullSuccess = false;
		World worldIn = this.world.getWorld();
		if (this.world != null && !this.world.isRemote) {
			if (this.workingSim != null)
				this.workingSim.setWorking(this.doFarming, this.pos);
			else {
				for (Sim find : Main.simRegistry.getSims())
					if (find.isAlive() && find.getWorkPlace() != null && pos != null)
						if (find.getWorkPlace().getX() == this.pos.getX()
								&& find.getWorkPlace().getY() == this.pos.getY()
								&& find.getWorkPlace().getZ() == this.pos.getZ())
							this.workingSim = find;
				if (this.workingSim != null) {
					this.mySimIndex = Main.simRegistry.getSims().indexOf(this.workingSim);
					this.workingSim.setJob();
					this.hasASim = true;
				}
			}
			if (this.mySimIndex == -1) {
				this.workingSim = null;
				this.hasASim = false;
			}
			this.getUpdatePacket();
			if (this.container != null) {
				CompoundNBT comp = (new CompoundNBT());
				comp.putInt("Esperience", this.currentXp);
				comp.putInt("Level", this.level);
				handleUpdateTag(comp);
			}
			--this.transferCooldown;
			this.tickedGameTime = this.world.getGameTime();
			if (chestPos == null) {
				for (Direction facing : ChestBlock.FACING.getAllowedValues()) {
					if ((worldIn.getTileEntity(pos.offset(facing))) instanceof ChestTileEntity) {
						chestPos = pos.offset(facing);
						chestDirection = facing;
					}
				}
				if (chestPos != null) {
					shouldState++;
					worldIn.setBlockState(pos,
							worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
				}

			} else {
				if (!(worldIn.getTileEntity(chestPos) instanceof ChestTileEntity)) {
					chestPos = null;
					shouldState--;
					worldIn.setBlockState(pos,
							worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
					return;
				}
			}
			if (worldIn.getBlockState(pos) == ModBlocks.SIM_FARM_BLOCK.get().getDefaultState().with(
					SimFarmBlockBlock.getFacing(), worldIn.getBlockState(pos).get(SimFarmBlockBlock.getFacing()))) {
				worldIn.setBlockState(pos,
						worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
			}
			// System.out.println(pos + " " + chestPos);
			if (!foundArea) {
				for (Area checking : AreaHandler.definedAreas) {
					if (checking.taken())
						continue;
					if (isNextTo(pos, checking.getPlacedCorner())) {
						shouldState++;
						foundArea = true;
						System.out.println("ooga booga");
						if (shouldState > 3)
							shouldState = 3;
						worldIn.setBlockState(pos,
								worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
						this.area = checking;
						checking.markTaken();
						this.areaArea = this.area.getArea();
						this.isDirty = true;
					}
				}
			}
			if (shouldState == 3 && isFarming() && !this.isOnTransferCooldown()) {
				if (!this.hasASim || this.workingSim == null) {
					this.doFarming = false;
					return;
				}
				this.workingSim.forceSetPosition(this.pos);
				this.clear();
				this.setTransferCooldown(this.baseTicks);
				boolean xGoPositive;
				boolean zGoPositive;
				BlockPos lookingPos;
				BlockPos plantingPos;
				if (area.getPlacedCorner().getX() > area.getGuessedCorner().getX())
					xGoPositive = false;
				else
					xGoPositive = true;
				if (area.getPlacedCorner().getZ() > area.getGuessedCorner().getZ())
					zGoPositive = false;
				else
					zGoPositive = true;
				int distX = area.getLength();
				int distZ = area.getWidth();
				for (int farmingIndeX = 0; farmingIndeX < distX; farmingIndeX++) {
					int realX = xGoPositive ? farmingIndeX + 1 : -farmingIndeX - 1;
					realX += area.getPlacedCorner().getX();
					for (int farmingIndeZ = 0; farmingIndeZ < distZ; farmingIndeZ++) {
						int realZ = zGoPositive ? farmingIndeZ + 1 : -farmingIndeZ - 1;
						realZ += area.getPlacedCorner().getZ();
						lookingPos = new BlockPos(realX, area.getPlacedCorner().getY() - 1, realZ);
						plantingPos = new BlockPos(realX, area.getPlacedCorner().getY(), realZ);
						if (!(worldIn.getBlockState(lookingPos).getBlock() == Blocks.DIRT // if there is a block,
								|| worldIn.getBlockState(lookingPos).getBlock() == Blocks.GRASS_BLOCK
								|| worldIn.getBlockState(lookingPos).getBlock() == Blocks.FARMLAND)
								&& !worldIn.hasWater(lookingPos)) {
							// if (this.updateHopper(() -> {
							// return
							dirtPullSuccess = pullItems(this, this.chestDirection, Blocks.DIRT.asItem());
							// })) dirtPullSuccess = true;
							if (!dirtPullSuccess)
								continue;
							if (worldIn.getBlockState(lookingPos).getBlock() != Blocks.AIR) {
								Block block = worldIn.getBlockState(lookingPos).getBlock();
								ItemStack item = block.getItem(worldIn, lookingPos, block.getDefaultState());
								putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
										getInventoryAtPosition(worldIn, pos.offset(chestDirection)), item,
										chestDirection);
								// this.transferItemsOut();
								this.clear();
								Main.KredsManager.addKreds(-0.01D);

							}
							worldIn.setBlockState(lookingPos, Blocks.DIRT.getDefaultState());

							if (dirtPullSuccess) {
								this.markDirty();
								break;
							} else
								continue;
						} else if (worldIn.getBlockState(lookingPos).getBlock() != Blocks.FARMLAND
								&& !worldIn.hasWater(lookingPos)) { // if there is dirt but no farmland,
							worldIn.setBlockState(lookingPos, Blocks.FARMLAND.getDefaultState());
							Main.KredsManager.addKreds(-0.01D);
							this.markDirty();
							return;
						} else { // there is farmland, lets plant some shit!
							if (worldIn.getBlockState(plantingPos).getBlock() != Blocks.WHEAT) {
								if (worldIn.getBlockState(plantingPos).getBlock() != Blocks.AIR) {
									Block block = worldIn.getBlockState(plantingPos).getBlock();
									ItemStack item = block.getItem(worldIn, plantingPos, block.getDefaultState());
									putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
											getInventoryAtPosition(worldIn, pos.offset(chestDirection)), item,
											chestDirection);
									// this.transferItemsOut();
									this.clear();
									worldIn.setBlockState(plantingPos, Blocks.AIR.getDefaultState());
									Main.KredsManager.addKreds(-0.01D);
									this.markDirty();
									return;
								} else {
									if (worldIn.getBlockState(lookingPos).getBlock() != Blocks.FARMLAND)
										continue;
									/*
									 * if (this.updateHopper(() -> { return pullItems(this, this.chestDirection,
									 * Items.WHEAT_SEEDS.asItem()); }))
									 */
									dirtPullSuccess = pullItems(this, this.chestDirection, Items.WHEAT_SEEDS.asItem());
									;
									if (!dirtPullSuccess)
										continue;
									if (worldIn.getBlockState(plantingPos).getBlock() != Blocks.AIR) {
										Block block = worldIn.getBlockState(plantingPos).getBlock();
										ItemStack item = block.getItem(worldIn, plantingPos, block.getDefaultState());
										putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
												getInventoryAtPosition(worldIn, pos.offset(chestDirection)), item,
												chestDirection);
										// this.transferItemsOut();
									}
									this.clear();
									this.addXp(3);
									this.markDirty();
									Main.KredsManager.addKreds(-0.01D);
									worldIn.setBlockState(plantingPos, Blocks.WHEAT.getDefaultState());
								}
								if (dirtPullSuccess)
									return;
							} else {
								if (worldIn.getBlockState(plantingPos).getBlockState() == Blocks.WHEAT.getDefaultState()
										.with(((CropsBlock) Blocks.WHEAT).getAgeProperty(),
												((CropsBlock) Blocks.WHEAT).getMaxAge())) {
									// Block block = worldIn.getBlockState(lookingPos).getBlock();
									// ItemStack item = block.getItem(worldIn, lookingPos, block.getDefaultState());
									putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
											getInventoryAtPosition(worldIn, pos.offset(chestDirection)),
											new ItemStack(Items.WHEAT, 1), chestDirection);
									putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
											getInventoryAtPosition(worldIn, pos.offset(chestDirection)),
											new ItemStack(Items.WHEAT_SEEDS, 2), chestDirection);
									// this.transferItemsOut();
									this.clear();
									worldIn.setBlockState(plantingPos, Blocks.AIR.getDefaultState());
									Main.KredsManager.addKreds(-0.01D);
									this.addXp(5);
									this.markDirty();
									return;
								}
							}
						}
					}
				}
			}
			// this.markDirty();
		}
		// this.sendState = this.shouldState;
		this.markDirty();
	}

	private boolean isNextTo(BlockPos pos, BlockPos other) {
		for (Direction facing : ChestBlock.FACING.getAllowedValues()) {
			if (pos.offset(facing).equals(other))
				return true;
		}
		return false;
	}

	private static IntStream func_213972_a(IInventory p_213972_0_, Direction p_213972_1_) {
		return p_213972_0_ instanceof ISidedInventory
				? IntStream.of(((ISidedInventory) p_213972_0_).getSlotsForFace(p_213972_1_))
				: IntStream.range(0, p_213972_0_.getSizeInventory());
	}

	private static boolean isInventoryEmpty(IInventory inventoryIn, Direction side) {
		return func_213972_a(inventoryIn, side).allMatch((p_213973_1_) -> {
			return inventoryIn.getStackInSlot(p_213973_1_).isEmpty();
		});
	}

	public boolean pullItems(IHopper hopper, Direction directionIn, Item itemRequested) {
		Boolean ret = InventoryCodeHooks.extractHook(hopper, directionIn, itemRequested);
		if (ret != null)
			return ret;
		IInventory iinventory = getInventoryAtPosition(this.world, this.pos.offset(directionIn));
		if (iinventory != null) {
			Direction direction = directionIn.getOpposite();
			return isInventoryEmpty(iinventory, direction) ? false
					: func_213972_a(iinventory, direction).anyMatch((p_213971_3_) -> {
						return pullItemFromSlot(hopper, iinventory, p_213971_3_, direction);
					});
		} else {
			return false;
		}
	}

	private static boolean pullItemFromSlot(IHopper hopper, IInventory inventoryIn, int index, Direction direction) {
		ItemStack itemstack = inventoryIn.getStackInSlot(index);
		if (!itemstack.isEmpty() && canExtractItemFromSlot(inventoryIn, itemstack, index, direction)) {
			ItemStack itemstack1 = itemstack.copy();
			ItemStack itemstack2 = putStackInInventoryAllSlots(inventoryIn, hopper, inventoryIn.decrStackSize(index, 1),
					(Direction) null);
			if (itemstack2.isEmpty()) {
				inventoryIn.markDirty();
				return true;
			}
			inventoryIn.setInventorySlotContents(index, itemstack1);
		}
		return false;
	}

	public static ItemStack putStackInInventoryAllSlots(@Nullable IInventory source, IInventory destination,
			ItemStack stack, @Nullable Direction direction) {
		if (destination instanceof ISidedInventory && direction != null) {
			ISidedInventory isidedinventory = (ISidedInventory) destination;
			int[] aint = isidedinventory.getSlotsForFace(direction);

			for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
				stack = insertStack(source, destination, stack, aint[k], direction);
			}
		} else {
			int i = destination.getSizeInventory();

			for (int j = 0; j < i && !stack.isEmpty(); ++j) {
				stack = insertStack(source, destination, stack, j, direction);
			}
		}

		return stack;
	}

	private static boolean canInsertItemInSlot(IInventory inventoryIn, ItemStack stack, int index,
			@Nullable Direction side) {
		if (!inventoryIn.isItemValidForSlot(index, stack)) {
			return false;
		} else {
			return !(inventoryIn instanceof ISidedInventory)
					|| ((ISidedInventory) inventoryIn).canInsertItem(index, stack, side);
		}
	}

	private static boolean canExtractItemFromSlot(IInventory inventoryIn, ItemStack stack, int index, Direction side) {
		return !(inventoryIn instanceof ISidedInventory)
				|| ((ISidedInventory) inventoryIn).canExtractItem(index, stack, side);
	}

	private static ItemStack insertStack(@Nullable IInventory source, IInventory destination, ItemStack stack,
			int index, @Nullable Direction direction) {
		ItemStack itemstack = destination.getStackInSlot(index);
		if (canInsertItemInSlot(destination, stack, index, direction)) {
			boolean flag = false;
			if (itemstack.isEmpty()) {
				destination.setInventorySlotContents(index, stack);
				stack = ItemStack.EMPTY;
				flag = true;
			} else if (canCombine(itemstack, stack)) {
				int i = stack.getMaxStackSize() - itemstack.getCount();
				int j = Math.min(stack.getCount(), i);
				stack.shrink(j);
				itemstack.grow(j);
				flag = j > 0;
			}

			if (flag) {
				destination.markDirty();
			}
		}

		return stack;
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
		return getInventoryAtPosition(p_195484_0_, (double) p_195484_1_.getX() + 0.5D,
				(double) p_195484_1_.getY() + 0.5D, (double) p_195484_1_.getZ() + 0.5D);
	}

	@Nullable
	public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
		IInventory iinventory = null;
		BlockPos blockpos = new BlockPos(x, y, z);
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

	private static boolean canCombine(ItemStack stack1, ItemStack stack2) {
		if (stack1.getItem() != stack2.getItem()) {
			return false;
		} else if (stack1.getDamage() != stack2.getDamage()) {
			return false;
		} else if (stack1.getCount() > stack1.getMaxStackSize()) {
			return false;
		} else {
			return ItemStack.areItemStackTagsEqual(stack1, stack2);
		}
	}

	public double getXPos() {
		return (double) this.pos.getX() + 0.5D;
	}

	public double getYPos() {
		return (double) this.pos.getY() + 0.5D;
	}

	public double getZPos() {
		return (double) this.pos.getZ() + 0.5D;
	}

	public void setTransferCooldown(int ticks) {
		this.transferCooldown = (int) Math.ceil(ticks * Math.pow(0.85, this.workingSim.getFarmingLevel()));
	}

	private boolean isOnTransferCooldown() {
		return this.transferCooldown > 0;
	}

	protected NonNullList<ItemStack> getItems() {
		return this.inventory;
	}

	protected void setItems(NonNullList<ItemStack> itemsIn) {
		this.inventory = itemsIn;
	}

	protected Container createMenu(int id, PlayerInventory player) {
		this.container = new SimFarmBlockContainer(id, player, this);
		return this.container;
	}

	@Override
	protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
		return new HopperItemHandler(this);
	}

	public long getLastUpdateTime() {
		return this.tickedGameTime;
	}

	public boolean isFarming() {
		return this.doFarming;
	}

	public void buttonClicked(int myIndex) {
		if (myIndex <= 2) {
			if (myIndex == 1) {
				this.doFarming = true;
				this.workingSim.forceSetPosition(this.pos);
			}
			if (myIndex == 2)
				this.doFarming = false;
		} else {
			if (!this.hasASim) {
				this.workingSim = Main.simRegistry.getSims().get(myIndex - 3);
				this.mySimIndex = myIndex - 3;
				this.hasASim = true;
				this.workingSim.setJob();
				this.mySimName = this.workingSim.getMyName();
			}else {
				this.workingSim.unsetJob();
				this.workingSim = null;
				this.mySimIndex = -1;
				this.hasASim = false;
				this.mySimName = "";
			}
		}
	}

	public void setFarming(boolean farming) {
		this.doFarming = farming;
	}

	public int getXP() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getFarmingXp() : 0;
	}

	public int getLevel() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getFarmingLevel() : 0;
	}

	private void addXp(int xp) {
		this.totalXp += xp;
		this.currentXp += xp;
		this.workingSim.addFarmingXp(xp);
		if (this.currentXp > xpPerLevel) {
			this.level++;
			this.currentXp = 0;
		}
	}

	public int getCurrentXp() {
		return this.currentXp;
	}

	public boolean doIHaveSim() {
		return this.hasASim && this.mySimIndex != -1;
	}

	@Override
	public void markDirty() {
		super.markDirty();
		this.world.notifyBlockUpdate(this.getPos(), this.getBlockState(), this.getBlockState(), 3);
	}

	public void updateThis() {
		this.markDirty();
		if (this.getWorld() != null) {
			this.getWorld().notifyBlockUpdate(pos, this.getBlockState(), this.getBlockState(), 3);
		}
	}

	@Override
	public CompoundNBT getUpdateTag() {
		return this.write(new CompoundNBT());
	}

	public int myState() {
		return this.sendState;
	}

	public void releaseArea() {
		AreaHandler.definedAreas.add(this.area.markNotTaken());
	}

	public String mySimName() {
		if (this.workingSim != null)
			return this.mySimName;
		return "its null";
	}

	public String getSyncName() {
		return this.mySimName;
	}

	public int mySimLevel() {
		return this.workingSim.getFarmingLevel();
	}

	public int mySimIndex() {
		return this.mySimIndex;
	}

}