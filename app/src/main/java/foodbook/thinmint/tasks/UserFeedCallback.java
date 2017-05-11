package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.UserFeed;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserFeedCallback extends AbstractAsyncCallback<UserFeed> {

    private UserFeed mResult;

    public UserFeedCallback(IActivityCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(UserFeed result) {
        mResult = result;
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

    public UserFeed getResult() {
        return mResult;
    }
}
