package io.github.cjustinn.specialisedeconomics.services;

import org.json.simple.JSONObject;

import java.util.Map;

public class ApiService {
    public static JSONObject createObject(Map<String, Object> values) {
        JSONObject object = new JSONObject();
        object.putAll(values);

        return object;
    }
}
