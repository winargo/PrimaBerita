package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

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
