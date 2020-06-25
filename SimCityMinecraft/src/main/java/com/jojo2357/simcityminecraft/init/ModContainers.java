package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimContainer;
import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.jojo2357.simcityminecraft.container.SimWorkBenchContainer;

import net.minecraft.inventory.container.ContainerType;
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
	
	public static final RegistryObject<ContainerType<SimContainer>> SIM = CONTAINER_TYPES
			.register("sim", () -> IForgeContainerType.create(SimContainer::new));

}
