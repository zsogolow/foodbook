package foodbook.thinmint.models.domain;

import foodbook.thinmint.models.JsonField;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class User {

    @JsonField(name = "id", jsonGetMethod = "getLong", isAccessible = true)
    private long mId;

    @JsonField(name = "subject", jsonGetMethod = "getString", isAccessible = true)
    private String mSubject;

    @JsonField(name = "userName", jsonGetMethod = "getString", isAccessible = true)
    private String mUsername;

    public User() {
    }

    public User(long id, String subject, String username) {
        this.mId = id;
        this.mSubject = subject;
        this.mUsername = username;
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public String getUsername() {
        return mUsername;
    }

    public void setUsername(String username) {
        this.mUsername = username;
    }
}
