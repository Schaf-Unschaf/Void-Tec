package de.schafunschaf.voidtec.imported;

import com.fs.starfarer.api.Global;
import de.schafunschaf.voidtec.ids.VT_Settings;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SettingsLoader {
    private static final String FILE_PATH = "VoidTec_Settings.ini";

    public static void loadSettings() {
        try {
            JSONObject settings = Global.getSettings().loadJSON(FILE_PATH);
            VT_Settings.sheepDebug = settings.getBoolean("sheepDebug");
            VT_Settings.enableRemoveHullmodButton = settings.getBoolean("enableRemoveHullmodButton");
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }
}
