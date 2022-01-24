package de.schafunschaf.voidtec.scripts.combat.effects.vesai;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Submarkets;
import com.fs.starfarer.api.util.Misc;
import de.schafunschaf.voidtec.scripts.combat.hullmods.VoidTecEngineeringSuite;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.*;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;
import static de.schafunschaf.voidtec.util.ComparisonTools.isNullOrEmpty;

@Getter
@NoArgsConstructor
public class HullModDataStorage {
    public static final String KEY = "$voidTec_hullModStorageManager";
    private final Map<String, HullModManager> dataStorage = new HashMap<>();

    public static HullModDataStorage getInstance() {
        MemoryAPI memory = Global.getSector().getMemoryWithoutUpdate();
        Object instance = memory.get(KEY);
        if (isNull(instance)) {
            HullModDataStorage hullModDataStorage = new HullModDataStorage();
            memory.set(KEY, hullModDataStorage);

            return hullModDataStorage;
        }

        return (HullModDataStorage) instance;
    }

    public HullModManager getHullModManager(String fleetMemberID) {
        return dataStorage.get(fleetMemberID);
    }

    public void storeShipID(String fleetMemberID, HullModManager hullmodManager) {
        dataStorage.put(fleetMemberID, hullmodManager);
    }

    public void cleanDataStorage() {
        Set<String> currentKeys = dataStorage.keySet();
        Set<String> newKeys = new HashSet<>();
        List<FleetMemberAPI> allPlayerOwnedShips = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy();
        allPlayerOwnedShips.addAll(getPlayerStoredShips());

        for (FleetMemberAPI fleetMember : allPlayerOwnedShips) {
            if (fleetMember.getVariant().hasHullMod(VoidTecEngineeringSuite.HULL_MOD_ID)) {
                newKeys.add(fleetMember.getId());
            }
        }

        currentKeys.retainAll(newKeys);
    }

    public void saveToMemory() {
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    private List<FleetMemberAPI> getPlayerStoredShips() {
        List<FleetMemberAPI> storedShips = new ArrayList<>();
        List<SubmarketAPI> playerStorage = getCompletePlayerStorage();

        for (SubmarketAPI storage : playerStorage) {
            List<FleetMemberAPI> mothballedShips = storage.getCargo().getMothballedShips().getMembersListCopy();
            if (isNullOrEmpty(mothballedShips)) {
                continue;
            }

            storedShips.addAll(mothballedShips);
        }

        return storedShips;
    }

    private List<SubmarketAPI> getCompletePlayerStorage() {
        List<SubmarketAPI> storageList = new ArrayList<>();

        for (FactionAPI faction : Global.getSector().getAllFactions()) {
            for (MarketAPI factionMarket : Misc.getFactionMarkets(faction)) {
                if (Misc.playerHasStorageAccess(factionMarket)) {
                    SubmarketAPI storage = factionMarket.getSubmarket(Submarkets.SUBMARKET_STORAGE);
                    if (isNull(storage)) {
                        continue;
                    }

                    storageList.add(storage);
                }
            }
        }

        return storageList;
    }
}
