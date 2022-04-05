package de.schafunschaf.voidtec.campaign.items.chests;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.ids.VT_Items;
import de.schafunschaf.voidtec.util.VoidTecUtils;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class TreasureChestXS extends BaseChestData {

    public TreasureChestXS(String name, String manufacturer, boolean isLocked) {
        super(VT_Items.STORAGE_CHEST_XS, null, name, manufacturer, 50, null, isLocked);
    }

    @Override
    protected void addDescription(TooltipMakerAPI tooltip) {
        String manufacturerName = isNull(getManufacturer()) ? "Unknown" : getManufacturer();
        FactionAPI faction = Global.getSector().getFaction(manufacturerName);
        Color manufacturerColor = VoidTecUtils.getManufacturerColor(manufacturerName);
        if (!isNull(faction)) {
            manufacturerName = Misc.ucFirst(faction.getDisplayNameWithArticleWithoutArticle());
        }

        tooltip.addPara("This chest has been branded by the %s.\n\n" +
                                "Opening one of these to find it filled with credits and goods looted from traders over the years wouldn't be an unexpected find",
                        0f, manufacturerColor, manufacturerName);
    }
}
