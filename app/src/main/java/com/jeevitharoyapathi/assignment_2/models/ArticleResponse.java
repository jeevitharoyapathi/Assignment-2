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

        public metaData getMeta() {
            return mMeta;
        }

        public void setMeta(metaData meta) {
            mMeta = meta;
        }

        @SerializedName("meta")
        private metaData mMeta;

    }

    public static class metaData {

        @SerializedName("hits")
        private int mHits;

        @SerializedName("offset")
        private int mOffset;

        public int getHits() {
            return mHits;
        }

        public void setHits(final int hits) {
            mHits = hits;
        }

        public int getOffset() {
            return mOffset;
        }

        public void setOffset(final int offset) {
            mOffset = offset;
        }
    }
}
