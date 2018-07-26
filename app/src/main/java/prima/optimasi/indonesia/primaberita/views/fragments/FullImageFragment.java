package prima.optimasi.indonesia.primaberita.views.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ProgressBar;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by Constant-Lab LLP on 25-09-2017.
 */

public class FullImageFragment extends BaseFragment {

    private static final String GALLERY_TAG = "gallery_tag";
    PhotoView photoView;
    Bitmap originalBitmap;
    Gallery gallery;
    String downloadqueue;
    ProgressBar progressBar;
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            originalBitmap = bitmap;
            photoView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.INVISIBLE);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
            progressBar.setVisibility(View.VISIBLE);
        }
    };

    public static FullImageFragment getInstance(Gallery gallery) {
        FullImageFragment fragment = new FullImageFragment();
        Bundle args = new Bundle();
        args.putParcelable(GALLERY_TAG, gallery);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            gallery = getArguments().getParcelable(GALLERY_TAG);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_full_image, container, false);
        if (getActivity() != null) {
            initView(view);
            if (gallery != null) {
                setImage();
            }
        }
        return view;
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_media, menu);
        Drawable drawable = menu.findItem(R.id.download_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), android.R.color.white));
        menu.findItem(R.id.download_item).setIcon(drawable);
        drawable = menu.findItem(R.id.share_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(getContext(), android.R.color.white));
        menu.findItem(R.id.share_item).setIcon(drawable);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_item) {
            shareImage();
        } else if (item.getItemId() == R.id.download_item) {
            file_download(gallery.getFullSize(), getContext());
        }
        return super.onOptionsItemSelected(item);
    }

    //Download the shown media file
    public void file_download(String url, Context context) {

        String packageName = "com.android.providers.downloads";
        int state = getContext().getPackageManager().getApplicationEnabledSetting(packageName);

        //check if the download manager is disabled
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {

            //If it is disabled, allow the user to enable it.
            Toast.makeText(getContext().getApplicationContext(), R.string.downloadmanager_disabled, Toast.LENGTH_LONG).show();

            try {
                //Open the specific App Info page:
                Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);

            } catch (ActivityNotFoundException e) {
                //e.printStackTrace();

                //Open the generic Apps page:
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
                startActivity(intent);

            }


        } else {
            //If the download manager is enabled, check for permissions

            //check if there are sufficient permissions to download a file
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M && (
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

                downloadqueue = url;

            } else {
                url = url.replace(" ", "%20");
                DownloadManager downloadManager = (DownloadManager) ((Activity) context).getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
                String name = URLUtil.guessFileName(url, null, MimeTypeMap.getFileExtensionFromUrl(url));
                String title = "File";
                title = getResources().getString(R.string.file_image);


                request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE)
                        .setAllowedOverRoaming(false)
                        .setDescription(getResources().getString(R.string.downloading))
                        .setTitle(title)
                        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        .setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, name);
                downloadManager.enqueue(request);
                Toast.makeText(getContext().getApplicationContext(), R.string.downloading, Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void shareImage() {
        Bitmap bitmap = originalBitmap;
        try {
            File file = new File(getContext().getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(android.content.Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            intent.setType("image/png");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void setImage() {
        progressBar.setVisibility(View.VISIBLE);
        Picasso.with(getContext()).load(gallery.getFullSize()).into(target);

    }

    private void initView(View view) {
        photoView = view.findViewById(R.id.photos_view);
        progressBar = view.findViewById(R.id.progress_bar);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean foundfalse = false;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        foundfalse = true;
                    }
                }
                if (!foundfalse) {
                    file_download(downloadqueue, getContext());
                } else {
                    // Permission Denied
                    Toast.makeText(getContext().getApplicationContext(), getResources().getString(R.string.permissions_required), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
