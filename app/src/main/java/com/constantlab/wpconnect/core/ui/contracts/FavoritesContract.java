package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 21-04-2017.
 */

public interface FavoritesContract {
    interface ViewActions {
        void onLoadFavoritesRequested();
    }

    interface FavoritesView extends RemoteView {

        void showEmpty();

        void showPosts(List<Post> postList);

    }
}
