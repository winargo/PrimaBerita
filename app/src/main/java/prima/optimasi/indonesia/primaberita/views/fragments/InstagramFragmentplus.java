package prima.optimasi.indonesia.primaberita.views.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Social;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.InstagramContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.InstagramPresenter;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.InstagramPresenterplus;
import prima.optimasi.indonesia.primaberita.core.util.DisplayMetricsUtil;
import prima.optimasi.indonesia.primaberita.views.activities.MainActivity;
import prima.optimasi.indonesia.primaberita.views.activities.PhotoDetailActivity;
import prima.optimasi.indonesia.primaberita.views.adapters.InstagramAdapter;
import prima.optimasi.indonesia.primaberita.views.adapters.InstagramAdapterplus;
import prima.optimasi.indonesia.primaberita.views.custom.EndlessRecyclerViewOnScrollListener;

/**
 * Created by Constant-Lab LLP on 03-08-2016.
 */
public class InstagramFragmentplus extends BaseFragment implements InstagramContract.InstagramView, InstagramAdapterplus.InteractionListener {

    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;
    InstagramAdapterplus mInstagramAdapter;
    boolean isNextPageAvailable;
    String nextPageURL;
    ProgressBar progressBar;
    InstagramPresenterplus mPresenter;
    RecyclerView mRecyclerView;
    String title;
    String pageId;
    private AppCompatActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInstagramAdapter = new InstagramAdapterplus(getContext());
        mPresenter = new InstagramPresenterplus(DataManager.getInstance(getContext()));
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
            String[] arguments = getArguments().getStringArray(Constants.TAG_FOR_ARGUMENTS);
            pageId = arguments[0];
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (Config.INSTAGRAM_TOKEN_PLUS != null && !Config.INSTAGRAM_TOKEN_PLUS.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_instagram, container, false);
            if (getActivity() != null) {
                initView(view);
                mPresenter.attachView(this);
                if (mInstagramAdapter.isEmpty()) {
                    mPresenter.onInitialInstagramRequested(pageId);
                    mInstagramAdapter.setListInteractionListener(this);
                }
            }
        } else {
            view = inflater.inflate(R.layout.fragment_configuration_message, container, false);
            ((TextView) view.findViewById(R.id.message_tv)).setText("Instagram Not Configured");
        }
        setTitle();
        ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
        return view;
    }

    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        progressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mInstagramAdapter);
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
                switch (mInstagramAdapter.getItemViewType(position)) {
                    case InstagramAdapter.VIEW_TYPE_LOADING:
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
                if (isNextPageAvailable) {
                    if (mInstagramAdapter.addLoadingView()) {
                        mPresenter.onListEndReached(nextPageURL);
                    }
                }
            }
        };
    }


    private void setTitle() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title != null ? title : "");
    }

    @Override
    public void showProgress() {
        if (mInstagramAdapter.isEmpty())
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        mInstagramAdapter.removeLoadingView();
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showInstagramPosts(List<Social> instagramList, boolean isNextPage, String nextPageURL) {
        if (mInstagramAdapter.getViewType() != InstagramAdapter.VIEW_TYPE_LIST) {
            mInstagramAdapter.removeAll();
            mInstagramAdapter.setViewType(InstagramAdapter.VIEW_TYPE_LIST);
        }

        this.isNextPageAvailable = isNextPage;
        this.nextPageURL = nextPageURL;
        mInstagramAdapter.addItems(instagramList);
    }


    @Override
    public void onDestroyView() {
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(null);
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onListClick(Social instagramPost, View sharedElementView, int adapterPosition) {
        if (instagramPost.getType().equals("image")) {
            Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
            commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_IMG);
            commentIntent.putExtra(PhotoDetailActivity.URL, instagramPost.getImageUrl());
            commentIntent.putExtra(PhotoDetailActivity.CAPTION, instagramPost.getCaption());
            startActivity(commentIntent);
        } else {
            Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
            commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_VID);
            commentIntent.putExtra(PhotoDetailActivity.URL, instagramPost.getVideoUrl());
            startActivity(commentIntent);
        }
    }

    private void markFavorite(Social photo, int position) {
        if (applicationMain.mSocialFavorites != null) {
            applicationMain.mSocialFavorites.add(photo);
        } else {
            applicationMain.mSocialFavorites = new ArrayList<>();
            applicationMain.mSocialFavorites.add(photo);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        String json = gson.toJson(applicationMain.mSocialFavorites);
        sharedPreferences.edit().putString(Constants.KEY_FOR_INSTAGRAM_FAVORITES, json).apply();
        mInstagramAdapter.notifyItemChanged(position);
        Toast.makeText(getContext().getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite(Social photo, int position) {
        if (applicationMain.mSocialFavorites != null) {
            applicationMain.mSocialFavorites.remove(photo);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
            Gson gson = new Gson();
            String json = gson.toJson(applicationMain.mSocialFavorites);
            sharedPreferences.edit().putString(Constants.KEY_FOR_INSTAGRAM_FAVORITES, json).apply();
            mInstagramAdapter.notifyItemChanged(position);
            Toast.makeText(getContext().getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRemoveFromFavorites(Social instagramPost, View sharedElementView, int adapterPosition) {
        removeFromFavorite(instagramPost, adapterPosition);
    }

    @Override
    public void onAddToFavorites(Social instagramPost, View sharedElementView, int adapterPosition) {
        markFavorite(instagramPost, adapterPosition);
    }

    @Override
    public void onPlayVideoClick(Social instagramPost, View sharedElementView, int adapterPosition) {
        Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
        commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_VID);
        commentIntent.putExtra(PhotoDetailActivity.URL, instagramPost.getVideoUrl());
        startActivity(commentIntent);
    }
}
