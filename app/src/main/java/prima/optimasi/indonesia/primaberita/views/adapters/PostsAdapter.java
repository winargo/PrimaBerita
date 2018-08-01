package prima.optimasi.indonesia.primaberita.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.application.ApplicationMain;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.generator;
import prima.optimasi.indonesia.primaberita.views.activities.PostDetailActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.yayandroid.parallaxrecyclerview.ParallaxViewHolder;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;


public class PostsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    private static final int FAVORITE_POST = 98;
    private final List<Post> mPostList;
    Context context;
    private InteractionListener mListInteractionListener;
    private ApplicationMain applicationMain;
    @ViewType
    private int mViewType;


    public PostsAdapter(Context context) {
        this.mPostList = new ArrayList<>();
        this.context = context;
        this.applicationMain = (ApplicationMain) context.getApplicationContext();
    }


    public void add(Post item) {
        add(null, item);
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

    private void singleItemClick(int position) {
        try {
            generator.adcount++;
            generator.createad(context);
            Intent intent = new Intent(context, PostDetailActivity.class);
            intent.putExtra(Constants.POST_TAG, mPostList.get(position));
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_posts, parent, false);
        final PostHolder postHolder = new PostHolder(view);
        postHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int position = postHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        singleItemClick(position);
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
                    int position = postHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        shareFood(mPostList.get(postHolder.getAdapterPosition()));
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
                    int position = postHolder.getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        if (checkForFavorite(mPostList.get(position))) {
                            removeFromFavorite(mPostList.get(position), position);
                        } else {
                            markFavorite(mPostList.get(position), position);
                        }
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

    public void removeAll() {
        mPostList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public boolean addLoadingView() {
        if (getItemViewType(mPostList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (mPostList.size() > 1) {
            int loadingViewPosition = mPostList.size() - 1;
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
        onBindGenericItemViewHolder((PostHolder) holder, position);
    }


    private void onBindGenericItemViewHolder(final PostHolder holder, int position) {
        Post post = mPostList.get(position);
        if (checkForFavorite(post)) {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite_filled));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
        } else {
            holder.favoriteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_favorite));
            holder.favoriteButton.setColorFilter(ContextCompat.getColor(context, R.color.colorAccent), PorterDuff.Mode.SRC_IN);
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
                    holder.postType.setText("News");
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
                default:
                    holder.postType.setText("News");
            }
        } else {
            holder.postType.setText("News");
        }
        try {
            String url = post.getFeaturedImage().get(0).getUrl();
            if (url != null && !url.isEmpty()) {
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
            } else {
                holder.progressBar.setVisibility(View.INVISIBLE);
                holder.getBackgroundImage().setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_placeholder));
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


        holder.getBackgroundImage().reuse();
    }

    private void shareFood(Post post) {
        final Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.share_post, post.getTitle(), post.getDate(), post.getLink()));
        context.startActivity(Intent.createChooser(i, context.getString(R.string.share)));
    }

    private boolean checkForFavorite(Post post) {
        if (applicationMain.favoritePosts != null) {
            List<Post> postList = applicationMain.favoritePosts;
            return postList.contains(post);
        } else {
            return false;
        }
    }

    private void markFavorite(Post post, int position) {
        if (applicationMain.favoritePosts != null) {
            applicationMain.favoritePosts.add(post);
        } else {
            applicationMain.favoritePosts = new ArrayList<>();
            applicationMain.favoritePosts.add(post);
        }

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
        Gson gson = new Gson();
        String json = gson.toJson(applicationMain.favoritePosts);
        sharedPreferences.edit().putString(Constants.KEY_FOR_FAVORITES, json).apply();
        notifyItemChanged(position, FAVORITE_POST);
        Toast.makeText(context.getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite(Post post, int position) {
        if (applicationMain.favoritePosts != null) {
            applicationMain.favoritePosts.remove(post);
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
            Gson gson = new Gson();
            String json = gson.toJson(applicationMain.favoritePosts);
            sharedPreferences.edit().putString(Constants.KEY_FOR_FAVORITES, json).apply();
            notifyItemChanged(position, FAVORITE_POST);
            Toast.makeText(context.getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return mPostList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mPostList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    public interface InteractionListener {

        void onOpenComments(Post post, View sharedElementView, int adapterPosition);
    }

    private static class PostHolder extends ParallaxViewHolder {
        TextView postTitle;
        TextView postDate;
        ImageView comment;
        TextView postType;
        TextView categoryName;
        TextView commentCount;
        ImageView shareButton;
        ImageView favoriteButton;
        ProgressBar progressBar;


        PostHolder(View itemView) {
            super(itemView);
            postTitle = itemView.findViewById(R.id.post_title);
            postDate = itemView.findViewById(R.id.post_date);
            postType = itemView.findViewById(R.id.post_type_tv);
            progressBar = itemView.findViewById(R.id.progress_bar);
            categoryName = itemView.findViewById(R.id.post_category);
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
