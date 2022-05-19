package de.schafunschaf.voidtec.campaign.scripts.console.commands;

import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.statmodifiers.StatModValue;
import de.schafunschaf.voidtec.helper.AugmentCargoWrapper;
import de.schafunschaf.voidtec.helper.ModLoadingHelper;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.imported.AugmentDataLoader;
import de.schafunschaf.voidtec.util.CargoUtils;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;

import java.util.List;

public class VT_Reload implements BaseCommand {

    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull CommandContext context) {
        if (context != CommandContext.CAMPAIGN_MAP) {
            Console.showMessage("Error: This command is campaign-only.");
            return CommandResult.WRONG_CONTEXT;
        }

        try {
            ModLoadingHelper.loadExternalData();

            // Update all existing augments with new ranges based on updated modifier.
            for (AugmentCargoWrapper augmentWrapper : CargoUtils.getAugmentsInCargo()) {
                // Use original spec so we don't apply modifier to an already-modified range.
                AugmentApplier augSpec = AugmentDataManager.getAugment(augmentWrapper.getAugment().getAugmentID(),
                                                                       augmentWrapper.getAugment().getAugmentQuality());

                List<StatModValue<Float, Float, Boolean, Boolean>> primaryValuesFromSpec = augSpec.getPrimaryStatValues();
                for (int i = 0; i < primaryValuesFromSpec.size(); i++) {
                    augmentWrapper.getAugment().getPrimaryStatValues()
                                  .set(i, AugmentDataLoader.applyStatRangeModifier(primaryValuesFromSpec.get(i),
                                                                                   VT_Settings.statRollRangeModifier));
                }

                List<StatModValue<Float, Float, Boolean, Boolean>> secondaryValuesFromSpec = augSpec.getSecondaryStatValues();
                for (int i = 0; i < secondaryValuesFromSpec.size(); i++) {
                    augmentWrapper.getAugment().getSecondaryStatValues()
                                  .set(i, AugmentDataLoader.applyStatRangeModifier(secondaryValuesFromSpec.get(i),
                                                                                   VT_Settings.statRollRangeModifier));
                }
            }

            Console.showMessage("Settings reloaded.");
            return CommandResult.SUCCESS;
        } catch (Exception exception) {
            Console.showMessage(exception.getMessage());
            exception.printStackTrace();

            return CommandResult.ERROR;
        }
    }
}