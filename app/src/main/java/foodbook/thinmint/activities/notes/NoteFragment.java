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

import java.util.ArrayList;
import java.util.List;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenFragment;
import foodbook.thinmint.activities.adapters.notes.item.IOnNoteClickListener;
import foodbook.thinmint.activities.adapters.notes.item.NoteRecyclerAdapter;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.views.ListItem;
import foodbook.thinmint.models.views.ListItemTypes;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.TasksHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnNoteFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link NoteFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class NoteFragment extends TokenFragment implements IOnNoteClickListener {

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

    private AsyncCallback<WebAPIResult> mGetNoteCallback;
    private AsyncCallback<WebAPIResult> mDeleteNoteCallback;
    private AsyncCallback<WebAPIResult> mAddCommentCallback;
    private AsyncCallback<WebAPIResult> mLikeCallback;
    private AsyncCallback<WebAPIResult> mUnlikeCallback;

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
        mDeleteNoteCallback = new AsyncCallback<>(this);
        mAddCommentCallback = new AsyncCallback<>(this);
        mLikeCallback = new AsyncCallback<>(this);
        mUnlikeCallback = new AsyncCallback<>(this);
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
                refreshFragment();
            }
        });

        mListener.onNoteFragmentCreated(inflated);

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

    @Override
    public void onStart() {
        super.onStart();

        refreshFragment();
    }

    @Override
    public void onStop() {
        super.onStop();

        if (mRunningTask != null) {
            mRunningTask.cancel(true);
        }
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    protected void refreshFragment() {
        setLoading(true);
        mRunningTask = TasksHelper.getNote(getContext(), mGetNoteCallback, mToken, mNoteId);
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
        refreshFragment();
    }

    private void onLikeChanged() {
        refreshFragment();
    }

    private void onCommentFailed() {
        setLoading(false);
    }

    public void deleteNote() {
        setLoading(true);
        TasksHelper.deleteNote(getContext(), mDeleteNoteCallback, mToken, mNoteId);
    }

    @Override
    public void onAddCommentClick(EditText editText) {
        setLoading(true);
        String comment = editText.getText().toString();
        TasksHelper.addComment(getContext(), mAddCommentCallback, mToken, mNoteId, mUserId, comment);
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
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());
        setLoading(true);
        TasksHelper.likeNote(getContext(), mLikeCallback, mToken, noteId, mUserId);
    }


    @Override
    public void onUnlikeButtonClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());
        setLoading(true);
        TasksHelper.unlikeNote(getContext(), mUnlikeCallback, mToken, noteId, mUserId);
    }

    @Override
    public void onClick(View caller) {

    }

    @Override
    public void callback(IAsyncCallback cb) {
        super.callback(cb);

        if (cb.equals(mGetNoteCallback)) {
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            onNoteRetrieved(note);
            mListener.onNoteRetrieved(note);
        } else if (cb.equals(mDeleteNoteCallback)) {
            boolean success = mDeleteNoteCallback.getResult().isSuccess();
            onNoteDeleted(success);
        } else if (cb.equals(mAddCommentCallback)) {
            WebAPIResult result = mAddCommentCallback.getResult();
            if (result.isSuccess()) {
                Comment addedComment = JsonHelper.getComment(mAddCommentCallback.getResult().getResult());
                onCommentAdded(addedComment);
                mListener.onCommentAdded(addedComment);
            } else {
                onCommentFailed();
            }
        } else if (cb.equals(mLikeCallback)) {
            WebAPIResult result = mLikeCallback.getResult();
            if (result.isSuccess()) {
                Like addedLike = JsonHelper.getLike(mLikeCallback.getResult().getResult());
                onLikeChanged();
                mListener.onLikeAdded(addedLike);
            }
        } else if (cb.equals(mUnlikeCallback)) {
            WebAPIResult result = mUnlikeCallback.getResult();
            if (result.isSuccess()) {
                onLikeChanged();
            }
        }
    }

    public interface OnNoteFragmentDataListener {
        void onNoteFragmentCreated(View view);

        void onNoteRetrieved(Note note);

        void onNoteDeleted(long noteId);

        void onCommentAdded(Comment comment);

        void onLikeAdded(Like like);
    }
}
