package foodbook.thinmint.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public abstract class TokenActivity extends AppCompatActivity {
    protected Token mToken;
    protected String mUserSubject;
    protected String mUserName;
    protected long mUserId;

    protected void initToken() {
        mToken = TokenHelper.getToken(getApplicationContext());
    }

    protected void initUser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mUserSubject = prefs.getString(Constants.USER_SUBJECT, "");
        mUserName = prefs.getString(Constants.USER_NAME, "");
        mUserId = prefs.getLong(Constants.USER_ID, -1);
    }

    protected void setActionBarTitle(String title) {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(title);
        }
    }
}
