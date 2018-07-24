package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.model.Comment;
import com.constantlab.wpconnect.core.util.WordpressUtil;
import com.constantlab.wpconnect.views.activities.CommentsActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Constant-Lab LLP on 09-11-2016.
 */

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    private List<Comment> mCommentsList;

    public CommentsAdapter(Context context) {
        this.context = context;
        this.mCommentsList = new ArrayList<>();
    }

    public void add(Comment item) {
        add(null, item);
    }

    public void add(@Nullable Integer position, Comment item) {
        if (position != null) {
            mCommentsList.add(position, item);
            notifyItemInserted(position);
        } else {
            mCommentsList.add(item);
            notifyItemInserted(mCommentsList.size() - 1);
        }
    }

    public void addItems(List<Comment> items) {
        mCommentsList.addAll(items);
        notifyItemRangeInserted(getItemCount(), mCommentsList.size() - 1);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return onGenericItemViewHolder(parent);
    }

    private RecyclerView.ViewHolder onGenericItemViewHolder(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_comments, parent, false);
        return new CommentsHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        onBindGenericItemViewHolder((CommentsHolder) holder, position);
    }

    private void onBindGenericItemViewHolder(final CommentsHolder holder, int position) {
        Comment comment = mCommentsList.get(position);
        holder.comment.setText(WordpressUtil.html2text(comment.getComment()));
        holder.personName.setText(comment.getUser().getUsername());

        if (comment.getUser().getProfilePhoto() != null) {
            Picasso.with(context).load(comment.getUser().getProfilePhoto()).fit().centerCrop().into(holder.personImage);
        }
    }


    public void remove(int position) {
        if (mCommentsList.size() < position) {
            return;
        }
        mCommentsList.remove(position);
        notifyItemRemoved(position);
    }

    public void removeAll() {
        mCommentsList.clear();
        notifyDataSetChanged();
    }

    public boolean isEmpty() {
        return getItemCount() == 0;
    }

    @Override
    public int getItemCount() {
        return mCommentsList.size();
    }

    public void addComment(Comment comment) {
        try {
            if (mCommentsList == null) {
                mCommentsList = new ArrayList<>();
            }

            mCommentsList.add(0, comment);
            notifyItemInserted(0);
            ((CommentsActivity) context).scrollToCertainPosition(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void appendItems(List<Comment> items) {
        if (mCommentsList != null) {
            try {
                int count = getItemCount();
                mCommentsList.addAll(items);
                notifyItemRangeInserted(count, items.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private static class CommentsHolder extends RecyclerView.ViewHolder {

        TextView personName, comment;
        CircleImageView personImage;


        CommentsHolder(View itemView) {
            super(itemView);
            personImage = (CircleImageView) itemView.findViewById(R.id.person_image);
            personName = (TextView) itemView.findViewById(R.id.person_name);
            comment = (TextView) itemView.findViewById(R.id.comment);
        }
    }


}
