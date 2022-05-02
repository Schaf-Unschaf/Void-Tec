package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.ids.VT_Settings;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SettingsLoader {

    private static final String FILE_PATH = "modSettings.json";

    public static void loadSettings() {
        try {
            JSONObject settings = Global.getSettings().loadJSON(FILE_PATH);
            VT_Settings.sheepDebug = settings.getBoolean("sheepDebug");
            VT_Settings.enableRemoveHullmodButton = settings.getBoolean("enableRemoveHullmodButton");
            VT_Settings.enableChangeSlotButton = settings.getBoolean("enableChangeSlotButton");
            VT_Settings.aiHullmodChance = settings.getInt("aiHullmodChance");
            VT_Settings.aiSlotFillChance = settings.getInt("aiSlotFillChance");
            VT_Settings.recoverChance = settings.getInt("recoverChance");
            VT_Settings.destroyChanceOnRecover = settings.getInt("destroyChanceOnRecover");
            VT_Settings.damageChanceOnRecover = settings.getInt("damageChanceOnRecover");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
