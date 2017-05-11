package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.models.GlobalFeed;
import foodbook.thinmint.models.UserFeed;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class GlobalFeedCallback extends AbstractAsyncCallback<GlobalFeed> {

    private GlobalFeed mResult;

    public GlobalFeedCallback(IActivityCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(GlobalFeed result) {
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

    public GlobalFeed getResult() {
        return mResult;
    }
}
