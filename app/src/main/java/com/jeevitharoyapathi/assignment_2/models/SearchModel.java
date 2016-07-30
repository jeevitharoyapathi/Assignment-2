package com.jeevitharoyapathi.assignment_2.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class SearchModel implements Parcelable{
    String sortOrder;
    String beginDate;
    List<String> categories;

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
        }
    };

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }


    public SearchModel() {
    }

    protected SearchModel(Parcel in) {
        this.sortOrder = in.readString();
        this.beginDate = in.readString();
        this.categories = in.createStringArrayList();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sortOrder);
        dest.writeString(beginDate);
        dest.writeStringList(categories);
    }
}
