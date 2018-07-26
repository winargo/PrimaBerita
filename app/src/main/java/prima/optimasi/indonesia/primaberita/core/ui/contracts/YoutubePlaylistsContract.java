package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.YoutubePlaylist;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public interface YoutubePlaylistsContract {
    interface ViewActions {
        void onInitialListRequested(String channelId);

        void onListEndReached(String channelId, String nextToken, Integer limit);

    }

    interface YoutubePlaylistsView extends RemoteView {

        void showEmpty();

        void showPlaylists(List<YoutubePlaylist> playlistList, String nextToken);

    }
}
