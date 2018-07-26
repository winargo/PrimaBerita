package prima.optimasi.indonesia.primaberita.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public class YoutubePlaylist implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<YoutubePlaylist> CREATOR = new Parcelable.Creator<YoutubePlaylist>() {
        @Override
        public YoutubePlaylist createFromParcel(Parcel in) {
            return new YoutubePlaylist(in);
        }

        @Override
        public YoutubePlaylist[] newArray(int size) {
            return new YoutubePlaylist[size];
        }
    };
    private String title;
    private String id;
    private String description;
    private String thumbUrl;
    private String image;
    private String channel;

    public YoutubePlaylist(String title, String id, String description, String thumbUrl, String image, String channel) {
        super();
        this.title = title;
        this.id = id;
        this.description = description;
        this.thumbUrl = thumbUrl;
        this.image = image;
        this.channel = channel;
    }

    protected YoutubePlaylist(Parcel in) {
        title = in.readString();
        id = in.readString();
        description = in.readString();
        thumbUrl = in.readString();
        image = in.readString();
        channel = in.readString();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbUrl() {
        return thumbUrl;
    }

    public void setThumbUrl(String thumbUrl) {
        this.thumbUrl = thumbUrl;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(id);
        dest.writeString(description);
        dest.writeString(thumbUrl);
        dest.writeString(image);
        dest.writeString(channel);
    }
}