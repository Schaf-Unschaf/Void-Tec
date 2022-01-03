package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.effects.engineeringsuite.augments.BaseAugment;
import lombok.Getter;

import java.awt.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
public class AugmentItemPlugin extends BaseSpecialItemPlugin {
    protected BaseAugment augment;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        augment = ((AugmentItemData) stack.getSpecialDataIfSpecial()).getAugment();
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;
        float smallPad = 3f;
        String manufacturerName = isNull(augment.getManufacturer()) ? "Unknown" : augment.getManufacturer();
        Color manufacturerColor = isNull(Global.getSector().getFaction(manufacturerName)) ? Misc.getGrayColor() : Global.getSector().getFaction(manufacturerName).getColor();

        tooltip.addTitle(getName());
        tooltip.addPara("Manufacturer: %s", largePad, Misc.getGrayColor(), manufacturerColor, manufacturerName);

        tooltip.addPara(augment.getUpgradeQuality().getName(), augment.getUpgradeQuality().getColor(), smallPad);
        tooltip.addPara(augment.getDescription(), largePad);
    }

    @Override
    public boolean isTooltipExpandable() {
        return true;
    }

    @Override
    public String getName() {
        return super.getName() + " - " + augment.getName();
    }

    @Override
    public String getDesignType() {
        return super.getDesignType();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        return super.getPrice(market, submarket);
    }

    public String getImage() {
        return augment.getPrimarySlot().getIcon();
    }

    //    @Override
//    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
//        super.render(x, y, w, h, alphaMult, glowMult, renderer);
//    }
}
