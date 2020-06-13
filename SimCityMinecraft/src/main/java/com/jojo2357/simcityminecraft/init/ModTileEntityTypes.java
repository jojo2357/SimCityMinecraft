package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.tileentity.SimWorkBenchTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

	public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = new DeferredRegister<>(
			ForgeRegistries.TILE_ENTITIES, Main.MOD_ID);
	
	public static final RegistryObject<TileEntityType<SimWorkBenchTileEntity>> SIM_WORK_BENCH = TILE_ENTITY_TYPES
			.register("sim_work_bench", () -> TileEntityType.Builder
					.create(SimWorkBenchTileEntity::new, ModBlocks.SIM_WORK_BENCH.get()).build(null));

}
