package com.constantlab.wpconnect.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Constant-Lab LLP on 04-08-2016.
 */
public class Image implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Image> CREATOR = new Parcelable.Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };
    String id;
    private String caption;
    private String url;

    protected Image(Parcel in) {
        id = in.readString();
        caption = in.readString();
        url = in.readString();
    }

    public Image(){}
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(caption);
        dest.writeString(url);
    }
}
