package com.constantlab.wpconnect.views.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.views.activities.YoutubeDetailActivity;
import com.constantlab.wpconnect.views.adapters.YouTubeFavoritesAdapter;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 03-08-2017.
 */

public class YouTubeFavoritesFragment extends BaseFragment implements YouTubeFavoritesAdapter.InteractionListener {

    YouTubeFavoritesAdapter mYouTubeFavoritesAdapter;
    ParallaxRecyclerView mRecyclerView;
    List<YoutubeVideo> mYouTubeVideoList;
    TextView noVideos;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_youtube_favorites, container, false);
        if (getActivity() != null) {
            initView(view);
        }
        return view;
    }

    private void initView(View view) {
        mRecyclerView = view.findViewById(R.id.recycler_view);
        noVideos = view.findViewById(R.id.no_videos_tv);
        mRecyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setSmoothScrollbarEnabled(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        setAdapter();
        mRecyclerView.setNestedScrollingEnabled(false);

    }

    private void setAdapter() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
        Gson gson = new Gson();
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            mYouTubeVideoList = gson.fromJson(socialJson, type);
        }

        if (mYouTubeVideoList != null && mYouTubeVideoList.size() > 0) {
            mYouTubeFavoritesAdapter = new YouTubeFavoritesAdapter(getContext(), mYouTubeVideoList);
            mRecyclerView.setAdapter(mYouTubeFavoritesAdapter);
            mYouTubeFavoritesAdapter.setListInteractionListener(this);
        } else {
            noVideos.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onVideoClicked(YoutubeVideo video, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(getContext(), YoutubeDetailActivity.class);
        intent.putExtra(Constants.VIDEO_TAG, video);
        startActivity(intent);
    }

    @Override
    public void onItemCountChanged(int totalItems) {
        if (totalItems == 0)
            noVideos.setVisibility(View.VISIBLE);
        else
            noVideos.setVisibility(View.INVISIBLE);
    }
}
