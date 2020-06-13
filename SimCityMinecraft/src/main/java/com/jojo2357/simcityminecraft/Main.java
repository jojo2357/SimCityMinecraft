package com.jojo2357.simcityminecraft;

import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.init.ModEntityTypes;
import com.jojo2357.simcityminecraft.init.ModItems;
import com.jojo2357.simcityminecraft.init.ModTileEntityTypes;
import com.jojo2357.simcityminecraft.util.handler.SimKredsHandler;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.BlockMode;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistry;

@SuppressWarnings("deprecation")
@Mod("simcityminecraft")
@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Bus.MOD)
public class Main {

	public static Random rand = new Random(69);
	public static final SimKredsHandler KredsManager = new SimKredsHandler();
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MOD_ID = "simcityminecraft";
	public static Main instance;
	// public static final WorldType EXAMPLE_WORLDTYPE = new ExampleWorldType();
	public static final ResourceLocation EXAMPLE_DIM_TYPE = new ResourceLocation(MOD_ID, "example");

	public Main() {
		
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		modEventBus.addListener(this::renderGameOverlay);
		modEventBus.addListener(this::setup);

		ModItems.ITEMS.register(modEventBus);
		ModBlocks.BLOCKS.register(modEventBus);
		ModEntityTypes.ENTITY_TYPES.register(modEventBus);
		ModTileEntityTypes.TILE_ENTITY_TYPES.register(modEventBus);
		ModContainers.CONTAINER_TYPES.register(modEventBus);
		/*ParticleInit.PARTICLE_TYPES.register(modEventBus);
		SoundInit.SOUNDS.register(modEventBus);
		PotionInit.POTIONS.register(modEventBus);
		PotionInit.POTION_EFFECTS.register(modEventBus);
		EnchantmentInit.ENCHANTMENTS.register(modEventBus);
		FluidInit.FLUIDS.register(modEventBus);
		BiomeInit.BIOMES.register(modEventBus);
		DimensionInit.MOD_DIMENSIONS.register(modEventBus);*/

		instance = this;
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public static void onRegisterItems(final RegistryEvent.Register<Item> event) {
		final IForgeRegistry<Item> registry = event.getRegistry();

		ModBlocks.BLOCKS.getEntries().stream()
				.filter(block -> true)
				.map(RegistryObject::get).forEach(block -> {
					final Item.Properties properties = new Item.Properties().group(TutorialItemGroup.instance);
					final BlockItem blockItem = new BlockItem(block, properties);
					blockItem.setRegistryName(block.getRegistryName());
					registry.register(blockItem);
				});

		LOGGER.debug("Registered BlockItems!");
	}
	
	@SubscribeEvent
	public void renderGameOverlay(RenderGameOverlayEvent.Post event) {
		//System.out.println("tried");
		if(event.getType() == ElementType.HOTBAR)
		{
			Minecraft mc = Minecraft.getInstance();
			mc.fontRenderer.drawStringWithShadow("Kreds: " + KredsManager.getKreds(), 10, 10, Integer.parseInt("FFFFFF", 16));
		}
	}

	private void setup(final FMLCommonSetupEvent event) {// K9#8016
		/*ComposterBlock.registerCompostable(0.6f, BlockInit.JAZZ_LEAVES.get());
		ComposterBlock.registerCompostable(0.4f, ItemInit.SEED_ITEM.get());
		DeferredWorkQueue.runLater(TutorialOreGen::generateOre);
		/*
		 * DeferredWorkQueue.runLater(() -> { for (Biome biome : ForgeRegistries.BIOMES)
		 * { if (biome instanceof ExampleBiome) {
		 * biome.getSpawns(EntityClassification.MONSTER) .add(new
		 * Biome.SpawnListEntry(EntityType.ZOMBIE, 1000, 1, 4)); } } });
		 */
	}

	/*
	 * public static void registerPlacementType(EntityType type,
	 * EntitySpawnPlacementRegistry.PlacementType placementType) {
	 * EntitySpawnPlacementRegistry.register(type, placementType,
	 * Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
	 * MonsterEntity::canMonsterSpawnInLight); }
	 */

	@SubscribeEvent
	public static void onServerStarting(FMLServerStartingEvent event) {

	}

	@SubscribeEvent
	public static void loadCompleteEvent(FMLLoadCompleteEvent event) {
		// This doesnt work anymore
		// TutorialOreGen.generateOre();
	}

	public static class TutorialItemGroup extends ItemGroup {
		public static final ItemGroup instance = new TutorialItemGroup(ItemGroup.GROUPS.length, "tutorialtab");

		private TutorialItemGroup(int index, String label) {
			super(index, label);
		}

		@Override
		public ItemStack createIcon() {
			return new ItemStack(Blocks.DIRT);
					//ModBlocks.EXAMPLE_BLOCK.get());
		}
	}
}
