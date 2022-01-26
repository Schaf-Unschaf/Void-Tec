package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.campaign.scripts.VT_DialogHelperOpenStorage;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.Getter;
import lombok.SneakyThrows;

import java.awt.Color;
import java.awt.Robot;
import java.awt.event.KeyEvent;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
public class AugmentChestPlugin extends BaseSpecialItemPlugin {

    @SneakyThrows
    @Override
    public void performRightClickAction() {
        Robot robot = new Robot();
        Global.getSector().addTransientScript(new VT_DialogHelperOpenStorage(this));
        robot.keyPress(KeyEvent.VK_ESCAPE);
        robot.keyRelease(KeyEvent.VK_ESCAPE);
    }

    @Override
    public boolean hasRightClickAction() {
        return true;
    }

    @Override
    public boolean shouldRemoveOnRightClickAction() {
        return false;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;

        AugmentChestData augmentChestData = getAugmentChestData();
        int currentSize = augmentChestData.getCurrentSize();
        int maxSize = augmentChestData.getMaxSize();

        String manufacturerName = "VoidTec";
        Color manufacturerColor = VoidTecUtils.getManufacturerColor(manufacturerName);
        Color spaceHLColor = currentSize < maxSize ? Misc.getHighlightColor() : Misc.getNegativeHighlightColor();

        tooltip.addTitle(getName(), Misc.getHighlightColor());
        tooltip.addPara("Manufacturer: %s", largePad, Misc.getGrayColor(), manufacturerColor, manufacturerName);
        String hlString = maxSize + " Augment Chips";
        tooltip.addPara(String.format(
                "This handy little chest was designed for any eager captain or collector in the sector and stores up to %s inside.",
                hlString), largePad, Misc.getHighlightColor(), hlString);

        if (!isNull(stackSource)) {
            if (expanded && (transferHandler.getSubmarketTradedWith()
                                            .getSpecId()
                                            .equals(Submarkets.SUBMARKET_STORAGE) || transferHandler.getSubmarketTradedWith()
                                                                                                    .getSpecId()
                                                                                                    .equals(Submarkets.LOCAL_RESOURCES))) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            } else if (!(transferHandler.getSubmarketTradedWith()
                                        .getSpecId()
                                        .equals(Submarkets.SUBMARKET_STORAGE) || transferHandler.getSubmarketTradedWith()
                                                                                                .getSpecId()
                                                                                                .equals(Submarkets.LOCAL_RESOURCES))) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            }

            tooltip.addPara(String.format("Space used: %s/%s", currentSize, maxSize), largePad, Misc.getGrayColor(), spaceHLColor,
                            String.valueOf(currentSize), String.valueOf(maxSize));
            tooltip.addPara("Right-click to open", Misc.getPositiveHighlightColor(), largePad);
        }
    }

    @Override
    public boolean isTooltipExpandable() {
        return true;
    }

    @Override
    public String getName() {
        return super.getName();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        return super.getPrice(market, submarket);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        AugmentChestData augmentChestData = getAugmentChestData();
        ColorShifter colorShifter = augmentChestData.getColorShifter();

        SpriteAPI glow = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_CHEST_ICON_GLOW);

        Color glowColor = Misc.scaleColor(colorShifter.shiftColor(0.5f), 0.5f);

        glow.setColor(glowColor);
        glow.setAdditiveBlend();
        glow.renderAtCenter(0f, 0f);
    }

    @Override
    public String getDesignType() {
        return super.getDesignType();
    }

    public void addToSize(int num) {
        AugmentChestData augmentChestData = getAugmentChestData();
        int currentSize = augmentChestData.getCurrentSize();
        augmentChestData.setCurrentSize(currentSize + num);
    }

    public AugmentChestData getAugmentChestData() {
        return (AugmentChestData) stack.getSpecialDataIfSpecial();
    }
}
