package de.schafunschaf.voidtec.util.ui;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignClockAPI;
import com.fs.starfarer.api.ui.*;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.intel.buttons.DefaultButton;
import de.schafunschaf.voidtec.campaign.intel.buttons.IntelButton;
import lombok.Getter;
import org.jetbrains.annotations.Nullable;

import java.awt.Color;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class TimerBar {

    @Getter
    private final String timerID;
    private final float timerLength;
    private final float width;
    private final float height;
    private final float borderStrength;
    private final Color borderColor;
    private final Color backgroundColor;
    private final Color barColor;
    private final float padding;
    private final Alignment textAlignment;
    private final IntelButton button;
    private long startTimestamp;
    private boolean hasStarted = false;
    private boolean withButton = false;
    private String buttonText;

    public TimerBar(String timerID, float timerLength, float width, float height, float borderStrength,
                    Color borderColor, Color backgroundColor, Color barColor, float padding, Alignment textAlignment, IntelButton button) {
        this.timerID = "$vt_timer_" + timerID;
        this.width = width;
        this.height = height;
        this.borderStrength = borderStrength;
        this.timerLength = timerLength;
        this.borderColor = borderColor;
        this.backgroundColor = backgroundColor;
        this.barColor = barColor;
        this.padding = padding;
        this.textAlignment = textAlignment;
        this.button = button;
    }

    public static TimerBar getTimer(String timerID) {
        return (TimerBar) Global.getSector().getMemoryWithoutUpdate().get("$vt_timer_" + timerID);
    }

    public void startTimer(boolean autoExpire) {
        startTimestamp = Global.getSector().getClock().getTimestamp();
        hasStarted = true;

        if (autoExpire) {
            Global.getSector().getMemoryWithoutUpdate().set(this.timerID, this, timerLength + 1);
        } else {
            Global.getSector().getMemoryWithoutUpdate().set(this.timerID, this);
        }
    }

    public void removeTimer() {
        Global.getSector().getMemoryWithoutUpdate().unset(timerID);
    }

    public UIComponentAPI renderTimer(TooltipMakerAPI tooltip, @Nullable CustomPanelAPI mainPanel) {
        CampaignClockAPI clock = Global.getSector().getClock();
        float daysSinceStart = hasStarted ? clock.getElapsedDaysSince(startTimestamp) : 0;
        float innerBorderStrength = borderStrength * 2;
        float barWidth = this.width - (borderStrength + innerBorderStrength) * 2;
        float barHeight = this.height - (borderStrength + innerBorderStrength) * 2;
        float barProgress = barWidth / timerLength * daysSinceStart;

        CustomPanelAPI timerPanel = isNull(mainPanel)
                                    ? Global.getSettings().createCustom(width, height, null)
                                    : mainPanel.createCustomPanel(width, height, null);

        TooltipMakerAPI timerUIElement = timerPanel.createUIElement(width, height, false);

        // Outer border
        timerUIElement.addSectionHeading("", Color.BLACK, borderColor, Alignment.MID, 0f).getPosition()
                      .setSize(width, height)
                      .inTL(0, 0);

        // Inner border
        timerUIElement.addSectionHeading("", Color.BLACK, Color.BLACK, Alignment.MID, 0f).getPosition()
                      .setSize(width - borderStrength * 2, height - borderStrength * 2)
                      .inTL(borderStrength, borderStrength);

        // Inner background
        timerUIElement.addSectionHeading("", Color.BLACK, backgroundColor, Alignment.MID, 0f).getPosition()
                      .setSize(barWidth, barHeight)
                      .inTL(borderStrength + innerBorderStrength, borderStrength + innerBorderStrength);

        // Progress bar
        timerUIElement.addSectionHeading("", Color.BLACK, barColor, Alignment.MID, 0f).getPosition()
                      .setSize(barProgress, barHeight)
                      .inTMid(borderStrength + innerBorderStrength);

        if (hasStarted) {
            timerUIElement.setParaFont(Fonts.ORBITRON_12);
            timerUIElement.addPara(getTimeDisplay(), 0f).setAlignment(textAlignment);
            UIComponentAPI prev = timerUIElement.getPrev();
            prev.getPosition().inTMid(height / 2 - prev.getPosition().getHeight() / 2);
        }

        if (withButton && !hasStarted) {
            addStartButton(timerUIElement, button);
        }

        timerPanel.addUIElement(timerUIElement).inTL(0, 0);
        return tooltip.addCustom(timerPanel, padding + 1);
    }

    public String getTimeDisplay() {
        CampaignClockAPI clock = Global.getSector().getClock();
        float daysSinceStart = hasStarted ? clock.getElapsedDaysSince(startTimestamp) : 0;
        float totalSeconds = clock.convertToSeconds(timerLength - daysSinceStart);
        int secondsLeft = 0;
        int minutesLeft = 0;
        if (totalSeconds > 0) {
            secondsLeft = (int) Math.ceil(totalSeconds % 60);
            minutesLeft = (int) (totalSeconds / 60);
        }

        return String.format("%02d:%02d", minutesLeft, secondsLeft);
    }

    public void addStartButton(String buttonText) {
        if (hasStarted) {
            return;
        }

        withButton = true;
        this.buttonText = buttonText;
    }

    private void addStartButton(final TooltipMakerAPI tooltip, final IntelButton button) {
        new DefaultButton() {
            @Override
            public void buttonPressCancelled(IntelUIAPI ui) {
                button.buttonPressCancelled(ui);
            }

            @Override
            public void buttonPressConfirmed(IntelUIAPI ui) {
                button.buttonPressConfirmed(ui);
                startTimer(true);
            }

            @Override
            public void createConfirmationPrompt(TooltipMakerAPI tooltip) {
                button.createConfirmationPrompt(tooltip);
            }

            @Override
            public boolean doesButtonHaveConfirmDialog() {
                return button.doesButtonHaveConfirmDialog();
            }

            @Override
            public String getConfirmText() {
                return button.getConfirmText();
            }

            @Override
            public String getCancelText() {
                return button.getCancelText();
            }

            @Override
            public String getName() {
                return String.format("%s (%s)", button.getName(), getTimeDisplay());
            }

            @Override
            public int getShortcut() {
                return button.getShortcut();
            }

            @Override
            public ButtonAPI addButton(TooltipMakerAPI tooltip, float width, float height) {
                ButtonAPI checkboxButton = ButtonUtils.addCheckboxButton(tooltip, width, height, 0f, Misc.getTextColor(), borderColor,
                                                                         borderColor, this);
                checkboxButton.getPosition().inTL(0, 0);
                checkboxButton.setChecked(false);
                button.addTooltip(tooltip);

                return checkboxButton;
            }
        }.addButton(tooltip, width, height);
    }
}
