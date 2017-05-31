package foodbook.thinmint.activities.common;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public abstract class TokenFragment extends Fragment implements IApiCallback {
    protected Token mToken;
    protected String mUserSubject;
    protected String mUserName;
    protected long mUserId;

    protected AsyncTask mRunningTask;

    protected void initToken() {
        mToken = TokenHelper.getToken(getContext());
    }

    protected void initUser() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        mUserSubject = prefs.getString(Constants.USER_SUBJECT, "");
        mUserName = prefs.getString(Constants.USER_NAME, "");
        mUserId = prefs.getLong(Constants.USER_ID, -1);
    }

    protected abstract void refreshFragment();

    @Override
    public void callback(IAsyncCallback cb) {
        mRunningTask = null;
    }
}
