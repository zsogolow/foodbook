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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.ActivityHelper;
import foodbook.thinmint.activities.common.TokenFragment;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.activities.adapters.users.item.IOnUserClickListener;
import foodbook.thinmint.activities.adapters.users.item.UserRecyclerAdapter;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.EntityBase;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.models.views.ListItem;
import foodbook.thinmint.models.views.ListItemTypes;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.TasksHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UserFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserFragment extends TokenFragment implements OnNotesListInteractionListener,
        IOnUserClickListener, IApiCallback {
    private static final String ARG_USERID = "userid";
    private static final String ARG_USERNAME = "username";

    private String mCurrentUserId;
    private String mCurrentUserName;

    private OnUserFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UserRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private AsyncCallback<WebAPIResult> mGetNoteCallback;
    private AsyncCallback<WebAPIResult> mGetMyNotesCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;
    private AsyncCallback<WebAPIResult> mLikeCallback;
    private AsyncCallback<WebAPIResult> mUnlikeCallback;
    private long mLastNoteId;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public UserFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userId Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static UserFragment newInstance(String userId, String userName) {
        UserFragment fragment = new UserFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userId);
        args.putString(ARG_USERNAME, userName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCurrentUserId = getArguments().getString(ARG_USERID);
            mCurrentUserName = getArguments().getString(ARG_USERNAME);
        }

        initUser();
        initToken();

        mGetNoteCallback = new AsyncCallback<>(this);

        mGetMyNotesCallback = new AsyncCallback<>(this);
        mLoadMoreCallback = new AsyncCallback<>(this);
        mLikeCallback = new AsyncCallback<>(this);
        mUnlikeCallback = new AsyncCallback<>(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_user, container, false);

        mListView = (RecyclerView) inflated.findViewById(R.id.activity_user_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_user_swipe_refresh_layout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        List<ListItem<EntityBase>> models = new ArrayList<>();
        mAdapter = new UserRecyclerAdapter(models, this, getActivity());
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshMyNotes();
            }
        });

        mListener.onUserFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                TasksHelper.getNotes(getContext(), mLoadMoreCallback, mToken, mCurrentUserId, page + 1, "");
            }
        };

        mListView.addOnScrollListener(mScrollListener);
        refreshMyNotes();

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserFragmentDataListener) {
            mListener = (OnUserFragmentDataListener) context;
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

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    @Override
    public void onClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), false);
    }

    @Override
    public void onCommentButtonClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), true);
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
        mLastNoteId = noteId;
        setLoading(true);
        TasksHelper.unlikeNote(getContext(), mUnlikeCallback, mToken, noteId, mUserId);
    }

    private void refreshMyNotes() {
        setLoading(true);
        mScrollListener.resetState();
        TasksHelper.getNotes(getContext(), mGetMyNotesCallback, mToken, mCurrentUserId, 1, "");
    }

    private void onNotesRetrieved(List<Note> notes) {
        List<ListItem<EntityBase>> models = new ArrayList<>();
        User user = new User();
        user.setUsername(mCurrentUserName);
        user.setSubject(mCurrentUserId);
        user.setDateCreated(new Date());
        models.add(new ListItem<EntityBase>(ListItemTypes.User, user));
        for (Note note : notes) {
            models.add(new ListItem<EntityBase>(ListItemTypes.Note, note));
        }

        mAdapter.swap(models);
        setLoading(false);
    }

    private void onNoteRetrieved(Note note) {
        mAdapter.replace(new ListItem<EntityBase>(ListItemTypes.Note, note));
        setLoading(false);
    }

    private void onLoadedMore(List<Note> notes) {
        List<ListItem<EntityBase>> models = new ArrayList<>();
        for (Note note : notes) {
            models.add(new ListItem<EntityBase>(ListItemTypes.Note, note));
        }
        mAdapter.addAll(models);
    }

    @Override
    public void onNoteAdded(long noteId) {
        refreshMyNotes();
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
        if (cb.equals(mGetMyNotesCallback)) {
            List<Note> notes = JsonHelper.getNotes(mGetMyNotesCallback.getResult().getResult());
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
    public interface OnUserFragmentDataListener {
        void onUserFragmentCreated(View view);
    }
}
