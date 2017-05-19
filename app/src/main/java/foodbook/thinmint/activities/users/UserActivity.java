package foodbook.thinmint.activities.users;

import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.activities.notes.NoteActivity;

public class UserActivity extends TokenActivity implements
        UserInfoFragment.OnUserInfoFragmentDataListener,
        UserNotesFragment.OnUserNotesFragmentDataListener {

    private FragmentPagerAdapter mFragmentPagerAdapter;

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

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        viewPager.setAdapter(mFragmentPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        setActionBarTitle(username);
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

    @Override
    public void showNote(long noteId) {
        startNoteActivity(noteId);
    }

    public static class MyPagerAdapter extends FragmentPagerAdapter {
        private static int NUM_ITEMS = 2;
        private static String[] NAMES = {"Notes", "Info"};
        private String mUserId;

        private UserNotesFragment mUserNotesFragment;
        private UserInfoFragment mUserInfoFragment;

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
