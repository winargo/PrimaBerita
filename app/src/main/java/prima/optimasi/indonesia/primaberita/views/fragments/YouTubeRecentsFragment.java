package prima.optimasi.indonesia.primaberita.views.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.YoutubeRecentsContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.YoutubeRecentsPresenter;
import prima.optimasi.indonesia.primaberita.views.activities.MainActivity;
import prima.optimasi.indonesia.primaberita.views.activities.YoutubeDetailActivity;
import prima.optimasi.indonesia.primaberita.views.adapters.YoutubeVideosAdapter;
import prima.optimasi.indonesia.primaberita.views.custom.EndlessRecyclerOnScrollListener;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 11-09-2017.
 */

public class YouTubeRecentsFragment extends BaseFragment implements YoutubeRecentsContract.YoutubeRecentsView, YoutubeVideosAdapter.InteractionListener {


    ParallaxRecyclerView mRecyclerView;
    YoutubeRecentsPresenter mPresenter;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;
    ProgressBar mProgressBar;
    YoutubeVideosAdapter mYoutubeVideosAdapter;
    String nextToken;
    String title;
    String channelId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new YoutubeRecentsPresenter(DataManager.getInstance(getContext()));
        mYoutubeVideosAdapter = new YoutubeVideosAdapter(getContext());
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
            String[] arguments = getArguments().getStringArray(Constants.TAG_FOR_ARGUMENTS);
            Log.e("argumentdata",arguments[0] );
            channelId = arguments[0];
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = null;
        if (Config.YOUTUBE_TOKEN != null && !Config.YOUTUBE_TOKEN.isEmpty()) {
            view = inflater.inflate(R.layout.fragment_youtube_recents, container, false);
            if (getActivity() != null) {
                initView(view);
                mPresenter.attachView(this);
                if (mYoutubeVideosAdapter.isEmpty()) {
                    mPresenter.onInitialListRequested(channelId);
                    mYoutubeVideosAdapter.setListInteractionListener(this);
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
        mProgressBar = view.findViewById(R.id.progress_bar);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mYoutubeVideosAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        mRecyclerView.setLayoutManager(linearLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                if (nextToken != null) {
                    mPresenter.onListEndReached(channelId, nextToken, null);
                }
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
        mRecyclerView.setNestedScrollingEnabled(false);
    }

    @Override
    public void onVideoClicked(YoutubeVideo video, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(getContext(), YoutubeDetailActivity.class);
        intent.putExtra(Constants.VIDEO_TAG, video);
        startActivity(intent);
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
    public void onDestroy() {
        if (mRecyclerView != null)
            mRecyclerView.setAdapter(null);
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        super.onDestroyView();
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
}
