package com.constantlab.wpconnect.views.fragments;

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

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.config.Config;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Social;
import com.constantlab.wpconnect.core.ui.contracts.FacebookContract;
import com.constantlab.wpconnect.core.ui.presenters.FacebookPresenter;
import com.constantlab.wpconnect.core.util.DisplayMetricsUtil;
import com.constantlab.wpconnect.views.activities.MainActivity;
import com.constantlab.wpconnect.views.activities.PhotoDetailActivity;
import com.constantlab.wpconnect.views.adapters.FacebookAdapter;
import com.constantlab.wpconnect.views.custom.EndlessRecyclerViewOnScrollListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2016.
 */
public class FacebookFragment extends BaseFragment implements FacebookContract.FacebookView, FacebookAdapter.InteractionListener {

    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;
    FacebookAdapter mFacebookAdapter;
    boolean isNextPageAvailable;
    String nextPageURL;
    FacebookPresenter mPresenter;
    ProgressBar progressBar;
    RecyclerView mRecyclerView;
    String title;
    String pageId;
    private AppCompatActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FacebookPresenter(DataManager.getInstance(getContext()));
        mFacebookAdapter = new FacebookAdapter(getContext());
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
        if (Config.FACEBOOK_TOKEN != null && !Config.FACEBOOK_TOKEN.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_facebook, container, false);
            if (getActivity() != null) {
                initView(view);
                mPresenter.attachView(this);
                if (mFacebookAdapter.isEmpty()) {
                    mPresenter.onInitialFacebookRequested(pageId);
                    mFacebookAdapter.setListInteractionListener(this);
                }
            }
        } else {
            view = inflater.inflate(R.layout.fragment_configuration_message, container, false);
            ((TextView) view.findViewById(R.id.message_tv)).setText("Facebook Not Configured");
        }
        setTitle();
        ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
        return view;
    }

    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFacebookAdapter);
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
                switch (mFacebookAdapter.getItemViewType(position)) {
                    case FacebookAdapter.VIEW_TYPE_LOADING:
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
                    if (mFacebookAdapter.addLoadingView()) {
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
        if (mFacebookAdapter.isEmpty())
            progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
        mFacebookAdapter.removeLoadingView();
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showFacebookPosts(List<Social> faceBookList, boolean isNextPage, String nextPageURL) {
        if (mFacebookAdapter.getViewType() != FacebookAdapter.VIEW_TYPE_LIST) {
            mFacebookAdapter.removeAll();
            mFacebookAdapter.setViewType(FacebookAdapter.VIEW_TYPE_LIST);
        }

        this.isNextPageAvailable = isNextPage;
        this.nextPageURL = nextPageURL;
        mFacebookAdapter.addItems(faceBookList);
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

    private void markFavorite(Social item, int position) {
        if (applicationMain.mSocialFavorites != null) {
            applicationMain.mSocialFavorites.add(item);
        } else {
            applicationMain.mSocialFavorites = new ArrayList<>();
            applicationMain.mSocialFavorites.add(item);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        String json = gson.toJson(applicationMain.mSocialFavorites);
        sharedPreferences.edit().putString(Constants.KEY_FOR_INSTAGRAM_FAVORITES, json).apply();
        mFacebookAdapter.notifyItemChanged(position);
        Toast.makeText(getContext().getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite(Social item, int position) {
        if (applicationMain.mSocialFavorites != null) {
            applicationMain.mSocialFavorites.remove(item);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
            Gson gson = new Gson();
            String json = gson.toJson(applicationMain.mSocialFavorites);
            sharedPreferences.edit().putString(Constants.KEY_FOR_INSTAGRAM_FAVORITES, json).apply();
            mFacebookAdapter.notifyItemChanged(position);
            Toast.makeText(getContext().getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListClick(Social facebookPost, View sharedElementView, int adapterPosition) {
        if (facebookPost.getType().equals("photo")) {
            Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
            commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_IMG);
            commentIntent.putExtra(PhotoDetailActivity.URL, facebookPost.getImageUrl());
            commentIntent.putExtra(PhotoDetailActivity.CAPTION, facebookPost.getCaption());
            startActivity(commentIntent);
        } else if (facebookPost.getType().equals("video")) {
            Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
            commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_VID);
            commentIntent.putExtra(PhotoDetailActivity.URL, facebookPost.getVideoUrl());
            startActivity(commentIntent);
        }
    }

    @Override
    public void onRemovedFromFavorites(Social facebookPost, View sharedElementView, int adapterPosition) {
        removeFromFavorite(facebookPost, adapterPosition);
    }

    @Override
    public void onAddedToFavorites(Social facebookPost, View sharedElementView, int adapterPosition) {
        markFavorite(facebookPost, adapterPosition);
    }


    @Override
    public void onPlayVideoClick(Social facebookPost, View sharedElementView, int adapterPosition) {
        Intent commentIntent = new Intent(getContext(), PhotoDetailActivity.class);
        commentIntent.putExtra(PhotoDetailActivity.TYPE, PhotoDetailActivity.TYPE_VID);
        commentIntent.putExtra(PhotoDetailActivity.URL, facebookPost.getVideoUrl());
        startActivity(commentIntent);
    }
}
