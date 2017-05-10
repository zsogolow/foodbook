package foodbook.thinmint.activities;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;

import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public abstract class TokenActivity extends AppCompatActivity {
    protected Token mToken;

    protected void initToken() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mToken = TokenHelper.getToken(prefs);
    }
}
