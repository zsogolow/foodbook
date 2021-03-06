package foodbook.thinmint.activities.unused;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Locale;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.common.TokenFragment;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.User;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.GetAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnUserInfoFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link UserInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserInfoFragment extends TokenFragment {
    private static final String ARG_USERID = "userid";

    private String mUserId;

    private OnUserInfoFragmentDataListener mListener;

    private SwipeRefreshLayout mSwipeRefreshLayout;
    private TextView mUserName;

    private GetAsyncTask mGetUserTask;
    private AsyncCallback<WebAPIResult> mGetUserCallback;

    public UserInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param userid Parameter 1.
     * @return A new instance of fragment UserInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserInfoFragment newInstance(String userid) {
        UserInfoFragment fragment = new UserInfoFragment();
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

        mGetUserCallback = new AsyncCallback<>(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_user_info, container, false);
        mListener.onUserInfoFragmentCreated(inflated);

        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_note_swipe_refresh_layout);
        mUserName = (TextView) inflated.findViewById(R.id.user_name);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshFragment();
            }
        });

        return inflated;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserInfoFragmentDataListener) {
            mListener = (OnUserInfoFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnUserInfoFragmentDataListener");
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
        mGetUserTask = new GetAsyncTask(getContext(), mGetUserCallback, mToken);

        String path = String.format(Locale.US, "api/users/%s", mUserId);

        Query query = Query.builder()
                .setPath(path)
                .build();

        mGetUserTask.execute(query);
    }

    private void onUserRetrieved(User user) {
        setLoading(false);
        mUserName.setText(user.getUsername());
    }

    @Override
    public void callback(IAsyncCallback cb) {
        super.callback(cb);

        if (cb.equals(mGetUserCallback)) {
            mGetUserTask = null;
            User user = JsonHelper.getUser(mGetUserCallback.getResult().getResult());
            onUserRetrieved(user);
        }
    }

    public interface OnUserInfoFragmentDataListener {
        void onUserInfoFragmentCreated(View view);
    }
}
