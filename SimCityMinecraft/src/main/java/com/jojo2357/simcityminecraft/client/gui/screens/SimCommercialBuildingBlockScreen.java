package com.jojo2357.simcityminecraft.client.gui.screens;

import com.jojo2357.simcityminecraft.Main;
import com.jojo2357.simcityminecraft.client.gui.widgets.CommercialWidget;
import com.jojo2357.simcityminecraft.container.SimCommercialBuildingContainer;
import com.jojo2357.simcityminecraft.container.SimResidentialBuildingContainer;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class SimCommercialBuildingBlockScreen extends ContainerScreen<SimCommercialBuildingContainer> {

	private BlockPos ownerPos;
	private Boolean configureMode;
	private int residents;
	private int mode = 0;

	public SimCommercialBuildingBlockScreen(SimCommercialBuildingContainer screenContainer, PlayerInventory inv,
			ITextComponent titleIn) {
		super(screenContainer, inv, titleIn);
		this.ownerPos = screenContainer.getPos();
		this.configureMode = screenContainer.getMode();
		// this.residents = screenContainer.getWorkers();
		this.guiLeft = 0;
		this.guiTop = 0;
		this.xSize = 175;
		this.ySize = 183;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		this.children.clear();
		this.buttons.clear();
		// if (this.configureMode) {
		if (this.mode == 1) {
			int k = 0;
			for (int i = 0; i < Main.simRegistry.getSims().size(); i++) {
				if (!Main.simRegistry.getSims().get(i).hasJob()) {
					this.addButton(new CommercialWidget(30, 20 * k + 65,
						this.font.getStringWidth("Hire " + Main.simRegistry.getSims().get(i).getMyName()) + 5, 15,
						"Hire " + Main.simRegistry.getSims().get(i).getMyName(), i, 0, 0, 1, this.ownerPos, this));
					k++;
				}
			}
			return;
		}
		int i = 0;
		this.font.drawString("Welcome to " + this.container.getStoreName(), 100, 30, Integer.parseInt("FFFFFF", 16));
		this.font.drawString(this.container.getWorkers() == null ? this.container.getStoreName() + " has no workers" : "Workers (" + this.container.getWorkerCount() + ") :", 30, 50, Integer.parseInt("FFFFFF", 16));
		if (this.container.getWorkers().size() > 0)
			for (i = 0; i < this.container.getWorkers().size(); i++)
				this.addButton(new CommercialWidget(30, 15 * i + 65,
						this.font.getStringWidth("Fire " + this.container.getWorkers().get(i).getMyName()) + 5, 15,
						"Fire " + this.container.getWorkers().get(i).getMyName(), i, 0, 0, 0, this.ownerPos, this));
		this.addButton(new CommercialWidget(30, 15 * i + 65,
				this.font.getStringWidth("Hire workers") + 5, 15,
				"Hire workers", i, 0, 0, -1, this.ownerPos, this));

		// }else {

		// }
	}

	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {

	}

	@Override
	public void render(final int mouseX, final int mouseY, final float partialTicks) {
		this.renderBackground();
		super.render(mouseX, mouseY, partialTicks);
	}

	public void openHireMenu() {
		this.mode  = 1;
	}
}
