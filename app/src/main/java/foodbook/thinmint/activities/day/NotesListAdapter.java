package foodbook.thinmint.activities.day;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import foodbook.thinmint.R;
import foodbook.thinmint.models.domain.Note;

/**
 * Created by ZachS on 5/16/2017.
 */

public class NotesListAdapter extends ArrayAdapter<Note> {
    public NotesListAdapter(Context context, ArrayList<Note> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Note note = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_note, parent, false);
        }
        // Lookup view for data population
        TextView userName = (TextView) convertView.findViewById(R.id.user_name);
        TextView noteDate = (TextView) convertView.findViewById(R.id.note_date);
        TextView noteContents = (TextView) convertView.findViewById(R.id.note_contents);
        // Populate the data into the template view using the data object
        userName.setText(note.getUser().getUsername());
        noteDate.setText(note.getDateCreated().toString());
        noteContents.setText(note.getContent());
        // Return the completed view to render on screen
        return convertView;
    }
}
