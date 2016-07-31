package com.jeevitharoyapathi.assignment_2.models;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

/**
 * Created by jeevitha.royapathi on 7/31/16.
 */
@Parcel
public class Media {

    @SerializedName("width")
    int mWidth;

    @SerializedName("height")
    int mHeight;

    @SerializedName("url")
    String mUrl;

    @SerializedName("type")
    String mType;

    @SerializedName("subtype")
    String mSubtype;

    public int getWidth() {
        return mWidth;
    }

    public void setWidth(final int width) {
        mWidth = width;
    }

    public int getHeight() {
        return mHeight;
    }

    public void setHeight(final int height) {
        mHeight = height;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(final String url) {
        mUrl = url;
    }

    public String getType() {
        return mType;
    }

    public void setType(final String type) {
        mType = type;
    }

    public String getSubtype() {
        return mSubtype;
    }

    public void setSubtype(final String subtype) {
        mSubtype = subtype;
    }
}
