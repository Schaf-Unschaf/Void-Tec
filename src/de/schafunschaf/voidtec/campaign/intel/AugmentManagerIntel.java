package de.schafunschaf.voidtec.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.ui.CustomPanelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.campaign.listeners.VT_CampaignListener;
import de.schafunschaf.voidtec.campaign.scripts.VT_WelcomeMessageScript;
import de.schafunschaf.voidtec.combat.vesai.AugmentSlot;
import de.schafunschaf.voidtec.combat.vesai.SlotCategory;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.DamagedAugmentData;
import de.schafunschaf.voidtec.ids.VT_Colors;
import de.schafunschaf.voidtec.ids.VT_Strings;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.FormattingTools;
import de.schafunschaf.voidtec.util.VoidTecUtils;
import de.schafunschaf.voidtec.util.ui.UIUtils;
import lombok.Getter;
import lombok.Setter;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

public class AugmentManagerIntel extends BaseIntel {

    public static final String STACK_SOURCE = "augmentManagerIntel";
    @Getter
    private static List<AugmentCargoWrapper> augmentsInCargo;
    @Getter
    @Setter
    private static AugmentCargoWrapper selectedAugmentInCargo;
    @Getter
    @Setter
    private static AugmentApplier selectedInstalledAugment;
    @Getter
    @Setter
    private static AugmentSlot selectedSlot;
    @Getter
    @Setter
    private static SlotCategory activeCategoryFilter;
    @Getter
    @Setter
    private static AugmentQuality activeQualityFilter;
    @Getter
    @Setter
    private static boolean isShowingManufacturingPanel = false;

    private boolean welcomeMessageSent = false;

    public static AugmentManagerIntel getInstance() {
        IntelManagerAPI intelManager = Global.getSector().getIntelManager();
        AugmentManagerIntel instance;

        if (intelManager.hasIntelOfClass(AugmentManagerIntel.class)) {
            instance = ((AugmentManagerIntel) intelManager.getIntel(AugmentManagerIntel.class).get(0));
        } else {
            instance = new AugmentManagerIntel();
            instance.welcomeMessageSent = Global.getSector().getMemoryWithoutUpdate().getBoolean("$_vt_welcomeMessageSent");

            if (!instance.welcomeMessageSent) {
                Global.getSector().addTransientScript(new VT_WelcomeMessageScript());
            }
        }

        return instance;
    }

    public static AugmentApplier getSelectedAugment() {
        AugmentCargoWrapper selectedAugmentInCargo = getSelectedAugmentInCargo();

        return isNull(selectedAugmentInCargo) ? getSelectedInstalledAugment() : selectedAugmentInCargo.getAugment();
    }

    @Override
    public void notifyPlayerAboutToOpenIntelScreen() {
        reset();
    }

    @Override
    public boolean canTurnImportantOff() {
        return false;
    }

    @Override
    public boolean hasImportantButton() {
        return false;
    }

    @Override
    protected String getName() {
        return "VESAI";
    }

    @Override
    public boolean hasSmallDescription() {
        return false;
    }

    @Override
    public boolean hasLargeDescription() {
        return true;
    }

