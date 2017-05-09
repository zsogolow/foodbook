package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.models.AbstractAsyncCallback;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CallServiceCallback extends AbstractAsyncCallback<String> {

    private String mResult;

    public CallServiceCallback(IActivityCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(String result) {
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

    public String getResult() {
        return mResult;
    }
}
