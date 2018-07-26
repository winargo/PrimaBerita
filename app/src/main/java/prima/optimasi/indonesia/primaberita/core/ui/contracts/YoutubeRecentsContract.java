package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 11-09-2017.
 */

public interface YoutubeRecentsContract {

    interface ViewActions {
        void onInitialListRequested(String channelId);

        void onListEndReached(String channelId, String nextToken, Integer limit);

    }

    interface YoutubeRecentsView extends RemoteView {

        void showEmpty();

        void showVideos(List<YoutubeVideo> videoList, String nextToken);

    }
}
