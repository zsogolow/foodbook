package foodbook.thinmint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.JsonManipulation;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;

public class SplashActivity extends AppCompatActivity {

    private RefreshTokenAsyncTask mRefreshTask = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        if (!prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").equals("")) {
            // check if expired
            // refresh if necessary and store new stuff
            if (isTokenExpired(prefs)) {
                Token token = new Token();
                token.setAccessToken(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));
                token.setRefreshToken(prefs.getString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, ""));
                token.setExpiresIn(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, ""));
                token.setLastRetrieved(prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, 0));

                mRefreshTask = new RefreshTokenAsyncTask(token);
                mRefreshTask.execute();
            } else {
                finishLogin();
            }
        } else {
            startLogin();
        }
    }

    private void startLogin() {
        Intent loginActivity = new Intent(this, LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    private void finishLogin() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    private boolean isTokenExpired(SharedPreferences prefs) {
        long now = System.currentTimeMillis();
        long expiresInMs = Long.parseLong(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, "")) * 1000;
        long lastRefresh = prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, -1);
        return (lastRefresh + expiresInMs) < now;
    }

    private class RefreshTokenAsyncTask extends AsyncTask<String, String, TokenResult> {

        private static final String CLIENT_ID = "android";
        private static final String CLIENT_SECRET = "secret";

        private Token mToken;

        RefreshTokenAsyncTask(Token token) {
            mToken = token;
        }

        @Override
        protected TokenResult doInBackground(String... params) {
            publishProgress("Getting refresh token...");
            return mToken.getRefreshToken(CLIENT_ID, CLIENT_SECRET, mToken.getRefreshToken());
        }

        @Override
        protected void onPostExecute(TokenResult result) {
            mRefreshTask = null;

            if (result.isSuccess()) {
                Token tempToken = TokenHelper.getTokenFromJson(result.getTokenResult());
                long lastRetrieved = System.currentTimeMillis();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SplashActivity.this);
                prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, tempToken.getRefreshToken()).apply();
                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, tempToken.getAccessToken()).apply();
                prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, tempToken.getExpiresIn()).apply();
                prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, lastRetrieved).apply();

                mToken.setRefreshToken(tempToken.getRefreshToken());
                mToken.setAccessToken(tempToken.getAccessToken());
                mToken.setExpiresIn(tempToken.getExpiresIn());
                mToken.setLastRetrieved(lastRetrieved);

                finishLogin();
            } else {
                startLogin();
            }
        }

        @Override
        protected void onCancelled() {
            mRefreshTask = null;
        }
    }
}
