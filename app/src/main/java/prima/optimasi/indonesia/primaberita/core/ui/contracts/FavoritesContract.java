package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

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
