package foodbook.thinmint.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.day.DatePickerFragment;
import foodbook.thinmint.activities.day.DayFragment;
import foodbook.thinmint.activities.home.HomeFragment;
import foodbook.thinmint.activities.mystuff.MyStuffFragment;
import foodbook.thinmint.activities.notes.CreateNoteActivity;
import foodbook.thinmint.activities.notes.NoteActivity;
import foodbook.thinmint.activities.users.UserActivity;
import foodbook.thinmint.activities.users.UsersFragment;
import foodbook.thinmint.constants.Constants;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.PostServiceAsyncTask;

public class MainActivity extends TokenActivity implements
        IActivityCallback, NavigationView.OnNavigationItemSelectedListener,
        DayFragment.OnDayFragmentDataListener, HomeFragment.OnHomeFragmentDataListener,
        MyStuffFragment.OnMyStuffFragmentDataListener, UsersFragment.OnUsersFragmentDataListener {

    public static final DateFormat DATE_FORMAT = DateFormat.getDateInstance();

    private static final String TAG = "MainActivity";
    private static final DateFormat mDateFormatter = new SimpleDateFormat("EEE, d MMM yyyy", Locale.US);
    private Date mCurrentDate;

    private CallServiceAsyncTask mGetFeedTask;
    private CallServiceCallback mGetFeedCallback;

    private CallServiceAsyncTask mGetMyStuffTask;
    private CallServiceCallback mGetMyStuffCallback;

    private CallServiceAsyncTask mGetUsersTask;
    private CallServiceCallback mGetUsersCallback;

    private CallServiceAsyncTask mLoadingTask;
    private CallServiceCallback mLoadingCallback;

    private PostServiceAsyncTask mAddNoteTask;
    private CallServiceCallback mAddNoteCallback;

    private DayFragment mDayFragment;
    private HomeFragment mHomeFragment;
    private MyStuffFragment mMyStuffFragment;
    private UsersFragment mUsersFragment;
    private OnNotesListInteractionListener mCurrentFragment;

    private NavigationView mNavigationView;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mGetFeedCallback = new CallServiceCallback(this);
        mGetMyStuffCallback = new CallServiceCallback(this);
        mGetUsersCallback = new CallServiceCallback(this);
        mLoadingCallback = new CallServiceCallback(this);
        mAddNoteCallback = new CallServiceCallback(this);

        initToken();
        initUser();

        if (mCurrentDate == null) {
            mCurrentDate = new Date(System.currentTimeMillis());
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createNoteIntent = new Intent(MainActivity.this, CreateNoteActivity.class);
                startActivity(createNoteIntent);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
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
                            setActionBarTitle(mDateFormatter.format(mCurrentDate));
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
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
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

    private void startUserActivity(String userSubject, String username) {
        Intent userIntent = new Intent(getApplicationContext(), UserActivity.class);

        Bundle bundle = new Bundle();
        bundle.putString("user_subject", userSubject);
        bundle.putString("user_name", username);
        userIntent.putExtras(bundle);

        startActivity(userIntent);
    }

    private void showUsersFragment() {
        setActionBarTitle("Users");

        toggleDayFragmentActions(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mUsersFragment = UsersFragment.newInstance(mUserSubject);
        fragmentTransaction.replace(R.id.fragment_container, mUsersFragment, "Users");
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();
    }

    private void showMyStuffFragment() {
        setActionBarTitle("My Stuff");

        toggleDayFragmentActions(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mMyStuffFragment = MyStuffFragment.newInstance(mUserSubject);
        fragmentTransaction.replace(R.id.fragment_container, mMyStuffFragment, "MyStuff");
        fragmentTransaction.addToBackStack(null);
        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = mMyStuffFragment;
    }

    private void showDayFragment() {
        setActionBarTitle(mDateFormatter.format(mCurrentDate));

        toggleDayFragmentActions(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mDayFragment = new DayFragment();
        fragmentTransaction.replace(R.id.fragment_container, mDayFragment, "DayFragment");

        fragmentManager.popBackStack();

        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = mDayFragment;
    }

    private void showHomeFragment() {
        setActionBarTitle("Feed");

        toggleDayFragmentActions(false);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.addToBackStack(null);

        mHomeFragment = HomeFragment.newInstance("Hello home fragment");
        fragmentTransaction.replace(R.id.fragment_container, mHomeFragment, "HomeFragment");

        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = mHomeFragment;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {

            showDayFragment();

        } else if (id == R.id.nav_home) {

            showHomeFragment();

        } else if (id == R.id.nav_my_stuff) {

            showMyStuffFragment();

        } else if (id == R.id.nav_users) {

            showUsersFragment();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDayFragmentCreated(View view) {
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
    }

    @Override
    public void selectDay(Date date) {
        mCurrentDate = date;

        mDayFragment.setDate(mCurrentDate);

        setActionBarTitle(mDateFormatter.format(mCurrentDate));
        mLoadingTask = new CallServiceAsyncTask(this, mLoadingCallback, mToken);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);

        String path = String.format("api/users/%s/notes?filter=", mUserSubject);
        String rawQuery = String.format(Locale.US, "((DateCreated Ge %d-%d-%d 00:00:00 -0700) And (DateCreated Le %d-%d-%d 23:59:59 -0700))",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mLoadingTask.execute(path);
    }

    @Override
    public void showNote(long noteId) {
        startNoteActivity(noteId);
    }

    @Override
    public void onHomeFragmentCreated(View view) {
        setActionBarTitle("Feed");
    }

    @Override
    public void refreshFeed() {
        mGetFeedTask = new CallServiceAsyncTask(this, mGetFeedCallback, mToken);

        String path = "api/notes?sort=";
        String rawQuery = "-datecreated";

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mGetFeedTask.execute(path);
    }

    @Override
    public void onMyStuffFragmentCreated(View view) {
        setActionBarTitle("My Stuff");
    }

    @Override
    public void refreshMyStuff() {
        mGetMyStuffTask = new CallServiceAsyncTask(this, mGetMyStuffCallback, mToken);

        String path = String.format(Locale.US, "api/users/%s/notes?sort=", mUserSubject);
        String rawQuery = "-datecreated";

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mGetMyStuffTask.execute(path);
    }


    @Override
    public void onUsersFragmentCreated(View view) {
        setActionBarTitle("Users");
    }

    @Override
    public void refreshUsers() {
        mGetUsersTask = new CallServiceAsyncTask(this, mGetUsersCallback, mToken);

        String path = "api/users?filter=";
        String rawQuery = String.format(Locale.US, "(Subject Ne %s)", mUserSubject);

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mGetUsersTask.execute(path);
    }

    @Override
    public void showUser(String subject, String username) {
        startUserActivity(subject, username);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mLoadingCallback)) {
            mLoadingTask = null;
            List<Note> notes = JsonHelper.getNotes(mLoadingCallback.getResult().getResult());
            mDayFragment.onNotesRetrieved(notes);
        } else if (cb.equals(mAddNoteCallback)) {
            mAddNoteTask = null;
            Note newNote = JsonHelper.getNote(mAddNoteCallback.getResult().getResult());
            mCurrentFragment.onNoteAdded(newNote);
        } else if (cb.equals(mGetFeedCallback)) {
            mGetFeedTask = null;
            List<Note> notes = JsonHelper.getNotes(mGetFeedCallback.getResult().getResult());
            mHomeFragment.onNotesRetrieved(notes);
        } else if (cb.equals(mGetMyStuffCallback)) {
            mGetMyStuffTask = null;
            List<Note> notes = JsonHelper.getNotes(mGetMyStuffCallback.getResult().getResult());
            mMyStuffFragment.onNotesRetrieved(notes);
        } else if (cb.equals(mGetUsersCallback)) {
            mGetUsersTask = null;
            List<User> users = JsonHelper.getUsers(mGetUsersCallback.getResult().getResult());
            mUsersFragment.onUsersRetrieved(users);
        }
    }
}
