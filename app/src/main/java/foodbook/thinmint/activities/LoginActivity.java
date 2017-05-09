package foodbook.thinmint.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import foodbook.thinmint.idsrv.JsonManipulation;
import foodbook.thinmint.R;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.constants.Constants;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserLoginTask mAuthTask = null;

//    private RefreshTokenAsyncTask mRefreshTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        if (!prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").equals("")) {
//            showProgress(true);
//            // check if expired
//            // refresh if necessary and store new stuff
//            if (isTokenExpired(prefs)) {
//                Token token = new Token();
//                token.setAccessToken(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));
//                token.setRefreshToken(prefs.getString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, ""));
//
//                mRefreshTask = new RefreshTokenAsyncTask(token);
//                mRefreshTask.execute();
//            } else {
//                finishLogin();
//            }
//
//            showProgress(false);
//        }
    }

//    private boolean isTokenExpired(SharedPreferences prefs) {
//        long now = System.currentTimeMillis();
//        long expiresInMs = Long.parseLong(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, "")) * 1000;
//        long lastRefresh = prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, -1);
//        return (lastRefresh + expiresInMs) < now;
//    }

    private void finishLogin() {
        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(mainActivity);
        finish();
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return true;
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    private class UserLoginTask extends AsyncTask<String, String, TokenResult> {
        private static final String CLIENT_ID = "android";
        private static final String CLIENT_SECRET = "secret";
        private static final String SCOPES = "offline_access openid profile thinmintapi";

        private final String mEmail;
        private final String mPassword;
        private Token mToken;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
            mToken = new Token();
        }

        @Override
        protected TokenResult doInBackground(String... params) {
            publishProgress("Getting access token...");
            return mToken.getAccessToken(CLIENT_ID, CLIENT_SECRET, mEmail, mPassword, SCOPES);
        }

        @Override
        protected void onPostExecute(final TokenResult result) {

            if (result.isSuccess()) {
                Token tempToken = TokenHelper.getTokenFromJson(result.getTokenResult());
                long lastRetrieved = System.currentTimeMillis();

                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
                prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, tempToken.getRefreshToken()).apply();
                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, tempToken.getAccessToken()).apply();
                prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, tempToken.getExpiresIn()).apply();
                prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, lastRetrieved).apply();

                mToken.setRefreshToken(tempToken.getRefreshToken());
                mToken.setAccessToken(tempToken.getAccessToken());
                mToken.setExpiresIn(tempToken.getExpiresIn());
                mToken.setLastRetrieved(lastRetrieved);

                finishLogin();
            } else if (!result.isSuccess()) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }

            mAuthTask = null;
            showProgress(false);
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }
    }

//    private class RefreshTokenAsyncTask extends AsyncTask<String, String, TokenResult> {
//
//        private static final String CLIENT_ID = "android";
//        private static final String CLIENT_SECRET = "secret";
//
//        private Token mToken;
//
//        RefreshTokenAsyncTask(Token token) {
//            mToken = token;
//        }
//
//        @Override
//        protected TokenResult doInBackground(String... params) {
//            publishProgress("Getting refresh token...");
//            return mToken.getRefreshToken(CLIENT_ID, CLIENT_SECRET, mToken.getRefreshToken());
//        }
//
//        @Override
//        protected void onPostExecute(TokenResult result) {
//            mRefreshTask = null;
//            showProgress(false);
//
//            if (result.isSuccess()) {
//                String refreshToken = JsonManipulation.getAttrFromJson(result.getTokenResult(), "refresh_token");
//                String accessToken = JsonManipulation.getAttrFromJson(result.getTokenResult(), "access_token");
//                String expiresIn = JsonManipulation.getAttrFromJson(result.getTokenResult(), "expires_in");
//
//                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this);
//                prefs.edit().putString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, refreshToken).apply();
//                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, accessToken).apply();
//                prefs.edit().putString(Constants.EXPIRES_IN_PREFERENCE_KEY, expiresIn).apply();
//                prefs.edit().putLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, System.currentTimeMillis()).apply();
//
//                mToken.setRefreshToken(refreshToken);
//                mToken.setAccessToken(accessToken);
//
//                finishLogin();
//            }
//        }
//
//        @Override
//        protected void onCancelled() {
//            mRefreshTask = null;
//            showProgress(false);
//        }
//    }
}

