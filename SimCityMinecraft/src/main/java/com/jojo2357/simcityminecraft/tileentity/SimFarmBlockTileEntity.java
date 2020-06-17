package com.jojo2357.simcityminecraft.tileentity;

import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.objects.blocks.SimFarmBlockBlock;
import com.jojo2357.simcityminecraft.objects.blocks.SimMarker;
import com.jojo2357.simcityminecraft.util.handler.Area;
import com.jojo2357.simcityminecraft.util.handler.AreaHandler;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventoryProvider;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.LockableLootTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.InvWrapper;

public class SimFarmBlockTileEntity extends LockableLootTileEntity implements ITickableTileEntity, IInventory{
 
	private NonNullList<ItemStack> chestContents = NonNullList.withSize(1, ItemStack.EMPTY);
	protected int numPlayersUsing;
	private IItemHandlerModifiable items = createHandler();
	private LazyOptional<IItemHandlerModifiable> itemHandler = LazyOptional.of(() -> items);
	
	private int shouldState = 1;
	private boolean doFarming = false;
	private boolean xGoPositive;
	private boolean zGoPositive;
	
	boolean chestHasDirt;
	
	public boolean foundArea = false;
	public Area area;
	public BlockPos chestSpot = null;

	public SimFarmBlockTileEntity(TileEntityType<?> typeIn) {
		super(typeIn);
	}

	public SimFarmBlockTileEntity() {
		this(ModTileEntityTypes.SIM_FARM_BLOCK.get());
	}

	@Override
	public int getSizeInventory() {
		return 1;
	}

	@Override
	public NonNullList<ItemStack> getItems() {
		return this.chestContents;
	}

	@Override
	public void setItems(NonNullList<ItemStack> itemsIn) {
		this.chestContents = itemsIn;
	}

	@Override
	protected ITextComponent getDefaultName() {
		return new TranslationTextComponent("container.sim_farm_block");
	}

	@Override
	protected Container createMenu(int id, PlayerInventory player) {
		return new SimFarmBlockContainer(id, player, this);
	}

	@Override
	public CompoundNBT write(CompoundNBT compound) {
		super.write(compound);
		if (!this.checkLootAndWrite(compound)) {
			ItemStackHelper.saveAllItems(compound, this.chestContents);
		}
		return compound;
	}
	
	@Override
	public void read(CompoundNBT compound) {
		super.read(compound);
		this.chestContents = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
		if (!this.checkLootAndRead(compound)) {
			ItemStackHelper.loadAllItems(compound, this.chestContents);
		}
	}

	private void playSound(SoundEvent sound) {
		double dx = (double) this.pos.getX() + 0.5D;
		double dy = (double) this.pos.getY() + 0.5D;
		double dz = (double) this.pos.getZ() + 0.5D;
		this.world.playSound((PlayerEntity) null, dx, dy, dz, sound, SoundCategory.BLOCKS, 0.5f,
				this.world.rand.nextFloat() * 0.1f + 0.9f);
	}

	@Override
	public boolean receiveClientEvent(int id, int type) {
		if (id == 1) {
			this.numPlayersUsing = type;
			return true;
		} else {
			return super.receiveClientEvent(id, type);
		}
	}

