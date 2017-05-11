package foodbook.thinmint.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.idsrv.TokenHelper;
import foodbook.thinmint.idsrv.TokenResult;
import foodbook.thinmint.models.ObjectFactory;
import foodbook.thinmint.models.ParseException;
import foodbook.thinmint.models.UserFeed;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.GetUserFeedAsyncTask;
import foodbook.thinmint.tasks.RefreshTokenAsyncTask;
import foodbook.thinmint.tasks.RefreshTokenCallback;
import foodbook.thinmint.tasks.UserFeedCallback;

public class FeedActivity extends TokenActivity implements IActivityCallback {

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NoteArrayAdapter mAdapter;

    private GetUserFeedAsyncTask mUserFeedAsyncTask;
    private UserFeedCallback mUserFeedCallback;

    private RefreshTokenAsyncTask mRefreshTokenAsyncTask;
    private RefreshTokenCallback mRefreshTokenCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initToken();
        initUser();

        mRefreshTokenCallback = new RefreshTokenCallback(this);
        mUserFeedCallback = new UserFeedCallback(this);

        mUserFeedAsyncTask = new GetUserFeedAsyncTask(mUserFeedCallback, mToken);
        mUserFeedAsyncTask.execute(mUserSubject);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (ListView) findViewById(R.id.activity_main_listview);
        String[] fakeTweets = getResources().getStringArray(R.array.cat_names);
        mAdapter = new NoteArrayAdapter(this, android.R.layout.simple_list_item_1, new ArrayList<Note>());
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshContent();
            }
        });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    private void refreshContent() {
        mUserFeedAsyncTask = new GetUserFeedAsyncTask(mUserFeedCallback, mToken);
        mUserFeedAsyncTask.execute(mUserSubject);
    }

    private void refreshTokenIfNeeded() {
        if (TokenHelper.isTokenExpired(mToken)) {
            mRefreshTokenAsyncTask = new RefreshTokenAsyncTask(getApplicationContext(), mRefreshTokenCallback, mToken);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshTokenIfNeeded();
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
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(FeedActivity.this);
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
        if (cb.equals(mRefreshTokenCallback)) {
            // refresh token callback
            mRefreshTokenAsyncTask = null;
            TokenResult token = mRefreshTokenCallback.getTokenResult();
        } else if (cb.equals(mUserFeedCallback)) {
            // get user callback
            mUserFeedAsyncTask = null;
            UserFeed result = mUserFeedCallback.getResult();
            mUser = result.getUser();
            mAdapter = new NoteArrayAdapter(FeedActivity.this, android.R.layout.simple_list_item_1, result.getUsersNotes());
            mListView.setAdapter(mAdapter);
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private class NoteArrayAdapter extends ArrayAdapter<Note> {
        List<Note> mNotes = new ArrayList<>();

        public NoteArrayAdapter(Context context, int textViewResourceId, List<Note> objects) {
            super(context, textViewResourceId, objects);
            mNotes = objects;
        }

        @Override
        public int getCount() {
            return mNotes.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            View v = convertView;
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(android.R.layout.simple_list_item_1, null);
            TextView textView = (TextView) v.findViewById(android.R.id.text1);
            textView.setText(mNotes.get(position).getContent());
            return v;
        }
    }
}
