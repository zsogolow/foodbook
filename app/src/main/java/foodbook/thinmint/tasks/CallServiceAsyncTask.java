package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CallServiceAsyncTask extends AsyncTask<String, String, WebAPIResult> {

    private CallServiceCallback mCallback;
    private Token mToken;

    public CallServiceAsyncTask(CallServiceCallback callback, Token token) {
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected WebAPIResult doInBackground(String... params) { // params[0] is path
        WebAPIResult result = null;
        String path = params[0];
        WebAPIConnect connect = new WebAPIConnect();
        publishProgress("Getting data...");

        TokenResult tokenResult = new TokenResult();

        if (TokenHelper.isTokenExpired(mToken)) {
            tokenResult = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);
        }

        if (tokenResult.isSuccess()) {
            mToken = TokenHelper.getTokenFromJson(tokenResult.getTokenResult());
        }

        if (!TokenHelper.isTokenExpired(mToken)) {
            result = connect.callService(mToken.getAccessToken(), path);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(WebAPIResult result) {
        mCallback.onCompleted(result);
        mCallback.onPostExecute(this);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
