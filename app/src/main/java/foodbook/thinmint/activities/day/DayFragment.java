package foodbook.thinmint.activities.day;

import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityHelper;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.activities.adapters.notes.list.IOnNotesListClickListener;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.adapters.notes.list.NotesListRecyclerAdapter;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Like;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.DeleteAsyncTask;
import foodbook.thinmint.tasks.GetAsyncTask;
import foodbook.thinmint.tasks.PostAsyncTask;

public class DayFragment extends TokenFragment implements OnNotesListInteractionListener,
        IOnNotesListClickListener, IApiCallback {
    private static final String ARG_DATE = "date";

    private Date mCurrentDate;

    private OnDayFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesListRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private GetAsyncTask mGetNoteTask;
    private AsyncCallback<WebAPIResult> mGetNoteCallback;

    private GetAsyncTask mLoadingTask;
    private AsyncCallback<WebAPIResult> mLoadingCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;

    private PostAsyncTask mAddLikeTask;
    private AsyncCallback<WebAPIResult> mAddLikeCallback;

    private DeleteAsyncTask mRemoveUnlikeTask;
    private AsyncCallback<WebAPIResult> mRemoveUnlikeCallback;
    private long mLastNoteId;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public DayFragment() {
    }

    public static DayFragment newInstance(Date date) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, MainActivity.PARSABLE_DATE_FORMAT.format(date));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String dateString = getArguments().getString(ARG_DATE);
            try {
                mCurrentDate = MainActivity.PARSABLE_DATE_FORMAT.parse(dateString);
            } catch (ParseException pe) {
                mCurrentDate = new Date(System.currentTimeMillis());
            }
        }

        initToken();
        initUser();

        mGetNoteCallback = new AsyncCallback<>(this);

        mLoadingCallback = new AsyncCallback<>(this);
        mLoadMoreCallback = new AsyncCallback<>(this);
        mAddLikeCallback = new AsyncCallback<>(this);
        mRemoveUnlikeCallback = new AsyncCallback<>(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View inflated = inflater.inflate(R.layout.fragment_day, container, false);

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
                refreshList();
            }
        });

        mListener.onDayFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(mCurrentDate);

                String path = String.format("api/users/%s/notes", mUserSubject);
                String rawQuery = String.format(Locale.US, "((DateCreated Ge %d-%d-%d 00:00:00 -0700) And (DateCreated Le %d-%d-%d 23:59:59 -0700))",
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                        calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

                Query query = Query.builder()
                        .setPath(path)
//                        .setAccessToken(mToken.getAccessToken())
                        .setSort("-datecreated")
                        .setFilter(rawQuery)
                        .setPage(page + 1)
                        .build();

                mLoadingTask = new GetAsyncTask(getContext(), mLoadMoreCallback, mToken);
                mLoadingTask.execute(query);
            }
        };

        refreshList();

        return inflated;
    }

    @Override
    public void onAttach(Context activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mListener = (OnDayFragmentDataListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
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

        setLoading(true);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
        Map<String, Object> map = new HashMap<>();
        map.put("noteid", noteId);
        map.put("userid", mUserId);
        map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
        mAddLikeTask = new PostAsyncTask(getContext(), mAddLikeCallback, mToken, map);
        mAddLikeTask.execute("api/likes");
    }

    @Override
    public void onUnlikeButtonClick(View view) {
        TextView hiddenNoteIdTextView = (TextView) view.findViewById(R.id.hidden_note_id);
        long noteId = Long.parseLong(hiddenNoteIdTextView.getText().toString());
        mLastNoteId = noteId;
        setLoading(true);
        mRemoveUnlikeTask = new DeleteAsyncTask(getContext(), mRemoveUnlikeCallback, mToken);
        Query query = Query.builder()
                .setPath(String.format(Locale.US, "api/notes/%d/likes/%d", noteId, mUserId))
                .build();
        mRemoveUnlikeTask.execute(query);
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

    private void refreshList() {
        setLoading(true);

        mLoadingTask = new GetAsyncTask(getContext(), mLoadingCallback, mToken);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);

        String path = String.format("api/users/%s/notes", mUserSubject);
        String rawQuery = String.format(Locale.US, "((DateCreated Ge %d-%d-%d 00:00:00 -0700) And (DateCreated Le %d-%d-%d 23:59:59 -0700))",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        Query query = Query.builder()
                .setPath(path)
//                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .setFilter(rawQuery)
                .build();

        mLoadingTask.execute(query);
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
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
    public void onNoteAdded(Note note) {
        mAdapter.add(0, note);
        setLoading(false);
    }

    @Override
    public void onNoteAdded(long noteId) {
        refreshList();
    }

    @Override
    public void onNoteDeleted(long noteId) {
        mAdapter.remove(noteId);
    }

    @Override
    public void onCommentAdded(long noteId, long commentId) {
        mGetNoteTask = new GetAsyncTask(getContext(), mGetNoteCallback, mToken);
        String path = "api/notes/" + noteId;

        Query query = Query.builder()
                .setPath(path)
                .build();

        mGetNoteTask.execute(query);
    }

    @Override
    public void onLikeAdded(long noteId, long likeId) {
        mGetNoteTask = new GetAsyncTask(getContext(), mGetNoteCallback, mToken);
        String path = "api/notes/" + noteId;

        Query query = Query.builder()
                .setPath(path)
                .build();

        mGetNoteTask.execute(query);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mLoadingCallback)) {
            mLoadingTask = null;
            List<Note> notes = JsonHelper.getNotes(mLoadingCallback.getResult().getResult());
            onNotesRetrieved(notes);
        } else if (cb.equals(mLoadMoreCallback)) {
            mLoadingTask = null;
            List<Note> notes = JsonHelper.getNotes(mLoadMoreCallback.getResult().getResult());
            onLoadedMore(notes);
        } else if (cb.equals(mGetNoteCallback)) {
            mGetNoteTask = null;
            Note note = JsonHelper.getNote(mGetNoteCallback.getResult().getResult());
            onNoteRetrieved(note);
        } else if (cb.equals(mAddLikeCallback)) {
            mAddLikeTask = null;
            WebAPIResult result = mAddLikeCallback.getResult();
            if (result.isSuccess()) {
                Like addedLike = JsonHelper.getLike(mAddLikeCallback.getResult().getResult());
                onLikeAdded(addedLike.getNoteId(), addedLike.getId());
            }
        } else if (cb.equals(mRemoveUnlikeCallback)) {
            mRemoveUnlikeTask = null;
            WebAPIResult result = mRemoveUnlikeCallback.getResult();
            if (result.isSuccess()) {
                onLikeAdded(mLastNoteId, 0);
            }
        }
    }

    public void setDate(Date date) {
        mCurrentDate = date;
        refreshList();
    }

    public interface OnDayFragmentDataListener {
        void onDayFragmentCreated(View view);

        void selectDay(Date date);
    }
}

