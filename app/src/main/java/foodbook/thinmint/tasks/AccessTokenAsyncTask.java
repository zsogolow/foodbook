package foodbook.thinmint.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;

/**
 * Created by Zachery.Sogolow on 5/8/2017.
 */

public class AccessTokenAsyncTask extends AsyncTask<String, String, TokenResult> {

    private static final String CLIENT_ID = "android";
    private static final String CLIENT_SECRET = "secret";
    private static final String SCOPES = "offline_access openid profile thinmintapi";

    private ProgressDialog pd;

    private Context mContext;
    private AccessTokenCallback mCallback;
    private Token mToken;


    public AccessTokenAsyncTask(Context context, AccessTokenCallback callback, Token token) {
        mContext = context;
        mCallback = callback;
        mToken = token;

        pd = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        pd.show();
    }

    @Override
    protected TokenResult doInBackground(String... params) { //params[0] username params[1] for password.
        publishProgress("Getting access token...");
        return mToken.getAccessToken(CLIENT_ID, CLIENT_SECRET, params[0], params[1], SCOPES);
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        pd.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(TokenResult result) {
        if (result.isSuccess()) {
            Token tempToken = TokenHelper.getTokenFromJson(result.getTokenResult());
            long lastRetrieved = System.currentTimeMillis();

            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, tempToken.getRefreshToken()).apply();
            prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, tempToken.getAccessToken()).apply();
            prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, tempToken.getExpiresIn()).apply();
            prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, lastRetrieved).apply();

            mToken.setRefreshToken(tempToken.getRefreshToken());
            mToken.setAccessToken(tempToken.getAccessToken());
            mToken.setExpiresIn(tempToken.getExpiresIn());
            mToken.setLastRetrieved(lastRetrieved);

            mCallback.onCompleted(result);
        } else {
            mCallback.onError(result.getTokenResult());
        }

        mCallback.onPostExecute(this);
        pd.hide();
    }

    @Override
    protected void onCancelled() {
        mCallback.onCancelled(this);
    }

}
