package com.android.ifnewshub.cache;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * Classe de cache genérico para armazenar e recuperar JSONs
 * com expiração definida pelo usuário.
 */
public class NewsCache {

    private final String prefName;
    private final String keyData;
    private final String keyTimestamp;
    private final long expirationMillis;

    /**
     * Cria o cache
     *
     * @param prefName Nome do SharedPreferences
     * @param keyData Chave para armazenar os dados
     * @param expirationMillis Tempo de expiração em milissegundos
     */
    public NewsCache(String prefName, String keyData, long expirationMillis) {
        this.prefName = prefName;
        this.keyData = keyData;
        this.keyTimestamp = keyData + "_timestamp";
        this.expirationMillis = expirationMillis;
    }

    /**
     * Retorna os dados do cache se válidos, ou null caso contrário
     * */
    public JSONArray getCachedData(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        long savedTime = prefs.getLong(keyTimestamp, 0);

        if (!isValid(savedTime)) {
            clearCache(context);
            return null;
        }

        String json = prefs.getString(keyData, null);
        if (json == null) return null;

        try {
            return new JSONArray(json);
        } catch (JSONException e) {
            Log.e("Cache", "Erro ao ler cache", e);
            return null;
        }
    }

    /**
     * Salva os dados no cache SOMENTE se forem válidos (não nulos/vazios),
     * e atualiza o timestamp para o momento atual.
     */
    public void saveCacheIfValid(Context context, JSONArray data) {
        if (data == null || data.length() == 0) {
            Log.w("NewsCache", "Tentativa de salvar cache inválido (nulo ou vazio). Ignorando.");
            return;
        }

        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefs.edit()
                .putString(keyData, data.toString())
                .putLong(keyTimestamp, System.currentTimeMillis())
                .apply();

        Log.d("NewsCache", "Cache atualizado com " + data.length() + " itens.");
    }


    /**
     * Retorna a data/hora da última atualização bem-sucedida (em millis).
     * @return timestamp da última atualização válida, ou 0 se nunca foi atualizado.
     */
    public long getLastUpdate(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);

        // 0 = nunca atualizado com sucesso
        return prefs.getLong(keyTimestamp, 0);
    }

    public String getLastUpdateFormat(Context context, String format) {
        long ts = getLastUpdate(context);
        if (ts == 0)
            return "Nunca";

        return android.text.format.DateFormat.format(format, ts).toString();
    }

    /**
     * Limpa o cache manualmente
     */
    public void clearCache(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        prefs.edit().remove(keyData).remove(keyTimestamp).apply();
    }

    /**
     * Verifica se o cache ainda é válido
     * */
    private boolean isValid(long savedTime) {
        if (savedTime == 0) return false;
        long diff = System.currentTimeMillis() - savedTime;
        return diff < expirationMillis;
    }
}
