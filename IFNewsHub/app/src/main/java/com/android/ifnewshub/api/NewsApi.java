package com.android.ifnewshub.api;

import com.android.ifnewshub.model.News;
import com.android.ifnewshub.utils.ConfigUtils;

import java.util.List;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.Call;

public class NewsApi {

    private final ApiService apiService;

    public NewsApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(ConfigUtils.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    // Buscar lista de notícias
    public Call<List<News>> listNews() {
        return apiService.getNewsList();
    }

    // Buscar conteúdo completo da notícia
    public Call<News> getNewsContent(String url) {
        return apiService.getNewsContent(url);
    }
}
