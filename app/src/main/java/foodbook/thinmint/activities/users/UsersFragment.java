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
import java.util.List;

import foodbook.thinmint.R;
import foodbook.thinmint.activities.adapters.UsersRecyclerAdapter;
import foodbook.thinmint.models.domain.User;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUsersFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UsersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UsersFragment extends Fragment implements UsersRecyclerAdapter.ViewHolder.IOnUserClickListener {
    private static final String ARG_USERID = "userid";

    private String mUserId;

    private OnUsersFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private UsersRecyclerAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public UsersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment HomeFragment.
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

        mAdapter = new UsersRecyclerAdapter(new ArrayList<User>(), this);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshUsers();
            }
        });

        mListener.onUsersFragmentCreated(inflated);

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        refreshUsers();
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
        mListener.refreshUsers();
    }

    public void onUsersRetrieved(List<User> users) {
        mAdapter.swap(users);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();
        setLoading(false);
    }

    @Override
    public void onUserClicked(View caller) {
        TextView hiddenUserSubjectTextView = (TextView)caller.findViewById(R.id.hidden_user_id);
        TextView usernameTextView = (TextView)caller.findViewById(R.id.user_name);
        String userSubject = hiddenUserSubjectTextView.getText().toString();
        String username = usernameTextView.getText().toString();
        mListener.showUser(userSubject, username);
    }

    public interface OnUsersFragmentDataListener {
        void onUsersFragmentCreated(View view);

        void refreshUsers();

        void showUser(String subject, String username);
    }
}
