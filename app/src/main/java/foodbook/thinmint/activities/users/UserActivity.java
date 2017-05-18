package foodbook.thinmint.activities.users;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.activities.notes.NoteActivity;
import foodbook.thinmint.activities.users.UserFragment;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;

public class UserActivity extends TokenActivity implements IActivityCallback,
        UserFragment.OnUserFragmentDataListener {

    private CallServiceAsyncTask mGetUserTask;
    private CallServiceCallback mGetUserCallback;

    private UserFragment mUserFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGetUserCallback = new CallServiceCallback(this);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        String subject = bundle.getString("user_subject");
        String username = bundle.getString("user_name");

        showUserFragment(subject, username);
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

    public void showUserFragment(String userSubject, String username) {
        setActionBarTitle(username);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mUserFragment = UserFragment.newInstance(userSubject);
        fragmentTransaction.replace(R.id.fragment_container, mUserFragment, "User");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onUserFragmentCreated(View view) {

    }

    @Override
    public void refreshUser(String userSubject) {
        mGetUserTask = new CallServiceAsyncTask(this, mGetUserCallback, mToken);

        String path = String.format(Locale.US, "api/users/%s/notes/?sort=", userSubject);
        String rawQuery = "-datecreated";

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mGetUserTask.execute(path);
    }

    @Override
    public void showNote(long noteId) {
        startNoteActivity(noteId);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetUserCallback)) {
            mGetUserTask = null;
            List<Note> notes = JsonHelper.getNotes(mGetUserCallback.getResult().getResult());
            mUserFragment.onUserRetrieved(notes);
        }
    }
}
