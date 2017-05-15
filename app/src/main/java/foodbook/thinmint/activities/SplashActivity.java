package foodbook.thinmint.activities;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.idsrv.UserInfo;
import foodbook.thinmint.idsrv.UserInfoHelper;
import foodbook.thinmint.idsrv.UserInfoResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.User;

public class SplashActivity extends AppCompatActivity {

    private RefreshTokenAsyncTask mRefreshTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").equals("")) {
            // check if expired
            // refresh if necessary and store new stuff
            Token token = new Token();
            token.setAccessToken(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));
            token.setRefreshToken(prefs.getString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, ""));
            token.setExpiresIn(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, ""));
            token.setLastRetrieved(prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, 0));

            if (TokenHelper.isTokenExpired(token)) {
                mRefreshTask = new RefreshTokenAsyncTask(token);
                mRefreshTask.execute();
            } else {
                ActivityStarter.finishLogin(this);
            }
        } else {
            ActivityStarter.startLogin(this);
        }
    }

    private class RefreshTokenAsyncTask extends AsyncTask<String, String, TokenResult> {

        private Token mToken;

        RefreshTokenAsyncTask(Token token) {
            mToken = token;
        }

        @Override
        protected TokenResult doInBackground(String... params) {
            publishProgress("Getting refresh token...");
            TokenResult result = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);

            if (result.isSuccess()) {
                UserInfoResult userInfoResult = mToken.getUserInfo();
                UserInfo userInfo = UserInfoHelper.getUserInfoFromJson(userInfoResult.getUserInfoResult());
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString(Constants.USER_SUBJECT, userInfo.getSubject()).apply();

                WebAPIResult apiResult = new WebAPIConnect().callService(mToken.getAccessToken(), "api/users/" + userInfo.getSubject());
                User user = JsonHelper.getUser(apiResult.getResult());
                prefs.edit().putLong(Constants.USER_ID, user.getId()).apply();
            }

            return result;
        }

        @Override
        protected void onPostExecute(TokenResult result) {
            mRefreshTask = null;

            if (result.isSuccess()) {
                Token tempToken = TokenHelper.getTokenFromJson(result.getTokenResult());
                long lastRetrieved = System.currentTimeMillis();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, tempToken.getRefreshToken()).apply();
                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, tempToken.getAccessToken()).apply();
                prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, tempToken.getExpiresIn()).apply();
                prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, lastRetrieved).apply();

                mToken.setRefreshToken(tempToken.getRefreshToken());
                mToken.setAccessToken(tempToken.getAccessToken());
                mToken.setExpiresIn(tempToken.getExpiresIn());
                mToken.setLastRetrieved(lastRetrieved);

                ActivityStarter.finishLogin(SplashActivity.this);
            } else {
                ActivityStarter.startLogin(SplashActivity.this);
            }
        }

        @Override
        protected void onCancelled() {
            mRefreshTask = null;
        }
    }
}
