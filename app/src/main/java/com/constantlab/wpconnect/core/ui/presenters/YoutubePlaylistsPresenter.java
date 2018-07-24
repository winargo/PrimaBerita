package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.YoutubePlaylist;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.YoutubePlaylistsContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public class YoutubePlaylistsPresenter extends BasePresenter<YoutubePlaylistsContract.YoutubePlaylistsView> implements YoutubePlaylistsContract.ViewActions {

    private static final int ITEM_REQUEST_LIMIT = 20;

    private final DataManager mDataManager;

    public YoutubePlaylistsPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onInitialListRequested(String channelId) {
        requestPlaylists(channelId, null, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onListEndReached(String channelId, String nextToken, Integer limit) {
        requestPlaylists(channelId, nextToken, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }

    private void requestPlaylists(String channelId, String pageToken, Integer limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getYouTubePlaylists(channelId, pageToken, limit, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONObject object = new JSONObject(responseBody.string());
                    String pageToken = null;
                    try {
                        pageToken = object.getString("nextPageToken");
                    } catch (JSONException e) {
                        Timber.v("JSONException: %s", e.getMessage());
                    }

                    JSONArray array = object.getJSONArray("items");
                    if (array != null && array.length() > 0) {
                        List<YoutubePlaylist> playlistList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jsonObject = array.getJSONObject(i);
                                JSONObject jsonSnippet = array.getJSONObject(i).getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String description = jsonSnippet.getString("description");
                                String channel = jsonSnippet.getString("channelTitle");
                                String id = jsonObject.getString("id");
                                String thumbUrl = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                String image = jsonSnippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
                                playlistList.add(new YoutubePlaylist(title, id, description, thumbUrl, image, channel));
                            } catch (JSONException e) {
                                Timber.v("JSONException: %s", e.getMessage());
                            }
                        }
                        mView.showPlaylists(playlistList, pageToken);
                    } else {
                        mView.showEmpty();
                    }
                } catch (Exception e) {
                    mView.showError(e.getMessage());
                }
            }

            @Override
            public void onUnauthorized() {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError("Unauthorized");
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError(throwable.getMessage());
            }
        });
    }
}
