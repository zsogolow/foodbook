package foodbook.thinmint.activities;

import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import foodbook.thinmint.IActivityCallback;
import foodbook.thinmint.IAsyncCallback;
import foodbook.thinmint.R;

public class FeedActivity extends TokenActivity implements IActivityCallback {

    ListView mListView;
    SwipeRefreshLayout mSwipeRefreshLayout;
    BaseAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        initToken();

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.activity_main_swipe_refresh_layout);
        mListView = (ListView) findViewById(R.id.activity_main_listview);
        String[] fakeTweets = getResources().getStringArray(R.array.cat_names);
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, fakeTweets);
        mListView.setAdapter(mAdapter);

        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {

            @Override
            public void onRefresh() {
                refreshContent();
            }
        });
    }

    private void refreshContent() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mAdapter = new ArrayAdapter<String>(FeedActivity.this, android.R.layout.simple_list_item_1, getNewCatNames());
                mListView.setAdapter(mAdapter);
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 0);
    }

    private List<String> getNewCatNames() {
        String[] fakeTweets = getResources().getStringArray(R.array.cat_names);
        List<String> newCatNames = new ArrayList<String>();
        for (int i = 0; i < fakeTweets.length; i++) {
            int randomCatNameIndex = new Random().nextInt(fakeTweets.length - 1);
            newCatNames.add(fakeTweets[randomCatNameIndex]);
        }
        return newCatNames;
    }

    @Override
    public void callback(IAsyncCallback cb) {

    }
}
