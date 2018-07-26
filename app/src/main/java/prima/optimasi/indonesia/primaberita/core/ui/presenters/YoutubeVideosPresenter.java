package prima.optimasi.indonesia.primaberita.core.ui.presenters;

import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.ui.base.BasePresenter;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.YoutubeVideosContract;

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

public class YoutubeVideosPresenter extends BasePresenter<YoutubeVideosContract.YoutubeVideosView> implements YoutubeVideosContract.ViewActions {
    private static final int ITEM_REQUEST_LIMIT = 20;

    private final DataManager mDataManager;

    public YoutubeVideosPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }


    @Override
    public void onInitialListRequested(String playlistId) {
        requestVideos(playlistId, null, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onListEndReached(String playlistId, String nextToken, Integer limit) {
        requestVideos(playlistId, nextToken, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }

    private void requestVideos(String playlistId, String token, int limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getYouTubeVideosForPlaylist(playlistId, token, limit, new RemoteCallback<ResponseBody>() {
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
                        List<YoutubeVideo> videoList = new ArrayList<>();
                        for (int i = 0; i < array.length(); i++) {
                            try {
                                JSONObject jsonObject = array.getJSONObject(i);
                                JSONObject jsonSnippet = array.getJSONObject(i).getJSONObject("snippet");
                                String title = jsonSnippet.getString("title");
                                String updated = jsonSnippet.getString("publishedAt");
                                String description = jsonSnippet.getString("description");
                                String channel = jsonSnippet.getString("channelTitle");
                                String id;
                                try {
                                    id = jsonSnippet.getJSONObject("resourceId").getString("videoId");
                                } catch (Exception e) {
                                    id = jsonObject.getJSONObject("id").getString("videoId");
                                }
                                String thumbUrl = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                String image = null;
                                try {
                                    if (!jsonSnippet.getJSONObject("thumbnails").has("standard")) {
                                        image = jsonSnippet.getJSONObject("thumbnails").getJSONObject("standard").getString("url");
                                    } else {
                                        image = jsonSnippet.getJSONObject("thumbnails").getJSONObject("high").getString("url");
                                    }
                                } catch (Exception e) {
                                    Timber.v("%s", e.getMessage());
                                }

                                videoList.add(new YoutubeVideo(title, id, updated, description, thumbUrl, image, channel));
                            } catch (JSONException e) {
                                Timber.v("JSONException: %s", e.getMessage());
                            }
                        }
                        mView.showVideos(videoList, pageToken);
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
