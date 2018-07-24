package com.constantlab.wpconnect.core.util;

import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.Author;
import com.constantlab.wpconnect.core.data.model.Category;
import com.constantlab.wpconnect.core.data.model.Comment;
import com.constantlab.wpconnect.core.data.model.Gallery;
import com.constantlab.wpconnect.core.data.model.Image;
import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.data.model.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import timber.log.Timber;

/**
 * Created by Constant-Lab LLP on 31-08-2017.
 */

public class WordpressUtil {
    public static List<Post> getPosts(JSONArray data) throws JSONException {
        List<Post> postList = new ArrayList<Post>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject current = data.getJSONObject(i);
            Post post = new Post();
            post.setId(String.valueOf(current.getInt(Constants.POST_ID_KEY)));
            String content = current.getJSONObject(Constants.POST_CONTENT_KEY).getString(Constants.POST_RENDERED_KEY);
            post.setContent(content);
            post.setTitle(current.getJSONObject(Constants.POST_TITLE_KEY).getString(Constants.POST_RENDERED_KEY));
            post.setExcerpt(current.getJSONObject(Constants.POST_EXCERPT_KEY).getString(Constants.POST_RENDERED_KEY));

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = format.parse(current.getString(Constants.POST_DATE_KEY));
                post.setDate(desiredFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            post.setLink(current.getString(Constants.POST_LINK_KEY));

            if (current.get(Constants.POST_FORMAT_KEY) instanceof String) {
                post.setPostFormat(current.getString(Constants.POST_FORMAT_KEY));
            }
            //List of featured images:
            List<Image> featuredImages = new ArrayList<Image>();
            Image image = new Image();
            JSONObject embed = current.getJSONObject(Constants.POST_EMBED_KEY);
            JSONArray featuredMedia = embed.optJSONArray(Constants.POST_FEATURED_MEDIA_KEY);
            if (featuredMedia != null)
                image.setUrl(featuredMedia.getJSONObject(0).getString(Constants.POST_SOURCE_URL_KEY));
            post.setCommentCount(embed.has(Constants.POST_REPLIES_KEY) ? embed.getJSONArray(Constants.POST_REPLIES_KEY).getJSONArray(0).length() : 0);
            featuredImages.add(image);
            post.setFeaturedImage(featuredImages);

            //Author
            Author author = new Author();
            JSONArray a = embed.getJSONArray(Constants.POST_AUTHOR_KEY);
            JSONObject mAuthor = a.getJSONObject(0);
            author.setId(mAuthor.getString(Constants.POST_ID_KEY));
            author.setName(mAuthor.getString(Constants.NAME_KEY));
            JSONObject avatar = mAuthor.getJSONObject(Constants.POST_AUTHOR_AVATAR_KEY);
            author.setImage(avatar.getString("96"));
            post.setAuthor(author);


            //Category
            JSONArray wpTerms = embed.getJSONArray(Constants.POST_TERM_KEY);
            JSONArray categories = wpTerms.getJSONArray(0);

            if (categories != null) {
                Category category = new Category();
                category.setId(categories.getJSONObject(0).getLong(Constants.POST_ID_KEY));
                category.setName(categories.getJSONObject(0).getString(Constants.NAME_KEY));
                post.setCategory(category);
                post.setTotalCategories(categories.length());
            }

            postList.add(post);
        }
        return postList;
    }


    public static List<Comment> getComments(JSONArray data) throws JSONException {
        List<Comment> commentList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject o = data.getJSONObject(i);
            Comment comment = new Comment();
            comment.setCommentID(o.getLong(Constants.COMMENT_ID_KEY));
            comment.setCommentPostID(o.getLong(Constants.COMMENT_POST_ID_KEY));
            User user = new User();
            user.setId(o.getLong(Constants.POST_AUTHOR_KEY));
            user.setUsername(o.getString(Constants.AUTHOR_NAME_KEY));
            user.setProfilePhoto(o.getJSONObject(Constants.AUTHOR_AVATAR_URLS_KEY).getString("96"));
            comment.setUser(user);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MM-yyyy");
            try {
                Date date = format.parse(o.getString(Constants.POST_DATE_KEY));
                comment.setDate(desiredFormat.format(date));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            comment.setComment(o.getJSONObject(Constants.POST_CONTENT_KEY).getString(Constants.POST_RENDERED_KEY));
            commentList.add(comment);
        }
        return commentList;
    }

    public static List<Gallery> getGallery(JSONArray data) throws JSONException {
        List<Gallery> galleryList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            JSONObject o = data.getJSONObject(i);
            Gallery gallery = new Gallery();
            gallery.setFullSize(o.getString("source_url"));
            try {
                gallery.setThumbnail(o.getJSONObject("media_details").getJSONObject("sizes").getJSONObject("medium").getString(Constants.POST_SOURCE_URL_KEY));
            } catch (Exception e) {
                Timber.v(e.getMessage());
            }
            galleryList.add(gallery);
        }
        return galleryList;
    }

    public static List<Category> getCategories(JSONArray data) throws JSONException {
        List<Category> categoryList = new ArrayList<>();
        for (int i = 0; i < data.length(); i++) {
            Category category = new Category();
            JSONObject o = data.getJSONObject(i);
            category.setId(o.getLong(Constants.ID_KEY));
            category.setName(o.getString(Constants.NAME_KEY));
            category.setDescription(o.getString(Constants.DESCRIPTION_KEY));
            category.setPostCount(o.getLong(Constants.COUNT_KEY));
            categoryList.add(category);
        }
        return categoryList;
    }


    public static String html2text(String html) {
        String text = Jsoup.parse(html).text();
        text = text.replaceAll("\\[(.*?)\\]", "");
        return text;
    }
}
