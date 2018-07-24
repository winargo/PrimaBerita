package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.CustomCategoryContract;
import com.constantlab.wpconnect.core.util.WordpressUtil;

import org.json.JSONArray;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 04-08-2017.
 */

public class CustomCategoryPresenter extends BasePresenter<CustomCategoryContract.CustomCategoryView> implements CustomCategoryContract.ViewActions {
    private static final int ITEM_REQUEST_INITIAL_OFFSET = 1;
    private static final int ITEM_REQUEST_LIMIT = 10;

    private final DataManager mDataManager;

    public CustomCategoryPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }


    @Override
    public void onInitialListRequested(long categoryID) {
        getPosts(categoryID, ITEM_REQUEST_INITIAL_OFFSET, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onListEndReached(long categoryID, Integer offset, Integer limit) {
        getPosts(categoryID, offset, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }


    private void getPosts(long categoryID, Integer offset, Integer limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getPosts(categoryID, offset, limit, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONArray data = new JSONArray(responseBody.string());
                    List<Post> postList = WordpressUtil.getPosts(data);
                    if (postList.size() > 0) {
                        mView.showPosts(postList);
                    } else {
                        mView.showEmpty();
                    }
                } catch (Exception e) {
                    if (!isViewAttached()) return;
                    mView.hideProgress();
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
