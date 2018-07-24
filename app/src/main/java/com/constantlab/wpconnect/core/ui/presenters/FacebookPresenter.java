package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.config.Config;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Social;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.FacebookContract;
import com.constantlab.wpconnect.core.ui.contracts.FacebookContract.ViewActions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 27-04-2017.
 */

public class FacebookPresenter extends BasePresenter<FacebookContract.FacebookView> implements ViewActions {
    private final DataManager mDataManager;

    public FacebookPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }


    @Override
    public void onInitialFacebookRequested(String pageId) {
        String BASE_URL_FACEBOOK = "https://graph.facebook.com/v2.7/" + pageId + "/posts?date_format=U&fields=comments.limit(50).summary(1),likes.limit(0).summary(1),from,picture,message,story,name,link,id,created_time,full_picture,source,type";
        mDataManager.getFacebookPosts(BASE_URL_FACEBOOK, Config.FACEBOOK_TOKEN, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                readFacebookResponse(responseBody);
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
        mDataManager.getFacebookPosts(nextURL, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                readFacebookResponse(responseBody);
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

    private void readFacebookResponse(ResponseBody responseBody) {
        try {
            JSONObject object = new JSONObject(responseBody.string());
            boolean isNextPageAvailable = false;
            String nextPageURL = "";
            if (object.has(Constants.PAGING_KEY)) {
                JSONObject paging = object.getJSONObject(Constants.PAGING_KEY);
                if (paging != null) {
                    if (paging.has(Constants.NEXT_PAGE_KEY)) {
                        isNextPageAvailable = true;
                        nextPageURL = paging.getString(Constants.NEXT_PAGE_KEY);
                    } else {
                        isNextPageAvailable = false;
                    }
                } else {
                    isNextPageAvailable = false;
                }
            } else {
                isNextPageAvailable = false;
            }

            JSONArray data = object.getJSONArray(Constants.DATA_KEY);
            if (data != null && data.length() > 0) {
                List<Social> facebookItems = new ArrayList<Social>();
                for (int i = 0; i < data.length(); i++) {
                    JSONObject fbPost = data.getJSONObject(i);
                    Social post = new Social();
                    post.setSocialType(Social.SOCIAL_FACEBOOK);
                    post.setId(fbPost.getString(Constants.ID_KEY));
                    post.setType(fbPost.getString(Constants.TYPE_KEY));
                    post.setUserName(fbPost.getJSONObject(Constants.FROM_KEY).getString(Constants.FB_NAME_KEY));
                    post.setUserProfilePhotoUrl("https://graph.facebook.com/" + fbPost.getJSONObject(Constants.FROM_KEY).getString(Constants.ID_KEY) + "/picture?type=large");
                    post.setCreatedTime(new Date(fbPost.getLong(Constants.CREATED_TIME_KEY) * 1000));
                    post.setLikesCount(fbPost.getJSONObject(Constants.LIKES_KEY).getJSONObject(Constants.FB_SUMMARY_KEY).getInt(Constants.FB_TOTAL_COUNT_KEY));
                    post.setCommentsCount(fbPost.getJSONObject(Constants.COMMENTS_KEY).getJSONObject(Constants.FB_SUMMARY_KEY).getInt(Constants.FB_TOTAL_COUNT_KEY));
                    if (fbPost.has(Constants.LINK_KEY))
                        post.setLink(fbPost.getString(Constants.LINK_KEY));
                    else
                        post.setLink("https://www.facebook.com/" + post.getId());

                    if (post.getType().equals(Constants.VIDEO_KEY)) {
                        post.setVideoUrl(fbPost.getString(Constants.FB_VIDEO_SOURCE_KEY));
                    }

                    if (fbPost.has(Constants.FB_FULL_PICTURE_KEY)) {
                        post.setImageUrl(fbPost.getString(Constants.FB_FULL_PICTURE_KEY));
                    }

                    if (fbPost.has(Constants.FB_MESSAGE_KEY)) {
                        post.setCaption(fbPost.getString(Constants.FB_MESSAGE_KEY));
                    } else if (fbPost.has(Constants.FB_STORY_KEY)) {
                        post.setCaption(fbPost.getString(Constants.FB_STORY_KEY));
                    } else if (fbPost.has(Constants.FB_NAME_KEY)) {
                        post.setCaption(fbPost.getString(Constants.FB_NAME_KEY));
                    } else {
                        post.setCaption("");
                    }
                    facebookItems.add(post);
                }
                mView.showFacebookPosts(facebookItems, isNextPageAvailable, nextPageURL);
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
