package com.jojo2357.simcityminecraft.client.gui.screens;

import com.jojo2357.simcityminecraft.container.SimContainer;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class SimScreen extends ContainerScreen<SimContainer>{

	private SimContainer container;
	
	public SimScreen(SimContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		System.out.println("Screen Constructed");
		this.container = screenContainer;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		//this.font.drawString("Mark off an area and give me a chest to get started", 10.0f, 58.0f, Integer.parseInt("ffffff", 16));
		this.font.drawString("Hi I'm " + this.container.getName(), 30.0f, 30.0f, Integer.parseInt("ffffff", 16));
		this.font.drawString("Farming lvl: " + this.container.getFarmingLvl() + "/2000", 10.0f, 78.0f, Integer.parseInt("ffffff", 16));
		this.font.drawString("Farming Xp: " + this.container.getFarmingXp() + "/2000", 10.0f, 98.0f, Integer.parseInt("ffffff", 16));
		this.font.drawString("Mining lvl: " + this.container.getMiningLvl() + "/2000", 10.0f, 78.0f, Integer.parseInt("ffffff", 16));
		this.font.drawString("Mining Xp: " + this.container.getMiningXp() + "/2000", 10.0f, 98.0f, Integer.parseInt("ffffff", 16));	
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		
	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
	}
}
