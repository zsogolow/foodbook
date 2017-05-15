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

    private static final String SCOPES = "offline_access openid profile thinmintapi";

    private ProgressDialog pd;

    private Context mContext;
    private TokenResultCallback mCallback;
    private Token mToken;


    public AccessTokenAsyncTask(Context context, TokenResultCallback callback, Token token) {
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
        return mToken.getAccessToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET, params[0], params[1], SCOPES);
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        pd.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(TokenResult result) {
        if (result.isSuccess()) {
            Token tempToken = TokenHelper.getTokenFromJson(result);
            TokenHelper.saveToken(mContext, tempToken);
            TokenHelper.copyToken(tempToken, mToken);
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
