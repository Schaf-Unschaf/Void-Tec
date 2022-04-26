package de.schafunschaf.voidtec.campaign.intel.buttons.shippanel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomUIPanelPlugin;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.combat.hullmods.VoidTecEngineeringSuite;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.MathUtils;
import de.schafunschaf.voidtec.util.ShipUtils;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.ButtonUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static de.schafunschaf.voidtec.ids.VT_Settings.*;

public class InstallHullmodButton extends DefaultButton {

    private final FleetMemberAPI fleetMember;
    private final List<String> sModsToRemove;
    private final float installCost;
    private String selectedSMod = "";

    public InstallHullmodButton(FleetMemberAPI fleetMember) {
        this.fleetMember = fleetMember;
        this.sModsToRemove = getRemovableSMods(fleetMember.getVariant());
        this.installCost = MathUtils.roundWholeNumber(fleetMember.getHullSpec().getBaseValue() * installBaseValueFraction, 2);
    }

    @Override
    public void buttonPressConfirmed(IntelUIAPI ui) {
        ShipVariantAPI memberVariant = fleetMember.getVariant();
        MutableCharacterStatsAPI playerStats = Global.getSector().getPlayerStats();
        long bonusXP = VoidTecUtils.getBonusXPForInstalling(fleetMember);
        playerStats.addBonusXP(bonusXP, false, null, false);

        Set<String> permaMods = memberVariant.getPermaMods();
        String[] sMods = permaMods.toArray(new String[0]);
        for (int i = sMods.length; i > 0; i--) {
            memberVariant.removePermaMod(sMods[i - 1]);
        }

        if (!selectedSMod.isEmpty()) {
            memberVariant.addPermaMod(selectedSMod, true);
        }

        memberVariant.addPermaMod(VoidTecEngineeringSuite.HULL_MOD_ID);

        if (hullmodInstallationWithSP) {
            playerStats.addStoryPoints(-installCostSP);
        } else {
            Global.getSector().getPlayerFleet().getCargo().getCredits().subtract(installCost);
        }
    }

    @Override
    public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
        tooltip.setForceProcessInput(true);
        String creditString = Misc.getDGSCredits(installCost);
        Color hlColor = Misc.getHighlightColor();
        int bonusPercent = VoidTecUtils.getBonusXPPercentage(VoidTecUtils.getBonusXPForInstalling(fleetMember));
        if (hullmodInstallationWithSP) {
            creditString = installCostSP + " Story " + FormattingTools.singularOrPlural(installCostSP, "Point");
            hlColor = Misc.getStoryOptionColor();
        }

        String shipClass = ShipUtils.convertSizeToString(fleetMember.getHullSpec().getHullSize());
        tooltip.addPara("Do you want to install the VoidTec Engineering Suite on your ship?", 0f);
        tooltip.addPara(String.format("This will cost %s for a %s-sized vessel.", creditString, shipClass), 6f, hlColor,
                        creditString);

