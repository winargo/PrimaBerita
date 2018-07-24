package com.constantlab.wpconnect.core.ui.presenters;

import com.constantlab.wpconnect.core.data.Constants;
import com.constantlab.wpconnect.core.data.DataManager;
import com.constantlab.wpconnect.core.data.model.Comment;
import com.constantlab.wpconnect.core.data.model.User;
import com.constantlab.wpconnect.core.data.network.RemoteCallback;
import com.constantlab.wpconnect.core.ui.base.BasePresenter;
import com.constantlab.wpconnect.core.ui.contracts.CommentsContract;
import com.constantlab.wpconnect.core.util.WordpressUtil;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import okhttp3.ResponseBody;

/**
 * Created by Constant-Lab LLP on 18-05-2017.
 */

public class CommentsPresenter extends BasePresenter<CommentsContract.CommentsView> implements CommentsContract.ViewActions {
    private static final int ITEM_REQUEST_INITIAL_OFFSET = 1;
    private static final int ITEM_REQUEST_LIMIT = 10;
    private DataManager mDataManager;

    public CommentsPresenter(DataManager mDataManager) {
        this.mDataManager = mDataManager;
    }

    @Override
    public void onInitialListRequested(String postID) {
        requestComments(postID, ITEM_REQUEST_INITIAL_OFFSET, ITEM_REQUEST_LIMIT);
    }

    @Override
    public void onListEndReached(String postID, Integer offset, Integer limit) {
        requestComments(postID, offset, limit == null ? ITEM_REQUEST_LIMIT : limit);
    }

    @Override
    public void onRequestPostComment(String token, Comment comment) {
        requestPostComment(token, comment);
    }

    private void requestPostComment(String token, final Comment comment) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.comment(token, comment.getCommentPostID(), comment.getComment(), new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONObject o = new JSONObject(responseBody.string());
                    if (!o.has("code")) {
                        Comment comment = new Comment();
                        comment.setCommentID(o.getLong(Constants.COMMENT_ID_KEY));
                        comment.setCommentPostID(o.getLong(Constants.COMMENT_POST_ID_KEY));
                        User user = new User();
                        user.setId(o.getLong(Constants.POST_AUTHOR_KEY));
                        user.setUsername(o.getString(Constants.AUTHOR_NAME_KEY));
                        user.setProfilePhoto(o.getJSONObject(Constants.AUTHOR_AVATAR_URLS_KEY).getString("96"));
                        comment.setUser(user);
                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
                        SimpleDateFormat desiredFormat = new SimpleDateFormat("dd-MM-yyyy");
                        try {
                            Date date = format.parse(o.getString(Constants.POST_DATE_KEY));
                            comment.setDate(desiredFormat.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        comment.setComment(o.getJSONObject(Constants.POST_CONTENT_KEY).getString(Constants.POST_RENDERED_KEY));
                        mView.onCommentPosted(true, comment, null);
                    } else {
                        String message = o.getString("message");
                        mView.onCommentPosted(false, null, message);
                    }
                } catch (Exception e) {
                    mView.showError(e.getMessage());
                }
            }

            @Override
            public void onUnauthorized() {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError("Unauthorized");
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError(throwable.getMessage());
            }
        });
    }

    private void requestComments(String postID, final Integer offset, Integer limit) {
        if (!isViewAttached()) return;
        mView.showProgress();
        mDataManager.getComments(postID, offset, limit, new RemoteCallback<ResponseBody>() {
            @Override
            public void onSuccess(ResponseBody responseBody) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                try {
                    JSONArray data = new JSONArray(responseBody.string());
                    List<Comment> commentList = WordpressUtil.getComments(data);
                    if (commentList.size() > 0) {
                        mView.onCommentsResponse(commentList);
                    } else {
                        if (offset == 1)
                            mView.onCommentsEmpty();
                    }
                } catch (Exception e) {
                    mView.showError(e.getMessage());
                }
            }

            @Override
            public void onUnauthorized() {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError("Unauthorized");
            }

            @Override
            public void onFailed(Throwable throwable) {
                if (!isViewAttached()) return;
                mView.hideProgress();
                mView.showError(throwable.getMessage());
            }
        });
    }
}
