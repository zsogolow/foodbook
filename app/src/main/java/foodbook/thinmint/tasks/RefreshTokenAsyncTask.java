package foodbook.thinmint.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.UserInfo;
import foodbook.thinmint.idsrv.UserInfoHelper;
import foodbook.thinmint.idsrv.UserInfoResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class RefreshTokenAsyncTask extends AsyncTask<String, String, TokenResult> {

    private static final String CLIENT_ID = "android";
    private static final String CLIENT_SECRET = "secret";

    private ProgressDialog pd;

    private Context mContext;
    private RefreshTokenCallback mCallback;
    private Token mToken;

    public RefreshTokenAsyncTask(Context context, RefreshTokenCallback callback, Token token) {
        this.mContext = context;
        this.mCallback = callback;
        this.mToken = token;

        this.pd = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        pd.show();
    }

    @Override
    protected TokenResult doInBackground(String... params) {
        publishProgress("Getting refresh token...");
        TokenResult result = mToken.getRefreshToken(CLIENT_ID, CLIENT_SECRET, mToken.getRefreshToken());

        if (result.isSuccess()) {
            UserInfoResult userInfoResult = mToken.getUserInfo();
            UserInfo userInfo = UserInfoHelper.getUserInfoFromJson(userInfoResult.getUserInfoResult());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            prefs.edit().putString(Constants.USER_SUBJECT, userInfo.getSubject()).apply();
        }

        return result;
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

