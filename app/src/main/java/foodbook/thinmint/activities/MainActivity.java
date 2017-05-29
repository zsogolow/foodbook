package foodbook.thinmint.activities;

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

import java.util.Date;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.common.RequestCodes;
import foodbook.thinmint.activities.common.TokenActivity;
import foodbook.thinmint.activities.feed.FeedFragment;
import foodbook.thinmint.activities.users.UsersFragment;

public class MainActivity extends TokenActivity implements
        NavigationView.OnNavigationItemSelectedListener, FeedFragment.OnFeedFragmentDataListener,
        UsersFragment.OnUsersFragmentDataListener {

    private static final String TAG = "MainActivity";

    private FeedFragment mFeedFragment;
    private OnNotesListInteractionListener mCurrentFragment;

    private NavigationView mNavigationView;
    private DrawerLayout mDrawerLayout;

    private View mProgressView;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initToken();
        initUser();

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

        mContentView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.loading_progress);

        MenuItem dailyItem = mNavigationView.getMenu().getItem(0);
        dailyItem.setChecked(true);
        onNavigationItemSelected(dailyItem);
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
                        mCurrentFragment.onCommentAdded(id);
                    } else if (action.equals(RequestCodes.DELETE_NOTE_ACTION)) {
                        mCurrentFragment.onNoteDeleted(id);
                    } else if (action.equals(RequestCodes.CREATE_NOTE_ACTION)) {
                        mCurrentFragment.onNoteAdded(id);
                    } else if (action.equals(RequestCodes.LIKE_NOTE_ACTION)) {
                        mCurrentFragment.onLikeAdded(id);
                    }
                }
                break;
        }
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
            mCurrentFragment = mFeedFragment;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            // User chose the "Settings" item, show the app settings UI...
            ActivityHelper.logout(MainActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_calendar) {

            startDayActivity(new Date(System.currentTimeMillis()));

        } else if (id == R.id.nav_home) {

            setActionBarTitle("Feed");
            showHomeFragment();

        } else if (id == R.id.nav_my_stuff) {

            startUserActivity(mUserSubject, mUserName);

        } else if (id == R.id.nav_users) {

            setActionBarTitle("Users");
            showUsersFragment();

        }

        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    // Private Helpers
    private FragmentManager.OnBackStackChangedListener getBackStackChangedListener() {
        final FragmentManager.OnBackStackChangedListener listener = new FragmentManager.OnBackStackChangedListener() {
            public void onBackStackChanged() {
                // Update your UI here.
                FragmentManager fragmentManager = getSupportFragmentManager();

                if (fragmentManager.getBackStackEntryCount() > 0) {
                    String fragmentTag = fragmentManager.getBackStackEntryAt(fragmentManager.getBackStackEntryCount() - 1).getName();
                } else if (fragmentManager.getBackStackEntryCount() == 0) {
                    try {
                        FeedFragment feedFragment = (FeedFragment) fragmentManager.findFragmentByTag("FeedFragment");
                        if (feedFragment != null) {
                            setActionBarTitle("Feed");
                            mNavigationView.getMenu().getItem(0).setChecked(true);
                        }
                    } catch (ClassCastException cce) {
                    }
                }
            }
        };

        return listener;
    }

    private void startDayActivity(Date date) {
        ActivityHelper.startDayActivity(MainActivity.this, date);
    }

    private void startCreateNoteActivity() {
        ActivityHelper.startCreateNoteActivityForResult(MainActivity.this);
    }

    private void startUserActivity(String userSubject, String username) {
        ActivityHelper.startUserActivity(MainActivity.this, userSubject, username);
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

    private void showHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        mFeedFragment = FeedFragment.newInstance("Hello home fragment");
        fragmentTransaction.replace(R.id.fragment_container, mFeedFragment, "FeedFragment");

        fragmentManager.popBackStack();

        // Commit the transaction
        fragmentTransaction.commit();

        mCurrentFragment = mFeedFragment;
    }

    @Override
    public void onFeedFragmentCreated(View view) {
    }

    @Override
    public void onUsersFragmentCreated(View view) {
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
