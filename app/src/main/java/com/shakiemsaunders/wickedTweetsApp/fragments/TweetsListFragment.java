package com.shakiemsaunders.wickedTweetsApp.fragments;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.adapters.TweetsArrayAdapter;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by shakiem on 11/6/16.
 */

public class TweetsListFragment extends Fragment {

    protected List<Tweet> tweets;
    protected TweetsArrayAdapter tweetsAdapter;
    protected ListView timeLineListView;

    @Nullable @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup parent, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweets_list, parent, false);

        timeLineListView = (ListView)view.findViewById(R.id.timeLineListView);
        timeLineListView.setAdapter(tweetsAdapter);

        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tweets = new ArrayList<>();
        tweetsAdapter = new TweetsArrayAdapter(getActivity(), tweets);
    }

    public void addAll(List<Tweet> tweetsToAdd){
        tweets.addAll(tweetsToAdd);
        tweetsAdapter.notifyDataSetChanged();
    }

}
