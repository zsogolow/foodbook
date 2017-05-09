package foodbook.thinmint.models;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class User {
    private long mId;
    private String mSubject;
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
