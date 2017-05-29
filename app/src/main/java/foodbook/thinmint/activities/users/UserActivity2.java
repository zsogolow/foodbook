package foodbook.thinmint.activities.users;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenActivity;
import foodbook.thinmint.activities.common.RequestCodes;

public class UserActivity2 extends TokenActivity implements UserFragment.OnUserFragmentDataListener {

    private UserFragment mUserFragment;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user2);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        String userId = bundle.getString("user_subject");
        String userName = bundle.getString("user_name");

        setActionBarTitle(userName);

        showUserFragment(userId, userName);
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
                        mUserFragment.onCommentAdded(id);
                    } else if (action.equals(RequestCodes.DELETE_NOTE_ACTION)) {
                        mUserFragment.onNoteDeleted(id);
                    } else if (action.equals(RequestCodes.CREATE_NOTE_ACTION)) {
                        mUserFragment.onNoteAdded(id);
                    } else if (action.equals(RequestCodes.LIKE_NOTE_ACTION)) {
                        mUserFragment.onLikeAdded(id);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        mMenu = menu;

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
            ActivityHelper.logout(UserActivity2.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showUserFragment(String userId, String userName) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mUserFragment = UserFragment.newInstance(userId, userName);
        fragmentTransaction.replace(R.id.fragment_container, mUserFragment, "User");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onUserFragmentCreated(View view) {

    }
}
