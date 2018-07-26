package prima.optimasi.indonesia.primaberita.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
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
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import prima.optimasi.indonesia.primaberita.views.custom.HackyViewPager;
import com.github.chrisbanes.photoview.OnPhotoTapListener;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;


public class FullImageViewSliderActivity extends AppCompatActivity {

    public static boolean HIDE_TOOLBAR_OLD_DEVICES = true;
    Toolbar toolbar;
    ProgressBar progressBar;
    TextView count;
    String downloadqueue;
    Bitmap originalBitmap;
    SamplePagerAdapter samplePagerAdapter;
    List<Gallery> photos;
    int currentPosition;
    private boolean systemUIVisible;
    private View.OnSystemUiVisibilityChangeListener mOnSystemUiVisibilityChangeListener = new View.OnSystemUiVisibilityChangeListener() {
        @Override
        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == View.VISIBLE) {
                showSystemUI();
            } else {
                hideSystemUI();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View mDecorView = getWindow().getDecorView();
        mDecorView.setOnSystemUiVisibilityChangeListener(mOnSystemUiVisibilityChangeListener);
        setContentView(R.layout.activity_full_image_view_slider);
        ViewPager mViewPager = (HackyViewPager) findViewById(R.id.view_pager);
        count = findViewById(R.id.count);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("");
        hideSystemUI();
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        if (getIntent().getExtras() != null) {
            photos = getIntent().getParcelableArrayListExtra(Constants.IMAGES_LIST_KEY);
            final int p = getIntent().getExtras().getInt(Constants.IMAGE_POSITION_KEY);
            samplePagerAdapter = new SamplePagerAdapter(photos, progressBar);
            mViewPager.setAdapter(samplePagerAdapter);
            mViewPager.setCurrentItem(p);
            mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                @Override
                public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                    if (photos != null) {
                        currentPosition = position;
                        count.setText(getString(R.string.photos_count, position + 1, photos.size()));
                    }
                }


                @Override
                public void onPageSelected(int position) {
                    if (photos != null) {
                        currentPosition = position;
                        count.setText(getString(R.string.photos_count, position + 1, photos.size()));
                    }
                }

                @Override
                public void onPageScrollStateChanged(int state) {

                }
            });


        }
    }


    //Download the shown media file
    public void file_download(String url, Context context) {

        String packageName = "com.android.providers.downloads";
        int state = this.getPackageManager().getApplicationEnabledSetting(packageName);

        //check if the download manager is disabled
        if (state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED ||
                state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_USER
                || state == PackageManager.COMPONENT_ENABLED_STATE_DISABLED_UNTIL_USED) {

            //If it is disabled, allow the user to enable it.
            Toast.makeText(this, R.string.downloadmanager_disabled, Toast.LENGTH_LONG).show();

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
                    checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                            checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

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
                Toast.makeText(getApplicationContext(), R.string.downloading, Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressLint("NewApi")
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                boolean foundfalse = false;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        foundfalse = true;
                    }
                }
                if (!foundfalse) {
                    file_download(downloadqueue, this);
                } else {
                    // Permission Denied
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.permissions_required), Toast.LENGTH_SHORT)
                            .show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_media, menu);
        Drawable drawable = menu.findItem(R.id.download_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.download_item).setIcon(drawable);
        drawable = menu.findItem(R.id.share_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.share_item).setIcon(drawable);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.share_item) {
            shareImage();
        } else if (item.getItemId() == R.id.download_item) {
            file_download(photos.get(currentPosition).getFullSize(), FullImageViewSliderActivity.this);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private void shareImage() {
        Bitmap bitmap = originalBitmap;
        try {
            File file = new File(this.getExternalCacheDir(), "logicchip.png");
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

    private void showSystemUI() {
        getSupportActionBar().show();
        if (android.os.Build.VERSION.SDK_INT >= 19)
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        systemUIVisible = true;
    }


    private void hideSystemUI() {

        if (getSupportActionBar() != null) {
            if (!(HIDE_TOOLBAR_OLD_DEVICES && android.os.Build.VERSION.SDK_INT <= 19)) {
                getSupportActionBar().hide();
            }
        }

        if (android.os.Build.VERSION.SDK_INT >= 19) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }

        systemUIVisible = false;
    }

    private class SamplePagerAdapter extends PagerAdapter {
        List<Gallery> photos;
        ProgressBar progressBar;
        PhotoView photoView;

        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                originalBitmap = bitmap;
                progressBar.setVisibility(View.INVISIBLE);
                photoView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
                progressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                progressBar.setVisibility(View.VISIBLE);
            }
        };

        public SamplePagerAdapter() {

        }

        SamplePagerAdapter(List<Gallery> photos, ProgressBar progressBar) {
            this.photos = photos;
            this.progressBar = progressBar;
        }

        @Override
        public int getCount() {
            return photos.size();
        }

        @Override
        public View instantiateItem(final ViewGroup container, final int position) {
            photoView = new PhotoView(container.getContext());
            photoView.setOnPhotoTapListener(new OnPhotoTapListener() {
                @Override
                public void onPhotoTap(ImageView view, float x, float y) {
                    if (systemUIVisible)
                        hideSystemUI();
                    else
                        showSystemUI();
                }
            });

            Picasso.with(container.getContext()).load(photos.get(position).getFullSize()).into(target);


//            actionBar.setTitle(photos.get(position).getDescription());

            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            return photoView;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }


}
