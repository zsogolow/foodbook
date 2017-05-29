package foodbook.thinmint.activities.common;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public abstract class TokenActivity extends AppCompatActivity {

    public static final DateFormat PARSABLE_DATE_FORMAT = DateFormat.getDateInstance();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    public static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

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
