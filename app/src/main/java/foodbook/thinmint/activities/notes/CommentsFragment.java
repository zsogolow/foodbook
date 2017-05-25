package foodbook.thinmint.activities.notes;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import foodbook.thinmint.IApiCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;
import foodbook.thinmint.activities.ActivityStarter;
import foodbook.thinmint.activities.TokenFragment;
import foodbook.thinmint.activities.adapters.CommentsRecyclerAdapter;
import foodbook.thinmint.activities.adapters.EndlessRecyclerViewScrollListener;
import foodbook.thinmint.api.Query;
import foodbook.thinmint.api.WebAPIResult;
import foodbook.thinmint.models.JsonHelper;
import foodbook.thinmint.models.domain.Comment;
import foodbook.thinmint.tasks.AsyncCallback;
import foodbook.thinmint.tasks.GetAsyncTask;
import foodbook.thinmint.tasks.PostAsyncTask;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link OnCommentsFragmentDataListener} interface
 * to handle interaction events.
 * Use the {@link CommentsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CommentsFragment extends TokenFragment implements IApiCallback,
        CommentsRecyclerAdapter.ViewHolder.IOnCommentClickListener {
    private static final String ARG_NOTEID = "noteid";

    private long mNoteId;

    private OnCommentsFragmentDataListener mListener;

    private RecyclerView mListView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private CommentsRecyclerAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private Button mAddCommentButton;
    private EditText mCommentText;

    private GetAsyncTask mGetCommentsTask;
    private AsyncCallback<WebAPIResult> mGetCommentsCallback;
    private AsyncCallback<WebAPIResult> mLoadMoreCallback;

    private PostAsyncTask mAddCommentTask;
    private AsyncCallback<WebAPIResult> mAddCommentCallback;

    private EndlessRecyclerViewScrollListener mScrollListener;

    public CommentsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param noteid Parameter 1.
     * @return A new instance of fragment FeedFragment.
     */
    public static CommentsFragment newInstance(long noteid) {
        CommentsFragment fragment = new CommentsFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_NOTEID, noteid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mNoteId = getArguments().getLong(ARG_NOTEID);
        }

        initToken();
        initUser();

        mGetCommentsCallback = new AsyncCallback<WebAPIResult>(this);
        mLoadMoreCallback = new AsyncCallback<WebAPIResult>(this);
        mAddCommentCallback = new AsyncCallback<WebAPIResult>(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View inflated = inflater.inflate(R.layout.fragment_comments, container, false);

        mListView = (RecyclerView) inflated.findViewById(R.id.activity_main_listview);
        mSwipeRefreshLayout = (SwipeRefreshLayout) inflated.findViewById(R.id.activity_main_swipe_refresh_layout);

        mAddCommentButton = (Button) inflated.findViewById(R.id.add_comment_button);
        mCommentText = (EditText) inflated.findViewById(R.id.comment_edit_text);
        mAddCommentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.US);
                Map<String, Object> map = new HashMap<>();
                map.put("noteid", mNoteId);
                map.put("userid", mUserId);
                map.put("text", mCommentText.getText().toString());
                map.put("datecreated", dateFormat.format(new Date(System.currentTimeMillis())));
                mAddCommentTask = new PostAsyncTask(getContext(), mAddCommentCallback, mToken, map);
                mAddCommentTask.execute("api/comments");
                ActivityStarter.hideSoftKeyboard(getActivity());
            }
        });

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(getActivity());
        mListView.setLayoutManager(mLayoutManager);

        mAdapter = new CommentsRecyclerAdapter(new ArrayList<Comment>(), this);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshComments();
            }
        });

        mListener.onCommentsFragmentCreated(inflated);

        mScrollListener = new EndlessRecyclerViewScrollListener(mLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {

                String path = String.format(Locale.US, "api/notes/%d/comments", mNoteId);

                Query query = Query.builder()
                        .setPath(path)
//                .setAccessToken(mToken.getAccessToken())
                        .setSort("-datecreated")
                        .build();
                mGetCommentsTask = new GetAsyncTask(getContext(), mLoadMoreCallback, mToken);
                mGetCommentsTask.execute(query);
            }
        };

        refreshComments();

        return inflated;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCommentsFragmentDataListener) {
            mListener = (OnCommentsFragmentDataListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCommentsFragmentDataListener");
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

    private void refreshComments() {
        setLoading(true);
        mGetCommentsTask = new GetAsyncTask(getContext(), mGetCommentsCallback, mToken);

        String path = String.format(Locale.US, "api/notes/%d/comments", mNoteId);

        Query query = Query.builder()
                .setPath(path)
//                .setAccessToken(mToken.getAccessToken())
                .setSort("-datecreated")
                .build();

        mGetCommentsTask.execute(query);
    }

    private void onCommentsRetrieved(List<Comment> comments) {
        mAdapter.swap(comments);
        setLoading(false);
    }

    private void onLoadedMore(List<Comment> comments) {
        mAdapter.append(comments);
    }

    private void onCommentAdded(Comment comment) {
        mAdapter.append(0, comment);
        setLoading(false);
    }

    @Override
    public void callback(IAsyncCallback cb) {
        if (cb.equals(mGetCommentsCallback)) {
            mGetCommentsTask = null;
            List<Comment> comments = JsonHelper.getComments(mGetCommentsCallback.getResult().getResult());
            onCommentsRetrieved(comments);
        } else if (cb.equals(mLoadMoreCallback)) {
            mGetCommentsTask = null;
            List<Comment> comments = JsonHelper.getComments(mLoadMoreCallback.getResult().getResult());
            onLoadedMore(comments);
        } else if (cb.equals(mAddCommentCallback)) {
            mAddCommentTask = null;
            WebAPIResult result = mAddCommentCallback.getResult();
            if (result.isSuccess()) {
                Comment addedComment = JsonHelper.getComment(mAddCommentCallback.getResult().getResult());
                onCommentAdded(addedComment);
                mListener.onCommentAdded(addedComment);
                mCommentText.setText("");
            }
        }
    }

    @Override
    public void onUserClicked(View caller) {
        TextView hiddenUserIdTextView = (TextView) caller.findViewById(R.id.hidden_user_id);
        TextView userNameTextView = (TextView) caller.findViewById(R.id.user_name);
        String userId = hiddenUserIdTextView.getText().toString();
        String username = userNameTextView.getText().toString();
        ActivityStarter.startUserActivity(getActivity(), userId, username);
    }

    public interface OnCommentsFragmentDataListener {
        void onCommentsFragmentCreated(View view);
        void onCommentAdded(Comment comment);
    }
}
