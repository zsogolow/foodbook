package foodbook.thinmint.activities.adapters.notes;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.common.AbstractRecyclerAdapter;
import foodbook.thinmint.activities.adapters.common.AbstractViewHolder;
import foodbook.thinmint.models.domain.Note;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class NotesRecyclerAdapter extends AbstractRecyclerAdapter<Note, IOnNoteClickListener> {

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotesRecyclerAdapter(List<Note> notes, IOnNoteClickListener listener) {
        super(notes, listener);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        // set the view's size, margins, paddings and layout parameters
        NotesViewHolder vh = new NotesViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(AbstractViewHolder<IOnNoteClickListener> holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        long nowInMillis = System.currentTimeMillis();
        Date dateCreated = mItems.get(position).getDateCreated();

        Calendar now = Calendar.getInstance();
        Calendar created = Calendar.getInstance();
        now.setTime(new Date(nowInMillis));
        created.setTime(dateCreated);

        boolean sameDay = now.get(Calendar.YEAR) == created.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == created.get(Calendar.DAY_OF_YEAR);
        boolean sameYear = now.get(Calendar.YEAR) == created.get(Calendar.YEAR);

        DateFormat dateFormat = sameYear ? DATE_FORMAT : DATE_FORMAT_YEAR;
        String dateString = sameDay ? TIME_FORMAT.format(dateCreated)
                : dateFormat.format(dateCreated) + " at " + TIME_FORMAT.format(dateCreated);

        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mItems.get(position).getUser().getSubject());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_note_id))
                .setText(mItems.get(position).getId() + "");
        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mItems.get(position).getUser().getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_date))
                .setText(dateString);
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_contents))
                .setText(mItems.get(position).getContent());
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_comments))
                .setText(mItems.get(position).getComments().size() + " comments");
    }
}

