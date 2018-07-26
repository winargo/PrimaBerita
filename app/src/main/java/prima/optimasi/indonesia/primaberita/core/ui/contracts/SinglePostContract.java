package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 18-05-2017.
 */

public interface SinglePostContract {
    interface ViewActions {
        void onRequestGallery(String ids);

        void onRequestYouTubeVideos(String ids);
    }

    interface SinglePostView extends RemoteView {
        void onGalleryResponse(boolean success, List<Gallery> galleryList);

        void onYouTubeVideos(boolean success, List<YoutubeVideo> videoList);

    }
}
