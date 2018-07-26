package prima.optimasi.indonesia.primaberita.core.data.model;

import java.util.Date;

/**
 * Created by Constant-Lab LLP on 18-05-2017.
 */

public class Social {

    public static final int SOCIAL_FACEBOOK = 2;
    public static final int SOCIAL_INSTAGRAM = 4;
    String id;
    private String captionUsername;
    private String type;
    private String userName;
    private String userProfilePhotoUrl;
    private String caption;
    private String imageUrl;
    private String videoUrl;
    private String link;
    private Date createdTime;
    private int likesCount;
    private int commentsCount;
    private int socialType;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Social social = (Social) o;

        return id.equals(social.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getCaptionUsername() {
        return captionUsername;
    }

    public void setCaptionUsername(String captionUsername) {
        this.captionUsername = captionUsername;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserProfilePhotoUrl() {
        return userProfilePhotoUrl;
    }

    public void setUserProfilePhotoUrl(String userProfilePhotoUrl) {
        this.userProfilePhotoUrl = userProfilePhotoUrl;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Date createdTime) {
        this.createdTime = createdTime;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getSocialType() {
        return socialType;
    }

    public void setSocialType(int socialType) {
        this.socialType = socialType;
    }


}
