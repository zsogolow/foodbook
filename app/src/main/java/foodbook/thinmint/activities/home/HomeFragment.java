package foodbook.thinmint.activities.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.BaseFragment;
import foodbook.thinmint.activities.OnNotesInteractionListener;
import foodbook.thinmint.activities.adapters.NotesListAdapter;
import foodbook.thinmint.models.domain.Note;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnHomeFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends BaseFragment implements OnNotesInteractionListener {
    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private OnHomeFragmentDataListener mListener;

    private ListView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesListAdapter mAdapter;

    public HomeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    public static HomeFragment newInstance(String param1) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_home, container, false);

        mListView = (ListView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);
        mAdapter = new NotesListAdapter(getActivity(), new ArrayList<Note>());

        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFeed();
            }
        });

        mListener.onHomeFragmentCreated(inflated);

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshFeed();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentDataListener) {
            mListener = (OnHomeFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnHomeFragmentDataListener");
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

    private void refreshFeed() {
        setLoading(true);
        mListener.refreshFeed();
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
    public interface OnHomeFragmentDataListener {
        void onHomeFragmentCreated(View view);
        void refreshFeed();
    }
}
