package prima.optimasi.indonesia.primaberita.core.data.model;

/**
 * Created by Constant-Lab LLP on 03-11-2016.
 */

public class Comment {
    private long commentID;
    private long commentPostID;
    private User user;
    private String date;
    private String comment;

    public long getCommentID() {
        return commentID;
    }

    public void setCommentID(long commentID) {
        this.commentID = commentID;
    }

    public long getCommentPostID() {
        return commentPostID;
    }

    public void setCommentPostID(long commentPostID) {
        this.commentPostID = commentPostID;
    }
//    private List<Comment> childCommentsList;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
