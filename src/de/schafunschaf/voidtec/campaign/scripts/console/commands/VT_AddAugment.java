package de.schafunschaf.voidtec.campaign.scripts.console.commands;

import de.schafunschaf.voidtec.combat.vesai.augments.AugmentApplier;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentDataManager;
import de.schafunschaf.voidtec.combat.vesai.augments.AugmentQuality;
import de.schafunschaf.voidtec.util.CargoUtils;
import de.schafunschaf.voidtec.util.ComparisonTools;
import org.jetbrains.annotations.NotNull;
import org.lazywizard.console.BaseCommand;
import org.lazywizard.console.Console;

public class VT_AddAugment implements BaseCommand {

    @Override
    public CommandResult runCommand(@NotNull String args, @NotNull BaseCommand.CommandContext context) {
        if (context != CommandContext.CAMPAIGN_MAP) {
            Console.showMessage("Error: This command is campaign-only.");
            return CommandResult.WRONG_CONTEXT;
        }

        if (args.isEmpty()) {
            return CommandResult.BAD_SYNTAX;
        }

        String[] strings = args.split(" ");
        String augmentID = strings[0];
        AugmentQuality quality = strings.length > 1 ? AugmentQuality.getEnum(strings[1]) : null;
        if (ComparisonTools.isNull(quality)) {
            quality = AugmentQuality.getRandomQuality(null, true);
        }

        try {
            AugmentApplier augment = AugmentDataManager.getAugment(augmentID, quality);
            CargoUtils.addAugmentToFleetCargo(augment);

            Console.showMessage(
                    String.format("Added %s in %s quality to your cargo (%s).", augment.getName(), quality.getName(), augmentID));

            return CommandResult.SUCCESS;
        } catch (Exception exception) {
            Console.showMessage(String.format("Error: No Augment with the ID '%s' found.", augmentID));

            return CommandResult.ERROR;
        }
    }
}
