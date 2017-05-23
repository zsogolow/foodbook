package foodbook.thinmint.tasks;

import android.content.Context;
import android.os.AsyncTask;

import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class DeleteServiceAsyncTask extends AsyncTask<Query, String, WebAPIResult> {

    private Context mContext;
    private DeleteServiceCallback mCallback;
    private Token mToken;

    public DeleteServiceAsyncTask(Context context, DeleteServiceCallback callback, Token token) {
        this.mContext = context;
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected WebAPIResult doInBackground(Query... params) { // params[0] is path
        WebAPIResult result = null;
        Query query = params[0];
        WebAPIConnect connect = new WebAPIConnect();
        publishProgress("Getting data...");

        if (TokenHelper.isTokenExpired(mToken)) {
            TokenResult tokenResult = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);
            Token tempToken = TokenHelper.getTokenFromJson(tokenResult);
            TokenHelper.saveToken(mContext, tempToken);
            TokenHelper.copyToken(tempToken, mToken);
        }

        if (!TokenHelper.isTokenExpired(mToken)) {
            result = connect.delete(query, mToken.getAccessToken());
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
