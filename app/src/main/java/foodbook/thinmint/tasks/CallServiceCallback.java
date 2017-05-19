package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import java.lang.reflect.Type;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.api.WebAPIResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CallServiceCallback extends AbstractAsyncCallback<WebAPIResult> {

    private WebAPIResult mResult;
    private Type mMyType;

    public CallServiceCallback(IApiCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onCompleted(WebAPIResult result) {
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

    public WebAPIResult getResult() {
        return mResult;
    }
}
