package prima.optimasi.indonesia.primaberita.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.util.MyUtils;
import prima.optimasi.indonesia.primaberita.generator;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public class YoutubeVideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    private static final int FAVORITE_VIDEO = 918;
    private List<YoutubeVideo> mVideoList;
    private Context mContext;
    private InteractionListener mListInteractionListener;
    @ViewType
    private int mViewType;

    public YoutubeVideosAdapter(Context context) {
        this.mContext = context;
        this.mVideoList = new ArrayList<>();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            return onIndicationViewHolder(parent);
        }
        return onGenericItemViewHolder(parent);
    }

    private RecyclerView.ViewHolder onIndicationViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_progress_bar, parent, false);
        return new ProgressBarViewHolder(view);
    }

    private RecyclerView.ViewHolder onGenericItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_video, parent, false);
        final VideoHolder holder = new VideoHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generator.adcount++;
                generator.createad(mContext);
                if (mListInteractionListener != null)
                    mListInteractionListener.onVideoClicked(mVideoList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
            }
        });
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int position = holder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (checkForFavorite(mVideoList.get(position))) {
                            removeFromFavorite(mVideoList.get(position), position);
                        } else {
                            markFavorite(mVideoList.get(position), position);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return holder;
    }

    public void remove(int position) {
        if (mVideoList.size() < position) {
            return;
        }
        mVideoList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        mVideoList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean addLoadingView() {
        if (getItemViewType(mVideoList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (mVideoList.size() > 1) {
            int loadingViewPosition = mVideoList.size() - 1;
            if (getItemViewType(loadingViewPosition) == VIEW_TYPE_LOADING) {
                remove(loadingViewPosition);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_LOADING) {
            return; // no-op
        }
        onBindGenericItemViewHolder((VideoHolder) holder, position);
    }

    private void onBindGenericItemViewHolder(final VideoHolder holder, int position) {
        YoutubeVideo video = mVideoList.get(position);
        holder.title.setText(video.getTitle());
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
        if (checkForFavorite(video)) {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite_filled));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } else {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_favorite));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(mContext, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
        holder.date.setText(MyUtils.formatDate(video.getUpdated()));

        holder.getBackgroundImage().reuse();
    }

    public void add(YoutubeVideo item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, YoutubeVideo item) {
        if (position != null) {
            mVideoList.add(position, item);
            notifyItemInserted(position);
        } else {
            mVideoList.add(item);
            notifyItemInserted(mVideoList.size());
        }
    }

    public void addItems(List<YoutubeVideo> items) {
        mVideoList.addAll(items);
        notifyItemRangeInserted(getItemCount(), mVideoList.size());
    }

    private boolean checkForFavorite(YoutubeVideo video) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
        Gson gson = new Gson();
        List<YoutubeVideo> mYouTubeVideoList = null;
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            mYouTubeVideoList = gson.fromJson(socialJson, type);
        }

        return mYouTubeVideoList != null && mYouTubeVideoList.contains(video);
    }

    private void markFavorite(YoutubeVideo video, int position) {
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
            mYouTubeVideoList.add(video);
        } else {
            mYouTubeVideoList = new ArrayList<>();
            mYouTubeVideoList.add(video);
        }
        String json = gson.toJson(mYouTubeVideoList);
        sharedPreferences.edit().putString(Constants.KEY_FOR_YOUTUBE_FAVORITES, json).apply();
        notifyItemChanged(position, FAVORITE_VIDEO);
        Toast.makeText(mContext.getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
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
            String json = gson.toJson(mYouTubeVideoList);
            sharedPreferences.edit().putString(Constants.KEY_FOR_YOUTUBE_FAVORITES, json).apply();
            notifyItemChanged(position, FAVORITE_VIDEO);
            Toast.makeText(mContext.getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return mVideoList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return mVideoList.size();
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public interface InteractionListener {

        void onVideoClicked(YoutubeVideo video, View sharedElementView, int adapterPosition);
    }

    private static class VideoHolder extends ParallaxViewHolder {

        TextView title, date;
        //        ImageView image;
        ProgressBar progressBar;
        ImageView favoriteButton;

        VideoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_tv);
            date = (TextView) itemView.findViewById(R.id.date_tv);
//            image = (ImageView) itemView.findViewById(R.id.image_iv);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            favoriteButton = (ImageView) itemView.findViewById(R.id.favorite_button);
        }

        @Override
        public int getParallaxImageId() {
            return R.id.image_iv;
        }
    }

    private class ProgressBarViewHolder extends ParallaxViewHolder {

        ProgressBar progressBar;

        ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }

        @Override
        public int getParallaxImageId() {
            return R.id.hidden_parallax;
        }
    }
}
