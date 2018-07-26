package prima.optimasi.indonesia.primaberita.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Constant-Lab LLP on 22-08-2016.
 */
public class Gallery implements Parcelable {
    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Gallery> CREATOR = new Parcelable.Creator<Gallery>() {
        @Override
        public Gallery createFromParcel(Parcel in) {
            return new Gallery(in);
        }

        @Override
        public Gallery[] newArray(int size) {
            return new Gallery[size];
        }
    };
    String thumbnail;
    String fullSize;

    public Gallery() {
    }

    protected Gallery(Parcel in) {
        thumbnail = in.readString();
        fullSize = in.readString();
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getFullSize() {
        return fullSize;
    }

    public void setFullSize(String fullSize) {
        this.fullSize = fullSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(thumbnail);
        dest.writeString(fullSize);
    }
}