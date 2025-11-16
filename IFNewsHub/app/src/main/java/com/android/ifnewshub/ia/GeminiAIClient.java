package com.android.ifnewshub.ia;

import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import com.android.ifnewshub.BuildConfig;


/**
 * Cliente para acessar a API do Gemini (Google Generative AI)
 * Permite enviar um prompt e receber o resumo/texto gerado.
 */
public class GeminiAIClient {

    String API_KEY = BuildConfig.GEMINI_API_KEY;

    String MODEL_ID = "gemini-2.0-flash";

    String API_URL = "https://generativelanguage.googleapis.com/v1/models/" + MODEL_ID + ":generateContent?key=" + API_KEY;

    private final OkHttpClient client;

    public GeminiAIClient() {
        this.client = new OkHttpClient.Builder().retryOnConnectionFailure(true).build();
    }

    /**
     * Gera um resumo ou texto a partir do prompt enviado.
     * @param prompt Texto da notícia ou qualquer prompt
     * @return Texto gerado pela IA
     */
    public String generateText(@NonNull String prompt) throws IOException, JSONException {

        JSONObject requestBodyJson = buildRequestJson(prompt);

        RequestBody body = RequestBody.create(
                requestBodyJson.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .build();

        try (Response response = client.newCall(request).execute()) {

            if (!response.isSuccessful()) {
                throw new IOException("Erro na API Gemini: " + response.code() + " " + response.message());
            }

            String responseStr = response.body() != null ? response.body().string() : "";
            JSONObject json = new JSONObject(responseStr);

            // A resposta vem com "candidates" → "content" → "parts" → "text"
            JSONArray candidates = json.getJSONArray("candidates");
            JSONObject content = candidates.getJSONObject(0).getJSONObject("content");
            JSONArray parts = content.getJSONArray("parts");

            return parts.getJSONObject(0).getString("text");
        }
    }

    @NonNull
    private JSONObject buildRequestJson(String prompt) throws JSONException {
        JSONObject textPart = new JSONObject();
        textPart.put("text", prompt);

        JSONArray partsArray = new JSONArray();
        partsArray.put(textPart);

        JSONObject contentObj = new JSONObject();
        contentObj.put("parts", partsArray);

        JSONArray contentsArray = new JSONArray();
        contentsArray.put(contentObj);

        JSONObject generationConfig = new JSONObject();
        generationConfig.put("temperature", 0.5);
        generationConfig.put("maxOutputTokens", 600);

        JSONObject requestBodyJson = new JSONObject();
        requestBodyJson.put("contents", contentsArray);
        requestBodyJson.put("generationConfig", generationConfig);

        return requestBodyJson;
    }
}
