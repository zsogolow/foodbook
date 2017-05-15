package foodbook.thinmint.models.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class Comment extends EntityBase {

    @SerializedName("noteID")
    private long mNoteId;

    @SerializedName("userID")
    private long mUserId;

    @SerializedName("user")
    private User mUser;

    @SerializedName("text")
    private String mText;

    public Comment() {
        super();
    }

    public long getNoteId() {
        return mNoteId;
    }

    public void setNoteId(long noteId) {
        this.mNoteId = noteId;
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

    public String getText() {
        return mText;
    }

    public void setText(String text) {
        this.mText = text;
    }
}
