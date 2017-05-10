package foodbook.thinmint.tasks;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.UserInfoResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class UserInfoAsyncTask extends AsyncTask<String, String, UserInfoResult> {

    private ProgressDialog pd;

    private Context mContext;
    private UserInfoCallback mCallback;
    private Token objTkn;

    public UserInfoAsyncTask(Context context, UserInfoCallback callback, Token token) {
        mContext = context;
        mCallback = callback;
        objTkn = token;

        pd = new ProgressDialog(mContext);
    }

    @Override
    protected void onPreExecute() {
        pd.show();
    }

    @Override
    protected UserInfoResult doInBackground(String... params) {
        UserInfoResult userInfo = new UserInfoResult();

        publishProgress("Getting user info...");
        if (!objTkn.getAccessToken().equals("")) {
            userInfo = objTkn.getUserInfo();
        }

        return userInfo;
    }

    @Override
    protected void onProgressUpdate(String... progress) {
        pd.setMessage(progress[0]);
    }

    @Override
    protected void onPostExecute(UserInfoResult result) {
        mCallback.onCompleted(result);
        mCallback.onPostExecute(this);
        pd.hide();
    }

    @Override
    protected void onCancelled() {
        mCallback.onCancelled(this);
    }
}