package com.constantlab.wpconnect.core.ui.contracts;

import com.constantlab.wpconnect.core.data.model.Category;
import com.constantlab.wpconnect.core.ui.base.RemoteView;

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
