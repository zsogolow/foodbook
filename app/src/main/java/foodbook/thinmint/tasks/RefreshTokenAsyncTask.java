package foodbook.thinmint.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.UserInfo;
import foodbook.thinmint.idsrv.UserInfoHelper;
import foodbook.thinmint.idsrv.UserInfoResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class RefreshTokenAsyncTask extends AsyncTask<String, String, TokenResult> {

    private ProgressDialog pd;

    private Context mContext;
    private TokenResultCallback mCallback;
    private Token mToken;

    public RefreshTokenAsyncTask(Context context, TokenResultCallback callback, Token token) {
        this.mContext = context;
        this.mCallback = callback;
        this.mToken = token;

        this.pd = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        //pd.show();
    }

    @Override
    protected TokenResult doInBackground(String... params) {
        publishProgress("Getting refresh token...");
        TokenResult result = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);

        if (result.isSuccess()) {
            Token tempToken = TokenHelper.getTokenFromJson(result);
            TokenHelper.saveToken(mContext, tempToken);
            TokenHelper.copyToken(tempToken, mToken);

            UserInfoResult userInfoResult = mToken.getUserInfo();
            UserInfo userInfo = UserInfoHelper.getUserInfoFromJson(userInfoResult.getUserInfoResult());
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext.getApplicationContext());
            prefs.edit().putString(Constants.USER_SUBJECT, userInfo.getSubject()).apply();

            WebAPIResult apiResult = new WebAPIConnect().callService(mToken.getAccessToken(), "api/users/" + userInfo.getSubject());
            User user = JsonHelper.getUser(apiResult.getResult());
            prefs.edit().putLong(Constants.USER_ID, user.getId()).apply();
            prefs.edit().putString(Constants.USER_NAME, user.getUsername()).apply();
        }

        return result;
    }


    @Override
    protected void onProgressUpdate(String... progress) {
        //pd.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(TokenResult result) {
        if (result.isSuccess()) {
            mCallback.onCompleted(result);
        } else {
            mCallback.onError(result.getTokenResult());
        }

        mCallback.onPostExecute(this);
//        pd.hide();
    }

    @Override
    protected void onCancelled() {
        mCallback.onCancelled(this);
    }
}

