package com.constantlab.wpconnect.views.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.config.Config;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.YoutubePlaylist;
import com.constantlab.wpconnect.core.ui.contracts.YoutubePlaylistsContract;
import com.constantlab.wpconnect.core.ui.presenters.YoutubePlaylistsPresenter;
import com.constantlab.wpconnect.core.util.DisplayMetricsUtil;
import com.constantlab.wpconnect.views.activities.MainActivity;
import com.constantlab.wpconnect.views.activities.YoutubeVideosActivity;
import com.constantlab.wpconnect.views.adapters.YoutubePlaylistsAdapter;
import com.constantlab.wpconnect.views.custom.EndlessRecyclerViewOnScrollListener;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public class YouTubePlaylistFragment extends BaseFragment implements YoutubePlaylistsContract.YoutubePlaylistsView, YoutubePlaylistsAdapter.InteractionListener {
    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;
    YoutubePlaylistsPresenter mPresenter;
    YoutubePlaylistsAdapter mYouTubePlaylistsAdapter;
    ParallaxRecyclerView mRecyclerView;
    ProgressBar mProgressBar;
    String nextToken;
    String title;
    String channelId;
    private AppCompatActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new YoutubePlaylistsPresenter(DataManager.getInstance(getContext()));
        mYouTubePlaylistsAdapter = new YoutubePlaylistsAdapter(getContext());
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
            String[] arguments = getArguments().getStringArray(Constants.TAG_FOR_ARGUMENTS);
            channelId = arguments[0];
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (Config.YOUTUBE_TOKEN != null && !Config.YOUTUBE_TOKEN.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_youtube_playlists, container, false);
            if (getActivity() != null) {
                initView(view);
                mPresenter.attachView(this);
                if (mYouTubePlaylistsAdapter.isEmpty()) {
                    mPresenter.onInitialListRequested(channelId);
                    mYouTubePlaylistsAdapter.setListInteractionListener(this);
                }
            }
        } else {
            view = inflater.inflate(R.layout.fragment_configuration_message, container, false);
            ((TextView) view.findViewById(R.id.message_tv)).setText("YouTube Not Configured");
        }
        ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
        setTitle();
        return view;
    }

    private void setTitle() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title != null ? title : "");
    }

    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        mProgressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        mRecyclerView = (ParallaxRecyclerView) view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mYouTubePlaylistsAdapter);
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
                switch (mYouTubePlaylistsAdapter.getItemViewType(position)) {
                    case YoutubePlaylistsAdapter.VIEW_TYPE_LOADING:
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
                if (nextToken != null) {
                    if (mYouTubePlaylistsAdapter.addLoadingView()) {
                        mPresenter.onListEndReached(channelId, nextToken, null);
                    }
                }
            }
        };
    }

    @Override
    public void showProgress() {
        if (mYouTubePlaylistsAdapter.isEmpty())
            mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mYouTubePlaylistsAdapter.removeLoadingView();
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showPlaylists(List<YoutubePlaylist> playlistList, String nextToken) {
        if (mYouTubePlaylistsAdapter.getViewType() != YoutubePlaylistsAdapter.VIEW_TYPE_LIST) {
            mYouTubePlaylistsAdapter.removeAll();
            mYouTubePlaylistsAdapter.setViewType(YoutubePlaylistsAdapter.VIEW_TYPE_LIST);
        }

        this.nextToken = nextToken;
        mYouTubePlaylistsAdapter.addItems(playlistList);
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
    public void onPlaylistClicked(YoutubePlaylist playlist, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(getContext(), YoutubeVideosActivity.class);
        intent.putExtra(Constants.PLAYLIST_TAG, playlist);
        startActivity(intent);
    }
}
