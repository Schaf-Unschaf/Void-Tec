package de.schafunschaf.voidtec.imported;


import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.util.Pair;
import de.schafunschaf.voidtec.plugins.VoidTecPlugin;
import lombok.Getter;
import lombok.extern.log4j.Log4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.schafunschaf.voidtec.util.ComparisonTools.isNull;

@Log4j
public class WelcomeMessageLoader {

    private static final String FILE_PATH = "data/config/voidtec/vt_welcome_messages.csv";

    @Getter
    private static final List<Pair<String, Float>> messageList = new ArrayList<>();

    public static void loadMessageFiles() {
        try {
            JSONArray spreadsheet = Global.getSettings()
                                          .getMergedSpreadsheetDataForMod("id", FILE_PATH, VoidTecPlugin.MOD_ID);

            for (int i = 0; i < spreadsheet.length(); i++) {
                JSONObject row = spreadsheet.getJSONObject(i);

                String messageID;
                if (row.has("id") && !isNull(row.getString("id")) && !row.getString("id").isEmpty() && !row.getString("id").contains("#")) {
                    messageID = row.getString("id");
                    log.info(String.format("VoidTec: Loading Message %s", messageID));
                } else {
                    log.info("VoidTec: Hit empty line, skipping");
                    continue;
                }

                String message = row.optString("message");
                double weighting = row.optDouble("weighting");

                messageList.add(new Pair<>(message, (float) weighting));
            }
        } catch (JSONException | IOException e) {
            log.warn("VoidTec: Error while loading intel messages");
        }
    }
}
