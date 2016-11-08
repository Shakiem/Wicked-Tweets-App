package com.shakiemsaunders.wickedTweetsApp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakiemsaunders.wickedTweetsApp.TwitterApplication;
import com.shakiemsaunders.wickedTweetsApp.TwitterClient;
import com.shakiemsaunders.wickedTweetsApp.listeners.EndlessScrollListener;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by shakiem on 11/7/16.
 */

public class UserTimeLineFragment extends TweetsListFragment {
    private TwitterClient client;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = TwitterApplication.getRestClient();
        populateTimeLine();
    }

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, parent, savedInstanceState);
        timeLineListView.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int page, int totalItemsCount) {

                loadMoreTweets(totalItemsCount);

                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });
        return view;
    }

    public static UserTimeLineFragment newInstance(String screenName) {
        UserTimeLineFragment userFragment = new UserTimeLineFragment();
        Bundle args = new Bundle();
        args.putString("screen_name", screenName);
        userFragment.setArguments(args);
        return userFragment;
    }

    private void populateTimeLine(){
        String screenName = getArguments().getString("screen_name");
        client.getUserTimeLine(screenName, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                List<Tweet> newTweets = Tweet.parseListFromJSONArray(response);
                //you can check from empty list here and do what you want...
                if(newTweets.isEmpty())
                    Toast.makeText(getContext(), "Could not load new tweets...", Toast.LENGTH_SHORT).show();

                addAll(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }
        });
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
                    Toast.makeText(getContext(), "Could not load new tweets...", Toast.LENGTH_SHORT).show();

                tweetsAdapter.addAll(newTweets);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                Toast.makeText(getContext(), errorResponse.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
