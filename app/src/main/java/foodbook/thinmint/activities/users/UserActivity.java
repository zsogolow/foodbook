package foodbook.thinmint.activities.users;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenActivity;
import foodbook.thinmint.activities.common.RequestCodes;

public class UserActivity extends TokenActivity implements
        UserInfoFragment.OnUserInfoFragmentDataListener,
        UserNotesFragment.OnUserNotesFragmentDataListener {

    private MyPagerAdapter mFragmentPagerAdapter;
    private ViewPager mPager;

    private View mProgressView;
    private View mContentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        String subject = bundle.getString("user_subject");
        String username = bundle.getString("user_name");

        mFragmentPagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), subject);

        mPager = (ViewPager) findViewById(R.id.view_pager);
        mPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mPager);

        mContentView = findViewById(R.id.fragment_container);
        mProgressView = findViewById(R.id.loading_progress);

        setActionBarTitle(username);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RequestCodes.NOTE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    UserNotesFragment notesFragment = mFragmentPagerAdapter.mNotesFragment;

                    String action = data.getStringExtra(RequestCodes.NOTE_EXTRA_ACTION);
                    long id = data.getLongExtra(RequestCodes.NOTE_EXTRA_ID, -1);
                    if (action.equals(RequestCodes.COMMENT_NOTE_ACTION)) {
                        notesFragment.onCommentAdded(id, 0);
                    } else if (action.equals(RequestCodes.DELETE_NOTE_ACTION)) {
                        notesFragment.onNoteDeleted(id);
                    } else if (action.equals(RequestCodes.CREATE_NOTE_ACTION)) {
                        notesFragment.onNoteAdded(id);
                    }
                }
                break;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.user, menu);

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
            ActivityHelper.logout(UserActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public void onUserInfoFragmentCreated(View view) {

    }

    @Override
    public void onUserNotesFragmentCreated(View view) {

    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private static String[] NAMES = {"Notes", "Info"};
        private String mUserId;
        public UserNotesFragment mNotesFragment;

        public MyPagerAdapter(FragmentManager fragmentManager, String userid) {
            super(fragmentManager);
            mUserId = userid;
        }

        // Returns total number of pages
        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        // Returns the fragment to display for that page
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    mNotesFragment = UserNotesFragment.newInstance(mUserId);
                    return mNotesFragment;
                case 1:
                    return UserInfoFragment.newInstance(mUserId);
                default:
                    return null;
            }
        }

        // Returns the page title for the top indicator
        @Override
        public CharSequence getPageTitle(int position) {
            return NAMES[position];
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
}