    @Override
    public void createLargeDescription(CustomPanelAPI panel, float width, float height) {
        augmentsInCargo = CargoUtils.getAugmentsInCargo();

        /*
         * Panel-Size at 1280x768 with 100% scaling is 895 x 550.
         * This is the minimum supported resolution and size which will act as a base and getting upscaled on higher res.
         */

        float padding = 1f;
        float titlePanelHeight = 18f;
        float cargoPanelWidth = 260f;
        float cargoPanelHeight = height - titlePanelHeight - padding;
        float infoPanelWidth = width - cargoPanelWidth - padding;
        float infoPanelHeight = width / 4f;
        float shipPanelWidth = width - cargoPanelWidth - padding;
        float shipPanelHeight = height - infoPanelHeight - titlePanelHeight - padding;

        new TitlePanel(width, titlePanelHeight).render(panel);
        new ShipPanel(shipPanelWidth, shipPanelHeight, titlePanelHeight + padding + infoPanelHeight + padding).render(panel);
        new CargoPanel(cargoPanelWidth - padding, cargoPanelHeight, titlePanelHeight).render(panel);
        new InfoPanel(infoPanelWidth, infoPanelHeight, titlePanelHeight + padding).render(panel);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "voidTec_hullmod_icon");
    }

    @Override
    public boolean isImportant() {
        return true;
    }

    @Override
    public void reportPlayerClickedOn() {
    }

    @Override
    public Color getTitleColor(ListInfoMode mode) {
        return VT_Colors.VT_COLOR_MAIN;
    }

    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        if (mode == ListInfoMode.MESSAGES) {
            if (!welcomeMessageSent) {
                createWelcomeMessage(info);
            } else {
                createDamageReport(info);
            }
        } else {
            createIntelText(info);
        }
    }

    private void createIntelText(TooltipMakerAPI info) {
        Color color = getTitleColor(null);
        info.addPara(getName(), color, 0f);
        String randomIntelMessage = VoidTecUtils.getRandomIntelMessage();
        String playerName = Global.getSector().getPlayerPerson().getNameString();
        FullName fullName = Global.getSector().getPlayerPerson().getName();

        randomIntelMessage = randomIntelMessage.replace("$captain", playerName);
        randomIntelMessage = randomIntelMessage.replace("$firstName", fullName.getFirst());
        randomIntelMessage = randomIntelMessage.replace("$lastName", fullName.getLast());

        info.addPara(randomIntelMessage, 3f, Misc.getHighlightColor(), playerName, fullName.getFirst(), fullName.getLast());
    }

    private void createWelcomeMessage(TooltipMakerAPI info) {
        String welcomeTitle = "VoidTec Engineering Suit Augmentation Interface";
        info.addPara(welcomeTitle, VT_Colors.VT_COLOR_MAIN, 0f);
        UIUtils.addHorizontalSeparator(info, info.computeStringWidth(welcomeTitle), 1f, VT_Colors.VT_COLOR_MAIN, 0f);
        info.addPara("Welcome Captain %s. To access the new Augmentation Interface, open your %s and go to the %s tab.", 3f,
                     Misc.getHighlightColor(), Global.getSector().getPlayerPerson().getNameString(), "Intel", "Important");
    }

    private void createDamageReport(TooltipMakerAPI info) {
        Map<String, Set<DamagedAugmentData>> damagedShipsInLastBattle = VT_CampaignListener.getDamagedShipsInLastBattle();
        int damagedShipAmount = damagedShipsInLastBattle.size();
        String text = info.addPara("%s %s took severe damage during battle:", 0f, Misc.getHighlightColor(),
                                   String.valueOf(damagedShipAmount), FormattingTools.singularOrPlural(damagedShipAmount, "Ship"))
                          .getText();
        UIUtils.addHorizontalSeparator(info, info.computeStringWidth(text), 1f, Misc.getTextColor(), 0f);

        info.setBulletedListMode(VT_Strings.BULLET_CHAR + " ");
        for (String shipName : damagedShipsInLastBattle.keySet()) {
            List<String> hlStrings = new ArrayList<>();
            StringBuilder augmentListBuilder = new StringBuilder();
            List<Color> hlColors = new ArrayList<>();

            Set<DamagedAugmentData> damagedAugmentData = damagedShipsInLastBattle.get(shipName);
            for (DamagedAugmentData augmentData : damagedAugmentData) {
                hlStrings.add(augmentData.getAugmentName());
                hlColors.add(augmentData.getSlotColor());
                String semicolon = augmentListBuilder.length() > 0 ? ", " : "";
                augmentListBuilder.append(semicolon).append(augmentData.getAugmentName());
            }

            info.addPara(String.format("%s (%s)", shipName, augmentListBuilder), 3f, hlColors.toArray(new Color[0]),
                         hlStrings.toArray(new String[0]));
        }
    }

    public static void reset() {
        selectedSlot = null;
        selectedInstalledAugment = null;
        selectedAugmentInCargo = null;
        activeCategoryFilter = null;
        activeQualityFilter = null;
        augmentsInCargo = null;
    }
}
