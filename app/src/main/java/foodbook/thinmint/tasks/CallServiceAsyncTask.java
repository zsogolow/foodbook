package foodbook.thinmint.tasks;

import android.net.MailTo;
import android.os.AsyncTask;

import foodbook.thinmint.WebAPIConnect;
import foodbook.thinmint.idsrv.Token;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CallServiceAsyncTask extends AsyncTask<String, String, String> {

    private CallServiceCallback mCallback;
    private Token mToken;

    public CallServiceAsyncTask(CallServiceCallback callback, Token token) {
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected String doInBackground(String... params) { // params[0] is path
        String result = "";
        String path = params[0];
        WebAPIConnect connect = new WebAPIConnect();

        if (!mToken.getAccessToken().equals("")) {
            result = connect.callService(mToken.getAccessToken(), path);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        mCallback.onCompleted(result);
        mCallback.onPostExecute(this);
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
    }
}
