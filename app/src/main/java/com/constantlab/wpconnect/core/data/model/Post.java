package com.constantlab.wpconnect.core.data.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;


public class Post implements Parcelable {


    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Post> CREATOR = new Parcelable.Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };
    private String id;
    private String date;
    private String content;
    private String title;
    private String excerpt;
    private String postModifiedDate;
    private String link;
    private int commentCount;
    private List<Image> featuredImage;
    private Category category;
    private Integer totalCategories;
    private Author author;
    private String postFormat;


    public Post() {
    }

    protected Post(Parcel in) {
        id = in.readString();
        date = in.readString();
        content = in.readString();
        title = in.readString();
        excerpt = in.readString();
        postModifiedDate = in.readString();
        link = in.readString();
        commentCount = in.readInt();
        if (in.readByte() == 0x01) {
            featuredImage = new ArrayList<Image>();
            in.readList(featuredImage, Image.class.getClassLoader());
        } else {
            featuredImage = null;
        }
        category = (Category) in.readValue(Category.class.getClassLoader());
        totalCategories = in.readByte() == 0x00 ? null : in.readInt();
        author = (Author) in.readValue(Author.class.getClassLoader());
        postFormat = in.readString();
    }

    public Integer getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(Integer totalCategories) {
        this.totalCategories = totalCategories;
    }

    public String getPostFormat() {
        return postFormat;
    }

    public void setPostFormat(String postFormat) {
        this.postFormat = postFormat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Post post = (Post) o;

        return id.equals(post.id);

    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getExcerpt() {
        return excerpt;
    }

    public void setExcerpt(String excerpt) {
        this.excerpt = excerpt;
    }

    public String getPostModifiedDate() {
        return postModifiedDate;
    }

    public void setPostModifiedDate(String postModifiedDate) {
        this.postModifiedDate = postModifiedDate;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public List<Image> getFeaturedImage() {
        return featuredImage;
    }

    public void setFeaturedImage(List<Image> featuredImage) {
        this.featuredImage = featuredImage;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(date);
        dest.writeString(content);
        dest.writeString(title);
        dest.writeString(excerpt);
        dest.writeString(postModifiedDate);
        dest.writeString(link);
        dest.writeInt(commentCount);
        if (featuredImage == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(featuredImage);
        }
        dest.writeValue(category);
        if (totalCategories == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(totalCategories);
        }
        dest.writeValue(author);
        dest.writeString(postFormat);
    }
}