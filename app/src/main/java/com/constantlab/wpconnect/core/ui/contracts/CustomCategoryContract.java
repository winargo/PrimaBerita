package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 04-08-2017.
 */

public interface CustomCategoryContract {
    interface ViewActions {
        void onInitialListRequested(long categoryID);

        void onListEndReached(long categoryID, Integer offset, Integer limit);

    }

    interface CustomCategoryView extends RemoteView {

        void showEmpty();

        void showPosts(List<Post> postList);

    }
}
