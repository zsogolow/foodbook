package foodbook.thinmint.activities.notes;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URLEncoder;
import java.util.Locale;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNoteFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends TokenFragment implements IApiCallback {
    private static final String ARG_NOTEID = "noteid";

    private long mNoteId;

    private OnNoteFragmentDataListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mNoteContents;
    private TextView mNoteUser;
    private TextView mNoteDate;

    private CallServiceAsyncTask mGetNoteTask;
    private CallServiceCallback mGetNoteCallback;


    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteid Parameter 1.
     * @return A new instance of fragment FeedFragment.
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

        initToken();
        initUser();

        mGetNoteCallback = new CallServiceCallback(this);

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_note, container, false);

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_note_swipe_refresh_layout);
        mNoteContents = (TextView) inflated.findViewById(R.id.note_contents);
        mNoteUser = (TextView) inflated.findViewById(R.id.note_user);
        mNoteDate = (TextView) inflated.findViewById(R.id.note_date);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshNote();
            }
        });

        mListener.onNoteFragmentCreated(inflated);

        refreshNote();

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

    private void onNoteRetrieved(Note note) {
        setLoading(false);
        mNoteContents.setText(note.getContent());
        mNoteUser.setText(note.getUser().getUsername());
        mNoteDate.setText(note.getDateCreated().toString());
    }

    private void refreshNote() {
        setLoading(true);
        mGetNoteTask = new CallServiceAsyncTask(getContext(), mGetNoteCallback, mToken);

        String path = String.format(Locale.US,"api/notes/%d", mNoteId);

        Query query = Query.builder()
                .setPath(path)
                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .build();

        mGetNoteTask.execute(query);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetNoteCallback)) {
            mGetNoteTask = null;
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            onNoteRetrieved(note);
            mListener.onNoteRetrieved(note);
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
    public interface OnNoteFragmentDataListener {
        void onNoteFragmentCreated(View view);
        void onNoteRetrieved(Note note);
    }
}
