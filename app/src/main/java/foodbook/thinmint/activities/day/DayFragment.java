package foodbook.thinmint.activities.day;

import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.BaseFragment;
import foodbook.thinmint.activities.OnNotesInteractionListener;
import foodbook.thinmint.activities.adapters.NotesListAdapter;
import foodbook.thinmint.models.domain.Note;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayFragment extends BaseFragment implements OnNotesInteractionListener {

    private OnDayFragmentDataListener mListener;

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesListAdapter mAdapter;

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

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    public void setDate(Date date) {
        mCurrentDate = date;
    }

    @Override
    public void onNotesRetrieved(List<Note> notes) {
        mAdapter.clear();
        mAdapter.addAll(notes);
        mAdapter.notifyDataSetChanged();
        setLoading(false);
    }


    @Override
    public void onNoteAdded(Note note) {
        mAdapter.add(note);
        mAdapter.notifyDataSetChanged();
        setLoading(false);
    }

    public interface OnDayFragmentDataListener {
        void onDayFragmentCreated(View view);

        void addNote(Note note);

        void selectDay(Date date);
    }
}

