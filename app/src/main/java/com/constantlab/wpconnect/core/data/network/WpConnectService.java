package com.constantlab.wpconnect.core.data.network;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Constant-Lab LLP on 17-04-2017.
 */

public interface WpConnectService {

    @POST("wp-json/jwt-auth/v1/token")
    @FormUrlEncoded
    Call<ResponseBody> login(@Field("username") String username, @Field("password") String password);

    @POST("wp-json/jwt-auth/v1/token/validate")
    Call<ResponseBody> validateToken(@Header("Authorization") String headerValue);


    @GET()
    Call<ResponseBody> getInstagramPosts(@Url String URL, @Query("access_token") String accessToken);

    @GET()
    Call<ResponseBody> getInstagramPosts(@Url String nextPageURL);

    @GET()
    Call<ResponseBody> getFacebookPosts(@Url String URL, @Query("access_token") String accessToken);

    @GET()
    Call<ResponseBody> getFacebookPosts(@Url String nextPageURL);


    @POST("wp-json/wp/v2/comments")
    @FormUrlEncoded
    Call<ResponseBody> commentOnPost(@Header("Authorization") String headerValue, @Field("content") String content,
                                     @Field("post") String postID);


    @GET("wp-json/wp/v2/posts?_embed")
    Call<ResponseBody> featuredPosts(@Query("categories") Integer categoryID);

    @GET("wp-json/wp/v2/comments")
    Call<ResponseBody> getComments(@Query("post") String postID, @Query("page") Integer page, @Query("per_page") Integer limit);

    @GET("wp-json/wp/v2/posts?_embed")
    Call<ResponseBody> getPosts(@Query("categories") Long categoryID, @Query("page") Integer page, @Query("per_page") Integer limit);

    @GET("wp-json/wp/v2/categories")
    Call<ResponseBody> getCategories(@Query("page") int pageNumber, @Query("per_page") int limit);


    @GET("wp-json/wp/v2/posts?_embed")
    Call<ResponseBody> getSearchResults(
            @Query("search") String query,
            @Query("page") Integer pageNumber,
            @Query("per_page") Integer limit);


    @GET("/wp-json/wp/v2/media")
    Call<ResponseBody> getPostDetails(@Query("include") String ids);


    @GET()
    Call<ResponseBody> getYouTubePlaylists(@Url String url,
                                           @Query("pageToken") String token,
                                           @Query("maxResults") int maxResults,
                                           @Query("channelId") String channelId,
                                           @Query("key") String serverKey
    );

    @GET()
    Call<ResponseBody> getYouTubeVideosForPlaylist(@Url String url,
                                                   @Query("pageToken") String token,
                                                   @Query("maxResults") int maxResults,
                                                   @Query("playlistId") String playlistId,
                                                   @Query("key") String serverKey
    );


    @GET()
    Call<ResponseBody> getYouTubeRecentVideos(@Url String url,
                                              @Query("pageToken") String token,
                                              @Query("maxResults") int maxResults,
                                              @Query("channelId") String channelId,
                                              @Query("key") String serverKey
    );

    @GET()
    Call<ResponseBody> getYouTubeVideos(@Url String url,
                                        @Query("pageToken") String token,
                                        @Query("maxResults") int maxResults,
                                        @Query("key") String serverKey,
                                        @Query("id") String ids
    );


    @GET
    Call<ResponseBody> getNavigation(@Url String url);

}
