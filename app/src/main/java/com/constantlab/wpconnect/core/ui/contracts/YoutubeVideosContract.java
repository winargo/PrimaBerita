package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public interface YoutubeVideosContract {
    interface ViewActions {
        void onInitialListRequested(String playlistId);

        void onListEndReached(String playlistId, String nextToken, Integer limit);

    }

    interface YoutubeVideosView extends RemoteView {

        void showEmpty();

        void showVideos(List<YoutubeVideo> videoList, String nextToken);

    }
}
