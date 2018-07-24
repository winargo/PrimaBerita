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
 * Created by Constant-Lab LLP on 02-08-2016.
 */
public class FacebookAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    Context context;
    private InteractionListener mListInteractionListener;
    private List<Social> facebookItemList;
    private ApplicationMain applicationMain;
    @ViewType
    private int mViewType;

    public FacebookAdapter(Context context) {
        this.context = context;
        this.facebookItemList = new ArrayList<>();
        this.applicationMain = (ApplicationMain) context.getApplicationContext();
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }


    public void add(Social item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, Social item) {
        if (position != null) {
            facebookItemList.add(position, item);
            notifyItemInserted(position);
        } else {
            facebookItemList.add(item);
            notifyItemInserted(facebookItemList.size() - 1);
        }
    }

    public void addItems(List<Social> items) {
        facebookItemList.addAll(items);
        notifyItemRangeInserted(getItemCount(), facebookItemList.size() - 1);
    }

    private boolean checkForFavorite(Social item) {
        if (applicationMain.mSocialFavorites != null) {
            List<Social> socialFavorites = applicationMain.mSocialFavorites;
            return socialFavorites.contains(item);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_facebook, parent, false);
        final FacebookPostHolder holder = new FacebookPostHolder(view);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        if (mListInteractionListener != null) {
                            mListInteractionListener.onListClick(facebookItemList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        if (mListInteractionListener != null) {
                            mListInteractionListener.onPlayVideoClick(facebookItemList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
                        }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                Social social = facebookItemList.get(position);
                if (position != RecyclerView.NO_POSITION) {
                    if (mListInteractionListener != null) {
                        if (checkForFavorite(facebookItemList.get(position))) {
                            mListInteractionListener.onRemovedFromFavorites(social, view, position);
                        } else {
                            mListInteractionListener.onAddedToFavorites(social, view, position);
                        }
                    }
                }
            }
        });
        return holder;
    }

    public void remove(int position) {
        if (facebookItemList.size() < position) {
            return;
        }
        facebookItemList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        facebookItemList.clear();
        notifyDataSetChanged();
    }


    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean addLoadingView() {
        if (getItemViewType(facebookItemList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (facebookItemList.size() > 1) {
            int loadingViewPosition = facebookItemList.size() - 1;
            if (getItemViewType(loadingViewPosition) == VIEW_TYPE_LOADING) {
                remove(loadingViewPosition);
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_LOADING) {
            return; // no-op
        }
        onBindGenericItemViewHolder((FacebookPostHolder) holder, position);
    }

    private void onBindGenericItemViewHolder(final FacebookPostHolder holder, int position) {
        Social post = facebookItemList.get(position);
        if (checkForFavorite(post)) {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_filled));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } else {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        }
        holder.date.setText(DateUtils.getRelativeDateTimeString(context, post.getCreatedTime().getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL));

        if (post.getImageUrl() != null) {
            Picasso.with(context).load(post.getImageUrl()).fit().centerCrop().into(holder.photo, new Callback() {
                @Override
                public void onSuccess() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onError() {
                    holder.progressBar.setVisibility(View.INVISIBLE);
                }
            });
        } else {
            holder.photo.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_placeholder));
        }

        switch (post.getType()) {
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

        holder.caption.setText(post.getCaption());


    }

    @Override
    public int getItemCount() {
        return facebookItemList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return facebookItemList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public interface InteractionListener {
        void onListClick(Social facebookPost, View sharedElementView, int adapterPosition);

        void onRemovedFromFavorites(Social facebookPost, View sharedElementView, int adapterPosition);

        void onAddedToFavorites(Social facebookPost, View sharedElementView, int adapterPosition);

        void onPlayVideoClick(Social facebookPost, View sharedElementView, int adapterPosition);
    }


    private static class FacebookPostHolder extends RecyclerView.ViewHolder {

        TextView date;
        ImageView photo;
        ImageButton playButton;
        ImageView favoriteButton;
        ProgressBar progressBar;
        TextView caption;

        FacebookPostHolder(View itemView) {
            super(itemView);

            date = (TextView) itemView.findViewById(R.id.date);
            photo = (ImageView) itemView.findViewById(R.id.cover_photo);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            playButton = (ImageButton) itemView.findViewById(R.id.playbutton);
            favoriteButton = (ImageView) itemView.findViewById(R.id.favorite_button);
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
