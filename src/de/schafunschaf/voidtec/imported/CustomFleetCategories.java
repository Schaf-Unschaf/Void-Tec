package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.impl.campaign.ids.Factions;

import java.util.HashSet;
import java.util.Set;

public class CustomFleetCategories {

    private static final Set<String> IGNORED_FACTIONS = new HashSet<>();
    private static final Set<String> OUTLAW_FACTIONS = new HashSet<>();
    private static final Set<String> CIVILIAN_FACTIONS = new HashSet<>();
    private static final Set<String> SPECIAL_FACTIONS = new HashSet<>();
    private static final Set<String> REMNANT_FACTIONS = new HashSet<>();
    private static final Set<String> DOMAIN_FACTIONS = new HashSet<>();

    public static void initVanillaFactions() {
        addIgnoredFaction(Factions.NEUTRAL);
        addIgnoredFaction(Factions.DERELICT);
        addIgnoredFaction(Factions.PLAYER);
        addIgnoredFaction(Factions.POOR);
        addIgnoredFaction(Factions.SLEEPER);

        addOutlawFaction(Factions.PIRATES);
        addOutlawFaction(Factions.LUDDIC_PATH);

        addCivilianFaction(Factions.LUDDIC_CHURCH);
        addCivilianFaction(Factions.SCAVENGERS);

        addSpecialFaction(Factions.LIONS_GUARD);
        addSpecialFaction(Factions.KOL);

        addRemnantFaction(Factions.REMNANTS);

        addDomainFaction(Factions.OMEGA);
    }

    public static void addIgnoredFaction(String factionID) {
        IGNORED_FACTIONS.add(factionID);
    }

    public static boolean isIgnoredFaction(String factionID) {
        return IGNORED_FACTIONS.contains(factionID);
    }

    public static void addOutlawFaction(String factionID) {
        OUTLAW_FACTIONS.add(factionID);
    }

    public static boolean isOutlawFaction(String factionID) {
        return OUTLAW_FACTIONS.contains(factionID);
    }

    public static void addCivilianFaction(String factionID) {
        CIVILIAN_FACTIONS.add(factionID);
    }

    public static boolean isCivilianFaction(String factionID) {
        return CIVILIAN_FACTIONS.contains(factionID);
    }

    public static void addSpecialFaction(String factionID) {
        SPECIAL_FACTIONS.add(factionID);
    }

    public static boolean isSpecialFaction(String factionID) {
        return SPECIAL_FACTIONS.contains(factionID);
    }

    public static void addRemnantFaction(String factionID) {
        REMNANT_FACTIONS.add(factionID);
    }

    public static boolean isRemnantFaction(String factionID) {
        return REMNANT_FACTIONS.contains(factionID);
    }

    public static void addDomainFaction(String factionID) {
        DOMAIN_FACTIONS.add(factionID);
    }

    public static boolean isDomainFaction(String factionID) {
        return DOMAIN_FACTIONS.contains(factionID);
    }
}