package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.model.Post;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 29-07-2016.
 */
public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.PostHolder> {

    Context context;
    private List<Post> mPostList;
    private InteractionListener mListInteractionListener;


    public FavoritesAdapter(Context context) {
        this.context = context;
        this.mPostList = new ArrayList<>();
    }


    public void add(Post item) {
        add(null, item);
    }


    public void remove(Post item) {
        mPostList.remove(item);
    }

    public void add(@Nullable Integer position, Post item) {
        if (position != null) {
            mPostList.add(position, item);
            notifyItemInserted(position);
        } else {
            mPostList.add(item);
            notifyItemInserted(mPostList.size() - 1);
        }
    }

    public void addItems(List<Post> items) {
        mPostList.addAll(items);
        notifyItemRangeInserted(getItemCount(), mPostList.size() - 1);
    }


    @Override
    public PostHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onGenericItemViewHolder(parent);
    }

    private PostHolder onGenericItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_posts, parent, false);
        final PostHolder postHolder = new PostHolder(view);
        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (postHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null)
                            mListInteractionListener.onListItemClick(mPostList.get(postHolder.getAdapterPosition()), view, postHolder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        postHolder.shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (postHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null)
                            mListInteractionListener.onShare(mPostList.get(postHolder.getAdapterPosition()), view, postHolder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        postHolder.favoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (postHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null)
                            mListInteractionListener.onRemoveFromFavorites(mPostList.get(postHolder.getAdapterPosition()), view, postHolder.getAdapterPosition());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        postHolder.comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (postHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null) {
                            mListInteractionListener.onOpenComments(mPostList.get(postHolder.getAdapterPosition()), v, postHolder.getAdapterPosition());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        postHolder.commentCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (postHolder.getAdapterPosition() != RecyclerView.NO_POSITION) {
                        if (mListInteractionListener != null) {
                            mListInteractionListener.onOpenComments(mPostList.get(postHolder.getAdapterPosition()), v, postHolder.getAdapterPosition());
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return postHolder;
    }


    public void remove(int position) {
        if (mPostList.size() < position) {
            return;
        }
        mPostList.remove(position);
        notifyItemRemoved(position);
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }


    @Override
    public void onBindViewHolder(PostHolder holder, int position) {
        onBindGenericItemViewHolder(holder, position);
    }


    private void onBindGenericItemViewHolder(final PostHolder holder, int position) {
        Post post = mPostList.get(position);
        try {
            if (post.getFeaturedImage().get(0).getUrl() != null) {
                Picasso.with(context).load(post.getFeaturedImage().get(0).getUrl()).into(holder.getBackgroundImage(), new Callback() {
                    @Override
                    public void onSuccess() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError() {
                        holder.progressBar.setVisibility(View.INVISIBLE);
                    }
                });
            }
            if (Build.VERSION.SDK_INT >= 24) {
                holder.postTitle.setText(Html.fromHtml(post.getTitle(), Html.FROM_HTML_MODE_LEGACY));
            } else {
                holder.postTitle.setText(Html.fromHtml(post.getTitle()));
            }

            holder.postDate.setText(post.getDate());
            holder.commentCount.setText(context.getString(R.string.count, post.getCommentCount()));
            if (post.getTotalCategories() > 1) {
                holder.categoryName.setVisibility(View.VISIBLE);
                holder.categoryName.setText(context.getString(R.string.category_count_tag, post.getCategory().getName(), post.getTotalCategories()));
            } else if (post.getTotalCategories() == 1) {
                holder.categoryName.setVisibility(View.VISIBLE);
                holder.categoryName.setText(post.getCategory().getName());
            } else {
                holder.categoryName.setVisibility(View.GONE);
            }

            if (post.getPostFormat() != null) {
                holder.postType.setVisibility(View.VISIBLE);
                switch (post.getPostFormat()) {
                    case "video":
                        holder.postType.setText("video");
                        break;
                    case "gallery":
                        holder.postType.setText("gallery");
                        break;
                    case "standard":
                        holder.postType.setText("standard");
                        break;
                    case "link":
                        holder.postType.setText("link");
                        break;
                    case "audio":
                        holder.postType.setText("audio");
                        break;
                    case "quote":
                        holder.postType.setText("quote");
                        break;
                }
            } else {
                holder.postType.setText("standard");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.getBackgroundImage().reuse();
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    public interface InteractionListener {

        void onOpenComments(Post post, View sharedElementView, int adapterPosition);

        void onListItemClick(Post post, View sharedElementView, int adapterPosition);

        void onRemoveFromFavorites(Post post, View sharedElementView, int adapterPosition);

        void onShare(Post post, View sharedElementView, int adapterPosition);
    }


    static class PostHolder extends ParallaxViewHolder {

        TextView postTitle;
        TextView postDate;
        ImageView comment;
        TextView commentCount;
        ImageView shareButton;
        ImageView favoriteButton;
        ProgressBar progressBar;
        TextView categoryName;
        TextView postType;

        PostHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.post_category);
            postTitle = itemView.findViewById(R.id.post_title);
            postDate = itemView.findViewById(R.id.post_date);
            postType = itemView.findViewById(R.id.post_type_tv);
            progressBar = itemView.findViewById(R.id.progress_bar);
            commentCount = itemView.findViewById(R.id.comment_count);
            shareButton = itemView.findViewById(R.id.share_button);
            favoriteButton = itemView.findViewById(R.id.favorite_button);
            comment = itemView
                    .findViewById(R.id.comment_button);
        }

        @Override
        public int getParallaxImageId() {
            return R.id.cover_photo;
        }
    }
}
