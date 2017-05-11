package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import java.util.List;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.models.GlobalFeed;
import foodbook.thinmint.models.ObjectFactory;
import foodbook.thinmint.models.ParseException;
import foodbook.thinmint.models.domain.Note;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class GetGlobalFeedAsyncTask extends AsyncTask<String, String, GlobalFeed> {

    private GlobalFeedCallback mCallback;
    private Token mToken;

    public GetGlobalFeedAsyncTask(GlobalFeedCallback callback, Token token) {
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected GlobalFeed doInBackground(String... params) {
        GlobalFeed result = new GlobalFeed();
        WebAPIConnect connect = new WebAPIConnect();
        publishProgress("Getting global feed...");

        TokenResult tokenResult = new TokenResult();

        if (TokenHelper.isTokenExpired(mToken)) {
            tokenResult = mToken.getRefreshToken(Constants.CLIENT_ID, Constants.CLIENT_SECRET);
        }

        if (!TokenHelper.isTokenExpired(mToken) || tokenResult.isSuccess()) {
            WebAPIResult userNotesResult = connect.callService(mToken.getAccessToken(), "api/notes");
            try {
                List<Note> notes = new ObjectFactory<Note>().DeserializeCollection(new Note(), userNotesResult.getResult());
                result.setNotes(notes);
            } catch (ParseException e) {
            }
        }

        return result;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onPostExecute(GlobalFeed result) {
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
