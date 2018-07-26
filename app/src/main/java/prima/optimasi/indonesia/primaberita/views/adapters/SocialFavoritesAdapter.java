package prima.optimasi.indonesia.primaberita.views.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.preference.PreferenceManager;
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
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.application.ApplicationMain;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.model.Social;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Constant-Lab LLP on 07-09-2016.
 */
public class SocialFavoritesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_TYPE_INSTAGRAM = 4;
    private static final int VIEW_TYPE_FACEBOOK = 5;
    Context context;
    private InteractionListener mListInteractionListener;
    private List<Social> socialList;
    private ApplicationMain applicationMain;

    public SocialFavoritesAdapter(Context context) {
        this.context = context;
        this.applicationMain = (ApplicationMain) context.getApplicationContext();
    }

    public void setSocialList(List<Social> instagramPhotoList) {
        this.socialList = instagramPhotoList;
        notifyDataSetChanged();
    }

    public void clear() {
        if (socialList != null) {
            socialList.clear();
            notifyDataSetChanged();
        }
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_FACEBOOK) {
            return onFacebookItemViewHolder(parent);
        } else if (viewType == VIEW_TYPE_INSTAGRAM) {
            return onInstagramItemViewHolder(parent);
        }
        return null;
    }

    private RecyclerView.ViewHolder onFacebookItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_facebook, parent, false);
        final FacebookPostHolder holder = new FacebookPostHolder(view);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        if (mListInteractionListener != null)
                            mListInteractionListener.onSocialPostClicked(socialList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
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
                        if (mListInteractionListener != null)
                            mListInteractionListener.onPlayButton(socialList.get(holder.getAdapterPosition()), v, holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        removeFromFavorite(socialList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return holder;
    }


    private RecyclerView.ViewHolder onInstagramItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_instagram, parent, false);
        final InstagramPostHolder holder = new InstagramPostHolder(view);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        if (mListInteractionListener != null)
                            mListInteractionListener.onSocialPostClicked(socialList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null)
                            mListInteractionListener.onPlayButton(socialList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        holder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        removeFromFavorite(socialList.get(holder.getAdapterPosition()), holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return holder;
    }


    @Override
    public int getItemViewType(int position) {
        Social social = socialList.get(position);
        if (social.getSocialType() == Social.SOCIAL_FACEBOOK) {
            return VIEW_TYPE_FACEBOOK;
        } else {
            return VIEW_TYPE_INSTAGRAM;
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder mainHolder, int position) {
        if (mainHolder instanceof InstagramPostHolder) {
            final InstagramPostHolder holder = (InstagramPostHolder) mainHolder;
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            Social instagramPhoto = socialList.get(position);
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

        } else if (mainHolder instanceof FacebookPostHolder) {
            final FacebookPostHolder holder = (FacebookPostHolder) mainHolder;
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
            Social post = socialList.get(position);
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
    }

    @Override
    public int getItemCount() {
        if (socialList != null) {
            int size = socialList.size();
            if (mListInteractionListener != null)
                mListInteractionListener.onItemCountChanged(size);
            return size;
        } else
            return 0;
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    private void removeFromFavorite(Social photo, int position) {
        if (applicationMain.mSocialFavorites != null) {
            applicationMain.mSocialFavorites.remove(photo);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            Gson gson = new Gson();
            String json = gson.toJson(applicationMain.mSocialFavorites);
            sharedPreferences.edit().putString(Constants.KEY_FOR_INSTAGRAM_FAVORITES, json).apply();
            notifyItemRemoved(position);
            Toast.makeText(context.getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    public interface InteractionListener {

        void onSocialPostClicked(Social social, View sharedElementView, int adapterPosition);

        void onItemCountChanged(int totalItems);

        void onPlayButton(Social social, View sharedElementView, int adapterPosition);
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
            date = itemView.findViewById(R.id.date);
            progressBar = itemView.findViewById(R.id.progress_bar);
            photo = itemView.findViewById(R.id.cover_photo);
            playButton = itemView.findViewById(R.id.playbutton);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            caption = itemView.findViewById(R.id.caption_tv);
        }
    }

    private static class InstagramPostHolder extends RecyclerView.ViewHolder {

        TextView date;
        ImageView photo;
        ImageButton playButton;
        ImageView favoriteButton;
        ProgressBar progressBar;

        TextView caption;

        InstagramPostHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            photo = itemView.findViewById(R.id.cover_photo);
            progressBar = itemView.findViewById(R.id.progress_bar);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            playButton = itemView.findViewById(R.id.playbutton);
            caption = itemView.findViewById(R.id.caption_tv);

        }


    }

}
