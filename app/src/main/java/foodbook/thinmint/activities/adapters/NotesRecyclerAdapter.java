package foodbook.thinmint.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.R;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private List<Note> mNotes;
    private ViewHolder.IOnNoteClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        public LinearLayout mLinearLayout;
        private ViewHolder.IOnNoteClickListener mListener;

        public ViewHolder(LinearLayout v, IOnNoteClickListener listener) {
            super(v);
            mLinearLayout = v;
            mListener = listener;
            mLinearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onNoteClicked(v);
        }

        public interface IOnNoteClickListener {
            void onNoteClicked(View caller);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public NotesRecyclerAdapter(List<Note> notes, ViewHolder.IOnNoteClickListener listener) {
        mNotes = notes;
        mListener = listener;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NotesRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        LinearLayout v = (LinearLayout) LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_note, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v, mListener);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element

        long nowInMillis = System.currentTimeMillis();
        Date dateCreated = mNotes.get(position).getDateCreated();

        Calendar now = Calendar.getInstance();
        Calendar created = Calendar.getInstance();
        now.setTime(new Date(nowInMillis));
        created.setTime(dateCreated);
        boolean sameDay = now.get(Calendar.YEAR) == created.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == created.get(Calendar.DAY_OF_YEAR);

        String dateString = sameDay ? TIME_FORMAT.format(dateCreated) : DATE_FORMAT.format(dateCreated);

        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_user_id))
                .setText(mNotes.get(position).getUser().getSubject());
        ((TextView) holder.mLinearLayout.findViewById(R.id.hidden_note_id))
                .setText(mNotes.get(position).getId() + "");
        ((TextView) holder.mLinearLayout.findViewById(R.id.user_name))
                .setText(mNotes.get(position).getUser().getUsername());
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_date))
                .setText(dateString);
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_contents))
                .setText(mNotes.get(position).getContent());
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void swap(List<Note> notes){
        mNotes = notes;
        notifyDataSetChanged();
    }

    public void append(List<Note> notes){
        mNotes.addAll(notes);
        notifyDataSetChanged();
    }

    public void add(Note note){
        mNotes.add(0, note);
        notifyDataSetChanged();
    }
}

