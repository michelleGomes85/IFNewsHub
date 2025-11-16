package com.android.ifnewshub.utils;

import com.android.ifnewshub.model.News;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

public class JsonUtils {
    private static final Gson gson = new Gson();

    public static JSONArray newsListToJsonArray(List<News> newsList) {
        String json = gson.toJson(newsList);
        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            throw new RuntimeException("Erro ao converter lista para JSONArray", e);
        }
    }

    public static List<News> jsonArrayToNewsList(JSONArray jsonArray) {
        try {
            String json = jsonArray.toString();
            Type listType = new TypeToken<List<News>>(){}.getType();
            return gson.fromJson(json, listType);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao converter JSONArray para lista", e);
        }
    }

    public static News jsonToNews(String json) {
        return gson.fromJson(json, News.class);
    }

    public static String newsToJson(News n) {
        return gson.toJson(n);
    }
}
