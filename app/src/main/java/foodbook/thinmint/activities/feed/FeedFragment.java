package foodbook.thinmint.activities.feed;

import android.content.Context;
import android.os.Bundle;
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
import foodbook.thinmint.activities.adapters.notes.list.NotesListRecyclerAdapter;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.TasksHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFeedFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends TokenFragment implements OnNotesListInteractionListener,
        IOnNotesListClickListener, IApiCallback {
    private static final String TAG = "FeedFragment";

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private OnFeedFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesListRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private AsyncCallback<WebAPIResult> mGetNoteCallback;
    private AsyncCallback<WebAPIResult> mGetFeedCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;
    private AsyncCallback<WebAPIResult> mLikeCallback;
    private AsyncCallback<WebAPIResult> mUnlikeCallback;
    private long mLastNoteId;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static FeedFragment newInstance(String param1) {
        FeedFragment fragment = new FeedFragment();
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

        initToken();
        initUser();

        mGetNoteCallback = new AsyncCallback<>(this);

        mGetFeedCallback = new AsyncCallback<>(this);
        mLoadMoreCallback = new AsyncCallback<>(this);
        mLikeCallback = new AsyncCallback<>(this);
        mUnlikeCallback = new AsyncCallback<>(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_home, container, false);

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
                refreshFeed();
            }
        });

        mListener.onFeedFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                TasksHelper.getNotes(getContext(), mLoadMoreCallback, mToken, page + 1, "");
            }
        };

        mListView.addOnScrollListener(mScrollListener);

        refreshFeed();

        return inflated;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFeedFragmentDataListener) {
            mListener = (OnFeedFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFeedFragmentDataListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
        TextView hiddenUserIdTextView = (TextView) caller.findViewById(R.id.hidden_user_id);
        TextView userNameTextView = (TextView) caller.findViewById(R.id.user_name);
        String userId = hiddenUserIdTextView.getText().toString();
        String username = userNameTextView.getText().toString();
        ActivityHelper.startUserActivity(getActivity(), userId, username);
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    private void refreshFeed() {
        setLoading(true);
        mScrollListener.resetState();
        TasksHelper.getNotes(getContext(), mGetFeedCallback, mToken, 1, "");
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
        refreshFeed();
    }

    @Override
    public void onNoteDeleted(long noteId) {
        mAdapter.remove(noteId);
    }

    @Override
    public void onCommentAdded(long noteId) {
        TasksHelper.getNote(getContext(), mGetNoteCallback, mToken, noteId);
    }

    @Override
    public void onLikeAdded(long noteId) {
        TasksHelper.getNote(getContext(), mGetNoteCallback, mToken, noteId);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetFeedCallback)) {
            List<Note> notes = JsonHelper.getNotes(mGetFeedCallback.getResult().getResult());
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

    public interface OnFeedFragmentDataListener {
        void onFeedFragmentCreated(View view);
    }
}
