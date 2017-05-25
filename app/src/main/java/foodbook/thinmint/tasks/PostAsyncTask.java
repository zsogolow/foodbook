package foodbook.thinmint.tasks;

import android.content.Context;
import android.os.AsyncTask;

import org.json.JSONObject;

import java.util.Map;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class PostAsyncTask extends AsyncTask<String, String, WebAPIResult> {

    private Context mContext;
    private AsyncCallback<WebAPIResult> mCallback;
    private Token mToken;
    private Map mMap;

    public PostAsyncTask(Context context, AsyncCallback<WebAPIResult> callback, Token token, Map map) {
        this.mContext = context;
        this.mCallback = callback;
        this.mToken = token;
        this.mMap = map;
    }

    @Override
    protected WebAPIResult doInBackground(String... params) { // params[0] is path
        WebAPIResult result = null;
        String path = params[0];
        WebAPIConnect connect = new WebAPIConnect();

        if (TokenHelper.isTokenExpired(mToken)) {
            TokenResult tokenResult = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);
            Token tempToken = TokenHelper.getTokenFromJson(tokenResult);
            TokenHelper.saveToken(mContext, tempToken);
            TokenHelper.copyToken(tempToken, mToken);
        }

        if (!TokenHelper.isTokenExpired(mToken)) {
            JSONObject jsonObject = new JSONObject(mMap);
            result = connect.post(mToken.getAccessToken(), path, jsonObject);
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(WebAPIResult result) {
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
