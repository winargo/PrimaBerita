package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
import android.graphics.PorterDuff;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.application.ApplicationMain;
import com.constantlab.wpconnect.core.data.model.Social;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 03-08-2016.
 */
public class InstagramAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    Context context;
    private InteractionListener mListInteractionListener;
    private List<Social> instagramPhotoList;
    private ApplicationMain applicationMain;
    @ViewType
    private int mViewType;


    public InstagramAdapter(Context context) {
        this.context = context;
        this.instagramPhotoList = new ArrayList<>();
        this.applicationMain = (ApplicationMain) context.getApplicationContext();
    }


    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    public void add(Social item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, Social item) {
        if (position != null) {
            instagramPhotoList.add(position, item);
            notifyItemInserted(position);
        } else {
            instagramPhotoList.add(item);
            notifyItemInserted(instagramPhotoList.size() - 1);
        }
    }

    public void addItems(List<Social> items) {
        instagramPhotoList.addAll(items);
        notifyItemRangeInserted(getItemCount(), instagramPhotoList.size() - 1);
    }


    private boolean checkForFavorite(Social photo) {
        if (applicationMain.mSocialFavorites != null) {
            List<Social> socialFavorites = applicationMain.mSocialFavorites;
            return socialFavorites.contains(photo);
        } else {
            return false;
        }
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_instagram, parent, false);
        final InstagramPostHolder holder = new InstagramPostHolder(view);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    if (mListInteractionListener != null) {
                        mListInteractionListener.onListClick(instagramPhotoList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                    if (mListInteractionListener != null) {
                        mListInteractionListener.onPlayVideoClick(instagramPhotoList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                    }
                }
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    if (mListInteractionListener != null) {
                        if (checkForFavorite(instagramPhotoList.get(position))) {
                            mListInteractionListener.onRemoveFromFavorites(instagramPhotoList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                        } else {
                            mListInteractionListener.onAddToFavorites(instagramPhotoList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                        }
                    }
                }
            }
        });
        return holder;
    }


    public void remove(int position) {
        if (instagramPhotoList.size() < position) {
            return;
        }
        instagramPhotoList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        instagramPhotoList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean addLoadingView() {
        if (getItemViewType(instagramPhotoList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (instagramPhotoList.size() > 1) {
            int loadingViewPosition = instagramPhotoList.size() - 1;
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
        onBindGenericItemViewHolder((InstagramPostHolder) holder, position);
    }

    private void onBindGenericItemViewHolder(final InstagramPostHolder holder, int position) {
        Social instagramPhoto = instagramPhotoList.get(position);
        if (checkForFavorite(instagramPhoto)) {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_filled));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } else {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }


        Picasso.with(context).load(instagramPhoto.getImageUrl()).fit().centerCrop().into(holder.photo, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onError() {
                holder.progressBar.setVisibility(View.INVISIBLE);
            }
        });
        switch (instagramPhoto.getType()) {
            case "image":
                holder.playButton.setVisibility(View.INVISIBLE);
                break;
            case "video":
                holder.playButton.setVisibility(View.VISIBLE);
                break;
            default:
                holder.playButton.setVisibility(View.INVISIBLE);
                break;
        }
        holder.date.setText(DateUtils.getRelativeDateTimeString(context, instagramPhoto.getCreatedTime().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));
        holder.caption.setText(instagramPhoto.getCaption());
    }


    @Override
    public int getItemCount() {
        return instagramPhotoList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return instagramPhotoList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }


    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public interface InteractionListener {
        void onListClick(Social instagramPost, View sharedElementView, int adapterPosition);

        void onRemoveFromFavorites(Social instagramPost, View sharedElementView, int adapterPosition);

        void onAddToFavorites(Social instagramPost, View sharedElementView, int adapterPosition);

        void onPlayVideoClick(Social instagramPost, View sharedElementView, int adapterPosition);
    }

    private static class InstagramPostHolder extends RecyclerView.ViewHolder {

        TextView date;
        ImageView photo;
        ImageButton playButton;
        ProgressBar progressBar;
        ImageView favoriteButton;
        TextView caption;

        InstagramPostHolder(View itemView) {
            super(itemView);
            date = (TextView) itemView.findViewById(R.id.date);
            photo = (ImageView) itemView.findViewById(R.id.cover_photo);
            playButton = (ImageButton) itemView.findViewById(R.id.playbutton);
            favoriteButton = (ImageView) itemView.findViewById(R.id.favorite_button);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            caption = (TextView) itemView.findViewById(R.id.caption_tv);
        }
    }


    private class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        ProgressBarViewHolder(View view) {
            super(view);
            progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
        }
    }
}
