package foodbook.thinmint.models.domain;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class User extends EntityBase {

    @SerializedName("subject")
    private String mSubject;

    @SerializedName("userName")
    private String mUsername;

    public User() {
        super();
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
