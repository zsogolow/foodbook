package foodbook.thinmint.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.models.domain.Note;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayActivityFragment extends Fragment {

    private DayFragmentDataListener mDayCallback;

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesAdapter mAdapter;

    private Date mCurrentDate;

    public DayActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentDate = new Date(System.currentTimeMillis());
        mDayCallback.selectDay(mCurrentDate);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_day, container, false);

        FloatingActionButton fab = (FloatingActionButton) inflated.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Note note = new Note();
                note.setContent("this is a note from the fragment");
                mDayCallback.addNote(note);
            }
        });

        mListView = (ListView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);
        mAdapter = new NotesAdapter(getActivity(), new ArrayList<Note>());

        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mDayCallback.selectDay(mCurrentDate);
            }
        });

        return inflated;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mDayCallback = (DayFragmentDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    public void setLoading(boolean isLoading){
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    public void onDataRetrieved(Date date, List<Note> notes) {
        mCurrentDate = date;
        mAdapter.clear();
        mAdapter.addAll(notes);
        mAdapter.notifyDataSetChanged();
        setLoading(false);
    }

    public void onNoteAdded(Note newNote) {
        mAdapter.add(newNote);
        mAdapter.notifyDataSetChanged();
        setLoading(false);
    }

    public interface DayFragmentDataListener {
        void addNote(Note note);

        void selectDay(Date date);
    }

    public class NotesAdapter extends ArrayAdapter<Note> {
        public NotesAdapter(Context context, ArrayList<Note> users) {
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
}

