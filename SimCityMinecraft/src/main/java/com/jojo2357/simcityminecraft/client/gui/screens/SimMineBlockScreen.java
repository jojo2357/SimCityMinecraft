package com.jojo2357.simcityminecraft.client.gui.screens;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.widgets.MineButtonWidget;
import com.jojo2357.simcityminecraft.client.gui.widgets.MiningSettingWidget;
import com.jojo2357.simcityminecraft.container.SimMineBlockContainer;
import com.jojo2357.simcityminecraft.entities.sim.Sim;

import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimMineBlockScreen extends ContainerScreen<SimMineBlockContainer> {
	
	private int mode;
	//private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(Main.MOD_ID,
	//		"textures/gui/sim_work_bench.png");

	public SimMineBlockScreen(SimMineBlockContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
			if (this.container.tileEntity.isMining()) out = "Working";
			else out = "Stopped";
			String xp = "" + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getMiningXp();
			String level = "" + this.container.getLvl();
			this.font.drawString("XP: " + xp, 8.0f, 58.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Level: " + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getMiningLevel(), 8.0f, 76.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Status: " + out, 8.0f, 94.0f, Integer.parseInt("ffffff", 16));
			this.font.drawString("Mine area: " + (this.container.getArea()), 8.0f, 112.0f, Integer.parseInt("ffffff", 16));
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
				this.addButton(new MineButtonWidget(xLocation, yLocation, 75, 20, registry.get(i).getMyName() + " (" + registry.get(i).getMiningLevel() + ")", 3 + i, this));
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
		boolean Mining = this.container.isMining();
		if (Mining) {
			message = "OFF";
			myIndex = 2;
		}
		else {
			message = "ON";
			myIndex = 1;
		}
		this.addButton(new MineButtonWidget(3 * x / 2, y, 50, 20, message, myIndex, this));
		this.addButton(new MineButtonWidget(5 * x / 4, 3 * y / 4, 150, 20, "Fire " + this.container.tileEntity.getSyncName() + 
				" (" + Main.simRegistry.getSims().get(this.container.tileEntity.mySimIndex()).getMiningXp() + ")", this.container.tileEntity.mySimIndex() + 3, this));
		this.addButton(new MiningSettingWidget(5 * x / 4, 5 * y / 4, 150, 20, this.container.tileEntity.mode(), this));
	}

}
