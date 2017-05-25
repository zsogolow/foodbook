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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.adapters.CommentsRecyclerAdapter;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.GetAsyncTask;
import foodbook.thinmint.tasks.DeleteAsyncTask;
import foodbook.thinmint.tasks.PostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNoteFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends TokenFragment implements IApiCallback,
        CommentsRecyclerAdapter.ViewHolder.IOnCommentClickListener {

    private static final String ARG_NOTEID = "noteid";

    private long mNoteId;

    private OnNoteFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommentsRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private Button mAddCommentButton;
    private EditText mCommentText;

    private TextView mNoteContents;
    private TextView mNoteUser;
    private TextView mNoteDate;
    private TextView mCommentsText;
    private TextView mHiddenUserSubject;

    private GetAsyncTask mGetNoteTask;
    private AsyncCallback<WebAPIResult> mGetNoteCallback;

    private DeleteAsyncTask mDeleteServiceAsyncTask;
    private AsyncCallback<WebAPIResult> mDeleteServiceCallback;

    private PostAsyncTask mAddCommentTask;
    private AsyncCallback<WebAPIResult> mAddCommentCallback;

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

        mGetNoteCallback = new AsyncCallback<WebAPIResult>(this);
        mDeleteServiceCallback = new AsyncCallback<WebAPIResult>(this);
        mAddCommentCallback = new AsyncCallback<WebAPIResult>(this);
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

        mListView = (RecyclerView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_note_swipe_refresh_layout);

        mAddCommentButton = (Button) inflated.findViewById(R.id.add_comment_button);
        mCommentText = (EditText) inflated.findViewById(R.id.comment_edit_text);
        mAddCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
                Map<String, Object> map = new HashMap<>();
                map.put("noteid", mNoteId);
                map.put("userid", mUserId);
                map.put("text", mCommentText.getText().toString());
                map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
                mAddCommentTask = new PostAsyncTask(getContext(), mAddCommentCallback, mToken, map);
                mAddCommentTask.execute("api/comments");
                ActivityHelper.hideSoftKeyboard(getActivity());
            }
        });

        mNoteContents = (TextView) inflated.findViewById(R.id.note_contents);
        mNoteUser = (TextView) inflated.findViewById(R.id.note_user);
        mNoteDate = (TextView) inflated.findViewById(R.id.note_date);
        mCommentsText = (TextView) inflated.findViewById(R.id.note_comments);
        mHiddenUserSubject = (TextView) inflated.findViewById(R.id.hidden_user_id);

        mNoteUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String subject = mHiddenUserSubject.getText().toString();
                String username = mNoteUser.getText().toString();
                ActivityHelper.startUserActivity(getActivity(), subject, username);
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentsRecyclerAdapter(new ArrayList<Comment>(), this);
        mListView.setAdapter(mAdapter);

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

    private void refreshNote() {
        setLoading(true);
        mGetNoteTask = new GetAsyncTask(getContext(), mGetNoteCallback, mToken);

        String path = String.format(Locale.US, "api/notes/%d", mNoteId);

        Query query = Query.builder()
                .setPath(path)
//                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .build();

        mGetNoteTask.execute(query);
    }

    private void onNoteRetrieved(Note note) {
        setLoading(false);
        mNoteContents.setText(note.getContent());
        mNoteUser.setText(note.getUser().getUsername());

        long nowInMillis = System.currentTimeMillis();
        Date dateCreated = note.getDateCreated();

        Calendar now = Calendar.getInstance();
        Calendar created = Calendar.getInstance();
        now.setTime(new Date(nowInMillis));
        created.setTime(dateCreated);

        boolean sameDay = now.get(Calendar.YEAR) == created.get(Calendar.YEAR) &&
                now.get(Calendar.DAY_OF_YEAR) == created.get(Calendar.DAY_OF_YEAR);
        boolean sameYear = now.get(Calendar.YEAR) == created.get(Calendar.YEAR);

        DateFormat dateFormat = sameYear ? MainActivity.DATE_FORMAT : MainActivity.DATE_FORMAT_YEAR;
        String dateString = sameDay ? MainActivity.TIME_FORMAT.format(dateCreated)
                : dateFormat.format(dateCreated) + " at " + MainActivity.TIME_FORMAT.format(dateCreated);

        mNoteDate.setText(dateString);
        mCommentsText.setText(note.getComments().size() + " comments");
        mHiddenUserSubject.setText(note.getUser().getSubject());

        mAdapter.swap(note.getComments());
    }

    private void onNoteDeleted(boolean success) {
        setLoading(false);
        if (success) {
            mListener.onNoteDeleted(mNoteId);
        }
    }

    private void onCommentAdded(Comment comment) {
        mAdapter.add(0, comment);
        mCommentsText.setText(mAdapter.getItemCount() + " comments");
        setLoading(false);
        mCommentText.setText("");
    }

    @Override
    public void onUserClicked(View caller) {
        TextView hiddenUserIdTextView = (TextView) caller.findViewById(R.id.hidden_user_id);
        TextView userNameTextView = (TextView) caller.findViewById(R.id.user_name);
        String userId = hiddenUserIdTextView.getText().toString();
        String username = userNameTextView.getText().toString();
        ActivityHelper.startUserActivity(getActivity(), userId, username);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetNoteCallback)) {
            mGetNoteTask = null;
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            onNoteRetrieved(note);
            mListener.onNoteRetrieved(note);
        } else if (cb.equals(mDeleteServiceCallback)) {
            mDeleteServiceAsyncTask = null;
            boolean success = mDeleteServiceCallback.getResult().isSuccess();
            onNoteDeleted(success);
        } else if (cb.equals(mAddCommentCallback)) {
            mAddCommentTask = null;
            WebAPIResult result = mAddCommentCallback.getResult();
            if (result.isSuccess()) {
                Comment addedComment = JsonHelper.getComment(mAddCommentCallback.getResult().getResult());
                onCommentAdded(addedComment);
            }
        }
    }

    public void deleteNote() {
        setLoading(true);
        mDeleteServiceAsyncTask = new DeleteAsyncTask(getContext(), mDeleteServiceCallback, mToken);
        Query query = Query.builder()
                .setPath("api/notes/" + mNoteId)
                .build();
        mDeleteServiceAsyncTask.execute(query);
    }

    public interface OnNoteFragmentDataListener {
        void onNoteFragmentCreated(View view);

        void onNoteRetrieved(Note note);

        void onNoteDeleted(long noteId);

        void onCommentAdded(Comment comment);
    }
}
