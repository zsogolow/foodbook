package foodbook.thinmint.activities.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by Zachery.Sogolow on 5/18/2017.
 */

public class NotesRecyclerAdapter extends RecyclerView.Adapter<NotesRecyclerAdapter.ViewHolder> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("MMM d", Locale.US);
    private static final DateFormat DATE_FORMAT_YEAR = new SimpleDateFormat("MMM d yyyy", Locale.US);
    private static final DateFormat TIME_FORMAT = new SimpleDateFormat("h:mm a", Locale.US);

    private List<Note> mNotes;
    private ViewHolder.IOnNoteClickListener mListener;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        // each data item is just a string in this case
        private LinearLayout mLinearLayout;
        private Button mLikeButton;
        private Button mCommentButton;
        private TextView mUserName;
        private TextView mNoteComments;
        private ViewHolder.IOnNoteClickListener mListener;

        public ViewHolder(LinearLayout v, IOnNoteClickListener listener) {
            super(v);
            mLinearLayout = v;
            mLikeButton = (Button) mLinearLayout.findViewById(R.id.like_button);
            mCommentButton = (Button) mLinearLayout.findViewById(R.id.comment_button);
            mUserName = (TextView) mLinearLayout.findViewById(R.id.user_name);
            mNoteComments = (TextView) mLinearLayout.findViewById(R.id.note_comments);
            mListener = listener;
            mLikeButton.setOnClickListener(this);
            mCommentButton.setOnClickListener(this);
            mUserName.setOnClickListener(this);
            mNoteComments.setOnClickListener(this);
            mLinearLayout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            if (v.equals(mLikeButton)) {
                mListener.onLikeNoteClicked(mLinearLayout);
            } else if (v.equals(mCommentButton)) {
                mListener.onCommentClicked(mLinearLayout);
            } else if (v.equals(mUserName)) {
                mListener.onUserClicked(mLinearLayout);
            } else { // if (v.equals(mNoteComments)) {
                mListener.onNoteClicked(mLinearLayout);
            }
        }

        public interface IOnNoteClickListener {
            void onNoteClicked(View caller);

            void onLikeNoteClicked(View caller);

            void onCommentClicked(View caller);

            void onUserClicked(View caller);
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
        boolean sameYear = now.get(Calendar.YEAR) == created.get(Calendar.YEAR);

        DateFormat dateFormat = sameYear ? DATE_FORMAT : DATE_FORMAT_YEAR;
        String dateString = sameDay ? TIME_FORMAT.format(dateCreated)
                : dateFormat.format(dateCreated) + " at " + TIME_FORMAT.format(dateCreated);

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
        ((TextView) holder.mLinearLayout.findViewById(R.id.note_comments))
                .setText(mNotes.get(position).getComments().size() + " comments");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mNotes.size();
    }

    public void swap(List<Note> notes) {
        mNotes = notes;
        notifyDataSetChanged();
    }

    public void append(List<Note> notes) {
        mNotes.addAll(notes);
        notifyDataSetChanged();
    }

    public void add(Note note) {
        mNotes.add(0, note);
        notifyDataSetChanged();
    }

    public void remove(long noteId) {
        int indexToRemove = -1;
        for (int i = 0; i < mNotes.size(); i++) {
            if (mNotes.get(i).getId() == noteId) {
                indexToRemove = i;
                break;
            }
        }

        if (indexToRemove >= 0) {
            mNotes.remove(indexToRemove);
            notifyDataSetChanged();
        }
    }
}

