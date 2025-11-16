package com.android.ifnewshub.service;

import com.android.ifnewshub.model.News;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("noticias_if_bq")
    Call<List<News>> getNewsList();

    @GET("noticia/conteudo_texto")
    Call<News> getNewsContent(@Query("url") String url);
}
