package de.schafunschaf.voidtec.campaign;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import de.schafunschaf.voidtec.scripts.combat.effects.vesai.AugmentQuality;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Getter
@AllArgsConstructor
public enum LootCategory {
    PIRATE(new String[]{AugmentQuality.DAMAGED.name(), AugmentQuality.COMMON.name()}),
    CIVILIAN(new String[]{AugmentQuality.DAMAGED.name(), AugmentQuality.MILITARY.name()}),
    MILITARY(new String[]{AugmentQuality.COMMON.name(), AugmentQuality.MILITARY.name()}),
    SPECIAL(new String[]{AugmentQuality.MILITARY.name(), AugmentQuality.EXPERIMENTAL.name()}),
    REMNANT(new String[]{AugmentQuality.REMNANT.name()}),
    DOMAIN(new String[]{AugmentQuality.DOMAIN.name()});

    public static final String VT_FLEET_LOOT_CATEGORY_KEY = "$vt_lootCategory";
    public static final LootCategory[] values = values();
    String[] qualityRange;

    public static LootCategory getFleetType(CampaignFleetAPI fleet) {
        MemoryAPI memory = fleet.getMemoryWithoutUpdate();
        String lootCategoryFromMemory = (String) memory.get(VT_FLEET_LOOT_CATEGORY_KEY);
        if (!isNull(lootCategoryFromMemory)) {
            return getEnum(lootCategoryFromMemory);
        }

        String fleetType = (String) memory.get("$fleetType");
        if (FleetTypes.TASK_FORCE.equals(fleetType)) {
            return SPECIAL;
        }

        FactionAPI faction = fleet.getFaction();

        switch (faction.getId()) {
            case Factions.PIRATES:
            case Factions.LUDDIC_PATH:
                return PIRATE;

            case Factions.INDEPENDENT:
            case Factions.MERCENARY:
            case Factions.SCAVENGERS:
                return CIVILIAN;

            case Factions.REMNANTS:
                return REMNANT;

            case Factions.DERELICT:
                return null;

            default:
                return MILITARY;
        }
    }

    public static LootCategory getEnum(String valueString) {
        for (LootCategory value : values) {
            if (value.name().equalsIgnoreCase(valueString)) {
                return value;
            }
        }

        return null;
    }
}
