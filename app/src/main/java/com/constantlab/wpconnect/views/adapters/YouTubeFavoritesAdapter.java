package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.core.util.MyUtils;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 03-08-2017.
 */

public class YouTubeFavoritesAdapter extends RecyclerView.Adapter<YouTubeFavoritesAdapter.VideoHolder> {
    private Context mContext;
    private List<YoutubeVideo> mVideoList;
    private InteractionListener mListInteractionListener;

    public YouTubeFavoritesAdapter(Context context, List<YoutubeVideo> mVideoList) {
        this.mVideoList = mVideoList;
        this.mContext = context;
        notifyDataSetChanged();
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_video, parent, false);
        final VideoHolder holder = new VideoHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListInteractionListener != null)
                    mListInteractionListener.onVideoClicked(mVideoList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
            }
        });
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        removeFromFavorite(mVideoList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(final VideoHolder holder, int position) {
        YoutubeVideo video = mVideoList.get(position);
        holder.title.setText(video.getTitle());
        holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_filled));
        holder.favoriteButton.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        Picasso.with(mContext).load(video.getImage()).into(holder.getBackgroundImage(), new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        holder.date.setText(MyUtils.formatDate(video.getUpdated()));


        holder.getBackgroundImage().reuse();
    }


    @Override
    public int getItemCount() {
        if (mVideoList != null) {
            int size = mVideoList.size();
            if (mListInteractionListener != null)
                mListInteractionListener.onItemCountChanged(size);
            return size;
        } else
            return 0;
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    private void removeFromFavorite(YoutubeVideo video, int position) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        Gson gson = new Gson();
        List<YoutubeVideo> mYouTubeVideoList = null;
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            mYouTubeVideoList = gson.fromJson(socialJson, type);
        }
        if (mYouTubeVideoList != null) {
            mYouTubeVideoList.remove(video);
            mVideoList.remove(video);
            String json = gson.toJson(mYouTubeVideoList);
            sharedPreferences.edit().putString(Constants.KEY_FOR_YOUTUBE_FAVORITES, json).apply();
            notifyItemRemoved(position);
            Toast.makeText(mContext.getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    public interface InteractionListener {

        void onVideoClicked(YoutubeVideo video, View sharedElementView, int adapterPosition);

        void onItemCountChanged(int totalItems);
    }

    static class VideoHolder extends ParallaxViewHolder {

        TextView title, date;
        ImageView favoriteButton;
        ProgressBar progressBar;

        VideoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_tv);
            date = (TextView) itemView.findViewById(R.id.date_tv);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            favoriteButton = (ImageView) itemView.findViewById(R.id.favorite_button);
        }

        @Override
        public int getParallaxImageId() {
            return R.id.image_iv;
        }
    }
}
