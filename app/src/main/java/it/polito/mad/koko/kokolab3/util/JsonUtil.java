package it.polito.mad.koko.kokolab3.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.reflect.TypeToken;

import java.util.Map;

public class JsonUtil {
    public static final String formatJson(String uglyJsonString) {
        return new GsonBuilder().setPrettyPrinting().create().toJson(new JsonParser().parse(uglyJsonString));
    }

    public static final String serialize(Object data) {
        return new Gson().toJson(data);
    }

    public static final Map<String, String> deserialize(String data) {
        return new Gson().fromJson(data, new TypeToken<Map<String, String>>() {}.getType());
    }
}
