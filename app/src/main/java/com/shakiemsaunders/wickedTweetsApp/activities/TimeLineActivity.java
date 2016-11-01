package com.shakiemsaunders.wickedTweetsApp.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.TwitterApplication;
import com.shakiemsaunders.wickedTweetsApp.TwitterClient;
import com.shakiemsaunders.wickedTweetsApp.adapters.TweetsArrayAdapter;
import com.shakiemsaunders.wickedTweetsApp.fragments.ComposeTweetFragment;
import com.shakiemsaunders.wickedTweetsApp.listeners.EndlessScrollListener;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;
import com.shakiemsaunders.wickedTweetsApp.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {
    private TwitterClient client;
    private List<Tweet> tweets;
    private TweetsArrayAdapter tweetsAdapter;
    private ListView timeLineListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);

        timeLineListView = (ListView)findViewById(R.id.timeLineListView);
        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsArrayAdapter(this, tweets);
        timeLineListView.setAdapter(tweetsAdapter);
        client = TwitterApplication.getRestClient();
        populateTimeLine();

        timeLineListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                loadMoreTweets(totalItemsCount);

                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_time_line, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.

        switch(item.getItemId()){
            case R.id.action_compose_tweet:
                showEditDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void populateTimeLine(){
        client.getHomeTimeLine(new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newTweets = Tweet.parseListFromJSONArray(response);
                //you can check from empty list here and do what you want...
                if(newTweets.isEmpty())
                    Toast.makeText(getApplicationContext(), "Could not load new tweets...", Toast.LENGTH_SHORT).show();

                tweetsAdapter.addAll(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimeLineActivity.this, errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showEditDialog() {
        client.getUserInfo(new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                User user;
                try {
                    user = User.parseUserFromJSON(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to retrieve user data", Toast.LENGTH_SHORT).show();
                    user = null;
                }

                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(user);
                composeTweetFragment.show(fm, "fragment_compose_tweet");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getApplicationContext(), "Unable to retrieve user data", Toast.LENGTH_SHORT).show();
                FragmentManager fm = getSupportFragmentManager();
                ComposeTweetFragment composeTweetFragment = ComposeTweetFragment.newInstance(null);
                composeTweetFragment.show(fm, "fragment_compose_tweet");
            }
        });



    }

    public void updateTimeLine(Tweet tweet){
        if(tweet == null)
            return;
        else {
            tweets.add(0, tweet);
            tweetsAdapter.notifyDataSetChanged();
        }


    }

    private void loadMoreTweets(int totalItemsCount){
        Tweet tweet = tweets.get(totalItemsCount - 1);
        long id = tweet.getId();
        long lastValidId = Long.MAX_VALUE;
        if(id < 0 ) { //workaround where some ids were coming back as negatives...
            for (Tweet tweet1 : tweets) {
                if(tweet1.getId() > 0)
                    lastValidId = tweet1.getId();
            }
        }
        client.getMoreTweets(lastValidId, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newTweets = Tweet.parseListFromJSONArray(response);
                //you can check from empty list here and do what you want...
                if(newTweets.isEmpty())
                    Toast.makeText(getApplicationContext(), "Could not load new tweets...", Toast.LENGTH_SHORT).show();

                tweetsAdapter.addAll(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(TimeLineActivity.this, errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
