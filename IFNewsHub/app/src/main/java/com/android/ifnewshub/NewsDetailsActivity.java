package com.android.ifnewshub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.ifnewshub.api.NewsApi;
import com.android.ifnewshub.cache.NewsCache;
import com.android.ifnewshub.model.News;
import com.android.ifnewshub.utils.AssetUtil;
import com.android.ifnewshub.utils.JsonUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewsDetailsActivity extends AppCompatActivity {

    private WebView webView;
    private News news;
    private NewsCache cache;

    private boolean cachePending = false;

    private String cachedContent = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        initCache();
        loadNewsFromIntent();
        initWebView();
        loadInitialHtml();
        fetchFullContentAsync();
    }

    // ---------------------------------------------------------
    // Inicializa o cache com expiração de 24h
    // ---------------------------------------------------------
    private void initCache() {
        long expire24h = 24 * 60 * 60 * 1000;
        cache = new NewsCache("full_news_cache", expire24h);
    }

    // ---------------------------------------------------------
    // Carrega dados básicos da notícia enviados via Intent
    // ---------------------------------------------------------
    private void loadNewsFromIntent() {
        String json = getIntent().getStringExtra("news_json");
        news = JsonUtils.jsonToNews(json);
    }

    // ---------------------------------------------------------
    // Inicializa WebView e JS Bridge
    // ---------------------------------------------------------
    @SuppressLint("SetJavaScriptEnabled")
    private void initWebView() {
        webView = findViewById(R.id.webview_details);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (cachePending && cachedContent != null) {
                    injectFullContent(cachedContent);
                    cachePending = false;
                    cachedContent = null;
                }
            }
        });

        WebView.setWebContentsDebuggingEnabled(true);
        registerJsBridge();
    }

    private void registerJsBridge() {
        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void closePage() {
                runOnUiThread(() -> finish());
            }
        }, "Android");
    }

    // ---------------------------------------------------------
    // Carrega HTML inicial da notícia (sem conteúdo completo)
    // ---------------------------------------------------------
    private void loadInitialHtml() {
        String template = AssetUtil.readAsset(this, "news_details_template.html");
        String html = fillTemplate(template);
        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "utf-8",
                null
        );
    }

    private String fillTemplate(String template) {
        return template
                .replace("{{title}}", safe(news.getTitle()))
                .replace("{{description}}", safe(news.getDescription()))
                .replace("{{link}}", safe(news.getLink()))
                .replace("{{tags}}", buildTagsHtml());
    }

    private String buildTagsHtml() {
        if (news.getTags() == null) return "";
        StringBuilder sb = new StringBuilder();
        for (String tag : news.getTags()) {
            sb.append("<span class=\"tag-chip\">").append(safe(tag)).append("</span>");
        }
        return sb.toString();
    }

    // ---------------------------------------------------------
    // Busca assíncrona do conteúdo completo da notícia
    // ---------------------------------------------------------
    private void fetchFullContentAsync() {

        // Verifica se já existe conteúdo no cache
        String cached = getCachedContent(news.getLink());
        if (cached != null) {
            cachedContent = cached;
            cachePending = true;
            return;
        }

        // Senão, busca conteúdo completo via API
        NewsApi api = new NewsApi();
        api.getNewsContent(news.getLink()).enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<News> call, @NonNull Response<News> response) {
                if (!response.isSuccessful() || response.body() == null) return;

                String fullText = response.body().getContent();
                if (fullText != null && !fullText.isEmpty()) {
                    saveCachedContent(news.getLink(), fullText);
                    injectFullContent(fullText);
                }
            }

            @Override
            public void onFailure(@NonNull Call<News> call, @NonNull Throwable t) {
                Log.e("NewsDetails", "Erro API conteúdo", t);
            }
        });
    }


    // ---------------------------------------------------------
    // Injeta conteúdo completo no WebView (chamada JS)
    // ---------------------------------------------------------
    private void injectFullContent(String text) {
        String jsSafe = escapeJsString(text);
        String js = "javascript:updateFullContent(\"" + jsSafe + "\");";
        runOnUiThread(() -> webView.evaluateJavascript(js, null));
    }

    private String escapeJsString(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

    // ---------------------------------------------------------
    // Recupera conteúdo do cache usando o link da notícia
    // ---------------------------------------------------------
    private String getCachedContent(String newsLink) {
        News cachedNews = cache.getNews(this, newsLink);
        if (cachedNews != null && cachedNews.getContent() != null && !cachedNews.getContent().isEmpty()) {
            return cachedNews.getContent();
        }
        return null;
    }

    // ---------------------------------------------------------
    // Salva conteúdo completo da notícia no cache
    // ---------------------------------------------------------
    private void saveCachedContent(String newsLink, String content) {
        if (news == null) return;

        // Cria uma nova notícia para atualizar apenas o conteúdo
        News updated = new News();
        updated.setLink(newsLink);
        updated.setTitle(news.getTitle());
        updated.setDescription(news.getDescription());
        updated.setContent(content);
        updated.setTags(news.getTags());

        cache.updateNews(this, updated); // Salva notícia individualmente no cache
        Log.d("NewsDetails", "Cache salvo para newsLink: " + newsLink);
    }



    // ---------------------------------------------------------
    // Segurança
    // ---------------------------------------------------------
    private String safe(String text) {
        return text == null ? "" : text;
    }
}