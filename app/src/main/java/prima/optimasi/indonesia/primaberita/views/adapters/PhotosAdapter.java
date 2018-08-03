package prima.optimasi.indonesia.primaberita.views.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import prima.optimasi.indonesia.primaberita.generator;
import prima.optimasi.indonesia.primaberita.views.activities.FullImageSliderActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Constant-Lab LLP on 29-07-2016.
 */
public class PhotosAdapter extends RecyclerView.Adapter<PhotosAdapter.PhotoHolder> {


    Context context;
    private List<Gallery> galleryList;

    public PhotosAdapter(Context context) {
        this.context = context;
    }

    public void setGalleryList(List<Gallery> galleryList) {
        this.galleryList = galleryList;
        notifyDataSetChanged();
    }


    @Override
    public PhotoHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_photos_view, parent, false);
        final PhotoHolder holder = new PhotoHolder(view);
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

    @Override
    public void onBindViewHolder(PhotoHolder holder, int position) {
//        Picasso.with(context).load(urlList.get(position)).into(holder.photo);
        Picasso.with(context).load(galleryList.get(position).getThumbnail()).fit().centerCrop().into(holder.photo);
    }

    @Override
    public int getItemCount() {
        if (galleryList != null)
            return galleryList.size();
        else
            return 0;
    }


    private void singleItemClick(int position) {
        try {

            Intent intent = new Intent(context, FullImageSliderActivity.class);
            intent.putParcelableArrayListExtra(Constants.IMAGES_LIST_KEY, (ArrayList<? extends Parcelable>) galleryList);
            intent.putExtra(Constants.IMAGE_POSITION_KEY, position);
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    static class PhotoHolder extends RecyclerView.ViewHolder {

        ImageView photo;

        PhotoHolder(View itemView) {
            super(itemView);
            photo = itemView.findViewById(R.id.photo);
        }


    }
}
