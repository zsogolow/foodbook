package foodbook.thinmint.idsrv;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserInfo {
    private String mSubject;
    private String mGivenName;
    private String mFamilyName;

    public UserInfo() {
        this.mSubject = "";
        this.mGivenName = "";
        this.mFamilyName = "";
    }

    public String getSubject() {
        return mSubject;
    }

    public void setSubject(String subject) {
        this.mSubject = subject;
    }

    public String getGivenName() {
        return mGivenName;
    }

    public void setGivenName(String givenName) {
        this.mGivenName = givenName;
    }

    public String getFamilyName() {
        return mFamilyName;
    }

    public void setFamilyName(String familyName) {
        this.mFamilyName = familyName;
    }
}
