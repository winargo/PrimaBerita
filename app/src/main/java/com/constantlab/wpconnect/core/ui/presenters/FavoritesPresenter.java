package com.constantlab.wpconnect.core.ui.presenters;

import android.content.Context;

import com.constantlab.wpconnect.application.ApplicationMain;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.FavoritesContract;

/**
 * Created by Constant-Lab LLP on 21-04-2017.
 */

public class FavoritesPresenter extends BasePresenter<FavoritesContract.FavoritesView> implements FavoritesContract.ViewActions {

    private ApplicationMain applicationMain;

    public FavoritesPresenter(Context context) {
        this.applicationMain = (ApplicationMain) context.getApplicationContext();
    }

    @Override
    public void onLoadFavoritesRequested() {
        loadFavorites();
    }

    private void loadFavorites() {
        if (!isViewAttached()) return;
        mView.showProgress();
        if (applicationMain.favoritePosts != null) {
            mView.hideProgress();
            mView.showPosts(applicationMain.favoritePosts);
        } else {
            mView.hideProgress();
            mView.showEmpty();
        }
    }
}
