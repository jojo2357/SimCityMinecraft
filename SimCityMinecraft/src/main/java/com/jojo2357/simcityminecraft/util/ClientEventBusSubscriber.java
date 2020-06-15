package com.jojo2357.simcityminecraft.util;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.SimFarmBlockScreen;
import com.jojo2357.simcityminecraft.client.gui.SimWorkBenchScreen;
import com.jojo2357.simcityminecraft.entities.sim.render.SimRender;
import com.jojo2357.simcityminecraft.init.ModBlocks;
import com.jojo2357.simcityminecraft.init.ModContainers;
import com.jojo2357.simcityminecraft.init.ModEntityTypes;

import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@Mod.EventBusSubscriber(modid = Main.MOD_ID, bus = Bus.MOD, value = Dist.CLIENT)
public class ClientEventBusSubscriber {
	@SubscribeEvent
	public static void clientSetup(FMLClientSetupEvent event) {
		ScreenManager.registerFactory(ModContainers.SIM_WORK_BENCH.get(), SimWorkBenchScreen::new);
		ScreenManager.registerFactory(ModContainers.SIM_FARM_BLOCK.get(), SimFarmBlockScreen::new);
		RenderTypeLookup.setRenderLayer(ModBlocks.SIM_MARKER.get(), RenderType.getCutout());
		RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.SIM.get(), SimRender::new);
	}
}
