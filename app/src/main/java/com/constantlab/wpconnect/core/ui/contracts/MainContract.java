package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

import org.json.JSONObject;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 18-05-2017.
 */

public interface MainContract {
    interface ViewActions {
        void onRequestFeaturedPosts(int categoryID);

        void onRequestOnlineNavigation();

    }

    interface MainView extends RemoteView {

        void onFeaturedPostsResponse(boolean success, List<Post> featuredPosts);

        void onFeaturedPostsEmpty();

        void onNavigationSetup(JSONObject navigation);

    }
}
