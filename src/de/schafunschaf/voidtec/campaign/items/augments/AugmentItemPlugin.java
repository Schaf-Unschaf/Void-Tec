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
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

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

        StringBuilder manufacturerString = new StringBuilder();
        List<String> manufacturerNames = new ArrayList<>();
        List<Color> manufacturerHLColors = new ArrayList<>();

        if (augment.getManufacturer().isEmpty()) {
            manufacturerString.append("Unknown");
            manufacturerNames.add("Unknown");
            manufacturerHLColors.add(Misc.getTextColor());
        } else {
            for (Iterator<String> iterator = augment.getManufacturer().iterator(); iterator.hasNext(); ) {
                String manufacturer = iterator.next();
                FactionAPI faction = Global.getSector().getFaction(manufacturer.toLowerCase());
                Color manufacturerColor = VoidTecUtils.getManufacturerColor(manufacturer);
                if (!isNull(faction)) {
                    manufacturer = Misc.ucFirst(faction.getDisplayNameWithArticleWithoutArticle());
                }

                manufacturerString.append(manufacturer);
                manufacturerNames.add(manufacturer);
                manufacturerHLColors.add(manufacturerColor);
                if (iterator.hasNext()) {
                    manufacturerString.append(", ");
                }
            }
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
        tooltip.addPara(String.format("Manufacturer: %s", manufacturerString), padding, manufacturerHLColors.toArray(new Color[0]),
                        manufacturerNames.toArray(new String[0]));
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
    public void performRightClickAction() {
        getAugmentItemData().getAugment().runRightClickAction();
    }

    @Override
    public boolean hasRightClickAction() {
        return !isNull(getAugmentItemData().getAugment().getRightClickAction());
    }

    @Override
    public boolean shouldRemoveOnRightClickAction() {
        return false;
    }

    @Override
    public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, CargoTransferHandlerAPI transferHandler, Object stackSource) {
        float largePad = 10f;
        float smallPad = 3f;

        AugmentApplier augment = getAugmentItemData().getAugment();
        AugmentQuality augmentQuality = augment.getAugmentQuality();

        if (augment.isDestroyed()) {
            tooltip.addTitle("Destroyed Augment");
            tooltip.addSectionHeading("NON-FUNCTIONAL AUGMENT DETECTED", augmentQuality.getColor(),
                                      Misc.scaleColor(augmentQuality.getColor(), 0.4f), Alignment.MID, largePad);
            tooltip.addPara(VT_Strings.VT_DESTROYED_AUGMENT_DESC, augmentQuality.getColor(), smallPad);

            addCostLabel(tooltip, largePad, transferHandler, stackSource);

            return;
        }

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

        Color highlightColor = Misc.getHighlightColor();
        if (!isNull(augment.getRightClickAction()) && augment.getRightClickAction().getActionObject() instanceof Color) {
            highlightColor = ((Color) augment.getRightClickAction().getActionObject());
        }
        tooltip.addPara(augment.getDescription().getDisplayString(), largePad, highlightColor,
                        augment.getDescription().getHighlights());

        if (augmentQuality != augment.getInitialQuality()) {
            tooltip.addSectionHeading("DAMAGED AUGMENT DETECTED", Misc.getHighlightColor(), Misc.scaleColor(Misc.getGrayColor(), 0.4f),
                                      Alignment.MID,
                                      largePad);
            tooltip.addPara(VT_Strings.VT_DAMAGED_AUGMENT_DESC, Misc.getGrayColor(), smallPad);
        }

        if (expanded || VT_Settings.alwaysExpandTooltips) {
            if (!stackSource.equals(AugmentManagerIntel.STACK_SOURCE)) {
                addCostLabel(tooltip, largePad, transferHandler, stackSource);
            }

            if (!isNull(augment.getPrimaryStatMods()) && !augment.getPrimaryStatMods().isEmpty()) {
                tooltip.addSectionHeading("", augment.getPrimarySlot().getColor().brighter(),
                                          Misc.scaleColor(augment.getPrimarySlot().getColor(), 0.5f), Alignment.MID, largePad);
                tooltip.addPara("%s Slot", -17f, augment.getPrimarySlot().getColor(), augment.getPrimarySlot().getName())
                       .setAlignment(Alignment.MID);
                tooltip.addSpacer(smallPad);
                augment.generateStatDescription(tooltip, smallPad, true, null);
            }

            if (!isNull(augment.getSecondaryStatMods()) && !augment.getSecondaryStatMods()
                                                                   .isEmpty()) {
                StringBuilder secondaryText = new StringBuilder();
                List<String> hlStrings = new ArrayList<>();
                List<Color> hlColors = new ArrayList<>();

                for (Iterator<SlotCategory> iterator = augment.getSecondarySlots().iterator(); iterator.hasNext(); ) {
                    SlotCategory secondarySlot = iterator.next();
                    secondaryText.append(secondarySlot.getName());
                    if (iterator.hasNext()) {
                        secondaryText.append(" | ");
                    }

                    hlStrings.add(secondarySlot.getName());
                    hlColors.add(secondarySlot.getColor());
                }

                secondaryText.append(" Slot");

                tooltip.addSectionHeading("", augment.getPrimarySlot().getColor().brighter(),
                                          Misc.scaleColor(Misc.getGrayColor(), 0.5f), Alignment.MID, largePad);
                tooltip.addPara(secondaryText.toString(), -17f, hlColors.toArray(new Color[0]), hlStrings.toArray(new String[0]))
                       .setAlignment(Alignment.MID);
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

            if (!augment.getPrimaryStatMods().isEmpty() || !augment.getSecondaryStatMods().isEmpty()) {
                tooltip.addPara("Expand to see detailed modification info.", Misc.getGrayColor(), largePad);
            }
        }

        if (hasRightClickAction() && !stackSource.equals(AugmentManagerIntel.STACK_SOURCE)) {
            tooltip.addPara("Right-Click to open customisation dialog", Misc.getPositiveHighlightColor(), largePad);
        }
    }

    @Override
    public boolean isTooltipExpandable() {
        AugmentApplier augment = getAugmentItemData().getAugment();
        return (!augment.isDestroyed() && (!augment.getPrimaryStatMods().isEmpty() || !augment.getSecondaryStatMods()
                                                                                              .isEmpty())) && !VT_Settings.alwaysExpandTooltips;
    }

    @Override
    public String getName() {
        return getAugmentItemData().getAugment().getName() + " - " + super.getName();
    }

    @Override
    public int getPrice(MarketAPI market, SubmarketAPI submarket) {
        float modifier = getAugmentItemData().getAugment().getAugmentQuality().getModifier();
        int modifiedPrice = Math.round(spec.getBasePrice() * modifier * modifier);
        return Math.max(modifiedPrice, 400);
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

        Color coverColor = augment.isDestroyed() ? Color.DARK_GRAY : Color.WHITE;
        glowColor = augment.isDestroyed() ? Misc.scaleColorOnly(AugmentQuality.DESTROYED.getColor(), 0.5f) : glowColor;

        cover.setColor(coverColor);
        cover.renderAtCenter(0f, 0f);

        glow.setColor(glowColor);
        glow.renderAtCenter(0f, 0f);

        if (augment.isDestroyed()) {
            SpriteAPI destroyed = Global.getSettings().getSprite(VT_Icons.LOCKED_SLOT_ICON);
            destroyed.setSize(50, 50);
            destroyed.renderAtCenter(0, 0);
        } else if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
            addSlotIndicator(x, y);
            addQualityIndicator(x, y, w, h);
        }
    }

    @Override
    public String getDesignType() {
        return super.getDesignType();
    }

    public AugmentItemData getAugmentItemData() {
        return (AugmentItemData) stack.getSpecialDataIfSpecial();
    }

    private void addSlotIndicator(float x, float y) {
        AugmentApplier augment = getAugmentItemData().getAugment();
        List<SlotCategory> allSlots = new ArrayList<>();
        float xMargin = 5f;
        float yMargin = 5f;
        float lengthPrimary = 30f;
        float lengthSecondary = 50f / (augment.getSecondarySlots().size() + 1);
        float height = 3f;
        float padding = 0f;

        allSlots.add(augment.getPrimarySlot());
        allSlots.addAll(augment.getSecondarySlots());

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        for (SlotCategory slot : allSlots) {
            Color color = slot.getColor();
            float indicatorLength = augment.getPrimarySlot() == slot ? lengthPrimary : lengthSecondary;

            GL11.glColor4ub((byte) color.getRed(),
                            (byte) color.getGreen(),
                            (byte) color.getBlue(),
                            (byte) color.getAlpha());

            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(x + xMargin + padding, y + yMargin); // BL
                GL11.glVertex2f(x + xMargin + padding, y + yMargin + height); // TL
                GL11.glVertex2f(x + indicatorLength + padding, y + yMargin + height); // TR
                GL11.glVertex2f(x + indicatorLength + padding, y + yMargin); // BR
            }
            GL11.glEnd();

            xMargin = 1f;
            padding += indicatorLength;
        }
    }

    private void addQualityIndicator(float x, float y, float w, float h) {
        AugmentApplier augment = getAugmentItemData().getAugment();
        float xMargin = 5f;
        float yMargin = 5f;
        float length = 10f;
        float height = 10f;

        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        if (augment.getAugmentQuality() != augment.getInitialQuality()) {
            Color color = augment.getAugmentQuality().getColor();
            Color color2 = augment.getInitialQuality().getColor();

            GL11.glColor4ub((byte) color.getRed(),
                            (byte) color.getGreen(),
                            (byte) color.getBlue(),
                            (byte) color.getAlpha());

            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(x + xMargin - 0.5f, y + h - yMargin - height); // BL
                GL11.glVertex2f(x + xMargin - 0.5f, y + h - yMargin); // TL
                GL11.glVertex2f(x + xMargin - 0.5f + length / 2, y + h - yMargin); // TR
                GL11.glVertex2f(x + xMargin - 0.5f + length / 2, y + h - yMargin - height); // BR
            }
            GL11.glEnd();

            GL11.glColor4ub((byte) color2.getRed(),
                            (byte) color2.getGreen(),
                            (byte) color2.getBlue(),
                            (byte) color2.getAlpha());

            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(x + xMargin + 0.5f + length / 2, y + h - yMargin - height); // BL
                GL11.glVertex2f(x + xMargin + 0.5f + length / 2, y + h - yMargin); // TL
                GL11.glVertex2f(x + xMargin + 0.5f + length, y + h - yMargin); // TR
                GL11.glVertex2f(x + xMargin + 0.5f + length, y + h - yMargin - height); // BR
            }
            GL11.glEnd();
        } else {
            Color color = augment.getAugmentQuality().getColor();

            GL11.glColor4ub((byte) color.getRed(),
                            (byte) color.getGreen(),
                            (byte) color.getBlue(),
                            (byte) color.getAlpha());

            GL11.glBegin(GL11.GL_QUADS);
            {
                GL11.glVertex2f(x + xMargin, y + h - yMargin - height); // BL
                GL11.glVertex2f(x + xMargin, y + h - yMargin); // TL
                GL11.glVertex2f(x + xMargin + length, y + h - yMargin); // TR
                GL11.glVertex2f(x + xMargin + length, y + h - yMargin - height); // BR
            }
            GL11.glEnd();
        }
    }
}
