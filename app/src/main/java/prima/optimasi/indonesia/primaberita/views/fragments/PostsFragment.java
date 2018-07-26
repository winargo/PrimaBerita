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
import android.widget.TextView;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Category;
import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.PostsContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.PostPresenter;
import prima.optimasi.indonesia.primaberita.core.util.DisplayMetricsUtil;
import prima.optimasi.indonesia.primaberita.views.activities.CategoryPostsActivity;
import prima.optimasi.indonesia.primaberita.views.activities.CommentsActivity;
import prima.optimasi.indonesia.primaberita.views.adapters.PostsAdapter;
import prima.optimasi.indonesia.primaberita.views.custom.EndlessRecyclerViewOnScrollListener;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.util.List;


/**
 * Created by Constant-Lab LLP on 28-06-2016.
 */
public class PostsFragment extends BaseFragment implements PostsContract.PostsView, PostsAdapter.InteractionListener {
    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;
    PostPresenter mPresenter;
    Category category;
    ProgressBar progressBar;
    TextView comingSoon;
    ParallaxRecyclerView mRecyclerView;
    private AppCompatActivity mActivity;
    private PostsAdapter mPostsAdapter;

    public static PostsFragment newInstance(Category category) {
        PostsFragment postsFragment = new PostsFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.TAG_FOR_CATEGORY, category);
        postsFragment.setArguments(args);
        return postsFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            category = getArguments().getParcelable(Constants.TAG_FOR_CATEGORY);
            mPresenter = new PostPresenter(DataManager.getInstance(getContext()));
            mPostsAdapter = new PostsAdapter(getContext());
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        if (getActivity() != null) {
            initView(view);
            mPresenter.attachView(this);
            if (mPostsAdapter.isEmpty()) {
                mPresenter.onInitialListRequested(category.getId());
                mPostsAdapter.setListInteractionListener(this);
            }
            setTitle(category.getName());
        }
        return view;
    }

    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        comingSoon = (TextView) view.findViewById(R.id.comingsoon);
        mRecyclerView = (ParallaxRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mPostsAdapter);
        boolean isTabletLayout = DisplayMetricsUtil.isScreenW(SCREEN_TABLET_DP_WIDTH);
        mRecyclerView.setLayoutManager(setUpLayoutManager(isTabletLayout));
        mRecyclerView.addOnScrollListener(setupScrollListener(isTabletLayout,
                mRecyclerView.getLayoutManager()));

    }

    private void setTitle(String title) {
        ((CategoryPostsActivity) getActivity()).getSupportActionBar().setTitle(title);
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
                    mPresenter.onListEndReached(category.getId(), page, null);
                }
            }
        };
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return true;
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

    @Override
    public void onDestroyView() {
        mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onOpenComments(Post post, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(getContext(), CommentsActivity.class);
        intent.putExtra(Constants.POST_TAG, post);
        startActivity(intent);
    }
}
