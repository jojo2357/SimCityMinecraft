package com.jojo2357.simcityminecraft.tileentity;

import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;
import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimWorkBenchContainer;
import com.jojo2357.simcityminecraft.entities.sim.Jobs;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.objects.blocks.SimFarmBlockBlock;
import com.jojo2357.simcityminecraft.objects.items.HopperItemHandler;
import com.jojo2357.simcityminecraft.objects.items.InventoryCodeHooks;
import com.jojo2357.simcityminecraft.util.handler.BuildingTemplate;
import com.mojang.datafixers.util.Pair;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.ILiquidContainer;
import net.minecraft.block.PaneBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.IClearable;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
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
import net.minecraft.util.Mirror;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.shapes.BitSetVoxelShapePart;
import net.minecraft.util.math.shapes.VoxelShapePart;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;

public class SimWorkBenchTileEntity extends LockableLootTileEntity implements IHopper, ITickableTileEntity {
	private NonNullList<ItemStack> inventory = NonNullList.withSize(5, ItemStack.EMPTY);
	private int transferCooldown = -1;
	private long tickedGameTime;
	private boolean doBuilding = true;
	private Direction chestDirection;
	private BlockPos chestPos = null;
	private int shouldState = 1;
	private Boolean dirtPullSuccess = false;
	private final int baseTicks = 40;
	private final int xpPerLevel = 2000;
	private int level = 0;
	private int currentXp = 0;
	private int totalXp = 0;
	private Sim workingSim;
	private boolean hasASim = false;
	private int sendState;
	private int mySimIndex = -1;

	private BuildingTemplate building;

	private boolean isDirty;

	private SimWorkBenchContainer container;
	private String mySimName = "";
	private BlockPos refreshPosition;
	private int screenMode;
	private int buildingIndex = 0;
	private Jobs job = Jobs.BUILDER;

