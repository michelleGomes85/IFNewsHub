package com.android.ifnewshub;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.android.ifnewshub.model.News;
import com.android.ifnewshub.utils.AssetUtil;
import com.android.ifnewshub.utils.JsonUtils;

public class NewsDetailsActivity extends AppCompatActivity {

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_detail);

        configWebView();

        String json = getIntent().getStringExtra("news_json");
        News news = JsonUtils.jsonToNews(json);

        String html = loadTemplate(news);

        webView.loadDataWithBaseURL(
                "file:///android_asset/",
                html,
                "text/html",
                "utf-8",
                null
        );

    }

    private String loadTemplate(News news) {

        String template = AssetUtil.readAsset(this, "news_details_template.html");

        String tagsHtml = "";
        if (news.getTags() != null) {
            StringBuilder sb = new StringBuilder();

            for (String tag : news.getTags()) {
                sb.append("<span class=\"tag-chip\">").append(safe(tag)).append("</span>");
            }

            tagsHtml = sb.toString();
        }

        return template
                .replace("{{title}}", safe(news.getTitle()))
                .replace("{{description}}", safe(news.getDescription()))
                .replace("{{link}}", safe(news.getLink()))
                .replace("{{tags}}", tagsHtml);
    }

    private String safe(String text) {
        return text == null ? "" : text;
    }

    @SuppressLint("SetJavaScriptEnabled")
    private void configWebView() {
        webView = findViewById(R.id.webview_details);
        WebSettings ws = webView.getSettings();
        ws.setJavaScriptEnabled(true);
        ws.setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebViewClient(new WebViewClient());
        WebView.setWebContentsDebuggingEnabled(true);

        webView.addJavascriptInterface(new Object() {
            @android.webkit.JavascriptInterface
            public void closePage() {
                runOnUiThread(() -> finish());
            }
        }, "Android");
    }
}
