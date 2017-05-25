package foodbook.thinmint.activities.feed;

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
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.activities.adapters.NotesRecyclerAdapter;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.common.RequestCodes;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.GetAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnFeedFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends TokenFragment implements IApiCallback, OnNotesListInteractionListener,
        NotesRecyclerAdapter.ViewHolder.IOnNoteClickListener {
    private static final String TAG = "FeedFragment";

    private static final String ARG_PARAM1 = "param1";

    private String mParam1;

    private OnFeedFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private GetAsyncTask mGetFeedTask;
    private AsyncCallback<WebAPIResult> mGetFeedCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;

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

        mGetFeedCallback = new AsyncCallback<WebAPIResult>(this);
        mLoadMoreCallback = new AsyncCallback<WebAPIResult>(this);

        //mListener.showProgress(true);
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

        mAdapter = new NotesRecyclerAdapter(new ArrayList<Note>(), this);
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
                Query query = Query.builder()
                        .setPath("api/notes")
//                        .setAccessToken(mToken.getAccessToken())
                        .setSort("-datecreated")
                        .setPage(page + 1)
                        .build();
                mGetFeedTask = new GetAsyncTask(getContext(), mLoadMoreCallback, mToken);
                mGetFeedTask.execute(query);
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
    public void onLikeNoteClicked(View caller) {
//        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
//        String noteId = hiddenNoteIdTextView.getText().toString();
//        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId));
    }

    @Override
    public void onNoteClicked(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), RequestCodes.DELETE_NOTE_REQUEST_CODE);
    }

    @Override
    public void onCommentClicked(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        ActivityHelper.startNoteActivityForResult(getActivity(), Long.parseLong(noteId), RequestCodes.DELETE_NOTE_REQUEST_CODE);
    }

    @Override
    public void onUserClicked(View caller) {
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

        mGetFeedTask = new GetAsyncTask(getContext(), mGetFeedCallback, mToken);

        String path = "api/notes";

        Query query = Query.builder()
                .setPath(path)
//                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .build();

        mGetFeedTask.execute(query);
    }

    private void onNotesRetrieved(List<Note> notes) {
        mAdapter.swap(notes);
        setLoading(false);
    }

    private void onLoadedMore(List<Note> notes) {
        mAdapter.addAll(notes);
    }

    @Override
    public void onNoteAdded(Note note) {
        mAdapter.add(0, note);
        setLoading(false);
    }

    @Override
    public void onNoteAdded(long noteid) {
        refreshFeed();
    }

    @Override
    public void onNoteDeleted(long noteId) {
        mAdapter.remove(noteId);
    }

    @Override
    public void onCommentAdded(long noteId, long commentId) {

    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetFeedCallback)) {
            mGetFeedTask = null;
            List<Note> notes = JsonHelper.getNotes(mGetFeedCallback.getResult().getResult());
            onNotesRetrieved(notes);
//            mListener.showProgress(false);
        } else if (cb.equals(mLoadMoreCallback)) {
            mGetFeedTask = null;
            List<Note> notes = JsonHelper.getNotes(mLoadMoreCallback.getResult().getResult());
            onLoadedMore(notes);
        }
    }

    public interface OnFeedFragmentDataListener {
        void onFeedFragmentCreated(View view);
    }
}
