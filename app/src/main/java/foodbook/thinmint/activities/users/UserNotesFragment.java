package foodbook.thinmint.activities.users;

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
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.adapters.NotesRecyclerAdapter;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserNotesFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UserNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserNotesFragment extends TokenFragment implements OnNotesListInteractionListener,
        NotesRecyclerAdapter.ViewHolder.IOnNoteClickListener, IApiCallback {
    private static final String ARG_USERID = "userid";

    private String mUserId;

    private OnUserNotesFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    private CallServiceAsyncTask mGetMyStuffTask;
    private CallServiceCallback mGetMyStuffCallback;

    public UserNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment HomeFragment.
     */
    public static UserNotesFragment newInstance(String userid) {
        UserNotesFragment fragment = new UserNotesFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USERID);
        }

        initUser();
        initToken();

        mGetMyStuffCallback = new CallServiceCallback(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_user_notes, container, false);

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
                refreshMyStuff();
            }
        });

        mListener.onUserNotesFragmentCreated(inflated);

        return inflated;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshMyStuff();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserNotesFragmentDataListener) {
            mListener = (OnUserNotesFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserNotesFragmentDataListener");
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

    public void refreshMyStuff() {
        setLoading(true);
        mGetMyStuffTask = new CallServiceAsyncTask(getContext(), mGetMyStuffCallback, mToken);

        String path = String.format(Locale.US, "api/users/%s/notes?sort=", mUserId);
        String rawQuery = "-datecreated";

        String encodedQuery = "";
        try {
            encodedQuery = URLEncoder.encode(rawQuery, "UTF-8");
        } catch (Exception e) {
        }

        path += encodedQuery;
        mGetMyStuffTask.execute(path);
    }

    public void onNotesRetrieved(List<Note> notes) {
        mAdapter.swap(notes);
        setLoading(false);
    }

    @Override
    public void onNoteAdded(Note note) {
        mAdapter.add(note);
        setLoading(false);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetMyStuffCallback)) {
            mGetMyStuffTask = null;
            List<Note> notes = JsonHelper.getNotes(mGetMyStuffCallback.getResult().getResult());
            onNotesRetrieved(notes);
        }
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
    public interface OnUserNotesFragmentDataListener {
        void onUserNotesFragmentCreated(View view);

        void showNote(long noteId);
    }
}
