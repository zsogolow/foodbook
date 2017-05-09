package foodbook.thinmint.idsrv;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserInfoResult {
    private boolean mSuccess;
    private String mUserInfoResult;

    public UserInfoResult() {
        mSuccess = false;
        mUserInfoResult = "";
    }

    public UserInfoResult(boolean success, String result) {
        mSuccess = success;
        mUserInfoResult = result;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getUserInfoResult() {
        return mUserInfoResult;
    }

    public void setSuccess(boolean success) {
        this.mSuccess = success;
    }

    public void setUserInfoResult(String userInfoResult) {
        this.mUserInfoResult = userInfoResult;
    }
}
