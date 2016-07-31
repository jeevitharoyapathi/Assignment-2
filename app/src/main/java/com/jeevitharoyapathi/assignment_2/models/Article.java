package com.jeevitharoyapathi.assignment_2.models;

import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.Map;

@Parcel
public class Article {
    @SerializedName("web_url")
    String webUrl;

    @SerializedName("headline")
    Map<String, String> headline;

    @SerializedName("multimedia")
    String thumbNail;

    @SerializedName("lead_paragraph")
    String paragraph;

    public String getParagraph() {
        return paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public String getHeadline() {
        return headline.get("main");
    }

    public String getThumbNail() {
        return thumbNail;
    }


    public Article(JSONObject jsonObject) {
        try {
            this.webUrl = jsonObject.getString("web_url");
            this.headline = jsonObject.getJSONObject("headline");
            setParagraph(jsonObject.getString("lead_paragraph"));

            JSONArray multimedia = jsonObject.getJSONArray("multimedia");
            if (multimedia.length() > 0) {
                JSONObject multimediaObj = multimedia.getJSONObject(0);
                this.thumbNail = "http://www.nytimes.com/" + multimediaObj.getString("url");
            } else {
                this.thumbNail = "";
            }
        } catch (JSONException e) {

        }
    }

    public static ArrayList<Article> fromJSONArray(JSONArray array) {
        ArrayList<Article> results = new ArrayList<>();
        for (int i = 0; i < array.length(); i++) {
            try {
                results.add(new Article(array.getJSONObject(i)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return results;
    }
}
