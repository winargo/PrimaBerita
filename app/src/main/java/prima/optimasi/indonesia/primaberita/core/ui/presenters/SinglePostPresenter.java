package prima.optimasi.indonesia.primaberita.core.ui.presenters;

import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.ui.base.BasePresenter;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.SinglePostContract;
import prima.optimasi.indonesia.primaberita.core.util.WordpressUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import timber.log.Timber;

/**
 * Created by Constant-Lab LLP on 18-05-2017.
 */

public class SinglePostPresenter extends BasePresenter<SinglePostContract.SinglePostView> implements SinglePostContract.ViewActions {
    private final DataManager mDataManager;

    public SinglePostPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onRequestGallery(String ids) {
        requestGallery(ids);
    }

    @Override
    public void onRequestYouTubeVideos(String ids) {
        requestVideos(ids);
    }

    private void requestVideos(String ids) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getYouTubeVideos(ids, null, 40, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONObject object = new JSONObject(responseBody.string());
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
                                String id = jsonObject.getString("id");
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
                        mView.onYouTubeVideos(true, videoList);
                    } else {
                        mView.onYouTubeVideos(false, null);
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

    private void requestGallery(String ids) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getPostDetails(ids, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONArray array = new JSONArray(responseBody.string());
                    List<Gallery> galleryList = WordpressUtil.getGallery(array);
                    if (galleryList.size() > 0) {
                        mView.onGalleryResponse(true, galleryList);
                    } else {
                        mView.onGalleryResponse(false, null);
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
