package com.jojo2357.simcityminecraft.client.gui.screens;

import java.util.ArrayList;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.widgets.BuildButtonWidget;
import com.jojo2357.simcityminecraft.container.SimWorkBenchContainer;
import com.jojo2357.simcityminecraft.util.handler.BuildingTemplate;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimWorkBenchScreen extends ContainerScreen<SimWorkBenchContainer> {

	private int mode = 0;
	private int page = 0;
	private boolean simHired = false;
	private int displayBuilding = -1;
	private int displayMode = -1;
	// private static final ResourceLocation BACKGROUND_TEXTURE = new
	// ResourceLocation(Main.MOD_ID, "textures/gui/sim_work_bench.png");

	public SimWorkBenchScreen(SimWorkBenchContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
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
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		// this.font.drawString(this.title.getFormattedText(), 8.0f, 6.0f, 4210752);
		// this.font.drawString(this.playerInventory.getDisplayName().getFormattedText(),
		// 8.0f, 90.0f, 4210752);
		/*if (this.displayBuilding != -1) {
			displayRecipe();
			return;
		}*/
		if (mode == 0 && this.container.tileEntity.doIHaveSim())
			mode = 4;
		this.buttons.clear();
		this.children.clear();
		switch (mode) {
		case 0:
			this.drawSimOptions();
			break;
		case 1:
			this.drawBuildingOptions(Main.buildingLoader.allTemplates.get(0));
			break;
		case 2:
			this.drawBuildingOptions(Main.buildingLoader.allTemplates.get(1));
			break;
		case 3:
			this.drawBuildingOptions(Main.buildingLoader.allTemplates.get(2));
			break;
		case 4:
			this.selectClass();
			break;
		case 6:
			this.displayRecipe();
		}
	}

	private void displayRecipe() {
		if (this.displayBuilding == -1) {
			return;
		}
		BuildingTemplate bilding = Main.buildingLoader.allTemplates.get(this.displayMode).get(this.displayBuilding);
		this.font.drawString("Materials needed for " + bilding.getName(), 20, 5, Integer.parseInt("FFFFFF", 16));
		for (int i = 0; i < bilding.getRecipeNames().size(); i++) {
			this.font.drawString(bilding.getRecipeNames().get(i), 5, 15 * i + 20, Integer.parseInt("FFFFFF", 16));
			this.font.drawString(
					": " + bilding.getRecipeNumbers().get(i) + "(" + (bilding.getRecipeNumbers().get(i) / 64.0) + ") Stacks",
					5 + this.font.getStringWidth(bilding.getRecipeNames().get(i)), 15 * i + 20,
					Integer.parseInt("FFFFFF", 16));
		}
		this.addButton(new BuildButtonWidget(20, 45, this.font.getStringWidth("Build "), 20, "Build", 0, this, 7));
		this.addButton(new BuildButtonWidget(20, 75, this.font.getStringWidth("Build "), 20, "Back", 1, this, 7));
	}

	private void selectClass() {
		this.addButton(new BuildButtonWidget(20, 30, 100, 20, "Residential", 1, this, 5));
		this.addButton(new BuildButtonWidget(20, 60, 100, 20, "Commercial", 2, this, 5));
		this.addButton(new BuildButtonWidget(20, 90, 100, 20, "Industrial", 3, this, 5));
	}

	private void drawSimOptions() {
		int k = 0;
		for (int i = 0; i < Main.simRegistry.getSims().size(); i++) {
			if (Main.simRegistry.getSims().get(i).isWorking())
				continue;
			this.addButton(new BuildButtonWidget((120 * k + 20) % 720, 20 + (30 * (k / 6)), 100, 20,
					Main.simRegistry.getSims().get(i).getMyName(), i, this, this.mode));
			k++;
		}
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		// RenderSystem.color4f(1.0f, 1.0f, 1.0f, 1.0f);
		// this.minecraft.getTextureManager().bindTexture(BACKGROUND_TEXTURE);
		int x = (this.width - this.xSize) / 2;
		int y = (this.height - this.ySize) / 2;
		// this.blit(x, y, 0, 0, this.xSize, this.ySize);
	}

	private void drawBuildingOptions(ArrayList<BuildingTemplate> buildings) {
		for (int i = 0; i < buildings.size(); i++) {
			this.font.drawString(buildings.get(i).getName(), 20 + (120 * (i - 1)), 10, Integer.parseInt("FFFFFF", 16));
			this.font.drawString(buildings.get(i).getDimensions(), 20 + (120 * (i - 1)), 30,
					Integer.parseInt("FFFFFF", 16));
			this.addButton(new BuildButtonWidget(20 + (120 * i), 70,
					this.font.getStringWidth(buildings.get(i).getName()) > this.font
							.getStringWidth(buildings.get(i).getDimensions())
									? this.font.getStringWidth(buildings.get(i).getName())
									: this.font.getStringWidth(buildings.get(i).getDimensions()),
					20, "Build", i, this, this.mode));
		}
	}

	public void selectionMade(int myIndex) {
		this.mode = myIndex;
	}

	public void changeMode(int thingClicked) {
		this.buttons.clear();
		this.children.clear();
		switch (this.mode) {
		case 0:
			this.mode = 4;
			return;
		case 4:
			this.mode = thingClicked;
			this.displayMode = thingClicked - 1;
			return;
		case 5:
			this.mode = thingClicked;
			this.displayMode = thingClicked - 1;
			return;
		case 1:
			this.displayBuilding = thingClicked;
			this.mode = 6;
			return;
		case 2:
			this.displayBuilding = thingClicked;
			this.mode = 6;
			return;
		case 3:
			this.displayBuilding = thingClicked;
			this.mode = 6;
			return;
		case 6:
			if (thingClicked == 0) {
				this.mode = this.displayMode;
				this.displayBuilding = -1;
			}else {
				this.minecraft.displayGuiScreen(null);
			}
		}
	}

	public void ownerSetMode(int modeIn) {
		this.mode = modeIn;
	}
	/*
	 * public void buttonClicked(int indexClicked) {
	 * this.getContainer().tileEntity.clickHappened(indexClicked); }
	 */
}
