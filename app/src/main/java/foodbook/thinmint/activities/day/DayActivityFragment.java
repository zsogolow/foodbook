package foodbook.thinmint.activities.day;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

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
    private NotesListAdapter mAdapter;

    private Date mCurrentDate;

    public DayActivityFragment() {
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCurrentDate = new Date(System.currentTimeMillis());
        refreshList();
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
                setLoading(true);
                mDayCallback.addNote(note);
            }
        });

        mListView = (ListView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);
        mAdapter = new NotesListAdapter(getActivity(), new ArrayList<Note>());

        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        return inflated;
    }

    private void refreshList() {
        setLoading(true);
        mDayCallback.selectDay(mCurrentDate);
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

    private void setLoading(boolean isLoading) {
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

    interface DayFragmentDataListener {
        void addNote(Note note);

        void selectDay(Date date);
    }
}

