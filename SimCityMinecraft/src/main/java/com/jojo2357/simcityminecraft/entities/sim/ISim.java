package com.jojo2357.simcityminecraft.entities.sim;

import java.util.OptionalInt;

import javax.annotation.Nullable;

import com.jojo2357.simcityminecraft.container.SimContainer;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.MerchantContainer;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.item.MerchantOffer;
import net.minecraft.item.MerchantOffers;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface ISim {
	   void setCustomer(@Nullable PlayerEntity player);

	   @Nullable
	   PlayerEntity getCustomer();

	   MerchantOffers getOffers();

	   @OnlyIn(Dist.CLIENT)
	   void setClientSideOffers(@Nullable MerchantOffers offers);

	   void onTrade(MerchantOffer offer);

	   /**
	    * Notifies the merchant of a possible merchantrecipe being fulfilled or not. Usually, this is just a sound byte
	    * being played depending if the suggested itemstack is not null.
	    */
	   void verifySellingItem(ItemStack stack);

	   World getWorld();

	   int getXp();

	   void setXP(int xpIn);

	   boolean func_213705_dZ();

	   default boolean func_223340_ej() {
	      return false;
	   }

	   default void openSimContainer(PlayerEntity player, ITextComponent p_213707_2_, int level) {
	      OptionalInt optionalint = player.openContainer(new SimpleNamedContainerProvider((p_213701_1_, p_213701_2_, p_213701_3_) -> {
	         return new SimContainer(p_213701_1_, p_213701_2_, this);
	      }, p_213707_2_));
	      if (optionalint.isPresent()) {
	            player.openMerchantContainer(optionalint.getAsInt(), null, level, this.getXp(), this.func_213705_dZ(), this.func_223340_ej());
	      }

	   }
}
