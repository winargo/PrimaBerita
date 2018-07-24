package com.constantlab.wpconnect.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.model.YoutubeVideo;
import com.constantlab.wpconnect.core.util.MyUtils;
import com.constantlab.wpconnect.views.util.widgets.ytplayer.YouTubePlayerActivity;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class YoutubeDetailActivity extends BaseActivity {

    ImageView image;
    TextView title, description, date;
    FloatingActionButton play;
    YoutubeVideo video;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_youtube_detail);
        initView();
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            video = bundle.getParcelable(Constants.VIDEO_TAG);
            setTitle(R.string.video);
            displayData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_youtube_detail_menu, menu);
        Drawable drawable = menu.findItem(R.id.favorite_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        if (video != null) {
            if (checkForFavorite(video)) {
                drawable = ContextCompat.getDrawable(this, R.drawable.ic_favorite_filled);
                drawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.colorAccent));
            } else {
                DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
            }
        }
        menu.findItem(R.id.favorite_item).setIcon(drawable);
        drawable = menu.findItem(R.id.share_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, android.R.color.white));
        menu.findItem(R.id.share_item).setIcon(drawable);
        return super.onCreateOptionsMenu(menu);
    }


    private void displayData() {
        Picasso.with(this).load(video.getImage()).centerCrop().fit().into(image);
        title.setText(video.getTitle());
        description.setText(video.getDescription());
        date.setText(MyUtils.formatDate(video.getUpdated()));
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(YoutubeDetailActivity.this,
                        YouTubePlayerActivity.class);
                intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, video.getId());
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);

            }
        });
    }

    private void markFavorite(YoutubeVideo video) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        List<YoutubeVideo> mYouTubeVideoList = null;
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            mYouTubeVideoList = gson.fromJson(socialJson, type);
            mYouTubeVideoList.add(video);
        } else {
            mYouTubeVideoList = new ArrayList<>();
            mYouTubeVideoList.add(video);
        }

        String json = gson.toJson(mYouTubeVideoList);
        sharedPreferences.edit().putString(Constants.KEY_FOR_YOUTUBE_FAVORITES, json).apply();
        Toast.makeText(getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
    }

    private void removeFromFavorite(YoutubeVideo video) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        List<YoutubeVideo> mYouTubeVideoList = null;
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            mYouTubeVideoList = gson.fromJson(socialJson, type);
            mYouTubeVideoList.remove(video);
            String json = gson.toJson(mYouTubeVideoList);
            sharedPreferences.edit().putString(Constants.KEY_FOR_YOUTUBE_FAVORITES, json).apply();
            Toast.makeText(getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
        }
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        image = findViewById(R.id.image_iv);
        title = findViewById(R.id.title_tv);
        date = findViewById(R.id.date_tv);

        description = findViewById(R.id.description_tv);
        play = findViewById(R.id.playbutton);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if (item.getItemId() == R.id.favorite_item) {
            if (checkForFavorite(video)) {
                removeFromFavorite(video);

            } else {
                markFavorite(video);
            }
            invalidateOptionsMenu();
        } else if (item.getItemId() == R.id.share_item) {
            try {
                final Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_post_n, video.getTitle(), MyUtils.formatDate(video.getUpdated())));
                startActivity(Intent.createChooser(i, getString(R.string.share)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean checkForFavorite(YoutubeVideo video) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        Gson gson = new Gson();
        String socialJson = sharedPreferences.getString(Constants.KEY_FOR_YOUTUBE_FAVORITES, "");
        if (!socialJson.isEmpty()) {
            Type type = new TypeToken<List<YoutubeVideo>>() {
            }.getType();
            List<YoutubeVideo> mYouTubeVideoList = gson.fromJson(socialJson, type);
            return mYouTubeVideoList != null && mYouTubeVideoList.contains(video);
        } else {
            return false;
        }

    }
}
