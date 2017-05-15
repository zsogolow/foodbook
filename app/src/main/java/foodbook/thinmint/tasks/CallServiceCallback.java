package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Type;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.api.WebAPIResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CallServiceCallback<T> extends AbstractAsyncCallback<WebAPIResult> {

    private WebAPIResult mResult;
    private Type mMyType;
    private T mDeserialized;

    public CallServiceCallback(IActivityCallback callback, Type type) {
        super(callback);
        mMyType = type;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(WebAPIResult result) {
        mResult = result;
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
        mDeserialized = gson.fromJson(mResult.getResult(), mMyType);
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

    public T getDeserialized() {
        return mDeserialized;
    }
}
