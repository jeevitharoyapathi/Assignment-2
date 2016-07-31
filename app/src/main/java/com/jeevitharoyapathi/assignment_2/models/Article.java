package com.jeevitharoyapathi.assignment_2.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

import java.util.List;
import java.util.Map;

@Parcel
public class Article {
    @SerializedName("web_url")
    String webUrl;

    @SerializedName("headline")
    Map<String, String> headline;

    public void setThumbNail(List<Media> thumbNail) {
        this.thumbNail = thumbNail;
    }

    @SerializedName("multimedia")
    List<Media> thumbNail;

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
        if (thumbNail.size() > 0) {
            return "http://www.nytimes.com/" + thumbNail.get(0).getUrl();
        } else {
            return "";
        }
    }

}
