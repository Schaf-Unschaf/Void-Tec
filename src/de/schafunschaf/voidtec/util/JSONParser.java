package de.schafunschaf.voidtec.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class JSONParser {

    @SuppressWarnings("rawtypes")
    public static Map<String, Object[]> parseJSONObject(JSONObject jsonObject) throws JSONException {
        Iterator arrayKeys = jsonObject.keys();
        Map<String, Object[]> jsonObjectMap = new HashMap<>();

        while (arrayKeys.hasNext()) {
            String key = (String) arrayKeys.next();
            JSONArray jsonArray = jsonObject.getJSONArray(key);
            Object[] dataArray = parseJSONArray(jsonArray);

            jsonObjectMap.put(key, dataArray);
        }

        return jsonObjectMap;
    }

    public static Object[] parseJSONArray(JSONArray jsonArray) throws JSONException {
        int length = jsonArray.length();
        String[] dataArray = new String[length];

        for (int i = 0; i < length; i++) {
            dataArray[i] = jsonArray.getString(i);
        }

        return dataArray;
    }
}
