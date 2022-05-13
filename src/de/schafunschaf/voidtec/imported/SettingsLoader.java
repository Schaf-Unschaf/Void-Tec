package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.ids.VT_Settings;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SettingsLoader {

    private static final String FILE_PATH = "data/config/modSettings.json";

    public static void loadSettings() {
        try {
            JSONObject settings = Global.getSettings().getMergedJSONForMod(FILE_PATH, VoidTecPlugin.MOD_ID)
                    .optJSONObject("voidtec");
            VT_Settings.sheepDebug = settings.getBoolean("sheepDebug");
            VT_Settings.enableRemoveHullmodButton = settings.getBoolean("enableRemoveHullmodButton");
            VT_Settings.enableChangeSlotButton = settings.getBoolean("enableChangeSlotButton");
            VT_Settings.sModPenalty = settings.getBoolean("sModPenalty");
            VT_Settings.aiHullmodChance = settings.getInt("aiHullmodChance");
            VT_Settings.aiSlotFillChance = settings.getInt("aiSlotFillChance");
            VT_Settings.recoverChance = settings.getInt("recoverChance");
            VT_Settings.destroyChanceOnRecover = settings.getInt("destroyChanceOnRecover");
            VT_Settings.damageChanceOnRecover = settings.getInt("damageChanceOnRecover");
            VT_Settings.basePartAmount = settings.getInt("basePartAmount");
            VT_Settings.basicRepairMod = (float) settings.getDouble("basicRepairModifier");
            VT_Settings.primaryRepairMod = (float) settings.getDouble("primaryRepairModifier");
            VT_Settings.secondaryRepairMod = (float) settings.getDouble("secondaryRepairModifier");
            VT_Settings.basicDisassembleMod = (float) settings.getDouble("basicDisassembleModifier");
            VT_Settings.primaryDisassembleMod = (float) settings.getDouble("primaryDisassembleModifier");
            VT_Settings.secondaryDisassembleMod = (float) settings.getDouble("secondaryDisassembleModifier");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
