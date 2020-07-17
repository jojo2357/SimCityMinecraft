package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.tileentity.SimResidentialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimCommercialBuildingBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimFarmBlockTileEntity;
import com.jojo2357.simcityminecraft.tileentity.SimMineBlockTileEntity;
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

	public static final RegistryObject<TileEntityType<SimFarmBlockTileEntity>> SIM_FARM_BLOCK = TILE_ENTITY_TYPES
			.register("sim_farm_block", () -> TileEntityType.Builder
					.create(SimFarmBlockTileEntity::new, ModBlocks.SIM_FARM_BLOCK.get()).build(null));

	public static final RegistryObject<TileEntityType<SimMineBlockTileEntity>> SIM_MINE_BLOCK = TILE_ENTITY_TYPES
			.register("sim_mine_block", () -> TileEntityType.Builder
					.create(SimMineBlockTileEntity::new, ModBlocks.SIM_MINE_BLOCK.get()).build(null));

	public static final RegistryObject<TileEntityType<SimResidentialBuildingBlockTileEntity>> SIM_RESIDENTIAL_BUILDING_BLOCK = TILE_ENTITY_TYPES
			.register("sim_residential_building_block", () -> TileEntityType.Builder
					.create(SimResidentialBuildingBlockTileEntity::new, ModBlocks.SIM_RESIDENTIAL_BUILDING_BLOCK.get())
					.build(null));

	public static final RegistryObject<TileEntityType<SimCommercialBuildingBlockTileEntity>> SIM_COMMERCIAL_BUILDING_BLOCK = TILE_ENTITY_TYPES
			.register("sim_commercial_building_block", () -> TileEntityType.Builder
					.create(SimCommercialBuildingBlockTileEntity::new, ModBlocks.SIM_COMMERCIAL_BUILDING_BLOCK.get())
					.build(null));
}
