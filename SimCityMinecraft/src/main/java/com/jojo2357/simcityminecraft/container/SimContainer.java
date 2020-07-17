package com.jojo2357.simcityminecraft.container;

import java.util.List;
import java.util.Objects;

import com.jojo2357.simcityminecraft.Main;
//import com.jojo2357.simcityminecraft.entities.sim.ISim;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.init.ModEntityTypes;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.NPCMerchant;
import net.minecraft.entity.merchant.IMerchant;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.MerchantInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.MerchantResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffers;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;

public class SimContainer extends Container {

	private final IMerchant merchant;
	@OnlyIn(Dist.CLIENT)
	private int merchantLevel;
	@OnlyIn(Dist.CLIENT)
	private boolean field_217055_f;
	@OnlyIn(Dist.CLIENT)
	private boolean field_223433_g;

	private Sim owner;

	public SimContainer(int id, PlayerInventory player, PacketBuffer data) {
		this(id, player);
	}

	public SimContainer(int id, PlayerInventory playerInventoryIn) {
		this(id, playerInventoryIn, new NPCMerchant(playerInventoryIn.player));
	}

	public SimContainer(int id, PlayerInventory playerInventoryIn, IMerchant merchantIn) {
		super(ModContainers.SIM_CONTAINER.get(), id);
		this.merchant = merchantIn;

	}

	public void setOwner(Sim simIn) {
		this.owner = simIn;
	}

	@OnlyIn(Dist.CLIENT)
	public void func_217045_a(boolean p_217045_1_) {
		this.field_217055_f = p_217045_1_;
	}

	/**
	 * Callback for when the crafting matrix is changed.
	 */

	/**
	 * Determines whether supplied player can use this container
	 */
	public boolean canInteractWith(PlayerEntity playerIn) {
		return this.merchant.getCustomer() == playerIn;
	}

	@OnlyIn(Dist.CLIENT)
	public int getXp() {
		return this.merchant.getXp();
	}

	@OnlyIn(Dist.CLIENT)
	public void setXp(int xp) {
		this.merchant.setXP(xp);
	}

	@OnlyIn(Dist.CLIENT)
	public int getMerchantLevel() {
		return this.merchantLevel;
	}

	@OnlyIn(Dist.CLIENT)
	public void setMerchantLevel(int level) {
		this.merchantLevel = level;
	}

	@OnlyIn(Dist.CLIENT)
	public void func_223431_b(boolean p_223431_1_) {
		this.field_223433_g = p_223431_1_;
	}

	@OnlyIn(Dist.CLIENT)
	public boolean func_223432_h() {
		return this.field_223433_g;
	}

	/**
	 * Called to determine if the current slot is valid for the stack merging
	 * (double-click) code. The stack passed in is null for the initial slot that
	 * was double-clicked.
	 */

	/**
	 * Called when the container is closed.
	 */
	public void onContainerClosed(PlayerEntity playerIn) {
		System.out.println("Closed");
		super.onContainerClosed(playerIn);
		this.merchant.setCustomer((PlayerEntity) null);

	}

	/**
	 * net.minecraft.client.network.play.ClientPlayNetHandler uses this to set
	 * offers for the client side MerchantContainer
	 */
	@OnlyIn(Dist.CLIENT)
	public void setClientSideOffers(MerchantOffers offers) {
		this.merchant.setClientSideOffers(offers);
	}

	public MerchantOffers getOffers() {
		return this.merchant.getOffers();
	}

	@OnlyIn(Dist.CLIENT)
	public boolean func_217042_i() {
		return this.field_217055_f;
	}

	public int getFarmingLvl() {
		return this.owner.getFarmingLevel();
	}

	public int getFarmingXp() {
		return this.owner.getFarmingXp();
	}

	public int getMiningLvl() {
		return this.owner.getMiningLevel();
	}

	public int getMiningXp() {
		return this.owner.getMiningXp();
	}

	public String getName() {
		return this.owner.getMyName();
	}
}
