package prima.optimasi.indonesia.primaberita.views.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;


public class PhotoDetailActivity extends AppCompatActivity {

    private static final int UI_ANIMATION_DELAY = 300;
    public static String DOWNLOAD_ON_OPEN = "download_on_open";
    public static String TYPE = "type";
    public static String URL = "url";
    public static String CAPTION = "caption";
    public static int TYPE_VID = 1;
    public static int TYPE_IMG = 2;
    public static int TYPE_AUDIO = 3;
    private final Handler mHideHandler = new Handler();
    /**
     * Touch listener to use for in-layout UI controls to delay hiding the
     * system UI. This is to prevent the jarring behavior of controls going away
     * while interacting with activity UI.
     */

    String downloadqueue;
    int type;
    String url;
    String caption;
    TextView captionTV;
    Bitmap originalBitmap;
    ProgressDialog pDialog;
    PhotoView photoView;
    VideoView videoView;
    Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            originalBitmap = bitmap;
            photoView.setImageBitmap(bitmap);
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {

        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {

        }
    };
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            // Delayed removal of status and navigation bar

            // Note that some of these constants are new as of API 16 (Jelly Bean)
            // and API 19 (KitKat). It is safe to use them, as they are inlined
            // at compile-time and do nothing on earlier devices.
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            // Delayed display of UI elements
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            if (type == TYPE_IMG)
                mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_detail);
        mVisible = true;
        initView();
        setClicks();
        Bundle extras = getIntent().getExtras();
        url = extras.getString(URL);
        type = extras.getInt(TYPE);
        caption = extras.getString(CAPTION);
        invalidateOptionsMenu();
        if (extras.containsKey(DOWNLOAD_ON_OPEN) && extras.getBoolean(DOWNLOAD_ON_OPEN)) {
            file_download(url, PhotoDetailActivity.this);
            Toast.makeText(this, getResources().getString(R.string.downloading), Toast.LENGTH_LONG);
        }

        if (Constants.PLAY_EXTERNAL) {
            //Try opening it in another player and quitting the media activity
            openExternal(url);
            this.finish();
        }

        if (type == TYPE_VID || type == TYPE_AUDIO) {
            videoView.setVisibility(View.VISIBLE);

            pDialog = new ProgressDialog(this);
            if (type == TYPE_VID)
                pDialog.setTitle(getResources().getString(R.string.opening_video));
            else
                pDialog.setTitle(getResources().getString(R.string.opening_audio));
            pDialog.setMessage(getResources().getString(R.string.loading));
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();

            try {
                MediaController mediacontroller = new MediaController(this);
                mediacontroller.setAnchorView(videoView);
                Uri video = Uri.parse(url);
                videoView.setMediaController(mediacontroller);
                videoView.setVideoURI(video);
            } catch (Exception e) {
                e.printStackTrace();
            }

            videoView.requestFocus();
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    pDialog.dismiss();
                    videoView.start();
                }
            });

            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    //'Handle' the error
                    Log.e("INFO", "Can't play media. Code: " + what + " Extra: " + extra + ". Proceeding to open in another player.");
                    pDialog.dismiss();

                    //Try opening it in another player
                    openExternal(url);

                    return true;
                }

            });
            mControlsView.setVisibility(View.GONE);
        } else if (type == TYPE_IMG) {
            photoView.setVisibility(View.VISIBLE);
            photoView.setImageDrawable(null);
            Picasso.with(this).load(url).into(target);
            photoView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    toggle();
                }
            });
            if (caption != null && !caption.isEmpty()) {
                captionTV.setVisibility(View.VISIBLE);
                captionTV.setText(caption);
                captionTV.setMovementMethod(new ScrollingMovementMethod());
            }
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
                if (type == TYPE_IMG) {
                    title = getResources().getString(R.string.file_image);
                }
                if (type == TYPE_VID) {
                    title = getResources().getString(R.string.file_video);
                }
                if (type == TYPE_AUDIO) {
                    title = getResources().getString(R.string.file_audio);
                }

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

    private void setClicks() {
        // Set up the user interaction to manually show or hide the system UI.
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        // Upon interacting with UI controls, delay any scheduled hide()
        // operations to prevent the jarring behavior of controls going away
        // while interacting with the UI.

    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        mContentView = findViewById(R.id.content_view);
        photoView = findViewById(R.id.imageView);
        videoView = findViewById(R.id.videoView);
        captionTV = findViewById(R.id.caption_tv);
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


        if (type != TYPE_IMG) {
            menu.findItem(R.id.share_item).setVisible(false);
            menu.findItem(R.id.download_item).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.download_item) {
            file_download(url, PhotoDetailActivity.this);
        } else if (item.getItemId() == R.id.share_item) {
            shareImage();
        }
        return super.onOptionsItemSelected(item);
    }

    //Open the file with an intent instead of viewing it.
    public void openExternal(String url) {
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        Intent mediaIntent = new Intent(Intent.ACTION_VIEW);
        mediaIntent.setDataAndType(Uri.parse(url), mimeType);
        startActivity(mediaIntent);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        // Hide UI first
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        // Schedule a runnable to remove the status and navigation bar after a delay
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        // Show the system bar
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        // Schedule a runnable to display UI elements after a delay
        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    /**
     * Schedules a call to hide() in [delay] milliseconds, canceling any
     * previously scheduled calls.
     */
    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
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

    private void shareImage() {
        Bitmap bitmap = originalBitmap;
        try {
            File file = new File(this.getExternalCacheDir(), "logicchip.png");
            FileOutputStream fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
            file.setReadable(true, false);
            final Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
            if (caption != null)
                intent.putExtra(Intent.EXTRA_TEXT, caption + " " + getString(R.string.copyright_text, getString(R.string.wp_connect)));
            intent.setType("image/*");
            startActivity(Intent.createChooser(intent, "Share image via"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
