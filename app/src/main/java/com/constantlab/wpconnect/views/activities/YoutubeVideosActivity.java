package com.constantlab.wpconnect.views.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.YoutubePlaylist;
import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.core.ui.contracts.YoutubeVideosContract;
import com.constantlab.wpconnect.core.ui.presenters.YoutubeVideosPresenter;
import com.constantlab.wpconnect.views.adapters.YoutubeVideosAdapter;
import com.constantlab.wpconnect.views.custom.EndlessRecyclerOnScrollListener;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.util.List;

public class YoutubeVideosActivity extends BaseActivity implements YoutubeVideosContract.YoutubeVideosView, YoutubeVideosAdapter.InteractionListener {

    YoutubePlaylist playlist;
    ParallaxRecyclerView mRecyclerView;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    YoutubeVideosPresenter mPresenter;
    ProgressBar mProgressBar;
    YoutubeVideosAdapter mYoutubeVideosAdapter;
    String nextToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_videos);
        mPresenter = new YoutubeVideosPresenter(DataManager.getInstance(this));
        mYoutubeVideosAdapter = new YoutubeVideosAdapter(this);
        initView();
        mPresenter.attachView(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            playlist = bundle.getParcelable(Constants.PLAYLIST_TAG);
            if (mYoutubeVideosAdapter.isEmpty()) {
                mPresenter.onInitialListRequested(playlist.getId());
                mYoutubeVideosAdapter.setListInteractionListener(this);
            }
        }
    }

    private void initView() {
        Toolbar toolbar =  findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.videos);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mProgressBar = findViewById(R.id.progress_bar);
        mRecyclerView =  findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mYoutubeVideosAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (nextToken != null) {
                    mPresenter.onListEndReached(playlist.getId(), nextToken, null);
                }
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showProgress() {
        if (mYoutubeVideosAdapter.isEmpty())
            mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        mProgressBar.setVisibility(View.GONE);
        mYoutubeVideosAdapter.removeLoadingView();
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {

    }

    @Override
    public void showVideos(List<YoutubeVideo> videoList, String nextToken) {
        if (mYoutubeVideosAdapter.getViewType() != YoutubeVideosAdapter.VIEW_TYPE_LIST) {
            mYoutubeVideosAdapter.removeAll();
            mYoutubeVideosAdapter.setViewType(YoutubeVideosAdapter.VIEW_TYPE_LIST);
        }
        this.nextToken = nextToken;
        mYoutubeVideosAdapter.addItems(videoList);
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onVideoClicked(YoutubeVideo video, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(YoutubeVideosActivity.this, YoutubeDetailActivity.class);
        intent.putExtra(Constants.VIDEO_TAG, video);
        startActivity(intent);
    }
}
