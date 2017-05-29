package foodbook.thinmint.activities.users;

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
import foodbook.thinmint.activities.adapters.users.list.IOnUsersListClickListener;
import foodbook.thinmint.activities.adapters.users.list.UsersListRecyclerAdapter;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.GetAsyncTask;
import foodbook.thinmint.tasks.TasksHelper;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUsersFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends TokenFragment implements IApiCallback, IOnUsersListClickListener {
    private static final String ARG_USERID = "userid";

    private String mUserId;

    private OnUsersFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UsersListRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private GetAsyncTask mGetUsersTask;
    private AsyncCallback<WebAPIResult> mGetUsersCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static UsersFragment newInstance(String userid) {
        UsersFragment fragment = new UsersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERID, userid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserId = getArguments().getString(ARG_USERID);
        }

        initToken();
        initUser();

        mGetUsersCallback = new AsyncCallback<WebAPIResult>(this);
        mLoadMoreCallback = new AsyncCallback<WebAPIResult>(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_users, container, false);

        mListView = (RecyclerView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new UsersListRecyclerAdapter(new ArrayList<User>(), this, getActivity());
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
            }
        });

        mListener.onUsersFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                TasksHelper.getUsers(getContext(), mLoadMoreCallback, mToken, page + 1, "");
            }
        };

        refreshUsers();

        return inflated;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUsersFragmentDataListener) {
            mListener = (OnUsersFragmentDataListener) context;
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

    private void refreshUsers() {
        setLoading(true);
        TasksHelper.getUsers(getContext(), mLoadMoreCallback, mToken, 1, "");
    }

    private void onUsersRetrieved(List<User> users) {
        mAdapter.swap(users);
        setLoading(false);
    }

    private void onLoadedMore(List<User> users) {
        mAdapter.addAll(users);
    }

    @Override
    public void onUserClick(View caller) {
        TextView hiddenUserSubjectTextView = (TextView) caller.findViewById(R.id.hidden_user_id);
        TextView usernameTextView = (TextView) caller.findViewById(R.id.user_name);
        String userSubject = hiddenUserSubjectTextView.getText().toString();
        String username = usernameTextView.getText().toString();
        ActivityHelper.startUserActivity(getActivity(), userSubject, username);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetUsersCallback)) {
            mGetUsersTask = null;
            List<User> users = JsonHelper.getUsers(mGetUsersCallback.getResult().getResult());
            onUsersRetrieved(users);
        } else if (cb.equals(mLoadMoreCallback)) {
            mGetUsersTask = null;
            List<User> users = JsonHelper.getUsers(mLoadMoreCallback.getResult().getResult());
            onLoadedMore(users);
        }
    }

    public interface OnUsersFragmentDataListener {
        void onUsersFragmentCreated(View view);
    }
}
