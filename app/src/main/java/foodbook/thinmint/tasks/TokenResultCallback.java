package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.AbstractAsyncCallback;
import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.idsrv.TokenResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class TokenResultCallback extends AbstractAsyncCallback<TokenResult> {

    private TokenResult mTokenResult;

    public TokenResultCallback(IActivityCallback callback) {
        super(callback);
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onCompleted(TokenResult result) {
        mTokenResult = result;
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

    public TokenResult getTokenResult() {
        return mTokenResult;
    }
}