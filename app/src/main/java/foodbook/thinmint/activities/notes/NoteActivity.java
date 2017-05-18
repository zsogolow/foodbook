package foodbook.thinmint.activities.notes;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;

public class NoteActivity extends TokenActivity implements IActivityCallback,
        NoteFragment.OnNoteFragmentDataListener {

    private CallServiceAsyncTask mGetNoteTask;
    private CallServiceCallback mGetNoteCallback;

    private NoteFragment mNoteFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mGetNoteCallback = new CallServiceCallback(this);

        initToken();
        initUser();

        Bundle bundle = getIntent().getExtras();
        long noteId = bundle.getLong("note_id");

        showNoteFragment(noteId);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public void showNoteFragment(long noteId) {
        setActionBarTitle("Note");

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
    public void refreshNote(long noteId) {

    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetNoteCallback)) {
            mGetNoteTask = null;
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            mNoteFragment.onNoteRetrieved(note);
        }
    }
}
