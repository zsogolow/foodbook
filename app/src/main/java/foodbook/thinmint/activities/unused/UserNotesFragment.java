package foodbook.thinmint.activities.unused;

import android.content.Context;
import android.content.Intent;
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

import java.util.ArrayList;
import java.util.List;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenFragment;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.activities.adapters.notes.list.IOnNotesListClickListener;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.adapters.notes.list.NotesListRecyclerAdapter;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.TasksHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserNotesFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UserNotesFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserNotesFragment extends TokenFragment implements OnNotesListInteractionListener,
        IOnNotesListClickListener {
    private static final String ARG_USERID = "userid";

    private String mCurrentUserSubject;

    private OnUserNotesFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesListRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private AsyncCallback<WebAPIResult> mGetNoteCallback;
    private AsyncCallback<WebAPIResult> mGetMyStuffCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;
    private AsyncCallback<WebAPIResult> mLikeCallback;
    private AsyncCallback<WebAPIResult> mUnlikeCallback;
    private long mLastNoteId;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public UserNotesFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment FeedFragment.
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
            mCurrentUserSubject = getArguments().getString(ARG_USERID);
        }

        initUser();
        initToken();

        mGetNoteCallback = new AsyncCallback<>(this);

        mGetMyStuffCallback = new AsyncCallback<>(this);
        mLoadMoreCallback = new AsyncCallback<>(this);
        mLikeCallback = new AsyncCallback<>(this);
        mUnlikeCallback = new AsyncCallback<>(this);
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

        mAdapter = new NotesListRecyclerAdapter(new ArrayList<Note>(), this, getActivity());
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
            }
        });

        mListener.onUserNotesFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                mRunningTask = TasksHelper.getNotes(getContext(), mLoadMoreCallback, mToken, mCurrentUserSubject, page + 1, "");
            }
        };

        mListView.addOnScrollListener(mScrollListener);
        refreshFragment();

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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

    @Override
    public void onLikeNoteClick(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());
        TasksHelper.likeNote(getContext(), mLikeCallback, mToken, noteId, mUserId);
        setLoading(true);
    }

    @Override
    public void onUnlikeButtonClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());
        mLastNoteId = noteId;
        setLoading(true);
        TasksHelper.unlikeNote(getContext(), mUnlikeCallback, mToken, noteId, mUserId);
    }

    @Override
    public void onNoteClick(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), false);
    }

    @Override
    public void onCommentClick(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), true);
    }

    @Override
    public void onUserClick(View caller) {
//        TextView hiddenUserIdTextView = (TextView) caller.findViewById(R.id.hidden_user_id);
//        TextView userNameTextView = (TextView) caller.findViewById(R.id.user_name);
//        String userId = hiddenUserIdTextView.getText().toString();
//        String username = userNameTextView.getText().toString();
//        ActivityHelper.startUserActivity(getActivity(), userId, username);
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    protected void refreshFragment() {
        setLoading(true);
        mScrollListener.resetState();
        mRunningTask = TasksHelper.getNotes(getContext(), mGetMyStuffCallback, mToken, mCurrentUserSubject, 1, "");
    }

    private void onNotesRetrieved(List<Note> notes) {
        mAdapter.swap(notes);
        setLoading(false);
    }

    private void onNoteRetrieved(Note note) {
        mAdapter.replace(note);
        setLoading(false);
    }

    private void onLoadedMore(List<Note> notes) {
        mAdapter.addAll(notes);
    }

    @Override
    public void onNoteAdded(long noteId) {
        refreshFragment();
    }

    @Override
    public void onNoteDeleted(long noteId) {
        mAdapter.remove(noteId);
    }

    @Override
    public void onCommentAdded(long noteId) {
        mRunningTask = TasksHelper.getNote(getContext(), mGetNoteCallback, mToken, noteId);
    }

    @Override
    public void onLikeAdded(long noteId) {
        mRunningTask = TasksHelper.getNote(getContext(), mGetNoteCallback, mToken, noteId);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        super.callback(cb);

        if (cb.equals(mGetMyStuffCallback)) {
            List<Note> notes = JsonHelper.getNotes(mGetMyStuffCallback.getResult().getResult());
            onNotesRetrieved(notes);
        } else if (cb.equals(mLoadMoreCallback)) {
            List<Note> notes = JsonHelper.getNotes(mLoadMoreCallback.getResult().getResult());
            onLoadedMore(notes);
        } else if (cb.equals(mGetNoteCallback)) {
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            onNoteRetrieved(note);
        } else if (cb.equals(mLikeCallback)) {
            WebAPIResult result = mLikeCallback.getResult();
            if (result.isSuccess()) {
                Like addedLike = JsonHelper.getLike(mLikeCallback.getResult().getResult());
                onLikeAdded(addedLike.getNoteId());
            }
        } else if (cb.equals(mUnlikeCallback)) {
            WebAPIResult result = mUnlikeCallback.getResult();
            if (result.isSuccess()) {
                onLikeAdded(mLastNoteId);
            }
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
    }
}
