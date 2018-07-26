package prima.optimasi.indonesia.primaberita.core.ui.contracts;

import prima.optimasi.indonesia.primaberita.core.data.model.Category;
import prima.optimasi.indonesia.primaberita.core.ui.base.RemoteView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 20-04-2017.
 */

public interface CategoriesContract {
    interface ViewActions {
        void onInitialListRequested();

        void onListEndReached(Integer offset, Integer limit);

    }

    interface CategoriesView extends RemoteView {

        void showEmpty();


        void showCategories(List<Category> postList);

    }
}
