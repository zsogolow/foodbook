package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.idsrv.UserInfoResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserInfoCallback extends AbstractAsyncCallback<UserInfoResult> {
    private UserInfoResult mUserInfo;

    public UserInfoCallback(IApiCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(UserInfoResult result) {
        this.mUserInfo = result;
        mActivityCallback.callback(this);
    }

    @Override
    public void onPostExecute(AsyncTask task) {

    }

    @Override
    public void onCancelled(AsyncTask task) {

    }

    @Override
    public void onError(String err) {

    }

    public UserInfoResult getUserInfo() {
        return mUserInfo;
    }
}
