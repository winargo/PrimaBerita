package com.constantlab.wpconnect.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Constant-Lab LLP on 16-08-2016.
 */
public class User implements Parcelable {

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private long id;
    private String profilePhoto;
    private String username;
    private String email;

    public User() {
    }

    protected User(Parcel in) {
        username = in.readString();
        email = in.readString();
        id = in.readLong();
        profilePhoto = in.readString();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getProfilePhoto() {
        return profilePhoto;
    }

    public void setProfilePhoto(String profilePhoto) {
        this.profilePhoto = profilePhoto;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(username);
        dest.writeString(email);
        dest.writeLong(id);
        dest.writeString(profilePhoto);
    }
}