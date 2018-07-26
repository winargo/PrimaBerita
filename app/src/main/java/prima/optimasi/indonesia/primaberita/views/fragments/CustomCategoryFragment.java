package prima.optimasi.indonesia.primaberita.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.CustomCategoryContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.CustomCategoryPresenter;
import prima.optimasi.indonesia.primaberita.core.util.DisplayMetricsUtil;
import prima.optimasi.indonesia.primaberita.views.activities.CommentsActivity;
import prima.optimasi.indonesia.primaberita.views.activities.MainActivity;
import prima.optimasi.indonesia.primaberita.views.activities.SearchPostsActivity;
import prima.optimasi.indonesia.primaberita.views.adapters.PostsAdapter;
import prima.optimasi.indonesia.primaberita.views.custom.EndlessRecyclerViewOnScrollListener;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 19-07-2016.
 */
public class CustomCategoryFragment extends BaseFragment implements CustomCategoryContract.CustomCategoryView, PostsAdapter.InteractionListener {

    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;
    ProgressBar progressBar;
    CustomCategoryPresenter mPresenter;
    RecyclerView mRecyclerView;
    long categoryId;
    String title;
    private AppCompatActivity mActivity;
    private PostsAdapter mPostsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
            String[] arguments = getArguments().getStringArray(Constants.TAG_FOR_ARGUMENTS);
            categoryId = Long.parseLong(arguments[0]);
        }
        mPresenter = new CustomCategoryPresenter(DataManager.getInstance(getContext()));
        mPostsAdapter = new PostsAdapter(getContext());
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_category, container, false);
        if (getActivity() != null) {
            initView(view);
            mPresenter.attachView(this);
            ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
            if (mPostsAdapter.isEmpty()) {
                mPresenter.onInitialListRequested(categoryId);
                mPostsAdapter.setListInteractionListener(this);
                ((MainActivity) getActivity()).getSupportActionBar().setTitle(title != null ? title : "");
            }
        }
        return view;
    }

    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPostsAdapter);
        boolean isTabletLayout = DisplayMetricsUtil.isScreenW(SCREEN_TABLET_DP_WIDTH);
        mRecyclerView.setLayoutManager(setUpLayoutManager(isTabletLayout));
        mRecyclerView.addOnScrollListener(setupScrollListener(isTabletLayout,
                mRecyclerView.getLayoutManager()));
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    private RecyclerView.LayoutManager setUpLayoutManager(boolean isTabletLayout) {
        RecyclerView.LayoutManager layoutManager;
        if (!isTabletLayout) {
            layoutManager = new LinearLayoutManager(mActivity);
        } else {
            layoutManager = initGridLayoutManager(TAB_LAYOUT_SPAN_SIZE, TAB_LAYOUT_ITEM_SPAN_SIZE);
        }
        return layoutManager;
    }

    private RecyclerView.LayoutManager initGridLayoutManager(final int spanCount, final int itemSpanCount) {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mActivity, spanCount);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (mPostsAdapter.getItemViewType(position)) {
                    case PostsAdapter.VIEW_TYPE_LOADING:
                        // If it is a loading view we wish to accomplish a single item per row
                        return spanCount;
                    default:
                        // Else, define the number of items per row (considering TAB_LAYOUT_SPAN_SIZE).
                        return itemSpanCount;
                }
            }
        });
        return gridLayoutManager;
    }

    private EndlessRecyclerViewOnScrollListener setupScrollListener(boolean isTabletLayout,
                                                                    RecyclerView.LayoutManager layoutManager) {
        return new EndlessRecyclerViewOnScrollListener(isTabletLayout ?
                (GridLayoutManager) layoutManager : (LinearLayoutManager) layoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                if (mPostsAdapter.addLoadingView()) {
                    mPresenter.onListEndReached(categoryId, page, null);
                }

            }
        };
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.search_item) {
            startActivity(new Intent(getContext(), SearchPostsActivity.class));
        }
        return true;
    }


    @Override
    public void onOpenComments(Post post, View sharedElementView, int adapterPosition) {

        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra(Constants.POST_TAG, post);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void showProgress() {
        if (mPostsAdapter.isEmpty())
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        mPostsAdapter.removeLoadingView();
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showPosts(List<Post> postList) {
        if (mPostsAdapter.getViewType() != PostsAdapter.VIEW_TYPE_LIST) {
            mPostsAdapter.removeAll();
            mPostsAdapter.setViewType(PostsAdapter.VIEW_TYPE_LIST);
        }
        mPostsAdapter.addItems(postList);
    }
}
