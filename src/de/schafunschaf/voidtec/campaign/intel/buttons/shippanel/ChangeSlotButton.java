package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.AugmentManagerIntel;
import de.schafunschaf.voidtec.campaign.intel.buttons.ColorShiftingPlugin;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@RequiredArgsConstructor
public class ChangeSlotButton extends DefaultButton {

    private final AugmentSlot augmentSlot;
    private SlotCategory selectedCategory = null;

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        if (!isNull(selectedCategory) && hasEnoughSP()) {
            augmentSlot.setSlotCategory(selectedCategory);

            MutableCharacterStatsAPI playerStats = Global.getSector().getPlayerStats();
            playerStats.setStoryPoints(playerStats.getStoryPoints() - 1);

            AugmentManagerIntel.setActiveCategoryFilter(selectedCategory);
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.setForceProcessInput(true);

        tooltip.addPara("Change the currently selected %s-Slot?", 0f, augmentSlot.getSlotCategory().getColor(),
                        augmentSlot.getSlotCategory().getName());

        tooltip.addPara("This will modification will cost %s.", 6f, Misc.getStoryOptionColor(), "1 Story Point");

        final float size = 30f;
        final float borderSize = 1f;
        final float borderMargin = 1f;

        float panelWidth = size * SlotCategory.getGeneralCategories().size() + 5 * SlotCategory.getGeneralCategories().size();
        CustomPanelAPI selectionPanel = Global.getSettings().createCustom(panelWidth, size, null);
        TooltipMakerAPI selectionUIElement = selectionPanel.createUIElement(panelWidth, size, false);

        selectionUIElement.setParaFont(Fonts.INSIGNIA_LARGE);
        selectionUIElement.addPara("Select new slot type:", 10f);
        UIComponentAPI paraPrev = selectionUIElement.getPrev();

        UIComponentAPI prevElement = null;
        for (final SlotCategory category : SlotCategory.getGeneralCategories()) {
            if (category == augmentSlot.getSlotCategory()) {
                continue;
            }

            final String categoryName = category.getName();

            CustomPanelAPI buttonPanel = Global.getSettings().createCustom(size, size, null);
            final TooltipMakerAPI buttonUIElement = buttonPanel.createUIElement(size, size, false);

            UIUtils.addBox(buttonUIElement, "", null, null, size, size, borderSize, borderMargin, null,
                           category.getColor(), Misc.scaleColorOnly(category.getColor(), 0.3f),
                           new CustomUIPanelPlugin() {
                               private PositionAPI p;
                               private boolean isChecked = false;

                               @Override
                               public void positionChanged(PositionAPI position) {
                                   p = position;
                               }

                               @Override
                               public void renderBelow(float alphaMult) {

                               }

                               @Override
                               public void render(float alphaMult) {
                                   if (p == null || !isChecked) {
                                       return;
                                   }

                                   float x = p.getX();
                                   float y = p.getY();
                                   float padding = borderSize + borderMargin;
                                   float hlSize = size - padding * 2;
                                   Color color = category.getColor();

                                   GL11.glDisable(GL11.GL_TEXTURE_2D);
                                   GL11.glEnable(GL11.GL_BLEND);
                                   GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                                   GL11.glColor4ub((byte) color.getRed(),
                                                   (byte) color.getGreen(),
                                                   (byte) color.getBlue(),
                                                   (byte) (color.getAlpha()));

                                   GL11.glBegin(GL11.GL_QUADS);
                                   {
                                       GL11.glVertex2f(x + padding, y + padding);
                                       GL11.glVertex2f(x + padding, y + padding + hlSize);
                                       GL11.glVertex2f(x + padding + hlSize, y + padding + hlSize);
                                       GL11.glVertex2f(x + padding + hlSize, y + padding);
                                   }
                                   GL11.glEnd();
                               }

                               @Override
                               public void advance(float amount) {

                               }

                               @Override
                               public void processInput(List<InputEventAPI> events) {
                                   if (selectedCategory != category) {
                                       isChecked = false;
                                   }

                                   if (p == null) {
                                       return;
                                   }

                                   for (InputEventAPI event : events) {
                                       if (event.isConsumed()) {
                                           continue;
                                       }

                                       if (event.isLMBUpEvent()) {
                                           if (p.containsEvent(event)) {
                                               selectedCategory = category;
                                               isChecked = true;
                                               event.consume();
                                           }
                                       }
                                   }
                               }
                           });
            buttonUIElement.addTooltipToPrevious(new BaseTooltipCreator() {

                @Override
                public float getTooltipWidth(Object tooltipParam) {
                    return buttonUIElement.computeStringWidth(categoryName);
                }

                @Override
                public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                    tooltip.addPara(categoryName, category.getColor(), 0f);
                }
            }, TooltipMakerAPI.TooltipLocation.BELOW);

            buttonPanel.addUIElement(buttonUIElement).inTL(0f, 0);

            if (!isNull(prevElement)) {
                selectionUIElement.addCustom(buttonPanel, 0f).getPosition().rightOfTop(prevElement, 5f);
            } else {
                selectionUIElement.addCustom(buttonPanel, 0f).getPosition().rightOfMid(paraPrev, -35f);
            }

            prevElement = selectionUIElement.getPrev();
        }

        selectionPanel.addUIElement(selectionUIElement).inTL(-5f, 0);
        tooltip.addCustom(selectionPanel, 6f);
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Confirm";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        return "Change";
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        Color color = isNull(augmentSlot) ? Color.RED : augmentSlot.getSlotCategory().getColor();

        float padding = 2f;
        tooltip.setAreaCheckboxFont(Fonts.VICTOR_10);
        ColorShiftingPlugin plugin = new ColorShiftingPlugin(padding, padding, width - padding * 2, height - padding * 2,
                                                             augmentSlot.getSlotCategory().getColor());
        CustomPanelAPI customPanelAPI = Global.getSettings().createCustom(width, height, plugin);
        TooltipMakerAPI uiElement = customPanelAPI.createUIElement(width, height, false);
        customPanelAPI.addUIElement(uiElement).inTL(0f, 0f);
        tooltip.addCustom(customPanelAPI, 0f).getPosition().setYAlignOffset(3f).setXAlignOffset(4f);

        ButtonAPI button = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, Misc.getTextColor(), color, color,
                                                         new ChangeSlotButton(augmentSlot));
        button.getPosition().setYAlignOffset(height).setXAlignOffset(0f);
        tooltip.setAreaCheckboxFont(Fonts.DEFAULT_SMALL);

        addTooltip(tooltip);
        button.setEnabled(hasEnoughSP());

        return button;
    }

    @Override
    public void addTooltip(final TooltipMakerAPI tooltip) {
        tooltip.addTooltipToPrevious(new BaseTooltipCreator() {
            final String text = "Change the selected Augment Slot to a different one";
            final String cost = hasEnoughSP() ? "Cost: 1 SP" : "Not enough SP";

            @Override
            public float getTooltipWidth(Object tooltipParam) {
                return tooltip.computeStringWidth(text);
            }

            @Override
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                tooltip.addPara(text, 0f);
                tooltip.addPara(cost, 3f, Misc.getGrayColor(), Misc.getStoryOptionColor(), "1", "SP");
            }
        }, TooltipMakerAPI.TooltipLocation.BELOW);
    }

    private boolean hasEnoughSP() {
        MutableCharacterStatsAPI playerStats = Global.getSector().getPlayerStats();
        int storyPoints = playerStats.getStoryPoints();
        return storyPoints > 0;
    }
}
