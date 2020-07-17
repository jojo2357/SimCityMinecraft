package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimResidentialBuildingContainer;
import com.jojo2357.simcityminecraft.container.SimCommercialBuildingContainer;
import com.jojo2357.simcityminecraft.container.SimContainer;
import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.jojo2357.simcityminecraft.container.SimMineBlockContainer;
import com.jojo2357.simcityminecraft.container.SimWorkBenchContainer;

import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.common.extensions.IForgeContainerType;

public class ModContainers {

	public static final DeferredRegister<ContainerType<?>> CONTAINER_TYPES = new DeferredRegister<>(
			ForgeRegistries.CONTAINERS, Main.MOD_ID);

	public static final RegistryObject<ContainerType<SimWorkBenchContainer>> SIM_WORK_BENCH = CONTAINER_TYPES
			.register("sim_work_bench", () -> IForgeContainerType.create(SimWorkBenchContainer::new));

	public static final RegistryObject<ContainerType<SimFarmBlockContainer>> SIM_FARM_BLOCK = CONTAINER_TYPES
			.register("sim_farm_block", () -> IForgeContainerType.create(SimFarmBlockContainer::new));
	
	public static final RegistryObject<ContainerType<SimMineBlockContainer>> SIM_MINE_BLOCK = CONTAINER_TYPES
			.register("sim_mine_block", () -> IForgeContainerType.create(SimMineBlockContainer::new));
	
	public static final RegistryObject<ContainerType<SimResidentialBuildingContainer>> SIM_RESIDENTIAL_BUILDING_BLOCK = CONTAINER_TYPES
			.register("sim_residential_building_block", () -> IForgeContainerType.create(SimResidentialBuildingContainer::new));

	public static final RegistryObject<ContainerType<SimCommercialBuildingContainer>> SIM_COMMERCIAL_BUILDING_BLOCK = CONTAINER_TYPES
			.register("sim_commercial_building_block", () -> IForgeContainerType.create(SimCommercialBuildingContainer::new));
    /*public static final RegistryObject<ContainerType<SimContainer>> SIM_CONTAINER = CONTAINER_TYPES.register("sim", () -> IForgeContainerType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        return new SimContainer(windowId, Main.proxy.getClientWorld(), pos, inv, Main.proxy.getClientPlayer());
    }));*/
	
	public static final RegistryObject<ContainerType<SimContainer>> SIM_CONTAINER = CONTAINER_TYPES
			.register("sim", () -> IForgeContainerType.create(SimContainer::new));

}
