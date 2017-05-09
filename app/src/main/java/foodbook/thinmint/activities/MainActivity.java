package foodbook.thinmint.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.tasks.AccessTokenAsyncTask;
import foodbook.thinmint.tasks.AccessTokenCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.RefreshTokenAsyncTask;
import foodbook.thinmint.tasks.RefreshTokenCallback;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.idsrv.UserInfoResult;
import foodbook.thinmint.tasks.UserInfoAsyncTask;
import foodbook.thinmint.tasks.UserInfoCallback;
import foodbook.thinmint.constants.Constants;

public class MainActivity extends AppCompatActivity implements IActivityCallback {

    private RefreshTokenCallback mRefreshCallback;
    private AccessTokenCallback mAccessCallback;
    private UserInfoCallback mUserInfoCallback;
    private CallServiceCallback mCallServiceCallback;

    private Token mToken;

    private View mMainContentView;
    private View mProgressView;

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // TOKEN OBJECT INIT
        mToken = new Token();

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        mToken.setAccessToken(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));
        mToken.setRefreshToken(prefs.getString(Constants.REFRESH_TOKEN_PREFERENCE_KEY, ""));
        mToken.setExpiresIn(prefs.getString(Constants.EXPIRES_IN_PREFERENCE_KEY, ""));
        mToken.setLastRetrieved(prefs.getLong(Constants.LAST_RETRIEVED_PREFERENCE_KEY, 0));

        mRefreshCallback = new RefreshTokenCallback(this);
        mAccessCallback = new AccessTokenCallback(this);
        mUserInfoCallback = new UserInfoCallback(this);
        mCallServiceCallback = new CallServiceCallback(this);

        // TOKEN ACCESS BUTTON EVENT HANDLER
        Button _atbtn = (Button) findViewById(R.id.atbtn);
        _atbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new AccessTokenAsyncTask(MainActivity.this, mAccessCallback, mToken).execute("Zach", "secret");
            }
        });

        // TOKEN REFRESH BUTTON EVENT HANDLER
        Button _rtbtn = (Button) findViewById(R.id.rtbtn);
        _rtbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new RefreshTokenAsyncTask(MainActivity.this, mRefreshCallback, mToken).execute();
            }
        });

        // CALL USER INFO BUTTON EVENT HANDLER
        Button _uibtn = (Button) findViewById(R.id.uibtn);
        _uibtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new UserInfoAsyncTask(MainActivity.this, mUserInfoCallback, mToken).execute();
            }
        });

        // CALL API BUTTON EVENT HANDLER
        Button _cabtn = (Button) findViewById(R.id.cabtn);
        _cabtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new CallServiceAsyncTask(mCallServiceCallback, mToken).execute("api/users");
            }
        });

        mMainContentView = findViewById(R.id.main_content);
        mProgressView = findViewById(R.id.loading_progress);

        mResultTextView = (TextView) findViewById(R.id.resulttxt);
        mResultTextView.setText(prefs.getString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, ""));

    }

    private void refreshTokenIfNeeded() {
        if (TokenHelper.isTokenExpired(mToken)) {
            showProgress(true);
            new RefreshTokenAsyncTask(MainActivity.this, mRefreshCallback, mToken).execute();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        refreshTokenIfNeeded();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").apply();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mUserInfoCallback)) {
            UserInfoResult info = mUserInfoCallback.getUserInfo();
            mResultTextView.setText(info.getUserInfoResult());
        } else if (cb.equals(mRefreshCallback)) {
            TokenResult token = mRefreshCallback.getTokenResult();
            mResultTextView.setText(token.getTokenResult());
            showProgress(false);
        } else if (cb.equals(mAccessCallback)) {
            TokenResult token = mAccessCallback.getTokenResult();
            mResultTextView.setText(token.getTokenResult());
        } else if (cb.equals(mCallServiceCallback)) {
            String result = mCallServiceCallback.getResult();
            mResultTextView.setText(result);
        }
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

            mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            mMainContentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mMainContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
