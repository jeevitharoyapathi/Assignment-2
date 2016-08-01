package com.jeevitharoyapathi.assignment_2.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by jeevitha.royapathi on 7/31/16.
 */
public class ArticleResponse {
    public Result getResult() {
        return mResult;
    }

    public void setResult(Result result) {
        mResult = result;
    }

    @SerializedName("response")
    private Result mResult;

    public static class Result {

        public List<Article> getArticles() {
            return mArticles;
        }

        public void setArticles(List<Article> articles) {
            mArticles = articles;
        }

        @SerializedName("docs")
        private List<Article> mArticles;


    }

}
