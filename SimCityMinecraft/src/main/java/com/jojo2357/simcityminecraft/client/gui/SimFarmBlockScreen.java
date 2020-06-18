package com.jojo2357.simcityminecraft.client.gui;

import java.util.List;

import com.google.common.collect.Lists;
import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.mojang.blaze3d.systems.RenderSystem;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimFarmBlockScreen extends ContainerScreen<SimFarmBlockContainer> implements ITickableTileEntity{

	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MOD_ID, "textures/gui/sim_work_bench.png");
	
	public SimFarmBlockScreen(SimFarmBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 183;
	}
	
	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
		//this.renderHoveredToolTip(mouseX, mouseY);
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		this.font.drawString(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
		this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(), 8.0f, 90.0f, 4210752);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2; 
		this.blit(x, y, 0, 0, this.xSize, this.ySize);
		this.addButton(new FarmButtonWidget(10, 10, 50, 20, "test", 1, this));
		this.addButton(new FarmButtonWidget(10, 100, 50, 20, "test", 2, this));
	}

}
