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
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.contracts.FavoritesContract;
import com.constantlab.wpconnect.core.ui.presenters.FavoritesPresenter;
import com.constantlab.wpconnect.core.util.DisplayMetricsUtil;
import com.constantlab.wpconnect.views.activities.CommentsActivity;
import com.constantlab.wpconnect.views.activities.MainActivity;
import com.constantlab.wpconnect.views.activities.PostDetailActivity;
import com.constantlab.wpconnect.views.adapters.FavoritesAdapter;
import com.google.gson.Gson;
import com.yayandroid.parallaxrecyclerview.ParallaxRecyclerView;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 29-07-2016.
 */
public class FavoritesFragment extends BaseFragment implements FavoritesContract.FavoritesView, FavoritesAdapter.InteractionListener {

    private static final int SCREEN_TABLET_DP_WIDTH = 600;
    private static final int TAB_LAYOUT_SPAN_SIZE = 2;
    private static final int TAB_LAYOUT_ITEM_SPAN_SIZE = 1;

    ParallaxRecyclerView mRecyclerView;
    TextView empty;
    private AppCompatActivity mActivity;
    private FavoritesPresenter mPresenter;
    private FavoritesAdapter mFavoritesAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPresenter = new FavoritesPresenter(getContext());
        mFavoritesAdapter = new FavoritesAdapter(getContext());
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorites, container, false);
        if (getActivity() != null) {
            initView(view);
            mPresenter.attachView(this);
            ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
            if (mFavoritesAdapter.isEmpty()) {
                mPresenter.onLoadFavoritesRequested();
                mFavoritesAdapter.setListInteractionListener(this);
            }
        }
        return view;
    }


    private void initView(View view) {
        mActivity = (AppCompatActivity) getActivity();
        empty = view.findViewById(R.id.empty_tv);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mFavoritesAdapter);
        boolean isTabletLayout = DisplayMetricsUtil.isScreenW(SCREEN_TABLET_DP_WIDTH);
        mRecyclerView.setLayoutManager(setUpLayoutManager(isTabletLayout));
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
                return itemSpanCount;
            }
        });
        return gridLayoutManager;
    }


    @Override
    public void showProgress() {

    }

    @Override
    public void hideProgress() {

    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    public void showEmpty() {
        empty.setVisibility(View.VISIBLE);
    }

    @Override
    public void showPosts(List<Post> postList) {
        mFavoritesAdapter.addItems(postList);
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
        if (isUserLoggedIn()) {
            Intent intent = new Intent(getContext(), CommentsActivity.class);
            intent.putExtra(Constants.POST_TAG, post);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Login to Comment", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onListItemClick(Post post, View sharedElementView, int adapterPosition) {
        Intent intent = new Intent(getContext(), PostDetailActivity.class);
        intent.putExtra(Constants.POST_TAG, post);
        startActivity(intent);
    }

    @Override
    public void onRemoveFromFavorites(Post post, View sharedElementView, int adapterPosition) {
        if (applicationMain.favoritePosts != null) {
            applicationMain.favoritePosts.remove(post);
            mFavoritesAdapter.remove(post);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext().getApplicationContext());
            Gson gson = new Gson();
            String json = gson.toJson(applicationMain.favoritePosts);
            sharedPreferences.edit().putString(Constants.KEY_FOR_FAVORITES, json).apply();
            mFavoritesAdapter.notifyItemRemoved(adapterPosition);
        }
    }

    @Override
    public void onShare(Post post, View sharedElementView, int adapterPosition) {
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_post, post.getTitle(), post.getDate(), post.getLink()));
        startActivity(Intent.createChooser(i, getString(R.string.share)));
    }
}
