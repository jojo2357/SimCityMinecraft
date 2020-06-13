package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.entities.sim.Sim;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModEntityTypes {
	public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES,
			Main.MOD_ID);
	
	public static final RegistryObject<EntityType<Sim>> SIM = ENTITY_TYPES.register("sim",
			() -> EntityType.Builder.<Sim>create(Sim::new, EntityClassification.CREATURE)
			.size(0.9f, 1.9f).build(new ResourceLocation(Main.MOD_ID, "sim").toString()));
}
