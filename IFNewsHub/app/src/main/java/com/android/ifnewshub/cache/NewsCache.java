package com.android.ifnewshub.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.ifnewshub.model.News;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewsCache {

    private final String prefName;
    private final long expirationMillis;
    private final Gson gson = new Gson();
    private final String globalTimestampKey = "_last_update_global";

    public NewsCache(String prefName, long expirationMillis) {
        this.prefName = prefName;
        this.expirationMillis = expirationMillis;
    }

    /**
     * Salva lista de notícias (mantendo compatibilidade)
     */
    public void saveCacheIfValid(Context context, List<News> newsList) {
        if (newsList == null || newsList.isEmpty()) return;

        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        long now = System.currentTimeMillis();

        for (News news : newsList) {
            if (news.getLink() == null) continue;

            JSONObject obj = new JSONObject();
            try {
                obj.put("data", new JSONObject(gson.toJson(news)));
                obj.put("timestamp", now);
            } catch (JSONException e) {
                Log.e("NewsCache", "Erro ao salvar notícia: " + news.getLink(), e);
                continue;
            }

            editor.putString(news.getLink(), obj.toString());
        }

        // Atualiza o timestamp global
        editor.putLong(globalTimestampKey, now);
        editor.apply();
    }

    /**
     * Retorna todas as notícias válidas como JSONArray
     */
    public JSONArray getCachedData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        JSONArray result = new JSONArray();

        for (String key : prefs.getAll().keySet()) {
            if (key.equals(globalTimestampKey)) continue; // Ignora timestamp global
            String value = prefs.getString(key, null);
            if (value == null) continue;

            try {
                JSONObject obj = new JSONObject(value);
                long timestamp = obj.getLong("timestamp");

                if (isValid(timestamp)) {
                    prefs.edit().remove(key).apply();
                    continue;
                }

                result.put(obj.getJSONObject("data"));
            } catch (JSONException e) {
                Log.e("NewsCache", "Erro ao ler cache da notícia: " + key, e);
            }
        }

        return result.length() > 0 ? result : null;
    }

    /**
     * Atualiza ou adiciona uma notícia específica
     */
    public void updateNews(Context context, News news) {
        if (news.getLink() == null) return;

        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        JSONObject obj = new JSONObject();
        try {
            obj.put("data", new JSONObject(gson.toJson(news)));
            obj.put("timestamp", System.currentTimeMillis());
        } catch (JSONException e) {
            Log.e("NewsCache", "Erro ao atualizar notícia: " + news.getLink(), e);
            return;
        }

        prefs.edit().putString(news.getLink(), obj.toString())
                .putLong(globalTimestampKey, System.currentTimeMillis()) // atualiza global
                .apply();
    }

    /**
     * Recupera uma notícia específica
     */
    public News getNews(Context context, String link) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        String value = prefs.getString(link, null);
        if (value == null) return null;

        try {
            JSONObject obj = new JSONObject(value);
            long timestamp = obj.getLong("timestamp");

            if (isValid(timestamp)) {
                prefs.edit().remove(link).apply();
                return null;
            }

            JSONObject data = obj.getJSONObject("data");
            return gson.fromJson(data.toString(), News.class);

        } catch (JSONException e) {
            Log.e("NewsCache", "Erro ao recuperar notícia: " + link, e);
            return null;
        }
    }

    /**
     * Última atualização global
     */
    public long getLastUpdate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        return prefs.getLong(globalTimestampKey, 0);
    }

    public String getLastUpdateFormat(Context context, String format) {
        long ts = getLastUpdate(context);
        if (ts == 0) return "Nunca";
        return android.text.format.DateFormat.format(format, ts).toString();
    }

    private boolean isValid(long timestamp) {
        if (timestamp == 0) return true;
        return System.currentTimeMillis() - timestamp >= expirationMillis;
    }
}