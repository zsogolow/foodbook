package foodbook.thinmint;

import android.os.AsyncTask;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public interface IAsyncCallback<T> {
    void onPreExecute();
    void onCompleted(T result);
    void onPostExecute(AsyncTask task);
    void onCancelled(AsyncTask task);
    void onError(String err);
    T getResult();
}
