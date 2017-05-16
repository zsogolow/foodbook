package foodbook.thinmint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.TokenResultCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.PostServiceAsyncTask;
import foodbook.thinmint.tasks.RefreshTokenAsyncTask;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.idsrv.UserInfoResult;
import foodbook.thinmint.tasks.UserInfoAsyncTask;
import foodbook.thinmint.tasks.UserInfoCallback;
import foodbook.thinmint.constants.Constants;

public class MainActivity extends TokenActivity implements IActivityCallback {

    private TokenResultCallback mRefreshCallback;
    private UserInfoCallback mUserInfoCallback;
    private CallServiceCallback mGetUsersCallback;
    private CallServiceCallback mGetUserCallback;
    private CallServiceCallback mGetMyNotesCallback;
    private CallServiceCallback mCreateNoteCallback;

    private TextView mResultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initToken();
        initUser();

        mRefreshCallback = new TokenResultCallback(this);
        mUserInfoCallback = new UserInfoCallback(this);
        mGetUsersCallback = new CallServiceCallback(this);
        mGetUserCallback = new CallServiceCallback(this);
        mGetMyNotesCallback = new CallServiceCallback(this);
        mCreateNoteCallback = new CallServiceCallback(this);

        // TOKEN REFRESH BUTTON EVENT HANDLER
        Button _rtbtn = (Button) findViewById(R.id.rtbtn);
        _rtbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new RefreshTokenAsyncTask(MainActivity.this, mRefreshCallback, mToken).execute();
            }
        });

        // CALL USER INFO BUTTON EVENT HANDLER
        Button _uibtn = (Button) findViewById(R.id.uibtn);
        _uibtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new UserInfoAsyncTask(MainActivity.this, mUserInfoCallback, mToken).execute();
            }
        });

        // CALL API BUTTON EVENT HANDLER
        Button _cabtn = (Button) findViewById(R.id.cabtn);
        _cabtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new CallServiceAsyncTask(MainActivity.this, mGetUsersCallback, mToken).execute("api/users");
            }
        });

        // GET USER BUTTON EVENT HANDLER
        Button _atbtn = (Button) findViewById(R.id.gubtn);
        _atbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new CallServiceAsyncTask(MainActivity.this, mGetUserCallback, mToken).execute("api/users/" + mUserSubject);
            }
        });

//        filter=((DateCreated Ge 2017-05-16 00:00:00 -0700) And (DateCreated Le 2017-05-16 23:59:59 -0700))

        // GET MY NOTES BUTTON EVENT HANDLER
        Button _gnbtn = (Button) findViewById(R.id.gnbtn);
        _gnbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                new CallServiceAsyncTask(MainActivity.this, mGetMyNotesCallback, mToken).execute("api/users/" + mUserSubject + "/notes");
            }
        });


        // CREATE NOTE BUTTON EVENT HANDLER
        Button _cnbtn = (Button) findViewById(R.id.cnbtn);
        _cnbtn.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Map<String, Object> map = new HashMap<>();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
                map.put("content", "this is a note");
                map.put("userid", mUserId);
                map.put("datecreated", dateFormat.format(new Date()));
                new PostServiceAsyncTask(MainActivity.this, mCreateNoteCallback, mToken, map).execute("api/notes");
            }
        });

        mResultTextView = (TextView) findViewById(R.id.resulttxt);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").apply();
                Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(loginActivity);
                finish();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mUserInfoCallback)) {
            UserInfoResult info = mUserInfoCallback.getUserInfo();
            mResultTextView.setText(info.getUserInfoResult());
        } else if (cb.equals(mRefreshCallback)) {
            TokenResult token = mRefreshCallback.getTokenResult();
            mResultTextView.setText(token.getTokenResult());
        } else if (cb.equals(mGetUsersCallback)) {
            WebAPIResult result = mGetUsersCallback.getResult();
            String usersString = result.getResult();
//            List<User> users = JsonHelper.getUsers(usersString);
            mResultTextView.setText(result.getResult());
        } else if (cb.equals(mGetUserCallback)) {
            WebAPIResult result = mGetUserCallback.getResult();
            String userString = result.getResult();
//            User user = JsonHelper.getUser(userString);
            mResultTextView.setText(userString);
        } else if (cb.equals(mGetMyNotesCallback)) {
            WebAPIResult result = mGetMyNotesCallback.getResult();
            String noteString = result.getResult();
//            List<Note> notes = JsonHelper.getNotes(noteString);
            mResultTextView.setText(noteString);
        } else if (cb.equals(mCreateNoteCallback)) {
            WebAPIResult result = mCreateNoteCallback.getResult();
            String noteString = result.getResult();
//            Note note = JsonHelper.getNote(noteString);
            mResultTextView.setText(noteString);
        }
    }
}
