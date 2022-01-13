package de.schafunschaf.voidtec.campaign.items.augments;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.campaign.CargoTransferHandlerAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BaseSpecialItemPlugin;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.Settings;
import de.schafunschaf.voidtec.VT_Icons;
import de.schafunschaf.voidtec.VT_Strings;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.SlotCategory;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.augments.BaseAugment;
import de.schafunschaf.voidtec.util.ColorShifter;
import de.schafunschaf.voidtec.util.MalfunctionEffect;
import lombok.Getter;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
public class AugmentItemPlugin extends BaseSpecialItemPlugin {
    protected BaseAugment augment;
    private MalfunctionEffect malfunctionEffect;
    private ColorShifter colorShifter;

    @Override
    public void init(CargoStackAPI stack) {
        super.init(stack);
        augment = ((AugmentItemData) stack.getSpecialDataIfSpecial()).getAugment();
        float modifier = augment.getAugmentQuality().getModifier();
        float breathingLength = 300f;
        if (augment.getAugmentQuality() == AugmentQuality.UNIQUE) {
            colorShifter = new ColorShifter(null);
        } else if (!(augment.getAugmentQuality() == AugmentQuality.DESTROYED || augment.getAugmentQuality() == AugmentQuality.DOMAIN)) {
            float maxTimeAtFull = (1 / (3f - modifier)) * breathingLength * 3;
            int flickerChance = (int) ((2f - modifier) / (modifier * modifier) * 50);
            int maxNumFlickers = Math.max(Math.round(6 - modifier * 3), 1);

            malfunctionEffect = new MalfunctionEffect(breathingLength, maxTimeAtFull, flickerChance, maxNumFlickers, modifier);
        }
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;
        float smallPad = 3f;

        if (stackSource.equals(AugmentManagerIntel.STACK_SOURCE))
            tooltip.addTitle(String.format("%sx %s", ((int) stack.getSize()), getName()));
        else
            tooltip.addTitle(getName());

        createTechInfo(tooltip, largePad, smallPad);

        tooltip.addPara(augment.getDescription(), largePad);

        if (augment.getAugmentQuality() == AugmentQuality.DESTROYED) {
            tooltip.addSectionHeading("DAMAGED AUGMENT DETECTED", augment.getAugmentQuality().getColor(), Misc.scaleColor(augment.getAugmentQuality().getColor(), 0.5f), Alignment.MID, largePad);
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augment.getAugmentQuality().getColor(), 0f);
            return;
        }

        if (expanded) {
            tooltip.addSectionHeading("Primary Stat Modification Info", augment.getPrimarySlot().getColor(), Misc.scaleColor(augment.getPrimarySlot().getColor(), 0.5f), Alignment.MID, largePad);
            tooltip.addSpacer(smallPad);
            augment.generateStatDescription(tooltip, true, smallPad);

            if (!isNull(augment.getSecondaryStatMods()) && !augment.getSecondaryStatMods().isEmpty()) {
                StringBuilder secondarySlotStringBuilder = new StringBuilder("Compatible to: ");
                List<String> hlStrings = new ArrayList<>();
                hlStrings.add("Compatible to:");
                List<Color> hlColors = new ArrayList<>();
                hlColors.add(Misc.getGrayColor());

                for (Iterator<SlotCategory> iterator = augment.getSecondarySlots().iterator(); iterator.hasNext(); ) {
                    SlotCategory secondarySlot = iterator.next();
                    secondarySlotStringBuilder.append(secondarySlot.toString());
                    hlStrings.add(secondarySlot.toString());
                    hlColors.add(secondarySlot.getColor());
                    if (iterator.hasNext())
                        secondarySlotStringBuilder.append(", ");
                }

                tooltip.addSectionHeading("Secondary Stat Modification Info", augment.getPrimarySlot().getColor(), Misc.scaleColor(augment.getPrimarySlot().getColor(), 0.5f), Alignment.MID, largePad);
                tooltip.addSpacer(smallPad);
                augment.generateStatDescription(tooltip, false, smallPad);
                tooltip.addPara(secondarySlotStringBuilder.toString(), smallPad, hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]));
            }
        }
    }

    private void createTechInfo(TooltipMakerAPI tooltip, float largePad, float smallPad) {
        String manufacturerName = isNull(augment.getManufacturer()) ? "Unknown" : augment.getManufacturer();
        Color manufacturerColor = getManufacturerColor(manufacturerName);

        tooltip.addPara("Manufacturer: %s", largePad, Misc.getGrayColor(), manufacturerColor, manufacturerName);
        tooltip.addPara(String.format("Quality: %s", augment.getAugmentQuality().getName()), smallPad, Misc.getGrayColor(), augment.getAugmentQuality().getColor(), augment.getAugmentQuality().getName());
        tooltip.addPara("Type: %s", smallPad, Misc.getGrayColor(), augment.getPrimarySlot().getColor(), augment.getPrimarySlot().toString());
    }

    private Color getManufacturerColor(String manufacturerString) {
        FactionAPI faction = Global.getSector().getFaction(manufacturerString);
        Color manufacturerColor = isNull(faction) ? Global.getSettings().getDesignTypeColor(manufacturerString) : faction.getColor();

        return isNull(manufacturerColor) ? Misc.getGrayColor() : manufacturerColor;
    }

    @Override
    public boolean isTooltipExpandable() {
        return augment.getAugmentQuality() != AugmentQuality.DESTROYED;
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

    @Override
    public void render(float x, float y, float w, float h, float alphaMult, float glowMult, SpecialItemRendererAPI renderer) {
        SpriteAPI cover = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_COVER);
        SpriteAPI glow = Global.getSettings().getSprite(VT_Icons.AUGMENT_ITEM_ICON_GLOW);
        Color glowColor = augment.getPrimarySlot().getColor();

        if (Settings.iconFlicker)
            if (!isNull(colorShifter))
                glowColor = colorShifter.shiftColor(0.5f);
            else if (!isNull(malfunctionEffect))
                glowColor = malfunctionEffect.renderFlicker(augment.getPrimarySlot().getColor());

        if (augment.getAugmentQuality() == AugmentQuality.DESTROYED)
            glowColor = Misc.scaleColorOnly(augment.getAugmentQuality().getColor(), 0.3f);

        Color coverColor = augment.getAugmentQuality() == AugmentQuality.DESTROYED ? Color.LIGHT_GRAY : Color.WHITE;

        cover.setColor(coverColor);
        cover.renderAtCenter(0f, 0f);

        glow.setColor(glowColor);
        glow.renderAtCenter(0f, 0f);
    }
}
