package com.android.ifnewshub.ia;

import android.widget.Toast;

import androidx.annotation.NonNull;

import org.json.JSONException;

import java.io.IOException;

/**
 * Helper estático para chamar a IA de forma simples
 */
public class GeminiHelper {

    /**
     * Envia o prompt para o Gemini e retorna o texto gerado.
     *
     * @param prompt Texto da notícia ou prompt
     * @return Resumo ou texto gerado pela IA
     */
    public static String askGemini(@NonNull String prompt) throws IOException, JSONException {
        GeminiAIClient client = new GeminiAIClient();
        return client.generateText(prompt);
    }
}
