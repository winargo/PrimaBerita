package com.constantlab.wpconnect.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */


public class YoutubeVideo implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<YoutubeVideo> CREATOR = new Parcelable.Creator<YoutubeVideo>() {
        @Override
        public YoutubeVideo createFromParcel(Parcel in) {
            return new YoutubeVideo(in);
        }

        @Override
        public YoutubeVideo[] newArray(int size) {
            return new YoutubeVideo[size];
        }
    };
    private String title;
    private String id;
    private String updated;
    private String description;
    private String thumbUrl;
    private String image;
    private String channel;
    public YoutubeVideo(String title, String id, String updated, String description, String thumbUrl, String image, String channel) {
        super();
        this.title = title;
        this.id = id;
        this.updated = updated;
        this.description = description;
        this.thumbUrl = thumbUrl;
        this.image = image;
        this.channel = channel;
    }

    public YoutubeVideo() {
    }

    protected YoutubeVideo(Parcel in) {
        title = in.readString();
        id = in.readString();
        updated = in.readString();
        description = in.readString();
        thumbUrl = in.readString();
        image = in.readString();
        channel = in.readString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        YoutubeVideo video = (YoutubeVideo) o;

        return id.equals(video.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getTitle() {
        return title;
    }

    public String getId() {
        return id;
    }

    public String getUpdated() {
        return updated;
    }

    public String getDescription() {
        return description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public String getImage() {
        return image;
    }

    public String getChannel() {
        return channel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(updated);
        dest.writeString(description);
        dest.writeString(thumbUrl);
        dest.writeString(image);
        dest.writeString(channel);
    }
}