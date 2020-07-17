package com.jojo2357.simcityminecraft.tileentity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimMineBlockContainer;
import com.jojo2357.simcityminecraft.entities.sim.Jobs;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.objects.blocks.SimMineBlockBlock;
import com.jojo2357.simcityminecraft.objects.items.HopperItemHandler;
import com.jojo2357.simcityminecraft.objects.items.InventoryCodeHooks;
import com.jojo2357.simcityminecraft.util.handler.Area;
import com.jojo2357.simcityminecraft.util.handler.managers.AreaHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.CropsBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.PickaxeItem;
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
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.loot.LootContext;

public class SimMineBlockTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;
	private boolean doMining = true;
	private Direction chestDirection;
	private BlockPos chestPos = null;
	private int shouldState = 1;
	private Area area;
	private Boolean foundArea = false;
	private Boolean pullSuccess = false;
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
	private boolean mineFinished = false;
	private int indexY;
	private ArrayList<Item> discard = new ArrayList<Item>();

	private ItemStack myPickaxe = new ItemStack(Items.DIAMOND_PICKAXE, 1);

	private boolean isDirty;

	private SimMineBlockContainer container;
	private String mySimName = "";
	private int miningSetting = 0;
	
	private Jobs job = Jobs.MINER;

	public SimMineBlockTileEntity() {
		super(ModTileEntityTypes.SIM_MINE_BLOCK.get());
		this.discard = this.Discards();
	}

	public int getTicks() {
		return this.mySimIndex != -1 ? (int) Math.ceil(this.baseTicks * Math.pow(0.85,
				Main.simRegistry.getSims().get(this.mySimIndex).getMiningLevel() <= 10
						? Main.simRegistry.getSims().get(this.mySimIndex).getMiningLevel()
						: 10))
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
		this.miningSetting = compound.getInt("Mode");
		this.transferCooldown = compound.getInt("TransferCooldown");
		this.currentXp = compound.getInt("CurrentXp");
		this.level = compound.getInt("Level");
		this.mySimIndex = compound.getInt("Simdex");
		this.areaArea = compound.getInt("Area");
		this.sendState = compound.getInt("State");
		this.hasASim = compound.getBoolean("HasSim");
		this.doMining = compound.getBoolean("DoMining");
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
		compound.putInt("Mode", this.miningSetting);
		compound.putInt("TransferCooldown", this.transferCooldown);
		compound.putInt("CurrentXp", this.currentXp);
		compound.putInt("Level", this.level);
		compound.putInt("Simdex", Main.simRegistry.getSims().indexOf(this.workingSim));
		compound.putInt("Area", this.areaArea);
		compound.putInt("State", this.shouldState);
		compound.putBoolean("HasSim", this.hasASim);
		compound.putBoolean("DoMining", this.doMining);
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
		return new TranslationTextComponent("container." + Main.MOD_ID + ".sim_mine_block");
	}

	public void handleUpdateTag(CompoundNBT tag) {
		setInventorySlotContents(0, ItemStack.read(tag));
		updateThis();
	}

	@SuppressWarnings("deprecation")
	public void tick() {
		this.isDirty = false;
		pullSuccess = false;
		World worldIn = this.world.getWorld();
		if (this.world != null && !this.world.isRemote) {
			if (this.workingSim != null)
				this.workingSim.setWorking(this.doMining, this.pos);
			else {
				for (Sim find : Main.simRegistry.getSims())
					if (find.isAlive() && find.getWorkPlace() != null && pos != null)
						if (find.getWorkPlace().getX() == this.pos.getX()
								&& find.getWorkPlace().getY() == this.pos.getY()
								&& find.getWorkPlace().getZ() == this.pos.getZ())
							this.workingSim = find;
				if (this.workingSim != null) {
					this.mySimIndex = Main.simRegistry.getSims().indexOf(this.workingSim);
					this.workingSim.setJob(Jobs.MINER, this.pos);
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
							worldIn.getBlockState(pos).with(SimMineBlockBlock.getColorState(), shouldState));
				}

			} else {
				if (!(worldIn.getTileEntity(chestPos) instanceof ChestTileEntity)) {
					chestPos = null;
					shouldState--;
					worldIn.setBlockState(pos,
							worldIn.getBlockState(pos).with(SimMineBlockBlock.getColorState(), shouldState));
					return;
				}
			}
			if (worldIn.getBlockState(pos) == ModBlocks.SIM_MINE_BLOCK.get().getDefaultState().with(
					SimMineBlockBlock.getFacing(), worldIn.getBlockState(pos).get(SimMineBlockBlock.getFacing()))) {
				worldIn.setBlockState(pos,
						worldIn.getBlockState(pos).with(SimMineBlockBlock.getColorState(), shouldState));
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
								worldIn.getBlockState(pos).with(SimMineBlockBlock.getColorState(), shouldState));
						this.area = checking;
						checking.markTaken();
						this.areaArea = this.area.getArea();
						this.isDirty = true;
						this.indexY = this.area.getPlacedCorner().getY();
					}
				}
			}
			if (shouldState == 3 && isMining() && !this.isOnTransferCooldown() && !this.mineFinished) {
				if (indexY == 0)
					indexY = this.area.getPlacedCorner().getY();
				if (!this.hasASim || this.workingSim == null) {
					this.doMining = false;
					return;
				}
				this.workingSim.forceSetPosition(this.pos);
				this.clear();
				this.setTransferCooldown(this.baseTicks);
				boolean xGoPositive;
				boolean zGoPositive;
				BlockPos lookingPos;
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
				for (int MiningIndeY = indexY; MiningIndeY > 0; MiningIndeY--) {
					for (int MiningIndeX = 0; MiningIndeX < distX; MiningIndeX++) {
						int realX = xGoPositive ? MiningIndeX + 1 : -MiningIndeX - 1;
						realX += area.getPlacedCorner().getX();
						for (int MiningIndeZ = 0; MiningIndeZ < distZ; MiningIndeZ++) {
							int realZ = zGoPositive ? MiningIndeZ + 1 : -MiningIndeZ - 1;
							realZ += area.getPlacedCorner().getZ();
							lookingPos = new BlockPos(realX, MiningIndeY, realZ);
							if (this.world.getBlockState(lookingPos).getBlock() == Blocks.BEDROCK
									|| this.world.getBlockState(lookingPos).getBlock() == Blocks.AIR)
								continue;
							if (!(this.world.getBlockState(lookingPos).getBlock() == Blocks.WATER)
									&& !(this.world.getBlockState(lookingPos).getBlock() == Blocks.LAVA)) {
								this.world.getBlockState(lookingPos).getBlock();
								List<ItemStack> items = Block.getDrops(
										this.world.getBlockState(lookingPos).getBlockState(), (ServerWorld) worldIn,
										lookingPos, this, (Entity) null, this.myPickaxe);
								for (ItemStack item : items) {
									if (this.discard.contains(item.getItem())) continue;
									if (!SimMineBlockTileEntity.canPutStackInInventoryAllSlots(
											this.getInventoryAtPosition(worldIn, this.chestPos), item, null))
										return;
								}
								for (ItemStack item : items)
									if (!this.discard.contains(item.getItem()))
										putStackInInventoryAllSlots(getInventoryAtPosition(worldIn, pos),
												getInventoryAtPosition(worldIn, pos.offset(chestDirection)), item,
												chestDirection);
								this.addXp(500);
								Main.KredsManager.addKreds(-this.job.WAGE);
								this.workingSim.payTheSim(this.job.WAGE);
								this.world.setBlockState(lookingPos, Blocks.AIR.getDefaultState());
								return;
							} else if ((this.world.getBlockState(lookingPos).getFluidState().isSource())) {
								this.world.setBlockState(lookingPos, Blocks.AIR.getDefaultState());
							}
						}
					}
					this.indexY--;
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

	public static boolean canPutStackInInventoryAllSlots(IInventory destination, ItemStack stack,
			@Nullable Direction direction) {
		boolean flag = false;
		if (destination instanceof ISidedInventory && direction != null) {
			ISidedInventory isidedinventory = (ISidedInventory) destination;
			int[] aint = isidedinventory.getSlotsForFace(direction);

			for (int k = 0; k < aint.length && !stack.isEmpty(); ++k) {
				flag |= canInsertItemInSlot(destination, stack, aint[k], direction);
			}
		} else {
			int i = destination.getSizeInventory();

			for (int j = 0; j < i && !stack.isEmpty(); ++j) {
				flag |= canInsertItemInSlot(destination, stack, j, direction);
			}
		}

		return flag;
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
	public IInventory getInventoryAtPosition(World p_195484_0_, BlockPos p_195484_1_) {
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
		this.transferCooldown = (int) Math.ceil(
				ticks * Math.pow(0.85, this.workingSim.getMiningLevel() <= 10 ? this.workingSim.getMiningLevel() : 10));
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
		this.container = new SimMineBlockContainer(id, player, this);
		return this.container;
	}

	@Override
	protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
		return new HopperItemHandler(this);
	}

	public long getLastUpdateTime() {
		return this.tickedGameTime;
	}

	public boolean isMining() {
		return this.doMining;
	}

	public void buttonClicked(int myIndex) {
		if (myIndex <= 2) {
			if (myIndex == 1) {
				this.doMining = true;
				this.workingSim.forceSetPosition(this.pos);
			}
			if (myIndex == 2)
				this.doMining = false;
		} else {
			if (!this.hasASim) {
				this.workingSim = Main.simRegistry.getSims().get(myIndex - 3);
				this.mySimIndex = myIndex - 3;
				this.hasASim = true;
				this.workingSim.setJob(Jobs.MINER, this.pos);
				this.mySimName = this.workingSim.getMyName();
			} else {
				this.workingSim.setJob(Jobs.UNEMPLOYED, null);
				this.workingSim = null;
				this.mySimIndex = -1;
				this.hasASim = false;
				this.mySimName = "";
			}
		}
	}

	public void settingChanged(int index) {
		this.miningSetting = index;
		this.discard = Discards();
	}

	public void setMining(boolean Mining) {
		this.doMining = Mining;
	}

	public int getXP() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getMiningXp() : 0;
	}

	public int getLevel() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getMiningLevel() : 0;
	}

	private void addXp(int xp) {
		this.totalXp += xp;
		this.currentXp += xp;
		this.workingSim.addMiningXp(xp);
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
		return this.workingSim.getMiningLevel();
	}

	public int mySimIndex() {
		return this.mySimIndex;
	}

	public void justDied() {
		if (this.workingSim != null) {
			this.workingSim.unsetJob();
			this.hasASim = false;
			this.workingSim = null;
			this.mySimIndex = -1;
		}
	}

	public ArrayList<Item> Discards() {
		ArrayList<Item> discards = new ArrayList<Item>();
		switch (this.miningSetting) {
		case 0:
			return discards;
		case 1:
			discards.add(Items.COBBLESTONE);
			discards.add(Items.GRANITE);
			discards.add(Items.DIORITE);
			discards.add(Items.ANDESITE);
			return discards;
		case 2:
			discards.add(Items.DIRT);
			discards.add(Items.COARSE_DIRT);
			return discards;
		case 3:
			discards.add(Items.COBBLESTONE);
			discards.add(Items.GRANITE);
			discards.add(Items.DIORITE);
			discards.add(Items.ANDESITE);
			discards.add(Items.DIRT);
			discards.add(Items.COARSE_DIRT);
			return discards;
		}
		return discards;
	}

	public int mode() {
		return this.miningSetting;
	}
}