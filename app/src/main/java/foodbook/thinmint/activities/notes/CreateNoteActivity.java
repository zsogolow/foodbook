package foodbook.thinmint.activities.notes;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenActivity;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceCallback;
import foodbook.thinmint.tasks.PostServiceAsyncTask;

public class CreateNoteActivity extends TokenActivity implements IApiCallback {


    private PostServiceAsyncTask mAddNoteTask;
    private CallServiceCallback mAddNoteCallback;

    private EditText mNoteContents;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        initToken();
        initUser();

        mAddNoteCallback = new CallServiceCallback(this);

        mNoteContents = (EditText) findViewById(R.id.edit_note_contents);

        setActionBarTitle("");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.create, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_save) {
            addNote();
            finish();
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
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Are you sure?")
                .setMessage("Are you sure you want to exit without saving?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CreateNoteActivity.super.onBackPressed();
                    }

                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addNote() {
        Note note = new Note();
        note.setContent(mNoteContents.getText().toString());
        note.setDateCreated(new Date(System.currentTimeMillis()));
        note.setUserId(mUserId);
        saveNote(note);
    }

    private void saveNote(Note note) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("content", note.getContent());
        map.put("userid", note.getUserId());
        map.put("datecreated", dateFormat.format(note.getDateCreated()));
        mAddNoteTask = new PostServiceAsyncTask(this, mAddNoteCallback, mToken, map);
        mAddNoteTask.execute("api/notes");
    }


    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mAddNoteCallback)) {
            mAddNoteTask = null;
            WebAPIResult result = mAddNoteCallback.getResult();
            if (result.isSuccess()) {
                Note created = JsonHelper.getNote(result.getResult());
                finish();
            }
        }
    }
}
