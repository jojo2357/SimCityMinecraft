package com.jojo2357.simcityminecraft.client.gui.screens;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.widgets.FarmButtonWidget;
import com.jojo2357.simcityminecraft.container.SimFarmBlockContainer;
import com.jojo2357.simcityminecraft.entities.sim.Sim;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimFarmBlockScreen extends ContainerScreen<SimFarmBlockContainer> implements ITickableTileEntity {
	
	private int mode;
	//private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MOD_ID,
	//		"textures/gui/sim_work_bench.png");

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
		// this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.mode = this.container.getMode();
		//super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		if (this.mode == 3) {
			this.font.drawString("Rate: " + this.container.getSpeed(), 8.0f, 16.0f, Integer.parseInt("ffffff", 16));
			String out;
			if (this.container.tileEntity.isFarming()) out = "Working";
			else out = "Stopped";
			String xp = "" + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getFarmingXp();
			String level = "" + this.container.getLvl();
			this.font.drawString("XP: " + xp, 8.0f, 58.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Level: " + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getFarmingLevel(), 8.0f, 76.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Status: " + out, 8.0f, 94.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Optimal farm size: " + (13800/this.container.getSpeed()), 8.0f, 112.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Actual farm size: " + (this.container.getArea()), 8.0f, 130.0f, Integer.parseInt("ffffff", 16));
			//this.font.drawString(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
			//this.font.drawString(this.playerInventory.getDisplayName().getFormattedTexArrayList<E>, 90.0f, 4210752);
		}else if (mode == 1) {
			ArrayList<Sim> registry = Main.simRegistry.getSims();
			int xLocation = 10;
			int yLocation = 10;
			int xStep = 80;
			int yStep = 25;
			for (int i = 0; i < registry.size(); i++) {
				if (registry.get(i).isWorking()) {
					continue;
				}
				this.addButton(new FarmButtonWidget(xLocation, yLocation, 75, 20, registry.get(i).getMyName() + " (" + registry.get(i).getFarmingLevel() + ")", 3 + i, this));
				xLocation += xStep;
				if (xLocation > 400) {
					yLocation += yStep;
					xLocation = 10;
				}
			}
		}else {
			this.font.drawString("Mark off an area and give me a chest to get started", 1.0f, 58.0f, Integer.parseInt("ffffff", 16));
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.mode = this.container.getMode();
		if (mode != 3) return;
		//RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		// this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int x = (this.width) / 2;
		int y = (this.height) / 2;
		//this.blit(x, y, 0, 0, this.xSize, this.ySize);
		String message;
		int myIndex;
		boolean farming = this.container.isFarming();
		if (farming) {
			message = "OFF";
			myIndex = 2;
		}
		else {
			message = "ON";
			myIndex = 1;
		}
		this.buttons.clear();
		this.addButton(new FarmButtonWidget(3 * x / 2, y, 50, 20, message, myIndex, this));
		this.addButton(new FarmButtonWidget(4 * x / 3, 3 * y / 4, 150, 20, "Fire " + this.container.tileEntity.getSyncName() + 
				" (" + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getFarmingXp() + ")", this.container.tileEntity.mySimIndex() + 3, this));
	}

}
