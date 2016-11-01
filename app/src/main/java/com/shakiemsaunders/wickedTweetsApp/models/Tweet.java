package com.shakiemsaunders.wickedTweetsApp.models;

import android.text.format.DateUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class Tweet implements Serializable {
    private long id;
    private String body;
    private String createdAt;
    private User user;

    public Tweet(long id, String body, String createdAt, User user){
        this.id = id;
        this.body = body;
        this.createdAt = createdAt;
        this.user = user;
    }

    public static Tweet parseTweetFromJSON(JSONObject json){
        Tweet tweet;
        try{
            tweet = new Tweet(
                    json.getInt("id"),
                    json.getString("text"),
                    json.getString("created_at"),
                    User.parseUserFromJSON(json.getJSONObject("user"))
            );
        }
        catch (JSONException e){
            Log.e("ERROR:", e.getMessage(), e);
            return null;
        }
        return tweet;
    }

    public long getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public User getUser() {
        return user;
    }

    public static List<Tweet> parseListFromJSONArray(JSONArray jsonArray) {
        List<Tweet> tweets = new ArrayList<>();

        for(int i = 0; i < jsonArray.length(); i++){
            try {
                JSONObject tweetJSON = jsonArray.getJSONObject(i);
                Tweet tweet = Tweet.parseTweetFromJSON(tweetJSON);
                if(tweet != null)
                    tweets.add(tweet);
            }
            catch (JSONException e) {
                e.printStackTrace();
                continue;
            }
        }

        return tweets;
    }


    public static String  getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }


    /*
  {
    "created_at": "Tue Aug 28 21:16:23 +0000 2012",
    "text": "just another test",
    "id": 240558470661799936,
    "retweet_count": 0,
    "user": { },
  }
     */
}
