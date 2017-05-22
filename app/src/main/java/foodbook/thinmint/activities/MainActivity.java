package foodbook.thinmint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Date;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.day.DatePickerFragment;
import foodbook.thinmint.activities.day.DayFragment;
import foodbook.thinmint.activities.home.FeedFragment;
import foodbook.thinmint.activities.users.UserNotesFragment;
import foodbook.thinmint.activities.users.UserInfoFragment;
import foodbook.thinmint.activities.notes.NoteActivity;
import foodbook.thinmint.activities.users.UsersFragment;
import foodbook.thinmint.constants.Constants;

public class MainActivity extends TokenActivity implements
        IApiCallback, NavigationView.OnNavigationItemSelectedListener,
        DayFragment.OnDayFragmentDataListener, FeedFragment.OnHomeFragmentDataListener,
        UserNotesFragment.OnUserNotesFragmentDataListener, UserInfoFragment.OnUserInfoFragmentDataListener,
        UsersFragment.OnUsersFragmentDataListener {
    private static final String TAG = "MainActivity";

    public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    private Date mCurrentDate;

    private DayFragment mDayFragment;
    private OnNotesListInteractionListener mCurrentFragment;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        initToken();
        initUser();

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

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        mNavigationView = (NavigationView) findViewById(R.id.nav_view);
        mNavigationView.setNavigationItemSelectedListener(this);

        TextView userNameTextView = (TextView) mNavigationView.getHeaderView(0).findViewById(R.id.user_name);
        userNameTextView.setText(mUserName);
        getSupportFragmentManager()
                .addOnBackStackChangedListener(getBackStackChangedListener());


        MenuItem dailyItem = mNavigationView.getMenu().getItem(0);
        dailyItem.setChecked(true);
        onNavigationItemSelected(dailyItem);
    }

    private FragmentManager.OnBackStackChangedListener getBackStackChangedListener() {
        final FragmentManager.OnBackStackChangedListener listener = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                // Update your UI here.
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                } else if (fragmentManager.getBackStackEntryCount() == 0) {
                    try {
                        DayFragment dayFragment = (DayFragment) fragmentManager.findFragmentByTag("DayFragment");
                        if (dayFragment != null) {
                            setActionBarTitle(DATE_FORMAT.format(mCurrentDate));
                            toggleDayFragmentActions(true);
                            mNavigationView.getMenu().getItem(0).setChecked(true);
                        }
                    } catch (ClassCastException cce) {
                    }
                }
            }
        };

        return listener;
    }

    private void showDatePicker() {
        DatePickerFragment newFragment = new DatePickerFragment();
        newFragment.setDate(mCurrentDate);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    private void logout() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.edit().putString(Constants.ACCESS_TOKEN_PREFERENCE_KEY, "").apply();
        Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(loginActivity);
        finish();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.getBackStackEntryCount() > 0) {
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
            mCurrentFragment = mDayFragment;
        }
    }

    private void toggleDayFragmentActions(boolean show) {
        if (mMenu != null) {
            MenuItem selectDay = mMenu.findItem(R.id.action_date);
            MenuItem selectToday = mMenu.findItem(R.id.action_go_to_today);
            selectDay.setVisible(show);
            selectToday.setVisible(show);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        mMenu = menu;

        toggleDayFragmentActions(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.action_logout) {
            // User chose the "Settings" item, show the app settings UI...
            logout();
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

    private void startNoteActivity(long noteId) {
        Intent userIntent = new Intent(getApplicationContext(), NoteActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        userIntent.putExtras(bundle);

        startActivity(userIntent);
    }

    private void startCreateNoteActivity() {
        ActivityStarter.startCreateNoteActivity(MainActivity.this);
    }

    private void startUserActivity(String userSubject, String username) {
        ActivityStarter.startUserActivity(MainActivity.this, userSubject, username);
    }

    private void showUsersFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        UsersFragment usersFragment = UsersFragment.newInstance(mUserSubject);
        fragmentTransaction.replace(R.id.fragment_container, usersFragment, "Users");
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void showDayFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mDayFragment = DayFragment.newInstance(mCurrentDate);
        fragmentTransaction.replace(R.id.fragment_container, mDayFragment, "DayFragment");

        fragmentManager.popBackStack();

        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = mDayFragment;
    }

    private void showHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        FeedFragment feedFragment = FeedFragment.newInstance("Hello home fragment");
        fragmentTransaction.replace(R.id.fragment_container, feedFragment, "FeedFragment");

        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = feedFragment;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {

            setActionBarTitle(DATE_FORMAT.format(mCurrentDate));
            toggleDayFragmentActions(true);
            showDayFragment();

        } else if (id == R.id.nav_home) {

            setActionBarTitle("Feed");
            toggleDayFragmentActions(false);
            showHomeFragment();

        } else if (id == R.id.nav_my_stuff) {

            startUserActivity(mUserSubject, mUserName);

        } else if (id == R.id.nav_users) {

            setActionBarTitle("Users");
            toggleDayFragmentActions(false);
            showUsersFragment();

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDayFragmentCreated(View view) {
    }

    @Override
    public void selectDay(Date date) {
        setActionBarTitle(DATE_FORMAT.format(date));
        mDayFragment.setDate(date);
    }

    @Override
    public void showNote(long noteId) {
        startNoteActivity(noteId);
    }

    @Override
    public void showUser(String subject, String username) {
        startUserActivity(subject, username);
    }

    @Override
    public void onFeedFragmentCreated(View view) {
    }

    @Override
    public void onUserNotesFragmentCreated(View view) {
    }

    @Override
    public void onUserInfoFragmentCreated(View view) {
    }

    @Override
    public void onUsersFragmentCreated(View view) {
    }

    @Override
    public void callback(IAsyncCallback cb) {
    }
}
