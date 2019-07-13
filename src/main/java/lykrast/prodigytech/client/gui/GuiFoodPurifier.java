package lykrast.prodigytech.client.gui;

import lykrast.prodigytech.common.gui.ContainerFoodPurifier;
import lykrast.prodigytech.common.tileentity.TileFoodPurifier;
import lykrast.prodigytech.common.util.Config;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.math.MathHelper;

public class GuiFoodPurifier extends GuiHotAirMachineSimple {
	public GuiFoodPurifier(InventoryPlayer playerInv, TileFoodPurifier tile) {
		super(playerInv, tile, new ContainerFoodPurifier(playerInv, tile), 80);
	}

	@Override
	protected int getProcessLeftScaled(int pixels)
    {
        int i = tile.getField(1);

        if (i == 0) i = Config.foodPurifierBaseTime * 10;
        
        int j = MathHelper.clamp(i - tile.getField(0), 0, i);

        return j * pixels / i;
    }

}
