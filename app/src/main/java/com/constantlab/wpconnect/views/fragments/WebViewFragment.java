package com.constantlab.wpconnect.views.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.config.Config;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.views.activities.MainActivity;

/**
 * Created by Sunny Kinger on 17-03-2018.
 */

public class WebViewFragment extends BaseFragment {

    String title;
    String url;
    WebView webView;
    SwipeRefreshLayout swipeRefreshLayout;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            title = getArguments().getString(Constants.TAG_FOR_TITLE);
            String[] arguments = getArguments().getStringArray(Constants.TAG_FOR_ARGUMENTS);
            url = arguments[0];
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview, container, false);
        if (getActivity() != null) {
            initView(view);
            ((MainActivity) getActivity()).appBarLayout.setExpanded(false, true);
            setupWebView();
            showWebPage(url);
            loadSettings();
            setTitle();
            setListeners();
        }
        return view;
    }

    private void setListeners() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.reload();
            }
        });
    }

    private void loadSettings() {
        swipeRefreshLayout.setEnabled(Config.ENABLE_PULL_TO_REFRESH);
    }


    private void setTitle() {
        ((MainActivity) getActivity()).getSupportActionBar().setTitle(title != null ? title : "");
    }

    private void showWebPage(String url) {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (!swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(true);
                }
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }


            @Override
            public void onPageFinished(WebView view, String url) {
                if (swipeRefreshLayout.isRefreshing()) {
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        webView.loadUrl(url);
    }

    private void setupWebView() {
        if (Config.WEBVIEW_ZOOM_ALLOWED) {
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setUseWideViewPort(false);
        } else {
            webView.setInitialScale(1);
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
        }

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.setScrollbarFadingEnabled(false);
    }

    private void initView(View view) {
        webView = view.findViewById(R.id.webView);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_container);
    }
}
