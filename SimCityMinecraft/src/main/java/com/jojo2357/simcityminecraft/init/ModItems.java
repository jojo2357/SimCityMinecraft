package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.Main.TutorialItemGroup;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {
	
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS,
			Main.MOD_ID);
	
	//public static final RegistryObject<Item> EXAMPLE_ITEM = ITEMS.register("example_item",
			//() -> new Item(new Item.Properties().group(TutorialItemGroup.instance)));
			
}
