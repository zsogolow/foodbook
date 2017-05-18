package foodbook.thinmint.activities.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.BaseFragment;
import foodbook.thinmint.activities.adapters.NotesRecyclerAdapter;
import foodbook.thinmint.models.domain.Note;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNoteFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends BaseFragment {
    private static final String ARG_NOTEID = "noteid";

    private long mNoteId;

    private OnNoteFragmentDataListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteid Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    public static NoteFragment newInstance(long noteid) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTEID, noteid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = getArguments().getLong(ARG_NOTEID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_note, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_note_swipe_refresh_layout);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUser();
            }
        });

        mListener.onNoteFragmentCreated(inflated);

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshUser();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnNoteFragmentDataListener) {
            mListener = (OnNoteFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnNoteFragmentDataListener");
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

    private void refreshUser() {
        setLoading(true);
        mListener.refreshNote(mNoteId);
    }

    public void onNoteRetrieved(Note note) {
        setLoading(false);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnNoteFragmentDataListener {
        void onNoteFragmentCreated(View view);

        void refreshNote(long noteId);
    }
}
