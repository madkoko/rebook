package it.polito.mad.koko.kokolab3.util;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

public class JsonUtil {
    public static final String formatJson(String uglyJsonString) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(uglyJsonString));
    }
}
