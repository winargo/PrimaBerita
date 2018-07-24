package com.constantlab.wpconnect.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.views.util.widgets.ytplayer.YouTubePlayerActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

import timber.log.Timber;

/**
 * Created by Constant-Lab LLP on 25-09-2017.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoHolder> {

    Context context;
    private List<YoutubeVideo> videoList;

    public VideosAdapter(Context context) {
        this.context = context;
    }

    public void setVideoList(List<YoutubeVideo> videoList) {
        this.videoList = videoList;
        notifyDataSetChanged();
    }

    @Override
    public VideoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_videos_view, parent, false);
        final VideoHolder holder = new VideoHolder(view);
        holder.photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (holder.getAdapterPosition() != RecyclerView.NO_POSITION)
                        singleItemClick(holder.getAdapterPosition());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        return holder;
    }

    private void singleItemClick(int adapterPosition) {
        try {
            Intent intent = new Intent(context,
                    YouTubePlayerActivity.class);
            intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, videoList.get(adapterPosition).getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
            context.startActivity(intent);
        } catch (Exception e) {
            Timber.v("Exception : %s", e.getMessage());
        }
    }

    @Override
    public void onBindViewHolder(VideoHolder holder, int position) {
        Picasso.with(context).load(videoList.get(position).getThumbUrl()).fit().centerCrop().into(holder.photo);
    }

    @Override
    public int getItemCount() {
        if (videoList != null) {
            return videoList.size();
        } else {
            return 0;
        }
    }

    static class VideoHolder extends RecyclerView.ViewHolder {

        ImageView photo;

        VideoHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }


    }
}
