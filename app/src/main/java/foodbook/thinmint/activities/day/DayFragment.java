package foodbook.thinmint.activities.day;

import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.BaseFragment;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.adapters.NotesRecyclerAdapter;
import foodbook.thinmint.models.domain.Note;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayFragment extends BaseFragment implements OnNotesListInteractionListener,
        NotesRecyclerAdapter.ViewHolder.IOnNoteClickListener {

    private OnDayFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private Date mCurrentDate;

    public DayFragment() {
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

        mListView = (RecyclerView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new NotesRecyclerAdapter(new ArrayList<Note>(), this);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        mListener.onDayFragmentCreated(inflated);

        return inflated;
    }

    private void refreshList() {
        setLoading(true);
        mListener.selectDay(mCurrentDate);
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnDayFragmentDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onNoteClicked(View caller) {
        TextView hiddenNoteIdTextView = (TextView)caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        mListener.showNote(Long.parseLong(noteId));
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    public void setDate(Date date) {
        mCurrentDate = date;
    }

    @Override
    public void onNotesRetrieved(List<Note> notes) {
        mAdapter.swap(notes);
        setLoading(false);
    }


    @Override
    public void onNoteAdded(Note note) {
        mAdapter.add(note);
        setLoading(false);
    }

    public interface OnDayFragmentDataListener {
        void onDayFragmentCreated(View view);

        void addNote(Note note);

        void selectDay(Date date);

        void showNote(long noteId);
    }
}

