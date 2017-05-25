package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.api.WebAPIResult;

/**
 * Created by Zachery.Sogolow on 5/25/2017.
 */

public class AsyncCallback<T> extends AbstractAsyncCallback<T> {
    public AsyncCallback(IApiCallback callback) {
        super(callback);
    }
}
