package foodbook.thinmint.activities.users;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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

import java.util.Date;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityStarter;
import foodbook.thinmint.activities.LoginActivity;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.activities.notes.NoteActivity;
import foodbook.thinmint.constants.Constants;

import static foodbook.thinmint.activities.MainActivity.DELETE_NOTE_EXTRA_ID;
import static foodbook.thinmint.activities.MainActivity.DELETE_NOTE_REQUEST_CODE;

public class UserActivity extends TokenActivity implements
        UserInfoFragment.OnUserInfoFragmentDataListener,
        UserNotesFragment.OnUserNotesFragmentDataListener {

    private FragmentPagerAdapter mFragmentPagerAdapter;
    private ViewPager mPager;

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

        setActionBarTitle(username);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case DELETE_NOTE_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    long oldId = data.getLongExtra(DELETE_NOTE_EXTRA_ID, -1);

                    // TODO refresh user notes here

                    break;
                }
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
            ActivityStarter.logout(UserActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void startNoteActivity(long noteId) {
        Intent userIntent = new Intent(getApplicationContext(), NoteActivity.class);

        Bundle bundle = new Bundle();
        bundle.putLong("note_id", noteId);
        userIntent.putExtras(bundle);

        startActivity(userIntent);
    }

    @Override
    public void onUserInfoFragmentCreated(View view) {

    }

    @Override
    public void onUserNotesFragmentCreated(View view) {

    }
//
//    @Override
//    public void showNote(long noteId) {
//        startNoteActivity(noteId);
//    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private static String[] NAMES = {"Notes", "Info"};
        private String mUserId;

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
                    return UserNotesFragment.newInstance(mUserId);
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
}
