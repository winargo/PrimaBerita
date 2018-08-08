package prima.optimasi.indonesia.primaberita.views.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;
import prima.optimasi.indonesia.primaberita.core.config.Config;
import prima.optimasi.indonesia.primaberita.core.data.Constants;
import prima.optimasi.indonesia.primaberita.core.data.DataManager;
import prima.optimasi.indonesia.primaberita.core.data.model.Gallery;
import prima.optimasi.indonesia.primaberita.core.data.model.Post;
import prima.optimasi.indonesia.primaberita.core.data.model.YoutubeVideo;
import prima.optimasi.indonesia.primaberita.core.ui.contracts.SinglePostContract;
import prima.optimasi.indonesia.primaberita.core.ui.presenters.SinglePostPresenter;
import prima.optimasi.indonesia.primaberita.core.util.WordpressUtil;
import prima.optimasi.indonesia.primaberita.generator;
import prima.optimasi.indonesia.primaberita.views.adapters.PhotosAdapter;
import prima.optimasi.indonesia.primaberita.views.adapters.VideosAdapter;
import prima.optimasi.indonesia.primaberita.views.custom.GridSpacingItemDecoration;
import prima.optimasi.indonesia.primaberita.views.util.widgets.ytplayer.YouTubePlayerActivity;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import timber.log.Timber;

public class PostDetailActivity extends BaseActivity implements SinglePostContract.SinglePostView {

