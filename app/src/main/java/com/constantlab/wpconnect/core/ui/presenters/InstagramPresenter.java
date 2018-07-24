package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.config.Config;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Social;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.InstagramContract;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 24-04-2017.
 */

public class InstagramPresenter extends BasePresenter<InstagramContract.InstagramView> implements InstagramContract.ViewActions {


    private final DataManager mDataManager;

    public InstagramPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onInitialInstagramRequested(String pageId) {
        String BASE_URL_INSTAGRAM = "https://api.instagram.com/v1/users/" + pageId + "/media/recent";
        mDataManager.getInstagramPosts(BASE_URL_INSTAGRAM, Config.INSTAGRAM_TOKEN, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                readInstagramResponse(responseBody);
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

    @Override
    public void onListEndReached(String nextURL) {
        mDataManager.getInstagramPosts(nextURL, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                readInstagramResponse(responseBody);
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


    private void readInstagramResponse(ResponseBody responseBody) {
        try {
            JSONObject object = new JSONObject(responseBody.string());
            boolean isNextPageAvailable = false;
            String nextPageURL = "";
            if (object.has(Constants.PAGINATION_KEY)) {
                JSONObject pagination = object.getJSONObject(Constants.PAGINATION_KEY);
                if (pagination.has(Constants.INSTAGRAM_NEXT_PAGE_KEY)) {
                    isNextPageAvailable = true;
                    nextPageURL = pagination.getString(Constants.INSTAGRAM_NEXT_PAGE_KEY);
                } else {
                    isNextPageAvailable = false;
                }
            } else {
                isNextPageAvailable = false;
            }
            JSONArray data = object.getJSONArray(Constants.DATA_KEY);


            if (data != null && data.length() > 0) {
                List<Social> instagramPhotoList = new ArrayList<Social>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject photoJson = data.getJSONObject(i);
                    Social photo = new Social();
                    photo.setSocialType(Social.SOCIAL_INSTAGRAM);
                    photo.setId(photoJson.getString(Constants.ID_KEY));
                    photo.setType(photoJson.getString(Constants.TYPE_KEY));
                    photo.setUserName(photoJson.getJSONObject(Constants.INSTAGRAM_USER_KEY).getString(Constants.INSTAGRAM_USERNAME_KEY));
                    photo.setUserProfilePhotoUrl(photoJson.getJSONObject(Constants.INSTAGRAM_USER_KEY).getString(Constants.INSTAGRAM_PROFILE_PICTURE_KEY));
                    if (photoJson.has(Constants.INSTAGRAM_CAPTION_KEY) && !photoJson.isNull(Constants.INSTAGRAM_CAPTION_KEY)) {
                        photo.setCaption(photoJson.getJSONObject(Constants.INSTAGRAM_CAPTION_KEY).getString(Constants.INSTAGRAM_TEXT_KEY));
                        photo.setCaptionUsername(photoJson.getJSONObject(Constants.INSTAGRAM_CAPTION_KEY).getJSONObject(Constants.FROM_KEY).getString(Constants.INSTAGRAM_USERNAME_KEY));
                    }
                    photo.setImageUrl(photoJson.getJSONObject(Constants.INSTAGRAM_IMAGES_KEY).getJSONObject(Constants.INSTAGRAM_STANDARD_RESOLUTION_KEY).getString(Constants.INSTAGRAM_URL_KEY));
                    photo.setCreatedTime(new Date(photoJson.getLong(Constants.CREATED_TIME_KEY) * 1000));
                    photo.setLikesCount(photoJson.getJSONObject(Constants.LIKES_KEY).getInt(Constants.INSTAGRAM_COUNT_KEY));
                    photo.setLink(photoJson.getString(Constants.LINK_KEY));

                    if (photo.getType().equals(Constants.VIDEO_KEY)) {
                        photo.setVideoUrl(photoJson.getJSONObject(Constants.INSTAGRAM_VIDEOS_KEY).getJSONObject(Constants.INSTAGRAM_STANDARD_RESOLUTION_KEY).getString(Constants.INSTAGRAM_URL_KEY));
                    }

                    // TODO: Remove or check if there are at least two comments
                    photo.setCommentsCount(photoJson.getJSONObject(Constants.COMMENTS_KEY).getInt(Constants.INSTAGRAM_COUNT_KEY));
//                        photo.commentsArray = photoJson.getJSONObject(Constants.COMMENTS_KEY).getJSONArray(Constants.DATA_KEY);
                    instagramPhotoList.add(photo);
                }

                mView.showInstagramPosts(instagramPhotoList, isNextPageAvailable, nextPageURL);
            } else {
                mView.showEmpty();
            }
        } catch (Exception e) {
            if (!isViewAttached()) return;
            mView.hideProgress();
            mView.showError(e.getMessage());
        }
    }
}
