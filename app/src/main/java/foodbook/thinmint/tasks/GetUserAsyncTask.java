package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/10/2017.
 */

public class GetUserAsyncTask extends AsyncTask<String, String, WebAPIResult> {

    private CallServiceCallback mCallback;
    private Token mToken;

    public GetUserAsyncTask(CallServiceCallback callback, Token token) {
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected WebAPIResult doInBackground(String... params) {
        WebAPIResult result = null;
        String path = "api/users/" + params[0];
        WebAPIConnect<User> connect = new WebAPIConnect<>();

        if (!mToken.getAccessToken().equals("")) {
            result = connect.callService(mToken.getAccessToken(), path);
        }

        return result;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(WebAPIResult user) {
        mCallback.onCompleted(user);
        mCallback.onPostExecute(this);
    }
}
