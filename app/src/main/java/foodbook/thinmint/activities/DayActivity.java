package foodbook.thinmint.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.JsonSyntaxException;

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.PostServiceAsyncTask;

public class DayActivity extends TokenActivity implements IActivityCallback, DayActivityFragment.DayFragmentDataListener {

    private static final String TAG = "DayActivity";
    private static final DateFormat mDateFormatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    private Date mCurrentDate;

    private View mProgressView;
    private View mContentView;

    private CallServiceAsyncTask mLoadingTask;
    private CallServiceCallback mLoadingCallback;

    private PostServiceAsyncTask mAddNoteTask;
    private CallServiceCallback mAddNoteCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mCurrentDate = new Date(System.currentTimeMillis());
        setDateTitle(mCurrentDate);

        mLoadingCallback = new CallServiceCallback(this);
        mAddNoteCallback = new CallServiceCallback(this);

        mProgressView = findViewById(R.id.loading_progress);
        mContentView = findViewById(R.id.content_view);

        initToken();
        initUser();

//        DayActivityFragment dayActivityFragment = (DayActivityFragment)
//                getSupportFragmentManager().findFragmentById(R.id.fragment);
    }

    private void setDateTitle(Date date) {
        ActionBar toolbar = getSupportActionBar();
        if (toolbar != null) {
            toolbar.setTitle(mDateFormatter.format(date));
        }
    }


    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
            mContentView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mContentView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
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
            case R.id.action_date:
                DatePickerFragment newFragment = new DatePickerFragment();
                newFragment.setDate(mCurrentDate);
                newFragment.show(getSupportFragmentManager(), "datePicker");
                return true;

            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_logout:
                // User chose the "Settings" item, show the app settings UI...
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
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

    private void setFragmentLoading(boolean loading) {
        DayActivityFragment dayActivityFragment = (DayActivityFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        dayActivityFragment.setLoading(loading);
    }


    @Override
    public void addNote(Note note) {
        Log.d(TAG, note.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("content", note.getContent());
        map.put("userid", mUserId);
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        mAddNoteTask = new PostServiceAsyncTask(this, mAddNoteCallback, mToken, map);
        mAddNoteTask.execute("api/notes");
        setFragmentLoading(true);
    }

    @Override
    public void selectDay(Date date) {
        DayActivityFragment dayActivityFragment = (DayActivityFragment)
                getSupportFragmentManager().findFragmentById(R.id.fragment);
        dayActivityFragment.setLoading(true);

        mCurrentDate = date;
        setDateTitle(mCurrentDate);
        mLoadingTask = new CallServiceAsyncTask(this, mLoadingCallback, mToken);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);

        String path = String.format("api/users/%s/notes?filter=", mUserSubject);
        String rawQuery = String.format(Locale.US, "((DateCreated Ge %d-%d-%d 00:00:00 -0700) And (DateCreated Le %d-%d-%d 23:59:59 -0700))",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));
        try {
            String encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
            path += encodedQuery;
            mLoadingTask.execute(path);
        } catch (Exception e) {
        }

    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mLoadingCallback)) {
            mLoadingTask = null;
            DayActivityFragment dayActivityFragment = (DayActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
            List<Note> notes = new ArrayList<>();
            try {
                notes = JsonHelper.getNotes(mLoadingCallback.getResult().getResult());
            } catch (JsonSyntaxException jse) {
            }

            dayActivityFragment.onDataRetrieved(mCurrentDate, notes);
        } else if (cb.equals(mAddNoteCallback)) {
            mAddNoteTask = null;
            DayActivityFragment dayActivityFragment = (DayActivityFragment)
                    getSupportFragmentManager().findFragmentById(R.id.fragment);
            Note newNote = null;
            try {
                newNote = JsonHelper.getNote(mAddNoteCallback.getResult().getResult());
            } catch (JsonSyntaxException jse) {
            }
            dayActivityFragment.onNoteAdded(newNote);
        }
    }
}
