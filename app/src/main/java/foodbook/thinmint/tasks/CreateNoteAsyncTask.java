package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.models.domain.Note;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class CreateNoteAsyncTask extends AsyncTask<String, String, WebAPIResult> {

    private CallServiceCallback mCallback;
    private Token mToken;
    private Note mNote;

    public CreateNoteAsyncTask(CallServiceCallback callback, Token token, Note note) {
        this.mCallback = callback;
        this.mToken = token;
        this.mNote = note;
    }

    @Override
    protected WebAPIResult doInBackground(String... params) { // params[0] is path
        WebAPIResult result = null;
        String path = params[0];
        WebAPIConnect connect = new WebAPIConnect();

        if (!mToken.getAccessToken().equals("")) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("userid", 1);
                jsonObject.put("content","This is a fake note!");
                result = connect.postService(mToken.getAccessToken(), path, jsonObject);
            } catch (JSONException je) {}
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
