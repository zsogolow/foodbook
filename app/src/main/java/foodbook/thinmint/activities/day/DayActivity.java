package foodbook.thinmint.activities.day;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
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
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.activities.common.RequestCodes;

public class DayActivity extends TokenActivity
        implements DayFragment.OnDayFragmentDataListener {

    public static final DateFormat PARSABLE_DATE_FORMAT = DateFormat.getDateInstance();
    public static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    public static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    public static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private Date mCurrentDate;
    private DayFragment mDayFragment;

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
                        // todo
                    } else if (action.equals(RequestCodes.DELETE_NOTE_ACTION)) {
                        mDayFragment.onNoteDeleted(id);
                    } else if (action.equals(RequestCodes.CREATE_NOTE_ACTION)) {
                        mDayFragment.onNoteAdded(id);
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
}
