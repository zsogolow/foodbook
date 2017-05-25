package foodbook.thinmint;

import android.os.AsyncTask;

import foodbook.thinmint.api.WebAPIResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public abstract class AbstractAsyncCallback<T> implements IAsyncCallback<T> {
    protected IApiCallback mActivityCallback;
    protected T mResult;

    public AbstractAsyncCallback(IApiCallback callback) {
        this.mActivityCallback = callback;
    }

    @Override
    public void onPreExecute() {
    }

    @Override
    public void onCompleted(T result) {
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

    @Override
    public T getResult() {
        return mResult;
    }
}