	public SimWorkBenchTileEntity() {
		super(ModTileEntityTypes.SIM_WORK_BENCH.get());
		if (!Main.buildingLoader.buildingsLoaded && this.world != null && this.world.getWorld() != null) {
			try {
				Main.buildingLoader.LoadEm(this.world.getWorld());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		// building = Main.buildingLoader.residentialBuildings.get(this.buildingIndex);
	}

	public int getTicks() {
		return this.mySimIndex != -1 ? (int) Math.ceil(
				this.baseTicks * Math.pow(0.85, Main.simRegistry.getSims().get(this.mySimIndex).getBuildingLevel()))
				: 40;
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
		this.sendState = compound.getInt("State");
		this.hasASim = compound.getBoolean("HasSim");
		this.buildingIndex = compound.getInt("BuildingNumber");
		if (this.hasASim)
			this.mySimName = compound.getString("SimName");
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
		compound.putInt("State", this.shouldState);
		compound.putBoolean("HasSim", this.hasASim);
		compound.putInt("BuildingNumber", this.buildingIndex);
		if (this.hasASim)
			compound.putString("SimName", this.mySimName());
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
		return new TranslationTextComponent("container." + Main.MOD_ID + ".sim_work_bench");
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
			if (!Main.buildingLoader.buildingsLoaded) {
				try {
					Main.buildingLoader.LoadEm(worldIn);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (this.chestDirection == null) {
				for (Direction facing : ChestBlock.FACING.getAllowedValues()) {
					if ((worldIn.getTileEntity(pos.offset(facing))) instanceof ChestTileEntity) {
						this.chestPos = pos.offset(facing);
						this.chestDirection = facing;
					}
				}
			}
			if (this.refreshPosition != null) {
				if (worldIn.getBlockState(this.refreshPosition).getBlock() instanceof PaneBlock)
					for (Direction direct : ChestBlock.FACING.getAllowedValues())
						worldIn.setBlockState(this.refreshPosition,
								((PaneBlock) worldIn.getBlockState(this.refreshPosition).getBlock())
										.updatePostPlacement(worldIn.getBlockState(this.refreshPosition), direct,
												worldIn.getBlockState(this.refreshPosition.offset(direct)), worldIn,
												this.refreshPosition, this.refreshPosition.offset(direct)));
				this.refreshPosition = null;
			}
			if (this.workingSim != null)
				this.workingSim.setWorking(this.doBuilding, this.pos);
			else {
				for (Sim find : Main.simRegistry.getSims())
					if (find.isAlive() && find.getWorkPlace() != null && pos != null)
						if (find.getWorkPlace().getX() == this.pos.getX()
								&& find.getWorkPlace().getY() == this.pos.getY()
								&& find.getWorkPlace().getZ() == this.pos.getZ())
							this.workingSim = find;
				if (this.workingSim != null) {
					this.mySimIndex = Main.simRegistry.getSims().indexOf(this.workingSim);
					this.workingSim.setJob(this.job, this.pos);
					this.hasASim = true;
				}
			}
			if (this.mySimIndex == -1) {
				this.workingSim = null;
				this.hasASim = false;
			}
			--this.transferCooldown;
			this.tickedGameTime = this.world.getGameTime();
			this.inventory.clear();
			/*if (!this.isOnTransferCooldown() && this.chestPos != null) {
				if (this.building != null) {
					// this.building = Main.buildingLoader.residentialBuildings.get(buildingIndex);
					
					this.setTransferCooldown(40);
				}
			}*/
			if (!this.isOnTransferCooldown() && this.chestPos != null && this.building != null) {
				//this.building = Main.buildingLoader.residentialBuildings.get(buildingIndex);
				this.makeStructure(worldIn);
				this.setTransferCooldown(40);
			}
		}
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
		this.transferCooldown = 4;
		// (int) Math.ceil(ticks * Math.pow(0.85, this.workingSim.getBuildingLevel()));
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
		this.container = new SimWorkBenchContainer(id, player, this);
		return this.container;
	}

	@Override
	protected net.minecraftforge.items.IItemHandler createUnSidedHandler() {
		return new HopperItemHandler(this);
	}

	public long getLastUpdateTime() {
		return this.tickedGameTime;
	}

	public boolean isBuilding() {
		return this.doBuilding;
	}

	public void buttonClicked(int myIndex, int mode) {
		switch (mode) {
		case 0:
			this.mySimIndex = myIndex;
			this.workingSim = Main.simRegistry.getSims().get(myIndex);
			this.mySimName = this.workingSim.getMyName();
			this.doBuilding = false;
			this.hasASim = true;
			this.workingSim.setJob(this.job, this.pos);
			break;
		/*
		 * case 1: this.building =
		 * Main.buildingLoader.residentialBuildings.get(myIndex); this.buildingIndex =
		 * myIndex; this.doBuilding = true; break; case 2: this.building =
		 * Main.buildingLoader.commercialBuildings.get(myIndex); this.buildingIndex =
		 * myIndex; this.doBuilding = true; break; case 3: this.building =
		 * Main.buildingLoader.industrialBuildings.get(myIndex); this.buildingIndex =
		 * myIndex; this.doBuilding = true; break;
		 */
		case 4:
			this.workingSim.setJob(Jobs.UNEMPLOYED, null);
			this.mySimIndex = -1;
			this.workingSim = null;
			this.mySimName = "";
			this.doBuilding = false;
			this.hasASim = false;
			break;
		case 5:
			this.screenMode = myIndex - 1;
			break;
		case 6:
			if (myIndex == 0) {
				this.building = Main.buildingLoader.allTemplates.get(this.screenMode).get(myIndex);
				this.buildingIndex = myIndex;
				this.doBuilding = true;
			}
			break;
		}
		this.markDirty();
	}

	public void setBuilding(boolean Building) {
		this.doBuilding = Building;
	}

	public int getXP() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getBuildingXp() : 0;
	}

	public int getLevel() {
		return this.mySimIndex != -1 ? Main.simRegistry.getSims().get(this.mySimIndex).getBuildingLevel() : 0;
	}

	private void addXp(int xp) {
		this.totalXp += xp;
		this.currentXp += xp;
		this.workingSim.addBuildingXp(xp);
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

	public String mySimName() {
		if (this.workingSim != null)
			return this.mySimName;
		return "its null";
	}

	public String getSyncName() {
		return this.mySimName;
	}

	public int mySimLevel() {
		return this.workingSim.getBuildingLevel();
	}

	public int mySimIndex() {
		return this.mySimIndex;
	}

	public boolean makeStructure(World worldIn) {
		Rotation dir;
		switch (worldIn.getBlockState(this.pos).get(SimFarmBlockBlock.getFacing()).getOpposite()) {
		case NORTH:
			dir = Rotation.NONE;
			break;
		case EAST:
			dir = Rotation.CLOCKWISE_90;
			break;
		case SOUTH:
			dir = Rotation.CLOCKWISE_180;
			break;
		case WEST:
			dir = Rotation.COUNTERCLOCKWISE_90;
			break;
		default:
			dir = Rotation.NONE;
		}
		PlacementSettings placementIn = new PlacementSettings();
		placementIn.setIgnoreEntities(false).setMirror(Mirror.LEFT_RIGHT).setRotation(dir);
		Template temp = this.building.getTemplate();
		BlockPos size = temp.getSize();
		BlockPos pos = this.pos.offset(worldIn.getBlockState(this.pos).get(SimFarmBlockBlock.getFacing()).getOpposite());
        List<Template.BlockInfo> list = placementIn.func_227459_a_(temp.blocks, pos);
        if ((!list.isEmpty()) && size.getX() >= 1 && size.getY() >= 1 && size.getZ() >= 1) {
           MutableBoundingBox mutableboundingbox = placementIn.getBoundingBox();
           List<BlockPos> list1 = Lists.newArrayListWithCapacity(placementIn.func_204763_l() ? list.size() : 0);
           List<Pair<BlockPos, CompoundNBT>> list2 = Lists.newArrayListWithCapacity(list.size());
           int i = Integer.MAX_VALUE;
           int j = Integer.MAX_VALUE;
           int k = Integer.MAX_VALUE;
           int l = Integer.MIN_VALUE;
           int i1 = Integer.MIN_VALUE;
           int j1 = Integer.MIN_VALUE;

           for(Template.BlockInfo template$blockinfo : Template.processBlockInfos(temp, worldIn, pos, placementIn, list)) {
              BlockPos blockpos = template$blockinfo.pos;
              if (mutableboundingbox == null || mutableboundingbox.isVecInside(blockpos)) {
                 IFluidState ifluidstate = placementIn.func_204763_l() ? worldIn.getFluidState(blockpos) : null;
                 BlockState blockstate = template$blockinfo.state.mirror(placementIn.getMirror()).rotate(placementIn.getRotation());
                 if (template$blockinfo.nbt != null) {
                    TileEntity tileentity = worldIn.getTileEntity(blockpos);
                    IClearable.clearObj(tileentity);
                    worldIn.setBlockState(blockpos, Blocks.BARRIER.getDefaultState(), 20);
                 }
                 BlockState q = worldIn.getBlockState(blockpos);
                 BlockState p = blockstate;
                 if (worldIn.getBlockState(blockpos).equals(blockstate)) continue;
					if (this.chestPos != null && blockstate != Blocks.AIR.getDefaultState() && blockstate.getBlock().asItem() != null
							&& !this.pullItems(this, this.chestDirection,blockstate.getBlock().asItem())) {
						System.out.println("Returned due to item not found "
								+ blockstate.getBlock().asItem().getRegistryName());
						return false;
					}
                 if (worldIn.setBlockState(blockpos, blockstate, 3)) {
                    i = Math.min(i, blockpos.getX());
                    j = Math.min(j, blockpos.getY());
                    k = Math.min(k, blockpos.getZ());
                    l = Math.max(l, blockpos.getX());
                    i1 = Math.max(i1, blockpos.getY());
                    j1 = Math.max(j1, blockpos.getZ());
                    list2.add(Pair.of(blockpos, template$blockinfo.nbt));
                    if (template$blockinfo.nbt != null) {
                       TileEntity tileentity1 = worldIn.getTileEntity(blockpos);
                       if (tileentity1 != null) {
                          template$blockinfo.nbt.putInt("x", blockpos.getX());
                          template$blockinfo.nbt.putInt("y", blockpos.getY());
                          template$blockinfo.nbt.putInt("z", blockpos.getZ());
                          tileentity1.read(template$blockinfo.nbt);
                          tileentity1.mirror(placementIn.getMirror());
                          tileentity1.rotate(placementIn.getRotation());
                       }
                    }

                    if (ifluidstate != null && blockstate.getBlock() instanceof ILiquidContainer) {
                       ((ILiquidContainer)blockstate.getBlock()).receiveFluid(worldIn, blockpos, blockstate, ifluidstate);
                       if (!ifluidstate.isSource()) {
                          list1.add(blockpos);
                       }
                    }
                 }
                 break;
              }
           }

           boolean flag = true;
           Direction[] adirection = new Direction[]{Direction.UP, Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};

           while(flag && !list1.isEmpty()) {
              flag = false;
              Iterator<BlockPos> iterator = list1.iterator();

              while(iterator.hasNext()) {
                 BlockPos blockpos2 = iterator.next();
                 BlockPos blockpos3 = blockpos2;
                 IFluidState ifluidstate2 = worldIn.getFluidState(blockpos2);

                 for(int k1 = 0; k1 < adirection.length && !ifluidstate2.isSource(); ++k1) {
                    BlockPos blockpos1 = blockpos3.offset(adirection[k1]);
                    IFluidState ifluidstate1 = worldIn.getFluidState(blockpos1);
                    if (ifluidstate1.getActualHeight(worldIn, blockpos1) > ifluidstate2.getActualHeight(worldIn, blockpos3) || ifluidstate1.isSource() && !ifluidstate2.isSource()) {
                       ifluidstate2 = ifluidstate1;
                       blockpos3 = blockpos1;
                    }
                 }

                 if (ifluidstate2.isSource()) {
                    BlockState blockstate2 = worldIn.getBlockState(blockpos2);
                    Block block = blockstate2.getBlock();
                    if (block instanceof ILiquidContainer) {
                       ((ILiquidContainer)block).receiveFluid(worldIn, blockpos2, blockstate2, ifluidstate2);
                       flag = true;
                       iterator.remove();
                    }
                 }
              }
           }

           if (i <= l) {
              if (!placementIn.func_215218_i()) {
                 VoxelShapePart voxelshapepart = new BitSetVoxelShapePart(l - i + 1, i1 - j + 1, j1 - k + 1);
                 int l1 = i;
                 int i2 = j;
                 int j2 = k;

                 for(Pair<BlockPos, CompoundNBT> pair1 : list2) {
                    BlockPos blockpos5 = pair1.getFirst();
                    voxelshapepart.setFilled(blockpos5.getX() - l1, blockpos5.getY() - i2, blockpos5.getZ() - j2, true, true);
                 }

                 Template.func_222857_a(worldIn, 3, voxelshapepart, l1, i2, j2);
              }

              for(Pair<BlockPos, CompoundNBT> pair : list2) {
                 BlockPos blockpos4 = pair.getFirst();
                 if (!placementIn.func_215218_i()) {
                    BlockState blockstate1 = worldIn.getBlockState(blockpos4);
                    BlockState blockstate3 = Block.getValidBlockForPosition(blockstate1, worldIn, blockpos4);
                    if (blockstate1 != blockstate3) {
                       worldIn.setBlockState(blockpos4, blockstate3, 3 & -2 | 16);
                    }

                    worldIn.notifyNeighbors(blockpos4, blockstate3.getBlock());
                 }

                 if (pair.getSecond() != null) {
                    TileEntity tileentity2 = worldIn.getTileEntity(blockpos4);
                    if (tileentity2 != null) {
                       tileentity2.markDirty();
                    }
                 }
              }
           }
           return true;
        } else {
           return false;
        }
		//temp.addBlocksToWorldChunk(worldIn, chestPos, placementIn);;

	}

	public int dirToInt(Direction facingDir) {
		switch (facingDir) {
		case NORTH:
			return 0;
		case EAST:
			return 1;
		case SOUTH:
			return 2;
		case WEST:
			return 3;
		}
		return 0;
	}

	public Direction intToDir(int dirIn) {
		dirIn %= 4;
		switch (dirIn) {
		case 0:
			return Direction.NORTH;
		case 1:
			return Direction.EAST;
		case 2:
			return Direction.SOUTH;
		case 3:
			return Direction.WEST;
		}
		return Direction.NORTH;
	}

}