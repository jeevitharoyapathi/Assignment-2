package com.jeevitharoyapathi.assignment_2.api;

import android.support.annotation.Nullable;

import com.jeevitharoyapathi.assignment_2.models.ArticleResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by jeevitha.royapathi on 7/31/16.
 */
public interface ApiService {

    String BASE_URL = "articlesearch.json";

    @GET(BASE_URL)
    Call<ArticleResponse> getArticles(@Query("api-key") String ApiKey,
                                      @Query("q") String query,
                                      @Nullable @Query("begin_date") String date,
                                      @Query("fq") String categories,
                                      @Query("sort") String sort,
                                      @Query("page") int page);
}
