/*
 *  @version     1.0, Jan 31, 2012
 *  @author sunny
 */
package in.bucheeng.common.utils;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class JsonUtils {

    public static <T> T stringToJson(String json, Class<T> typeOfT) {
        return new Gson().fromJson(json, typeOfT);
    }

    public static Object stringToJson(String json, String type) {
        try {
            Class typeClass = Class.forName(type);
            return new Gson().fromJson(json, typeClass);
        } catch (ClassNotFoundException e) {
            throw new IllegalArgumentException("invalid type:" + type);
        }
    }

    public static Object stringToJson(String json) {
        return new JsonParser().parse(json);
    }

    public static String objectToString(Object jsonObject) {
        return new Gson().toJson(jsonObject);
    }

    public static String objectToString(Object jsonObject, boolean serializeNulls) {
        if (serializeNulls) {
            return new GsonBuilder().serializeNulls().create().toJson(jsonObject);
        }
        return objectToString(jsonObject);
    }

    public static String getAsString(JsonObject jsonObject, String memberName) {
        JsonElement element = jsonObject.get(memberName);
        return (element == null || element.isJsonNull()) ? null : element.getAsString();
    }

    public static Map<String, String> jsonToMap(String json) {
        return stringToJson(json, Map.class);
    }

    public static void main(String[] args) {
        String s = "{\"104500409\":\"{\\\"imei\\\":\\\"na\\\"}\"}";
        System.out.println(jsonToMap(s).get("104500409"));
    }
}
