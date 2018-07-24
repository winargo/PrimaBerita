package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 19-04-2017.
 */

public interface PostsContract {
    interface ViewActions {

        void onInitialListRequested(long categoryID);

        void onListEndReached(long categoryID, Integer offset, Integer limit);

    }

    interface PostsView extends RemoteView {

        void showEmpty();


        void showPosts(List<Post> postList);

    }
}
