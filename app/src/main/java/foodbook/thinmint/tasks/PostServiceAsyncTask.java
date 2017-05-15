package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.models.domain.Note;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class PostServiceAsyncTask extends AsyncTask<String, String, WebAPIResult> {

    private CallServiceCallback mCallback;
    private Token mToken;
    private Map mMap;

    public PostServiceAsyncTask(CallServiceCallback callback, Token token, Map map) {
        this.mCallback = callback;
        this.mToken = token;
        this.mMap = map;
    }

    @Override
    protected WebAPIResult doInBackground(String... params) { // params[0] is path
        WebAPIResult result = null;
        String path = params[0];
        WebAPIConnect connect = new WebAPIConnect();

        TokenResult tokenResult = new TokenResult();

        if (TokenHelper.isTokenExpired(mToken)) {
            tokenResult = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);
        }

        if (!TokenHelper.isTokenExpired(mToken) || tokenResult.isSuccess()) {
            JSONObject jsonObject = new JSONObject(mMap);
            result = connect.postService(mToken.getAccessToken(), path, jsonObject);
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