    private static final String REGEX_GALLERY = "\\[gallery.*ids=\"([\\d,]+)\"\\]";
    private static final String REGEX_YOUTUBE = "\\[youtube.+ids=([A-Za-z0-9_\\-,]+)";
    TextView postName, postDate, commentsCount, galleryLabel, videosLabel;
    TextView postText;
    WebView webView;
    FloatingActionButton playButton;
    ImageView featuredImage;
    Post post;
    ImageView commentButton;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    PhotosAdapter photosAdapter;
    VideosAdapter videosAdapter;
    ProgressBar progressBar;
    RecyclerView photosView, videosView;
    SinglePostPresenter mPresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(Config.VISUAL_COMPOSER_RENDER ? R.layout.activity_post_detail : R.layout.activity_post_detail_webview);
        mPresenter = new SinglePostPresenter(DataManager.getInstance(this));
        initView();
        mPresenter.attachView(this);
        setupClicks();
        if (getIntent().getExtras() != null) {
            post = getIntent().getExtras().getParcelable(Constants.POST_TAG);
            if (post != null) {
                setTitle(R.string.post_details);
                setTitleAndDate();
                setupClicks();
                setData();
            }
        }
    }

    private void setupClicks() {
        commentsCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
                intent.putExtra(Constants.POST_TAG, post);
                startActivity(intent);

            }
        });

        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PostDetailActivity.this, CommentsActivity.class);
                intent.putExtra(Constants.POST_TAG, post);
                startActivity(intent);
            }
        });
    }

    private void initView() {
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        appBarLayout = findViewById(R.id.appbar_layout);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        featuredImage = findViewById(R.id.featured_image);
        postDate = findViewById(R.id.post_date);
        galleryLabel = findViewById(R.id.gallery_label_tv);
        videosLabel = findViewById(R.id.videos_label_tv);
        if (Config.VISUAL_COMPOSER_RENDER) {
            postText = findViewById(R.id.main_text);
        } else {
            webView = findViewById(R.id.webView);
            webView.getSettings().setJavaScriptEnabled(true);
        }
        postName = findViewById(R.id.event_name);
        commentsCount = findViewById(R.id.comments_count);
        playButton = findViewById(R.id.playbutton);
        commentButton = findViewById(R.id.comment_button);
        photosView = findViewById(R.id.photos_view);
        videosView = findViewById(R.id.videos_view);
        photosView.setHasFixedSize(true);
        videosView.setHasFixedSize(true);
        photosView.setNestedScrollingEnabled(false);
        videosView.setNestedScrollingEnabled(false);
        GridLayoutManager galleryLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        galleryLayoutManager.setSmoothScrollbarEnabled(true);
        photosView.setLayoutManager(galleryLayoutManager);
        GridLayoutManager videosLayoutManager = new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        videosLayoutManager.setSmoothScrollbarEnabled(true);
        videosView.setLayoutManager(videosLayoutManager);
        photosView.addItemDecoration(new GridSpacingItemDecoration(3, 8, true));
        videosView.addItemDecoration(new GridSpacingItemDecoration(3, 8, true));
        photosView.setItemAnimator(new DefaultItemAnimator());
        videosView.setItemAnimator(new DefaultItemAnimator());
        photosAdapter = new PhotosAdapter(this);
        videosAdapter = new VideosAdapter(this);
        photosView.setAdapter(photosAdapter);
        videosView.setAdapter(videosAdapter);
    }


    private void setTitleAndDate() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= 24) {
                    postName.setText(Html.fromHtml(post.getTitle(), Html.FROM_HTML_MODE_LEGACY));
                } else {
                    postName.setText(Html.fromHtml(post.getTitle()));
                }

                postDate.setText(post.getDate());
            }
        });
    }

    private void setData() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (post.getContent() != null & !post.getContent().isEmpty()) {
                    checkPostType();
                    if (Config.VISUAL_COMPOSER_RENDER) {
                        String strippedContent = WordpressUtil.html2text(post.getContent());
                        postText.setText(strippedContent.trim());
                    } else {
                        String textWithoutTags = post.getContent().replaceAll("\\[(.*?)\\]", "");
                        String style = "<style>img{display: inline; height: auto; max-width: 100%;} iframe {display: inline;height:auto;max-width:100%;}</style>";
                        webView.loadDataWithBaseURL(post.getLink(), style + textWithoutTags, "text/html; charset=utf-8", "UTF-8", null);
                    }
                    Picasso.with(PostDetailActivity.this).load(post.getFeaturedImage().get(0).getUrl()).fit().centerCrop().into(featuredImage);
                    commentsCount.setText(getString(R.string.count, post.getCommentCount()));
                    checkForGalleryAndVideos();
                } else {
                    postText.setVisibility(View.GONE);
                }
            }
        });

    }

    private void checkForGalleryAndVideos() {
        Pattern pattern = Pattern.compile(REGEX_GALLERY);
        Matcher matcher = pattern.matcher(post.getExcerpt());
        if (matcher.find()) {
            String ids = matcher.group(1);
            mPresenter.onRequestGallery(ids);
        } else {
            photosView.setVisibility(View.GONE);
            galleryLabel.setVisibility(View.GONE);
        }
        pattern = Pattern.compile(REGEX_YOUTUBE);
        matcher = pattern.matcher(post.getExcerpt());
        if (matcher.find()) {
            String ids = matcher.group(1);
            mPresenter.onRequestYouTubeVideos(ids);
        } else {
            videosView.setVisibility(View.GONE);
            videosLabel.setVisibility(View.GONE);
        }
    }

    private void checkPostType() {
        playButton.setVisibility(View.VISIBLE);
        if (post.getPostFormat() != null) {
            switch (post.getPostFormat()) {
                case "gallery":
                    playButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_collections));
                    break;
                case "standard":
                    playButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_pin));
                    break;
                case "video":
                    playButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_play_arrow));
                    break;
                default:
                    playButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_pin));
            }
        } else {
            playButton.setImageDrawable(ContextCompat.getDrawable(PostDetailActivity.this, R.drawable.ic_pin));
        }
        if (playButton.getVisibility() == View.VISIBLE) {
            playButton.setColorFilter(ContextCompat.getColor(PostDetailActivity.this, android.R.color.white), PorterDuff.Mode.SRC_IN);
        }

        if (post != null && !post.getPostFormat().equals("video") && !post.getPostFormat().equals("gallery")) {
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    appBarLayout.setExpanded(false, true);
                }
            });
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.single_post_menu, menu);
        Drawable drawable = menu.findItem(R.id.favorite_item).getIcon();
        drawable = DrawableCompat.wrap(drawable);
        if (post != null) {
            if (checkForFavorite(post)) {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.favorite_item) {
            if (post != null) {
                if (checkForFavorite(post)) {
                    removeFromFavorite(post);
                    invalidateOptionsMenu();
                    Toast.makeText(getApplicationContext(), "Removed From Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    generator.adcount=2;
                    generator.createad(PostDetailActivity.this);
                    addToFavorite(post);
                    invalidateOptionsMenu();
                    Toast.makeText(getApplicationContext(), "Added to Favorites", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.share_item) {
            try {
                final Intent i = new Intent(Intent.ACTION_SEND);
                i.setType("text/plain");
                i.putExtra(Intent.EXTRA_TEXT, getString(R.string.share_post, post.getTitle(), post.getDate(), post.getLink()));
                startActivity(Intent.createChooser(i, getString(R.string.share)));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    private void addToFavorite(final Post post) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (applicationMain.favoritePosts != null) {
                    applicationMain.favoritePosts.add(post);
                } else {
                    applicationMain.favoritePosts = new ArrayList<>();
                    applicationMain.favoritePosts.add(post);
                }

                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                Gson gson = new Gson();
                String json = gson.toJson(applicationMain.favoritePosts);
                sharedPreferences.edit().putString(Constants.KEY_FOR_FAVORITES, json).apply();
                Toast.makeText(getApplicationContext(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeFromFavorite(final Post post) {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (applicationMain.favoritePosts != null) {
                    applicationMain.favoritePosts.remove(post);
                    SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    Gson gson = new Gson();
                    String json = gson.toJson(applicationMain.favoritePosts);
                    sharedPreferences.edit().putString(Constants.KEY_FOR_FAVORITES, json).apply();
                    Toast.makeText(getApplicationContext(), R.string.removed_from_favorites, Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private boolean checkForFavorite(Post post) {
        if (applicationMain.favoritePosts != null) {
            List<Post> postList = applicationMain.favoritePosts;
            return postList.contains(post);
        } else {
            return false;
        }
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showError(String errorMessage) {

    }

    @Override
    protected void onDestroy() {
        photosView.setAdapter(null);
        mPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void onGalleryResponse(boolean success, final List<Gallery> galleryList) {
        if (success) {
            photosAdapter.setGalleryList(galleryList);
            if (post.getPostFormat().equals("gallery")) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(PostDetailActivity.this, FullImageSliderActivity.class);
                            intent.putParcelableArrayListExtra(Constants.IMAGES_LIST_KEY, (ArrayList<? extends Parcelable>) galleryList);
                            intent.putExtra(Constants.IMAGE_POSITION_KEY, 0);
                            startActivity(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        } else {
            galleryLabel.setVisibility(View.GONE);
            photosView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onYouTubeVideos(boolean success, final List<YoutubeVideo> videoList) {
        if (success) {
            videosAdapter.setVideoList(videoList);
            if (post.getPostFormat().equals("video")) {
                playButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            Intent intent = new Intent(PostDetailActivity.this,
                                    YouTubePlayerActivity.class);
                            intent.putExtra(YouTubePlayerActivity.EXTRA_VIDEO_ID, videoList.get(0).getId());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                            startActivity(intent);
                        } catch (Exception e) {
                            Timber.v("Exception : %s", e.getMessage());
                        }
                    }
                });
            }
        } else {
            videosLabel.setVisibility(View.GONE);
            videosView.setVisibility(View.GONE);
        }
    }
}
