package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
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

import com.amulyakhare.textdrawable.TextDrawable;
import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.model.Category;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 20-04-2017.
 */

public class CategoriesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_TYPE_LIST = 1;
    public static final int VIEW_TYPE_LOADING = 2;
    Context context;
    private InteractionListener mListInteractionListener;
    private List<Category> mCategoryList;
    @ViewType
    private int mViewType;


    public CategoriesAdapter(Context context) {
        this.context = context;
        this.mCategoryList = new ArrayList<>();
    }

    public void setListInteractionListener(InteractionListener listInteractionListener) {
        mListInteractionListener = listInteractionListener;
    }

    public void add(Category item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, Category item) {
        if (position != null) {
            mCategoryList.add(position, item);
            notifyItemInserted(position);
        } else {
            mCategoryList.add(item);
            notifyItemInserted(mCategoryList.size() - 1);
        }
    }

    public void addItems(List<Category> items) {
        mCategoryList.addAll(items);
        notifyItemRangeInserted(getItemCount(), mCategoryList.size() - 1);
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_layout, parent, false);
        final CategoryHolder holder = new CategoryHolder(view);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListInteractionListener != null) {
                    mListInteractionListener.onListClick(mCategoryList.get(holder.getAdapterPosition()), view, holder.getAdapterPosition());
                }
            }
        });

        return holder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == VIEW_TYPE_LOADING) {
            return; // no-op
        }
        onBindGenericItemViewHolder((CategoryHolder) holder, position);
    }

    private void onBindGenericItemViewHolder(final CategoryHolder holder, int position) {
        Category category = mCategoryList.get(position);
        holder.postCount.setText(category.getPostCount() + "");
        holder.categoryName.setText(category.getName());
        holder.initials.setImageDrawable(TextDrawable.builder().buildRound(category.getName().charAt(0) + "",
                ContextCompat.getColor(context, R.color.colorPrimary)));
    }

    @Override
    public int getItemCount() {
        return mCategoryList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mCategoryList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_LIST;
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    public void remove(int position) {
        if (mCategoryList.size() < position) {
            return;
        }
        mCategoryList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        mCategoryList.clear();
        notifyDataSetChanged();
    }

    public boolean addLoadingView() {
        if (getItemViewType(mCategoryList.size() - 1) != VIEW_TYPE_LOADING) {
            add(null);
            return true;
        }
        return false;
    }

    public boolean removeLoadingView() {
        if (mCategoryList.size() > 1) {
            int loadingViewPosition = mCategoryList.size() - 1;
            if (getItemViewType(loadingViewPosition) == VIEW_TYPE_LOADING) {
                remove(loadingViewPosition);
                return true;
            }
        }
        return false;
    }

    public int getViewType() {
        return mViewType;
    }

    public void setViewType(@ViewType int viewType) {
        mViewType = viewType;
    }

    public interface InteractionListener {
        void onListClick(Category category, View sharedElementView, int adapterPosition);
    }

    @IntDef({VIEW_TYPE_LOADING, VIEW_TYPE_LIST})
    @Retention(RetentionPolicy.SOURCE)
    @interface ViewType {
    }

    private class CategoryHolder extends RecyclerView.ViewHolder {
        TextView categoryName;
        TextView postCount;
        ImageView initials;

        CategoryHolder(View itemView) {
            super(itemView);
            categoryName = itemView.findViewById(R.id.category_name);
            postCount = itemView.findViewById(R.id.post_count);
            initials = itemView.findViewById(R.id.initial_iv);
        }
    }

    private class ProgressBarViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        ProgressBarViewHolder(View view) {
            super(view);
            progressBar = view.findViewById(R.id.progress_bar);
        }
    }
}
