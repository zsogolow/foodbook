package foodbook.thinmint.activities.day;

import android.content.Context;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.net.URLEncoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.MainActivity;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.activities.common.OnNotesListInteractionListener;
import foodbook.thinmint.activities.adapters.NotesRecyclerAdapter;
import foodbook.thinmint.activities.users.UsersFragment;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Note;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.CallServiceAsyncTask;
import foodbook.thinmint.tasks.CallServiceCallback;

/**
 * A placeholder fragment containing a simple view.
 */
public class DayFragment extends TokenFragment implements OnNotesListInteractionListener,
        NotesRecyclerAdapter.ViewHolder.IOnNoteClickListener, IApiCallback {
    private static final String ARG_DATE = "date";

    private Date mCurrentDate;

    private OnDayFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NotesRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private CallServiceAsyncTask mLoadingTask;
    private CallServiceCallback mLoadingCallback;
    private CallServiceCallback mLoadMoreCallback;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public DayFragment() {
    }

    public static DayFragment newInstance(Date date) {
        DayFragment fragment = new DayFragment();
        Bundle args = new Bundle();
        args.putString(ARG_DATE, MainActivity.DATE_FORMAT.format(date));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            String dateString = getArguments().getString(ARG_DATE);
            try {
                mCurrentDate = MainActivity.DATE_FORMAT.parse(dateString);
            } catch (ParseException pe) {
                mCurrentDate = new Date(System.currentTimeMillis());
            }
        }

        initToken();
        initUser();

        mLoadingCallback = new CallServiceCallback(this);
        mLoadMoreCallback = new CallServiceCallback(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
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

        mAdapter = new NotesRecyclerAdapter(new ArrayList<Note>(), this);
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
                        .setAccessToken(mToken.getAccessToken())
                        .setSort("-datecreated")
                        .setFilter(rawQuery)
                        .setPage(page + 1)
                        .build();

                mLoadingTask = new CallServiceAsyncTask(getContext(), mLoadMoreCallback, mToken);
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
    public void onNoteClicked(View caller) {
        TextView hiddenNoteIdTextView = (TextView) caller.findViewById(R.id.hidden_note_id);
        String noteId = hiddenNoteIdTextView.getText().toString();
        mListener.showNote(Long.parseLong(noteId));
    }

    private void refreshList() {
        setLoading(true);

        mLoadingTask = new CallServiceAsyncTask(getContext(), mLoadingCallback, mToken);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mCurrentDate);

        String path = String.format("api/users/%s/notes", mUserSubject);
        String rawQuery = String.format(Locale.US, "((DateCreated Ge %d-%d-%d 00:00:00 -0700) And (DateCreated Le %d-%d-%d 23:59:59 -0700))",
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH));

        Query query = Query.builder()
                .setPath(path)
                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .setFilter(rawQuery)
                .build();

        mLoadingTask.execute(query);
    }

    private void setLoading(boolean isLoading) {
        mSwipeRefreshLayout.setRefreshing(isLoading);
    }

    public void setDate(Date date) {
        mCurrentDate = date;
        refreshList();
    }

    private void onNotesRetrieved(List<Note> notes) {
        mAdapter.swap(notes);
        setLoading(false);
    }

    private void onLoadedMore(List<Note> notes) {
        mAdapter.append(notes);
    }

    @Override
    public void onNoteAdded(Note note) {
        mAdapter.add(note);
        setLoading(false);
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
        }
    }

    public interface OnDayFragmentDataListener {
        void onDayFragmentCreated(View view);

        void selectDay(Date date);

        void showNote(long noteId);
    }
}

