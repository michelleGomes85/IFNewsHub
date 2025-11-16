package com.android.ifnewshub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.JavascriptInterface;
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
import com.android.ifnewshub.utils.ConfigUtils;
import com.android.ifnewshub.utils.HtmlBuilder;
import com.android.ifnewshub.utils.JsonUtils;

import org.json.JSONArray;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String htmlBase;
    private NewsCache newsCache;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa o cache
        newsCache = new NewsCache(
                "news_cache",
                "news_data",
                ConfigUtils.CACHE_EXPIRATION
        );

        configWebView();
        loadHtmlTemplate();
        loadNewsWithCache();
    }

    private void loadNewsWithCache() {

        JSONArray cached = newsCache.getCachedData(this);
        if (cached != null) {
            Log.d(ConfigUtils.TAG, "Cache válido encontrado");
            List<News> newsList = JsonUtils.jsonArrayToNewsList(cached);
            renderNews(newsList);

            fetchAndCacheNews(false);
        } else {
            fetchAndCacheNews(true);
        }
    }

    private void fetchAndCacheNews(boolean updateUI) {

        NewsApi api = new NewsApi();

        api.listNews().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call, @NonNull Response<List<News>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Resposta inválida");
                    return;
                }

                List<News> newsList = response.body();

                JSONArray jsonArray = JsonUtils.newsListToJsonArray(newsList);

                if (updateUI) {
                    newsCache.saveCacheIfValid(MainActivity.this, jsonArray);
                }

                if (updateUI || webView.getContentHeight() == 0) {
                    renderNews(newsList);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<News>> call, @NonNull Throwable t) {
                showError("Erro ao consultar API: " + t.getMessage());
            }
        });
    }

    private void renderNews(List<News> list) {

        String newsCards = HtmlBuilder.buildNewsCards(list);
        String htmlFinal = htmlBase
                .replace("{{news_cards}}", newsCards)
                .replace("{{news_count}}", String.valueOf(list.size()))
                .replace("{{last_update}}", newsCache.getLastUpdateFormat(this, "dd/MM HH:mm"));

        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                htmlFinal,
                "text/html",
                "utf-8",
                null
        );
    }

    private void loadHtmlTemplate() {
        htmlBase = AssetUtil.readAsset(this, "home_template.html");
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebView() {
        webView = findViewById(R.id.webview);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.addJavascriptInterface(this, "Android");
        webView.setWebViewClient(new WebViewClient());
        WebView.setWebContentsDebuggingEnabled(true);
    }

    @JavascriptInterface
    public void updateNews() {
        fetchAndCacheNews(true);
    }
}
