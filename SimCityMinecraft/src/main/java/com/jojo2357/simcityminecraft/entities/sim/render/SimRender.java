package com.jojo2357.simcityminecraft.entities.sim.render;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.entities.sim.Sim;
import com.jojo2357.simcityminecraft.entities.sim.model.SimModel;

import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;

public class SimRender extends MobRenderer<Sim, SimModel<Sim>>{
	
	protected static final ResourceLocation TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/entity/sim.png");
	
	public SimRender(EntityRendererManager manager) {
		super(manager, new SimModel<Sim>(1.0F, false), 0.5F);// , shadow size
	}
	
	@Override
	public ResourceLocation getEntityTexture(Sim entity) {
		return TEXTURE;
	}
}
