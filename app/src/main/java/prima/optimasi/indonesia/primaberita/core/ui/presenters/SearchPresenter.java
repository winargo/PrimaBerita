package prima.optimasi.indonesia.primaberita.core.ui.presenters;

import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.core.data.network.RemoteCallback;
import prima.optimasi.indonesia.primaberita.core.ui.base.BasePresenter;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.SearchContract;
import prima.optimasi.indonesia.primaberita.core.util.WordpressUtil;

import org.json.JSONArray;

import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 24-04-2017.
 */

public class SearchPresenter extends BasePresenter<SearchContract.SearchView> implements SearchContract.ViewActions {

    private static final int ITEM_REQUEST_INITIAL_OFFSET = 1;
    private static final int ITEM_REQUEST_LIMIT = 10;

    private final DataManager mDataManager;

    public SearchPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onRequestInitialSearch(String query) {
        requestSearch(query, ITEM_REQUEST_INITIAL_OFFSET, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onRequestSearch(String query, Integer offset, Integer limit) {
        requestSearch(query, offset, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }

    private void requestSearch(String query, final Integer offset, Integer limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getSearchResults(offset, limit, query, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONArray data = new JSONArray(responseBody.string());
                    List<Post> postList = WordpressUtil.getPosts(data);
                    if (postList.size() > 0) {
                        mView.showPosts(postList);
                    } else {
                        if (offset == 1) {
                            mView.showEmpty();
                        }
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
