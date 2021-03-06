package lykrast.prodigytech.client.gui;

import com.google.common.collect.ImmutableList;

import lykrast.prodigytech.common.gui.ContainerCapacitorCharger;
import lykrast.prodigytech.common.tileentity.TileCapacitorCharger;
import lykrast.prodigytech.common.tileentity.TileHotAirMachineSimple;
import lykrast.prodigytech.common.util.Config;
import lykrast.prodigytech.common.util.TooltipUtil;
import lykrast.prodigytech.core.ProdigyTech;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public class GuiCapacitorCharger extends GuiInventory {
	public static final ResourceLocation GUI = ProdigyTech.resource("textures/gui/capacitor_charger.png");
    private final IInventory playerInventory;
    private final TileCapacitorCharger tile;

	public GuiCapacitorCharger(InventoryPlayer playerInv, TileCapacitorCharger tile) {
		super(new ContainerCapacitorCharger(playerInv, tile));
		
		playerInventory = playerInv;
		this.tile = tile;
		
		this.xSize = 176;
		this.ySize = 166;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(GUI);
		this.drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	
		//Progress
	    if (TileHotAirMachineSimple.isProcessing(tile))
	    {
	        int k = getProcessLeftScaled(18);
	        this.drawTexturedModalRect(guiLeft + 61, guiTop + 34, 176, 0, k, 18);
	        this.drawTexturedModalRect(guiLeft + 97 + 18 - k, guiTop + 34, 194 + 18 - k, 0, k, 18);
	    }

        int l = getTemperatureScaled(17, 30, Math.max(31, tile.getField(1)));
        this.drawTexturedModalRect(guiLeft + 79, guiTop + 52 + (17 - l), 176, 18 + (17 - l), 18, l + 1);
	}

    /**
     * Draw the foreground layer for the GuiContainer (everything in front of the items)
     */
    @Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String s = tile.getDisplayName().getUnformattedText();
        this.fontRenderer.drawString(s, this.xSize / 2 - this.fontRenderer.getStringWidth(s) / 2, 6, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    private int getTemperatureScaled(int pixels, int min, int max)
    {
        int temp = MathHelper.clamp(tile.getField(2), min, max) - min;
        int interval = max - min;
        return temp != 0 && interval != 0 ? temp * pixels / interval : 0;
    }
    
	private int getProcessLeftScaled(int pixels) {
		int i = Config.capacitorChargerChargeTime * 10;
		int j = MathHelper.clamp(tile.getField(0), 0, i);
		return j * pixels / i;
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		super.drawScreen(mouseX, mouseY, partialTicks);
		renderTemperatureToolTip(mouseX, mouseY);
	}

    private void renderTemperatureToolTip(int x, int y)
    {
        if (x >= guiLeft + 79 && x < guiLeft + 97 && y >= guiTop + 52 && y < guiTop + 70)
        {
        	String tooltip = I18n.format(TooltipUtil.TEMPERATURE_INPUT, tile.getField(2));
            this.drawHoveringText(ImmutableList.of(tooltip), x, y, fontRenderer);
        }
    }

}
