package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.Fonts;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.ColorShifter;
import de.schafunschaf.voidtec.helper.MalfunctionEffect;
import de.schafunschaf.voidtec.helper.RainbowString;
import de.schafunschaf.voidtec.ids.VT_Icons;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import lombok.Getter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
public class AugmentItemPlugin extends BaseSpecialItemPlugin {

    public static void addTechInfo(TooltipMakerAPI tooltip, AugmentApplier augment, float padding, float paraPadding) {
        if (isNull(augment)) {
            return;
        }

        String manufacturerName = isNull(augment.getManufacturer()) ? "Unknown" : augment.getManufacturer();
        FactionAPI faction = Global.getSector().getFaction(manufacturerName.toLowerCase());
        Color manufacturerColor = VoidTecUtils.getManufacturerColor(manufacturerName);
        if (!isNull(faction)) {
            manufacturerName = Misc.ucFirst(faction.getDisplayNameWithArticleWithoutArticle());
        }

        String qualityDescription = String.format("Quality: %s", augment.getAugmentQuality().getName());
        List<Color> hlColors = new ArrayList<>();
        List<String> hlStrings = new ArrayList<>();
        hlColors.add(augment.getAugmentQuality().getColor());
        hlStrings.add(augment.getAugmentQuality().getName());

        boolean isDamaged = augment.getAugmentQuality() != augment.getInitialQuality()
                && augment.getInitialQuality() != AugmentQuality.DESTROYED;
        if (isDamaged) {
            qualityDescription += String.format(" (%s)", augment.getInitialQuality().getName());
            hlColors.add(augment.getInitialQuality().getColor());
            hlStrings.add(augment.getInitialQuality().getName());
        }

        String unique = "Unique";
        String uniqueString = augment.isUniqueMod() ? String.format(" (%s)", unique) : "";

        tooltip.setParaFontColor(Misc.getGrayColor());
        tooltip.addPara("Manufacturer: %s", padding, manufacturerColor, manufacturerName);
        tooltip.addPara(qualityDescription, paraPadding, hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]));
        tooltip.addPara("Slot: %s" + uniqueString, paraPadding, new Color[]{augment.getPrimarySlot().getColor(), Misc.getHighlightColor()},
                        augment.getPrimarySlot().getName(), unique);

        if (!augment.getSecondarySlots().isEmpty()) {
            StringBuilder secondarySlotStringBuilder = new StringBuilder("Compatible: ");
            List<String> secSlotStrings = new ArrayList<>();
            List<Color> secSlotColors = new ArrayList<>();

            for (Iterator<SlotCategory> iterator = augment.getSecondarySlots().iterator(); iterator.hasNext(); ) {
                SlotCategory secondarySlot = iterator.next();
                secondarySlotStringBuilder.append(secondarySlot.getName());
                secSlotStrings.add(secondarySlot.getName());
                secSlotColors.add(secondarySlot.getColor());
                if (iterator.hasNext()) {
                    secondarySlotStringBuilder.append(", ");
                }
            }

            tooltip.addPara(secondarySlotStringBuilder.toString(), paraPadding, secSlotColors.toArray(new Color[0]),
                            secSlotStrings.toArray(new String[0]));
        }

        tooltip.setParaFontColor(Misc.getTextColor());
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;
        float smallPad = 3f;

        AugmentApplier augment = getAugmentItemData().getAugment();
        AugmentQuality augmentQuality = augment.getAugmentQuality();

        if (augment.getName().toLowerCase().contains("rainbow")) {
            RainbowString rainbowString = new RainbowString(augment.getName(), Color.RED, 20);
            tooltip.setParaFont(Fonts.ORBITRON_12);
            tooltip.addPara(rainbowString.getConvertedString() + "- " + super.getName(), 0f, rainbowString.getHlColors(),
                            rainbowString.getHlStrings());
            tooltip.setParaFont(Fonts.DEFAULT_SMALL);
        } else {
            tooltip.addTitle(getName());
        }

        addTechInfo(tooltip, augment, largePad, smallPad);
        if (expanded && augment.isUniqueMod()) {
            tooltip.addPara("There can only be one unique augment installed in each category.", Misc.getGrayColor(), smallPad);
        }

        tooltip.addPara(augment.getDescription().getDisplayString(), largePad, Misc.getHighlightColor(),
                        augment.getDescription().getHighlights());
        if (augmentQuality == AugmentQuality.DESTROYED) {
            tooltip.addSectionHeading("NON-FUNCTIONAL AUGMENT DETECTED", augmentQuality.getColor(),
                                      Misc.scaleColor(augmentQuality.getColor(), 0.4f), Alignment.MID, largePad);
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), smallPad);
        } else if (augmentQuality != augment.getInitialQuality()) {
            tooltip.addSectionHeading("DAMAGED AUGMENT DETECTED", Misc.getHighlightColor(), Misc.scaleColor(Misc.getGrayColor(), 0.4f),
                                      Alignment.MID,
                                      largePad);
            tooltip.addPara(VT_Strings.VT_DAMAGED_AUGMENT_DESC, Misc.getGrayColor(), smallPad);
        }

        if (expanded) {
            if (!stackSource.equals(AugmentManagerIntel.STACK_SOURCE)) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            }

            if (!(augmentQuality == AugmentQuality.DESTROYED) && !isNull(augment.getPrimaryStatMods()) && !augment.getPrimaryStatMods()
                                                                                                                  .isEmpty()) {
                tooltip.addSectionHeading("Primary Stat Modification Info", augment.getPrimarySlot().getColor().brighter(),
                                          Misc.scaleColor(augment.getPrimarySlot().getColor(), 0.5f), Alignment.MID, largePad);
                tooltip.addSpacer(smallPad);
                augment.generateStatDescription(tooltip, smallPad, true, null);
            }

            if (!(augmentQuality == AugmentQuality.DESTROYED) && !isNull(augment.getSecondaryStatMods()) && !augment.getSecondaryStatMods()
                                                                                                                    .isEmpty()) {
                tooltip.addSectionHeading("Secondary Stat Modification Info", augment.getPrimarySlot().getColor().brighter(),
                                          Misc.scaleColor(augment.getPrimarySlot().getColor(), 0.5f), Alignment.MID, largePad);
                tooltip.addSpacer(smallPad);

                augment.generateStatDescription(tooltip, smallPad, false, null);
            }
        } else {
            if (!(transferHandler.getSubmarketTradedWith()
                                 .getSpecId()
                                 .equals(Submarkets.SUBMARKET_STORAGE) || transferHandler.getSubmarketTradedWith()
                                                                                         .getSpecId()
                                                                                         .equals(Submarkets.LOCAL_RESOURCES))) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            }

            if (augmentQuality != AugmentQuality.DESTROYED
                    && (!augment.getPrimaryStatMods().isEmpty() || !augment.getSecondaryStatMods().isEmpty())) {
                tooltip.addPara("Expand to see detailed modification info.", Misc.getGrayColor(), largePad);
            }
        }
    }

    @Override
    public boolean isTooltipExpandable() {
        AugmentApplier augment = getAugmentItemData().getAugment();
        return (augment.getAugmentQuality() != AugmentQuality.DESTROYED
                && (!augment.getPrimaryStatMods().isEmpty() || !augment.getSecondaryStatMods().isEmpty()));
    }

    @Override
    public String getName() {
        return getAugmentItemData().getAugment().getName() + " - " + super.getName();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        float modifier = getAugmentItemData().getAugment().getAugmentQuality().getModifier();
        int modifiedPrice = Math.round(spec.getBasePrice() * modifier * modifier);
        return Math.max(modifiedPrice, 1);
    }

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        AugmentApplier augment = getAugmentItemData().getAugment();
        ColorShifter colorShifter = getAugmentItemData().getColorShifter();
        MalfunctionEffect malfunctionEffect = getAugmentItemData().getMalfunctionEffect();

        SpriteAPI cover = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_COVER);
        SpriteAPI glow = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_GLOW);
        Color glowColor = augment.getPrimarySlot().getColor();

        if (VT_Settings.iconFlicker) {
            if (!isNull(colorShifter)) {
                glowColor = colorShifter.shiftColor(0.5f);
            } else if (!isNull(malfunctionEffect)) {
                glowColor = malfunctionEffect.renderFlicker(augment.getPrimarySlot().getColor());
            }
        }

        if (augment.getAugmentQuality() == AugmentQuality.DESTROYED) {
            glowColor = Misc.scaleColorOnly(augment.getAugmentQuality().getColor(), 0.3f);
        }

        Color coverColor = augment.getAugmentQuality() == AugmentQuality.DESTROYED ? Color.LIGHT_GRAY : Color.WHITE;

        cover.setColor(coverColor);
        cover.renderAtCenter(0f, 0f);

        glow.setColor(glowColor);
        glow.renderAtCenter(0f, 0f);
    }

    @Override
    public String getDesignType() {
        return super.getDesignType();
    }

    public AugmentItemData getAugmentItemData() {
        return (AugmentItemData) stack.getSpecialDataIfSpecial();
    }
}
