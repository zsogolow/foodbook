package foodbook.thinmint.activities.notes;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityStarter;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.Note;

public class CommentsActivity extends TokenActivity
        implements CommentsFragment.OnCommentsFragmentDataListener{

    public static final int ADD_COMMENT_REQUEST_CODE = 1;
    public static final String ADD_COMMENT_EXTRA_ID = "created_id";

    private CommentsFragment mCommentsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        long noteId = bundle.getLong("note_id");

        setActionBarTitle("Comments");

        showCommentsFragment(noteId);
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
        inflater.inflate(R.menu.comments, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            ActivityStarter.logout(CommentsActivity.this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void showCommentsFragment(long noteId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mCommentsFragment = CommentsFragment.newInstance(noteId);
        fragmentTransaction.replace(R.id.fragment_container, mCommentsFragment, "Note");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onCommentsFragmentCreated(View view) {

    }

    @Override
    public void onCommentAdded(Comment comment) {
    }
}
