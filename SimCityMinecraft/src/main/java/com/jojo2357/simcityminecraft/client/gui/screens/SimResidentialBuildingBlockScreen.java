package com.jojo2357.simcityminecraft.client.gui.screens;

import com.jojo2357.simcityminecraft.client.gui.widgets.BuildingConfigureWidget;
import com.jojo2357.simcityminecraft.container.SimResidentialBuildingContainer;
import com.jojo2357.simcityminecraft.container.SimContainer;

import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimResidentialBuildingBlockScreen extends ContainerScreen<SimResidentialBuildingContainer>{

	private BlockPos ownerPos;
	private Boolean configureMode;
	private int residents;
	
	public SimResidentialBuildingBlockScreen(SimResidentialBuildingContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.ownerPos = screenContainer.getPos();
		this.configureMode = screenContainer.getMode();
		this.residents = screenContainer.getResidents();
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 183;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		//this.children.clear();
		//this.buttons.clear();
		//if (this.configureMode) {
			this.font.drawString("Residents " + this.container.getResidents(), 30, 30, Integer.parseInt("FFFFFF", 16));
			this.addButton(new BuildingConfigureWidget(40 ,45, 20, 20, "-1", -1, 0, 0, 0, this.ownerPos, this));
			this.addButton(new BuildingConfigureWidget(60 ,45, 20, 20, "0", 0, 0, 0, 0, this.ownerPos, this));
			this.addButton(new BuildingConfigureWidget(80 ,45, 20, 20, "1", 1, 0, 0, 0, this.ownerPos, this));
			
			this.font.drawString("Rent " + this.container.getRent(), 30, 70, Integer.parseInt("FFFFFF", 16));
			this.addButton(new BuildingConfigureWidget(15 ,90, 40, 20, "-0.01", -1, 0, 0, 1, this.ownerPos, this));
			this.addButton(new BuildingConfigureWidget(50 ,90, 30, 20, "0.01", 1, 0, 0, 1, this.ownerPos, this));
			this.addButton(new BuildingConfigureWidget(95 ,90, 20, 20, ".1", 10, 0, 0, 1, this.ownerPos, this));
		//}else {
			
		//}
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

