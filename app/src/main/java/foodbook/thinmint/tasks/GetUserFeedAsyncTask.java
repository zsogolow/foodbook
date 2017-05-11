package foodbook.thinmint.tasks;

import android.os.AsyncTask;

import java.util.ArrayList;
import java.util.List;

import foodbook.thinmint.api.WebAPIConnect;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.idsrv.Token;
import foodbook.thinmint.models.ObjectFactory;
import foodbook.thinmint.models.ParseException;
import foodbook.thinmint.models.UserFeed;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/9/2017.
 */

public class GetUserFeedAsyncTask extends AsyncTask<String, String, UserFeed> {

    private UserFeedCallback mCallback;
    private Token mToken;

    public GetUserFeedAsyncTask(UserFeedCallback callback, Token token) {
        this.mCallback = callback;
        this.mToken = token;
    }

    @Override
    protected UserFeed doInBackground(String... params) { // params[0] is subject
        UserFeed result = new UserFeed();
        String subject = params[0];
        WebAPIConnect connect = new WebAPIConnect();
        publishProgress("Getting user's feed...");
        if (!mToken.getAccessToken().equals("")) {
            WebAPIResult userResult = connect.callService(mToken.getAccessToken(), "api/users/" + subject);
            WebAPIResult userNotesResult = connect.callService(mToken.getAccessToken(), "api/users/" + subject + "/notes");
            try {
                User user = (User) new ObjectFactory<User>().Deserialize(new User(), userResult.getResult());
                result.setUser(user);

                List<Note> notes = new ObjectFactory<Note>().DeserializeCollection(new Note(), userNotesResult.getResult());
                result.setUsersNotes(notes);
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
    protected void onPostExecute(UserFeed result) {
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
