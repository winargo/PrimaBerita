package prima.optimasi.indonesia.primaberita.core.ui.presenters;

import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Category;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.ui.base.BasePresenter;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.CategoriesContract;
import prima.optimasi.indonesia.primaberita.core.util.WordpressUtil;

import org.json.JSONArray;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 20-04-2017.
 */

public class CategoriesPresenter extends BasePresenter<CategoriesContract.CategoriesView> implements CategoriesContract.ViewActions {
    private static final int ITEM_REQUEST_INITIAL_OFFSET = 1;
    private static final int ITEM_REQUEST_LIMIT = 10;

    private DataManager mDataManager;

    public CategoriesPresenter(DataManager mDataManager) {

        this.mDataManager = mDataManager;
    }

    @Override
    public void onInitialListRequested() {
        getCategories(ITEM_REQUEST_INITIAL_OFFSET, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onListEndReached(Integer offset, Integer limit) {
        getCategories(offset, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }

    private void getCategories(Integer page, Integer limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getCategories(page, limit, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONArray data = new JSONArray(responseBody.string());
                    List<Category> categoryList = WordpressUtil.getCategories(data);
                    if (categoryList.size() > 0) {
                        mView.showCategories(categoryList);
                    } else {
                        mView.showEmpty();
                    }
                } catch (Exception e) {
                    if (!isViewAttached()) return;
                    mView.hideProgress();
                    mView.showError(e.getMessage());
                }
            }

            @Override
            public void onUnauthorized() {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError("Unauthorized");
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError(throwable.getMessage());
            }
        });
    }
}
