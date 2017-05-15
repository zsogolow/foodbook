package foodbook.thinmint.models.domain;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class Note extends EntityBase {

    @SerializedName("userID")
    private long mUserId;

    @SerializedName("user")
    private User mUser;

    @SerializedName("content")
    private String mContent;

    @SerializedName("comments")
    private List<Comment> mComments;

    public Note() {
        super();
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long userId) {
        this.mUserId = userId;
    }

    public User getUser() {
        return mUser;
    }

    public void setUser(User user) {
        this.mUser = user;
    }

    public String getContent() {
        return mContent;
    }

    public void setContent(String content) {
        this.mContent = content;
    }

    public List<Comment> getComments() {
        return mComments;
    }

    public void setComments(List<Comment> comments) {
        this.mComments = comments;
    }
}
