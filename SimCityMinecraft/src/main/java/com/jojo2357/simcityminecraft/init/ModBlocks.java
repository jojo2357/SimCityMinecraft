package com.jojo2357.simcityminecraft.init;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.objects.blocks.SimMarker;
import com.jojo2357.simcityminecraft.objects.blocks.SimWorkBenchBlock;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModBlocks {
	
	public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS,
			Main.MOD_ID);

	public static final RegistryObject<Block> SIM_MARKER = BLOCKS.register("sim_marker", () -> new SimMarker(
			SimMarker.Properties.create(Material.MISCELLANEOUS).doesNotBlockMovement().lightValue(10), 2));
	
	public static final RegistryObject<Block> SIM_WORK_BENCH = BLOCKS.register("sim_work_bench",() -> new SimWorkBenchBlock(
			SimWorkBenchBlock.Properties.create(Material.IRON).tickRandomly()));
	
	public static final RegistryObject<Block> SIM_LIGHT_WHITE = BLOCKS.register("sim_light_white", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_RED = BLOCKS.register("sim_light_red", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_LIME = BLOCKS.register("sim_light_lime", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_BLACK = BLOCKS.register("sim_light_black", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_BLUE = BLOCKS.register("sim_light_blue", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_CYAN = BLOCKS.register("sim_light_cyan", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_DARKGREY = BLOCKS.register("sim_light_darkgrey", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_GREEN = BLOCKS.register("sim_light_green", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_LIGHTBLUE = BLOCKS.register("sim_light_lightblue", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_LIGHTGREY = BLOCKS.register("sim_light_lightgrey", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_ORANGE = BLOCKS.register("sim_light_orange", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_YELLOW = BLOCKS.register("sim_light_yellow", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
	
	public static final RegistryObject<Block> SIM_LIGHT_PINK = BLOCKS.register("sim_light_pink", () -> new Block(
			Block.Properties.create(Material.WOOD).lightValue(15)));
}
