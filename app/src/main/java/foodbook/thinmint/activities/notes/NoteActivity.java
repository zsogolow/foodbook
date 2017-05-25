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
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.activities.common.RequestCodes;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.Note;

public class NoteActivity extends TokenActivity implements NoteFragment.OnNoteFragmentDataListener {

    private NoteFragment mNoteFragment;
    private Menu mMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        long noteId = bundle.getLong("note_id");

        setActionBarTitle("Note");

        showNoteFragment(noteId);
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
        inflater.inflate(R.menu.note, menu);

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
        if (id == R.id.action_delete) {
            attemptDelete();
            return true;
        }
//        else if (id == R.id.action_edit) {
//            return true;
//        }
        else if (id == R.id.action_save) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attemptDelete() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Delete Note")
                .setMessage("Are you sure you want to delete this note?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mNoteFragment.deleteNote();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void toggleNoteActions(boolean show) {
        if (mMenu != null) {
            MenuItem delete = mMenu.findItem(R.id.action_delete);
//            MenuItem edit = mMenu.findItem(R.id.action_edit);
            delete.setVisible(show);
//            edit.setVisible(show);
        }
    }

    public void showNoteFragment(long noteId) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        mNoteFragment = NoteFragment.newInstance(noteId);
        fragmentTransaction.replace(R.id.fragment_container, mNoteFragment, "Note");

        // Commit the transaction
        fragmentTransaction.commit();
    }

    @Override
    public void onNoteFragmentCreated(View view) {
    }

    @Override
    public void onNoteRetrieved(Note note) {
        if (note.getUserId() != mUserId) {
            toggleNoteActions(false);
        } else {
            toggleNoteActions(true);
        }
    }

    @Override
    public void onNoteDeleted(long noteId) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RequestCodes.DELETE_NOTE_EXTRA_ID, noteId);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    @Override
    public void onCommentAdded(Comment comment) {
        Intent resultIntent = new Intent();
        resultIntent.putExtra(RequestCodes.ADD_COMMENT_EXTRA_ID, comment.getId());
        resultIntent.putExtra(RequestCodes.ADD_COMMENT_EXTRA_NOTE_ID, comment.getNoteId());
        setResult(Activity.RESULT_OK, resultIntent);
    }
}
