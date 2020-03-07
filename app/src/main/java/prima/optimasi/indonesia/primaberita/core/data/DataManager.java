package prima.optimasi.indonesia.primaberita.core.data;

import android.content.Context;
import android.util.Log;

import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.data.network.RetrofitServiceFactory;
import prima.optimasi.indonesia.primaberita.core.data.network.WpConnectService;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 17-04-2017.
 */

public class DataManager {
    private static DataManager sInstance;
    private final WpConnectService mWpConnectService;


    private DataManager(Context context) {
        this.mWpConnectService = RetrofitServiceFactory.makePartysanService(context);
    }

    public static DataManager getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataManager(context);
        }
        return sInstance;
    }


    public void loginWithWordress(String username, String password, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.login(username, password).enqueue(remoteCallback);
    }

    public void getPosts(Long categoryID, Integer pageNumber, Integer limit, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getPosts(categoryID, pageNumber, limit).enqueue(remoteCallback);
    }


    public void getCategories(Integer pageNumber, Integer limit, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getCategories(pageNumber, limit).enqueue(remoteCallback);
    }

    public void getSearchResults(Integer pageNumber, Integer limit, String query, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getSearchResults(query, pageNumber, limit).enqueue(remoteCallback);
    }

    public void getInstagramPosts(String URL, String accessToken, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getInstagramPosts(URL, accessToken).enqueue(remoteCallback);
    }

    public void getInstagramPosts(String nextPageURL, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getInstagramPosts(nextPageURL).enqueue(remoteCallback);
    }

    public void getFacebookPosts(String URL, String accessToken, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getFacebookPosts(URL, accessToken).enqueue(remoteCallback);
    }


    public void getFacebookPosts(String nextPageURL, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getFacebookPosts(nextPageURL).enqueue(remoteCallback);
    }

    public void getFeaturedPosts(int categoryID, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.featuredPosts(categoryID).enqueue(remoteCallback);
    }

    public void getComments(String postID, Integer offset, Integer limit, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getComments(postID, offset, limit).enqueue(remoteCallback);
    }

    public void comment(String headerValue, long postID, String comment, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.commentOnPost(headerValue, comment, String.valueOf(postID)).enqueue(remoteCallback);
    }

    public void getPostDetails(String ids, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getPostDetails(ids).enqueue(remoteCallback);
    }

    public void getYouTubePlaylists(String channelId, String pageToken, int maxResults, RemoteCallback<ResponseBody> remoteCallback) {
        String url = "https://www.googleapis.com/youtube/v3/playlists?part=snippet&type=video";
        Log.e("chosenurl",url );
        mWpConnectService.getYouTubePlaylists(
                url,
                pageToken,
                maxResults,
                channelId,
                Config.YOUTUBE_TOKEN).enqueue(remoteCallback);
    }

    public void getYouTubeVideosForPlaylist(String playlistId, String pageToken, int maxResults, RemoteCallback<ResponseBody> remoteCallback) {
        String url = "https://www.googleapis.com/youtube/v3/playlistItems?part=snippet";
        Log.e("chosenurl",url );
        mWpConnectService.getYouTubeVideosForPlaylist(
                url,
                pageToken,
                maxResults,
                playlistId,
                Config.YOUTUBE_TOKEN).enqueue(remoteCallback);
    }

    public void getYouTubeVideos(String ids, String pageToken, int maxResults, RemoteCallback<ResponseBody> remoteCallback) {
        String url = "https://www.googleapis.com/youtube/v3/videos?part=snippet&type=video";
        Log.e("chosenurl",url );
        mWpConnectService.getYouTubeVideos(url, pageToken, maxResults, Config.YOUTUBE_TOKEN, ids).enqueue(remoteCallback);
    }

    public void getYouTubeRecentVideos(String channelId, String pageToken, int maxResults, RemoteCallback<ResponseBody> remoteCallback) {
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&order=date&type=video";
        Log.e("chosenurl",url );
        mWpConnectService.getYouTubeRecentVideos(
                url,
                pageToken,
                maxResults,
                channelId,
                Config.YOUTUBE_TOKEN).enqueue(remoteCallback);
    }

    public void getNavigation(String url, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.getNavigation(url).enqueue(remoteCallback);
    }

    public void validateUser(String headerValue, RemoteCallback<ResponseBody> remoteCallback) {
        mWpConnectService.validateToken(headerValue).enqueue(remoteCallback);
    }
}
