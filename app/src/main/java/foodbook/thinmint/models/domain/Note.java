package foodbook.thinmint.models.domain;

import java.util.List;

import foodbook.thinmint.models.JsonField;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class Note {

    @JsonField(name = "userID", jsonGetMethod = "getLong", isAccessible = true)
    private long mUserId;

    @JsonField(name = "user", jsonGetMethod = "get", isAccessible = true, isNav = true, objectType = "foodbook.thinmint.models.domain.User")
    private User mUser;

    @JsonField(name = "content", jsonGetMethod = "getString", isAccessible = true)
    private String mContent;

    @JsonField(name = "comments", jsonGetMethod = "get", isAccessible = false)
    private List<Comment> mComments;

    public Note() {
        this.mUserId = 0;
        this.mUser = null;
        this.mContent = "";
        this.mComments = null;
    }

    public long getUserId() {
        return mUserId;
    }

    public void setUserId(long mUserId) {
        this.mUserId = mUserId;
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
