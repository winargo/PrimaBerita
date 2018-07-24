package com.constantlab.wpconnect.views.activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.constantlab.wpconnect.R;
import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Comment;
import com.constantlab.wpconnect.core.data.model.Post;
import com.constantlab.wpconnect.core.ui.contracts.CommentsContract;
import com.constantlab.wpconnect.core.ui.presenters.CommentsPresenter;
import com.constantlab.wpconnect.views.adapters.CommentsAdapter;
import com.constantlab.wpconnect.views.custom.EndlessRecyclerOnScrollListener;

import java.util.List;

public class CommentsActivity extends BaseActivity implements CommentsContract.CommentsView {

    CommentsAdapter mCommentsAdapter;
    Post post;
    ProgressBar progressBar;
    TextView noComments;
    LinearLayoutManager linearLayoutManager;
    TextView commentsTicker;
    EditText postComment;
    ImageView commentButton;
    RecyclerView mRecyclerView;
    CommentsPresenter mPresenter;
    EndlessRecyclerOnScrollListener endlessRecyclerOnScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        mPresenter = new CommentsPresenter(DataManager.getInstance(this));
        mCommentsAdapter = new CommentsAdapter(this);
        initView();
        mPresenter.attachView(this);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            post = bundle.getParcelable(Constants.POST_TAG);
            if (mCommentsAdapter.isEmpty()) {
                mPresenter.onInitialListRequested(post.getId());
            }
        }
        setupClicks();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.comments);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressBar = findViewById(R.id.progress_bar);
        noComments = findViewById(R.id.no_comments);
        commentsTicker = findViewById(R.id.comment_ticker);
        postComment = findViewById(R.id.post_comment);
        commentButton = findViewById(R.id.comment_button);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setMotionEventSplittingEnabled(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mCommentsAdapter);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        endlessRecyclerOnScrollListener = new EndlessRecyclerOnScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int current_page) {
                mPresenter.onListEndReached(post.getId(), current_page, null);
            }
        };
        mRecyclerView.addOnScrollListener(endlessRecyclerOnScrollListener);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupClicks() {
        commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserLoggedIn()) {
                    Comment comment = new Comment();
                    comment.setCommentPostID(Long.parseLong(post.getId()));
                    comment.setUser(applicationMain.user);
                    comment.setComment(postComment.getText().toString().trim());
                    postComment.setEnabled(false);
                    mPresenter.onRequestPostComment(getUserToken(), comment);
                } else {
                    Toast.makeText(getApplicationContext(), "Login To Comment", Toast.LENGTH_SHORT).show();
                }
            }
        });
        if (!isUserLoggedIn()) {
            postComment.setEnabled(false);
            postComment.setText(R.string.login_required);
        }
        commentsTicker.setText(getString(R.string.reply_to, post.getTitle()));
    }

    private String getUserToken() {
        String bearer = null;
        if (applicationMain.token != null && !applicationMain.token.isEmpty()) {
            bearer = "Bearer " + applicationMain.token;
            return bearer;
        } else {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String token = sharedPreferences.getString(Constants.KEY_FOR_TOKEN, null);
            if (token != null) {
                bearer = "Bearer " + token;
            }
            return bearer;
        }
    }

    public void scrollToCertainPosition(int position) {
        linearLayoutManager.scrollToPosition(position);
    }


    @Override
    public void showProgress() {
        noComments.setVisibility(View.GONE);
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
    public void onCommentsResponse(List<Comment> commentList) {
        mCommentsAdapter.appendItems(commentList);
        linearLayoutManager.scrollToPosition(0);
    }

    @Override
    public void onCommentsEmpty() {
        noComments.setVisibility(View.VISIBLE);
    }

    @Override
    public void onCommentPosted(boolean success, Comment comment, String message) {
        if (success) {
            mCommentsAdapter.addComment(comment);
            postComment.setEnabled(true);
            postComment.setText(null);
            postComment.clearFocus();
        } else {
            postComment.setEnabled(true);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        mPresenter.detachView();
        super.onDestroy();
    }

}
