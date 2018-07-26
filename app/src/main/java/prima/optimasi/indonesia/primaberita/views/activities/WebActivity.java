package prima.optimasi.indonesia.primaberita.views.activities;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import prima.optimasi.indonesia.primaberita.R;

import java.io.InputStream;

public class WebActivity extends BaseActivity {
    public static final String URL = "url";
    public static final String CSS = "css";
    public static final String MESSAGE = "message";
    public static final String TITLE = "title";
    WebView webView;
    Toolbar toolbar;
    String url;
    String cssName;
    ProgressBar progressBar;
    String message;
    String title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_webview);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            url = bundle.getString(URL);
            cssName = bundle.getString(CSS);
            message = bundle.getString(MESSAGE);
            title = bundle.getString(TITLE);
            initUI();
            browserSettings();
            setup();
        } else {
            Toast.makeText(getApplicationContext(), "Arguments Missing", Toast.LENGTH_SHORT).show();
        }

    }

    private void setup() {
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressBar.setVisibility(View.INVISIBLE);
                if (WebActivity.this.url.equals(url)) {

                } else {
                    if (message != null)
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    finish();
                }
                super.onPageFinished(view, url);
            }
        });
        webView.loadUrl(url);
    }


    // Inject CSS method: read style.css from assets folder
// Append stylesheet to document head
    private void injectCSS() {
        try {
            InputStream inputStream = getAssets().open(cssName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            inputStream.close();
            String encoded = Base64.encodeToString(buffer, Base64.NO_WRAP);
            webView.loadUrl("javascript:(function() {" +
                    "var parent = document.getElementsByTagName('head').item(0);" +
                    "var style = document.createElement('style');" +
                    "style.type = 'text/css';" +
                    // Tell the browser to BASE64-decode the string into your script !!!
                    "style.innerHTML = window.atob('" + encoded + "');" +
                    "parent.appendChild(style)" +
                    "})()");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initUI() {
        webView = findViewById(R.id.webView);
        toolbar = findViewById(R.id.toolbar);
        progressBar = findViewById(R.id.progress_bar);
        setSupportActionBar(toolbar);
        setTitle(title != null ? title : "");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("SetJavaScriptEnabled")
    @SuppressWarnings("deprecation")
    private void browserSettings() {
        // set javascript and zoom and some other settings
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setAppCacheEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setSupportZoom(false);
        // enable all plugins (flash)
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
    }
}