        if (!sModsToRemove.isEmpty()) {
            tooltip.addPara("You can choose to keep one of the following SMods:", 10f);

            for (final String sMod : sModsToRemove) {
                HullModSpecAPI modSpec = Global.getSettings().getHullModSpec(sMod);
                final String displayName = modSpec.getDisplayName();
                final SpriteAPI sprite = Global.getSettings().getSprite(modSpec.getSpriteName());

                if (selectedSMod.isEmpty()) {
                    selectedSMod = sMod;
                }

                final float size = 20f;
                final float borderSize = 1f;
                sprite.setSize(size - borderSize * 2, size - borderSize * 2);
                CustomPanelAPI buttonPanelAPI = Global.getSettings().createCustom(500, size, null);

                TooltipMakerAPI uiElement = buttonPanelAPI.createUIElement(500, size, false);
                UIComponentAPI box = UIUtils.addBox(uiElement, "", null, null, size, size, borderSize, 0, null,
                                                    Misc.getStoryDarkColor(), Color.BLACK,
                                                    new CustomUIPanelPlugin() {
                                                        private PositionAPI p;
                                                        private boolean isChecked = sMod.equals(selectedSMod);

                                                        @Override
                                                        public void positionChanged(PositionAPI position) {
                                                            p = position;
                                                        }

                                                        @Override
                                                        public void renderBelow(float alphaMult) {

                                                        }

                                                        @Override
                                                        public void render(float alphaMult) {
                                                            if (p == null) {
                                                                return;
                                                            }

                                                            if (!isChecked) {
                                                                sprite.setAlphaMult(0.3f);
                                                            } else {
                                                                sprite.setAlphaMult(1f);
                                                            }

                                                            float x = p.getX();
                                                            float y = p.getY();

                                                            if (isChecked) {
                                                                Color color = Misc.getStoryOptionColor();

                                                                GL11.glDisable(GL11.GL_TEXTURE_2D);
                                                                GL11.glEnable(GL11.GL_BLEND);
                                                                GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

                                                                GL11.glColor4ub((byte) color.getRed(),
                                                                                (byte) color.getGreen(),
                                                                                (byte) color.getBlue(),
                                                                                (byte) (color.getAlpha()));

                                                                GL11.glBegin(GL11.GL_QUADS);
                                                                {
                                                                    GL11.glVertex2f(x, y);
                                                                    GL11.glVertex2f(x, y + size);
                                                                    GL11.glVertex2f(x + size, y + size);
                                                                    GL11.glVertex2f(x + size, y);
                                                                }
                                                                GL11.glEnd();
                                                            }

                                                            sprite.render(x + borderSize, y + borderSize);
                                                        }

                                                        @Override
                                                        public void advance(float amount) {

                                                        }

                                                        @Override
                                                        public void processInput(List<InputEventAPI> events) {
                                                            if (!sMod.equals(selectedSMod)) {
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
                                                                        selectedSMod = sMod;
                                                                        isChecked = true;
                                                                        event.consume();
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    });

                uiElement.setParaFont(Fonts.INSIGNIA_LARGE);
                uiElement.addPara(uiElement.shortenString(displayName, 450), Misc.getStoryOptionColor(), 0f)
                         .getPosition().rightOfBottom(box, 10f);

                buttonPanelAPI.addUIElement(uiElement).inTL(10f, 0);
                tooltip.addCustom(buttonPanelAPI, 6f);
            }

            if (sModsToRemove.size() > 1) {
                String bonusString = String.format("%s%% Bonus XP", bonusPercent);
                tooltip.addPara("You will gain an additional %s as compensation for the removed mods.", 10f, Misc.getStoryOptionColor(),
                                bonusString);
            }
        }
    }

    @Override
    public boolean doesButtonHaveConfirmDialog() {
        return true;
    }

    @Override
    public String getConfirmText() {
        return "Install";
    }

    @Override
    public String getCancelText() {
        return "Cancel";
    }

    @Override
    public String getName() {
        String buttonText;

        if (VoidTecUtils.isPlayerDockedAtSpaceport()) {
            if (VoidTecUtils.canPayForInstallation(installCost)) {
                buttonText = "Install VESAI";
            } else {
                buttonText = "Not enough credits";
            }
        } else {
            buttonText = "Need Spaceport";
        }

        return buttonText;
    }

    @Override
    public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
        boolean spEnabled = VT_Settings.hullmodInstallationWithSP;
        Color hlColor = spEnabled ? Misc.getStoryOptionColor() : Misc.getHighlightColor();
        String highlight = spEnabled
                           ? String.format("%s SP", VT_Settings.installCostSP)
                           : Misc.getDGSCredits(installCost);
        tooltip.addPara("Installation: %s", 6f, Misc.getGrayColor(), hlColor, highlight);

        Color base = spEnabled ? Misc.getStoryBrightColor() : Misc.getBrightPlayerColor();
        Color bg = spEnabled ? Misc.getStoryDarkColor() : Misc.getDarkPlayerColor();

        ButtonAPI button = ButtonUtils.addLabeledButton(tooltip, width, height, 0f, base, bg, CutStyle.C2_MENU,
                                                        new InstallHullmodButton(fleetMember));
        button.setEnabled(canInstall());

        return button;
    }

    private List<String> getRemovableSMods(ShipVariantAPI memberVariant) {
        return new ArrayList<>(memberVariant.getSMods());
    }

    private boolean canInstall() {
        return VoidTecUtils.isPlayerDockedAtSpaceport() && VoidTecUtils.canPayForInstallation(installCost);
    }
}
