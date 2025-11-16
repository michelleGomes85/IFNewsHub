package com.android.ifnewshub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.ifnewshub.api.NewsApi;
import com.android.ifnewshub.model.News;
import com.android.ifnewshub.utils.AssetUtil;
import com.android.ifnewshub.utils.HtmlBuilder;

import java.util.List;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {

    private WebView webView;
    private String htmlBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        configWebView();
        loadHtmlTemplate();
        getNewsFlask();
    }

    private void getNewsFlask() {
        NewsApi api = new NewsApi();

        api.listNews().enqueue(new Callback<>() {
            @Override
            public void onResponse(@NonNull Call<List<News>> call, @NonNull Response<List<News>> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    showError("Resposta inválida");
                    return;
                }
                renderNews(response.body());
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
                .replace("{{news_count}}", String.valueOf(list.size()));

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
        webView.addJavascriptInterface(this, "Android");

        webView.setWebViewClient(new WebViewClient());
        WebView.setWebContentsDebuggingEnabled(true);
    }

    @JavascriptInterface
    public void updateNews() {
        getNewsFlask();
    }
}
