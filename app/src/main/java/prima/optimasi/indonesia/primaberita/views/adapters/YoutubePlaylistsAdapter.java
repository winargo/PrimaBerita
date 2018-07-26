package prima.optimasi.indonesia.primaberita.views.adapters;

import android.content.Context;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubePlaylist;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 02-08-2017.
 */

public class YoutubePlaylistsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    private List<YoutubePlaylist> mPlaylistList;
    private Context mContext;
    private InteractionListener mListInteractionListener;
    @ViewType
    private int mViewType;

    public YoutubePlaylistsAdapter(Context context) {
        this.mContext = context;
        this.mPlaylistList = new ArrayList<>();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_youtube_playlist, parent, false);
        final PlaylistHolder holder = new PlaylistHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListInteractionListener != null) {
                    mListInteractionListener.onPlaylistClicked(mPlaylistList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
                }
            }
        });
        return holder;
    }

    public void remove(int position) {
        if (mPlaylistList.size() < position) {
            return;
        }
        mPlaylistList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        mPlaylistList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean addLoadingView() {
        if (getItemViewType(mPlaylistList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (mPlaylistList.size() > 1) {
            int loadingViewPosition = mPlaylistList.size() - 1;
            if (getItemViewType(loadingViewPosition) == VIEW_TYPE_LOADING) {
                remove(loadingViewPosition);
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mainHolder, int position) {
        if (mainHolder.getItemViewType() == VIEW_TYPE_LOADING) {
            return; // no-op
        }
        onBindGenericItemViewHolder((PlaylistHolder) mainHolder, position);
    }

    private void onBindGenericItemViewHolder(final PlaylistHolder holder, int position) {
        YoutubePlaylist playlist = mPlaylistList.get(position);
        holder.title.setText(playlist.getTitle());
        Picasso.with(mContext).load(playlist.getImage()).into(holder.getBackgroundImage(), new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });

        holder.getBackgroundImage().reuse();
    }

    public void add(YoutubePlaylist item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, YoutubePlaylist item) {
        if (position != null) {
            mPlaylistList.add(position, item);
            notifyItemInserted(position);
        } else {
            mPlaylistList.add(item);
            notifyItemInserted(mPlaylistList.size() - 1);
        }
    }

    public void addItems(List<YoutubePlaylist> items) {
        mPlaylistList.addAll(items);
        notifyItemRangeInserted(getItemCount(), mPlaylistList.size() - 1);
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }

    @Override
    public int getItemViewType(int position) {
        return mPlaylistList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }

    @Override
    public int getItemCount() {
        return mPlaylistList.size();
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public interface InteractionListener {

        void onPlaylistClicked(YoutubePlaylist playlist, View sharedElementView, int adapterPosition);
    }


    private static class PlaylistHolder extends ParallaxViewHolder {
        TextView title;
        //        ImageView image;
        ProgressBar progressBar;

        PlaylistHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.title_tv);
//            image = (ImageView) itemView.findViewById(R.id.image_iv);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
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
