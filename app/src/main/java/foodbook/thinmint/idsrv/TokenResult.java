package foodbook.thinmint.idsrv;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class TokenResult {
    private boolean mSuccess;
    private String mTokenResult;

    public TokenResult() {
        mSuccess = false;
        mTokenResult = "";
    }

    public TokenResult(boolean success, String result) {
        mSuccess = success;
        mTokenResult = result;
    }

    public boolean isSuccess() {
        return mSuccess;
    }

    public String getTokenResult() {
        return mTokenResult;
    }

    public void setSuccess(boolean mSuccess) {
        this.mSuccess = mSuccess;
    }

    public void setTokenResult(String mTokenResult) {
        this.mTokenResult = mTokenResult;
    }
}