	@Override
	public void openInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			if (this.numPlayersUsing < 0) {
				this.numPlayersUsing = 0;
			}
			++this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}

	@Override
	public void closeInventory(PlayerEntity player) {
		if (!player.isSpectator()) {
			--this.numPlayersUsing;
			this.onOpenOrClose();
		}
	}
 
	protected void onOpenOrClose() {
		Block block = this.getBlockState().getBlock();
		if (block instanceof SimFarmBlockBlock) {
			this.world.addBlockEvent(this.pos, block, 1, this.numPlayersUsing);
			this.world.notifyNeighborsOfStateChange(this.pos, block);
		}
	}

	public static int getPlayersUsing(IBlockReader reader, BlockPos pos) {
		BlockState blockstate = reader.getBlockState(pos);
		if (blockstate.hasTileEntity()) {
			TileEntity tileentity = reader.getTileEntity(pos);
			if (tileentity instanceof SimFarmBlockTileEntity) {
				return ((SimFarmBlockTileEntity) tileentity).numPlayersUsing;
			}
		}
		return 0;
	}

	public static void swapContents(SimFarmBlockTileEntity te, SimFarmBlockTileEntity otherTe) {
		NonNullList<ItemStack> list = te.getItems();
		te.setItems(otherTe.getItems());
		otherTe.setItems(list);
	}

	@Override
	public void updateContainingBlockInfo() {
		super.updateContainingBlockInfo();
		if (this.itemHandler != null) {
			this.itemHandler.invalidate();
			this.itemHandler = null;
		}
	}

	@Override
	public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nonnull Direction side) {
		if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			//return itemHandler.cast();
		}
		return super.getCapability(cap, side);
	}

	private IItemHandlerModifiable createHandler() {
		return new InvWrapper(this);
	}
	
	@Override
	public void remove() {
		super.remove();
		if(itemHandler != null) {
			itemHandler.invalidate();
		}
	}
	
	public void clickHappened(int index) {
		if (index == 1) doFarming = true;
		if (index == 2) doFarming = false;
	}

	//@Override
	public void tick() {
		World worldIn = this.world.getWorld();
		if (worldIn.getGameTime() % 20 == 1 && worldIn.isRemote) {
			
			BlockPos pos = this.pos;
			//System.out.println(pos + " " + chestSpot);
			if (chestSpot == null) {
				for(Direction facing : ChestBlock.FACING.getAllowedValues()) {
				    if(worldIn.getTileEntity(pos.offset(facing)) instanceof ChestTileEntity) {
				        chestSpot = pos.offset(facing);
				    }
				}// && worldIn.getTileEntity(pos) instanceof SimFarmBlockTileEntity
				if (chestSpot != null) {
					shouldState++;
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
				}
			}else {
				if (!(worldIn.getTileEntity(chestSpot) instanceof ChestTileEntity)) {
					//System.out.println("Chest Lost from " + chestSpot + " " + this.chestSpot);
					chestSpot = null;
					shouldState--;
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
				}
			}
			if (worldIn.getBlockState(pos) == ModBlocks.SIM_FARM_BLOCK.get().getDefaultState().with(SimFarmBlockBlock.getFacing(), 
					worldIn.getBlockState(pos).get(SimFarmBlockBlock.getFacing()))) {
				worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
			}
			//System.out.println(pos + " " + chestSpot);
			for (Area checking : AreaHandler.definedAreas) {
				if (checking.taken()) continue;
				if (isNextTo(pos, checking.getPlacedCorner())) {
					shouldState++;
					foundArea = true;
					System.out.println("ooga booga");
					worldIn.setBlockState(pos, worldIn.getBlockState(pos).with(SimFarmBlockBlock.getColorState(), shouldState));
					this.area = checking;
					checking.markTaken();
				}
			}
			if (shouldState == 3 && doFarming) {
				BlockPos lookingPos;
				IInventory chestInventory = null;
				int dirtIndex = -1;
				chestHasDirt = false;
				
				if (worldIn.getTileEntity(chestSpot) instanceof ChestTileEntity) {
					//System.out.println("Chest drawing from is @: " + chestSpot + " and I am @: " + pos);
					chestInventory = getInventoryAtPosition(worldIn, chestSpot);
					System.out.println(chestInventory + " " + chestInventory == null);
					//public ChestContainer(ContainerType<?> type, int id, PlayerInventory playerInventoryIn, IInventory p_i50092_4_, int rows)
					
					//chestInventory.clear();
					for (int i = 0; i < 27; i++) {
						if (((ChestTileEntity)chestInventory).getStackInSlot(i).getItem().equals(Items.DIRT)) {
							chestHasDirt = true;
							dirtIndex = i;
							System.out.println("Found dirt");
						}//else System.out.println(((LockableLootTileEntity)chestInventory).getStackInSlot(i).getItem() + " in slot " + i + " is not: " + Items.DIRT);
					}
				}
				if (area.getPlacedCorner().getX() > area.getGuessedCorner().getX()) xGoPositive = false;
				else xGoPositive = true;
				if (area.getPlacedCorner().getZ() > area.getGuessedCorner().getZ()) zGoPositive = false;
				else zGoPositive = true;
				int distX = Math.abs(area.getPlacedCorner().getX() - area.getGuessedCorner().getX()) - 1;
				int distZ = Math.abs(area.getPlacedCorner().getZ() - area.getGuessedCorner().getZ()) - 1;
				for (int farmingIndeX = 0; farmingIndeX < distX; farmingIndeX++) {
					int realX = xGoPositive ? farmingIndeX + 1: - farmingIndeX;
					realX += area.getPlacedCorner().getX();
					for (int farmingIndeZ = 0; farmingIndeZ < distZ; farmingIndeZ++) {
						int realZ = zGoPositive ? farmingIndeZ + 1: - farmingIndeZ;
						realZ += area.getPlacedCorner().getZ();
						lookingPos = new BlockPos(realX, area.getPlacedCorner().getY() - 1, realZ);
						//if (chestHasDirt) {
							if (!(worldIn.getBlockState(lookingPos).getBlock() == Blocks.DIRT || worldIn.getBlockState(lookingPos).getBlock() == Blocks.GRASS_BLOCK) 
									&& !worldIn.hasWater(lookingPos)
									) {
								System.out.println("PLACE SOME DIRT " + lookingPos);
								//decraseInventory(dirtIndex, chestInventory);
								return;
							}
						//}
					}
				}
			}
		}
	}
	
	public static void decraseInventory(int index, IInventory inventory) {
		int alreadyThere = inventory.getStackInSlot(index).getStack().getCount();
		System.out.println("Blocks detected: " + alreadyThere);
		if (alreadyThere == 1) {
			inventory.setInventorySlotContents(index, ItemStack.EMPTY);
		}else {
			inventory.setInventorySlotContents(index, new ItemStack(inventory.getStackInSlot(index).getStack().getItem(), alreadyThere - 1));
		}
	}
	
	public static IInventory getInventoryAtPosition(World worldIn, BlockPos pos) {
		return getInventoryAtPosition(worldIn, pos.getX(), pos.getY(), pos.getZ());
	}
	
	@Nullable
    public static IInventory getInventoryAtPosition(World worldIn, double x, double y, double z) {
	      IInventory iinventory = null;
	      BlockPos blockpos = new BlockPos(x, y, z);
	      BlockState blockstate = worldIn.getBlockState(blockpos);
	      Block block = blockstate.getBlock();
	      if (block instanceof ISidedInventoryProvider) {
	    	  iinventory = ((ISidedInventoryProvider)block).createInventory(blockstate, worldIn, blockpos);
	      } else if (blockstate.hasTileEntity()) {
	          TileEntity tileentity = worldIn.getTileEntity(blockpos);
	          if (tileentity instanceof IInventory) {
	            iinventory = (IInventory)tileentity;
	            if (iinventory instanceof ChestTileEntity && block instanceof ChestBlock) {
	               iinventory = ChestBlock.func_226916_a_((ChestBlock)block, blockstate, worldIn, blockpos, true);
	            }
	         }
	      }

	      if (iinventory == null) {
	         List<Entity> list = worldIn.getEntitiesInAABBexcluding((Entity)null, new AxisAlignedBB(x - 0.5D, y - 0.5D, z - 0.5D, x + 0.5D, y + 0.5D, z + 0.5D), EntityPredicates.HAS_INVENTORY);
	         if (!list.isEmpty()) {
	            iinventory = (IInventory)list.get(worldIn.rand.nextInt(list.size()));
	         }
	      }

	      return iinventory;
	}
	
	private static boolean isNextTo(BlockPos a, BlockPos b) {
		return a.north().equals(b) || a.east().equals(b) || a.south().equals(b) || a.west().equals(b);
	}
}
