package foodbook.thinmint.tasks;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.idsrv.Token;

/**
 * Created by ZachS on 5/29/2017.
 */

public class TasksHelper {
    public static AsyncTask likeNote(Context context, AsyncCallback<WebAPIResult> callback, Token token, long noteId, long userId) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("noteid", noteId);
        map.put("userid", userId);
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        PostAsyncTask task = new PostAsyncTask(context, callback, token, map);
        task.execute("api/likes");
        return task;
    }

    public static AsyncTask unlikeNote(Context context, AsyncCallback<WebAPIResult> callback, Token token, long noteId, long userId) {
        DeleteAsyncTask task = new DeleteAsyncTask(context, callback, token);
        Query query = Query.builder()
                .setPath(String.format(Locale.US, "api/notes/%d/likes/%d", noteId, userId))
                .build();
        task.execute(query);
        return task;
    }

    public static AsyncTask getNote(Context context, AsyncCallback<WebAPIResult> callback, Token token, long noteId) {
        GetAsyncTask task = new GetAsyncTask(context, callback, token);
        String path = "api/notes/" + noteId;

        Query query = Query.builder()
                .setPath(path)
                .build();

        task.execute(query);
        return task;
    }

    public static AsyncTask deleteNote(Context context, AsyncCallback<WebAPIResult> callback, Token token, long noteId) {
        DeleteAsyncTask task = new DeleteAsyncTask(context, callback, token);
        Query query = Query.builder()
                .setPath("api/notes/" + noteId)
                .build();
        task.execute(query);
        return task;
    }

    public static AsyncTask getNotes(Context context, AsyncCallback<WebAPIResult> callback, Token token,
                                String userSubject, int page, String filter) {
        String path = String.format(Locale.US, "api/users/%s/notes", userSubject);

        Query query = Query.builder()
                .setPath(path)
                .setSort("-datecreated")
                .setFilter(filter)
                .setPage(page)
                .build();

        GetAsyncTask task = new GetAsyncTask(context, callback, token);
        task.execute(query);
        return task;
    }

    public static AsyncTask getNotes(Context context, AsyncCallback<WebAPIResult> callback, Token token,
                                int page, String filter) {
        String path = "api/notes";

        Query query = Query.builder()
                .setPath(path)
                .setSort("-datecreated")
                .setFilter(filter)
                .setPage(page)
                .build();

        GetAsyncTask task = new GetAsyncTask(context, callback, token);
        task.execute(query);
        return task;
    }

    public static AsyncTask addComment(Context context, AsyncCallback<WebAPIResult> callback, Token token,
                                  long noteId, long userId, String comment) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("noteid", noteId);
        map.put("userid", userId);
        map.put("text", comment);
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        PostAsyncTask task = new PostAsyncTask(context, callback, token, map);
        task.execute("api/comments");
        return task;
    }

    public static AsyncTask getUsers(Context context, AsyncCallback<WebAPIResult> callback, Token token,
                                int page, String filter) {
        Query query = Query.builder()
                .setPath("api/users")
                .setSort("username")
                .setFilter(filter)
                .setPage(page)
                .build();

        GetAsyncTask task = new GetAsyncTask(context, callback, token);
        task.execute(query);
        return task;
    }
}
