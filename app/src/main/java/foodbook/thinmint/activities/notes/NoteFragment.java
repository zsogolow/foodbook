package foodbook.thinmint.activities.notes;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenFragment;
import foodbook.thinmint.activities.adapters.notes.item.IOnNoteClickListener;
import foodbook.thinmint.activities.adapters.notes.item.NoteRecyclerAdapter;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.views.ListItem;
import foodbook.thinmint.models.views.ListItemTypes;
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
public class NoteFragment extends TokenFragment implements IApiCallback, IOnNoteClickListener {

    private static final String ARG_NOTEID = "noteid";
    private static final String ARG_COMMENTFLAG = "commentflag";

    private long mNoteId;
    private boolean mCommentFlag;

    private OnNoteFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NoteRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private TextView mHiddenUserSubject;

    private GetAsyncTask mGetNoteTask;
    private AsyncCallback<WebAPIResult> mGetNoteCallback;

    private DeleteAsyncTask mDeleteServiceAsyncTask;
    private AsyncCallback<WebAPIResult> mDeleteServiceCallback;

    private PostAsyncTask mAddCommentTask;
    private AsyncCallback<WebAPIResult> mAddCommentCallback;

    private PostAsyncTask mAddLikeTask;
    private AsyncCallback<WebAPIResult> mAddLikeCallback;

    private DeleteAsyncTask mRemoveUnlikeTask;
    private AsyncCallback<WebAPIResult> mRemoveUnlikeCallback;

    public NoteFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteId Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static NoteFragment newInstance(long noteId, boolean commentFlag) {
        NoteFragment fragment = new NoteFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTEID, noteId);
        args.putBoolean(ARG_COMMENTFLAG, commentFlag);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = getArguments().getLong(ARG_NOTEID);
            mCommentFlag = getArguments().getBoolean(ARG_COMMENTFLAG);
        }

        initToken();
        initUser();

        mGetNoteCallback = new AsyncCallback<>(this);
        mDeleteServiceCallback = new AsyncCallback<>(this);
        mAddCommentCallback = new AsyncCallback<>(this);
        mAddLikeCallback = new AsyncCallback<>(this);
        mRemoveUnlikeCallback = new AsyncCallback<>(this);
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

        mHiddenUserSubject = (TextView) inflated.findViewById(R.id.hidden_user_id);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        List<ListItem<EntityBase>> models = new ArrayList<>();

        models.add(new ListItem<>(ListItemTypes.Note, null));
        models.add(new ListItem<>(ListItemTypes.AddComment, null));

        mAdapter = new NoteRecyclerAdapter(models, this, getActivity());
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
                .setSort("-datecreated")
                .build();

        mGetNoteTask.execute(query);
    }

    private void onNoteRetrieved(Note note) {
        setLoading(false);

        mHiddenUserSubject.setText(note.getUser().getSubject());

        List<ListItem<EntityBase>> models = new ArrayList<>();

        models.add(new ListItem<EntityBase>(ListItemTypes.Note, note));
        models.add(new ListItem<EntityBase>(ListItemTypes.AddComment, null));
        for (Comment comment : note.getComments()) {
            models.add(new ListItem<EntityBase>(ListItemTypes.Comment, comment));
        }

        mAdapter.swap(models);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mCommentFlag) {
                    mCommentFlag = false;
                    EditText editText = (EditText) mListView.findViewById(R.id.comment_edit_text);
                    editText.requestFocus();
                    ActivityHelper.showSoftKeyboard(getActivity());
                }
            }
        }, 250);
    }

    private void onNoteDeleted(boolean success) {
        setLoading(false);
        if (success) {
            mListener.onNoteDeleted(mNoteId);
        }
    }

    private void onCommentAdded(Comment comment) {
        mAdapter.add(2, new ListItem<EntityBase>(ListItemTypes.Comment, comment));
        refreshNote();
    }

    private void onLikeAdded(Like like) {
        refreshNote();
    }

    private void onCommentFailed() {
        setLoading(false);
    }

    @Override
    public void onAddCommentClick(EditText editText) {
        setLoading(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("noteid", mNoteId);
        map.put("userid", mUserId);
        map.put("text", editText.getText().toString());
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        mAddCommentTask = new PostAsyncTask(getContext(), mAddCommentCallback, mToken, map);
        mAddCommentTask.execute("api/comments");
        ActivityHelper.hideSoftKeyboard(getActivity());
    }

    @Override
    public void onUserClick(View view) {
        TextView hiddenUserIdTextView = (TextView) view.findViewById(R.id.hidden_user_id);
        TextView userNameTextView = (TextView) view.findViewById(R.id.user_name);
        String userId = hiddenUserIdTextView.getText().toString();
        String username = userNameTextView.getText().toString();
        ActivityHelper.startUserActivity(getActivity(), userId, username);
    }

    @Override
    public void onCommentButtonClick(View view) {
        TextView hiddenUserIdTextView = (TextView) view.findViewById(R.id.hidden_user_id);
        TextView userNameTextView = (TextView) view.findViewById(R.id.user_name);
        String userId = hiddenUserIdTextView.getText().toString();
        String username = userNameTextView.getText().toString();
        EditText editText = (EditText) mListView.findViewById(R.id.comment_edit_text);
        editText.requestFocus();
        ActivityHelper.showSoftKeyboard(getActivity());
    }

    @Override
    public void onLikeButtonClick(View view) {
        setLoading(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("noteid", mNoteId);
        map.put("userid", mUserId);
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        mAddLikeTask = new PostAsyncTask(getContext(), mAddLikeCallback, mToken, map);
        mAddLikeTask.execute("api/likes");
    }


    @Override
    public void onUnlikeButtonClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());

        setLoading(true);
        mRemoveUnlikeTask = new DeleteAsyncTask(getContext(), mRemoveUnlikeCallback, mToken);
        Query query = Query.builder()
                .setPath(String.format(Locale.US, "api/notes/%d/likes/%d", noteId, mUserId))
                .build();
        mRemoveUnlikeTask.execute(query);
    }

    @Override
    public void onClick(View caller) {

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
                mListener.onCommentAdded(addedComment);
            } else {
                onCommentFailed();
            }
        } else if (cb.equals(mAddLikeCallback)) {
            WebAPIResult result = mAddLikeCallback.getResult();
            if (result.isSuccess()) {
                Like addedLike = JsonHelper.getLike(mAddLikeCallback.getResult().getResult());
                onLikeAdded(addedLike);
                mListener.onLikeAdded(addedLike);
            }
        } else if (cb.equals(mRemoveUnlikeCallback)) {
            mRemoveUnlikeTask = null;
            WebAPIResult result = mRemoveUnlikeCallback.getResult();
            if (result.isSuccess()) {
                onLikeAdded(null);
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

        void onLikeAdded(Like like);
    }
}
