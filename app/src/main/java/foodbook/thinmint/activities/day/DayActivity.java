package foodbook.thinmint.activities.day;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenActivity;
import foodbook.thinmint.activities.common.RequestCodes;

public class DayActivity extends TokenActivity
        implements DayFragment.OnDayFragmentDataListener {

    public static final DateFormat PARSABLE_DATE_FORMAT = DateFormat.getDateInstance();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    public static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private Date mCurrentDate;
    private DayFragment mDayFragment;

    private View mProgressView;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_day);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        String date = bundle.getString("date");

        if (mCurrentDate == null) {
            mCurrentDate = new Date(System.currentTimeMillis());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startCreateNoteActivity();
            }
        });

        mContentView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.loading_progress);

        setActionBarTitle(DATE_FORMAT.format(mCurrentDate));
        showDayFragment();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.NOTE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    String action = data.getStringExtra(RequestCodes.NOTE_EXTRA_ACTION);
                    long id = data.getLongExtra(RequestCodes.NOTE_EXTRA_ID, -1);
                    if (action.equals(RequestCodes.COMMENT_NOTE_ACTION)) {
                        mDayFragment.onCommentAdded(id);
                    } else if (action.equals(RequestCodes.DELETE_NOTE_ACTION)) {
                        mDayFragment.onNoteDeleted(id);
                    } else if (action.equals(RequestCodes.CREATE_NOTE_ACTION)) {
                        mDayFragment.onNoteAdded(id);
                    } else if (action.equals(RequestCodes.LIKE_NOTE_ACTION)) {
                        mDayFragment.onLikeAdded(id);
                    }
                }
                break;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.day, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            // User chose the "Settings" item, show the app settings UI...
            ActivityHelper.logout(DayActivity.this);
            return true;
        } else if (id == R.id.action_go_to_today) {
            mCurrentDate = new Date(System.currentTimeMillis());
            selectDay(mCurrentDate);
            return true;
        } else if (id == R.id.action_date) {
            showDatePicker();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void startCreateNoteActivity() {
        ActivityHelper.startCreateNoteActivityForResult(DayActivity.this);
    }

    private void showDayFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mDayFragment = DayFragment.newInstance(mCurrentDate);
        fragmentTransaction.replace(R.id.fragment_container, mDayFragment, "DayFragment");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(mCurrentDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDayFragmentCreated(View view) {
    }

    @Override
    public void selectDay(Date date) {
        setActionBarTitle(DATE_FORMAT.format(date));
        mDayFragment.setDate(date);
        mCurrentDate = date;
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
}
