package com.shakiemsaunders.wickedTweetsApp.models;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

public class User implements Serializable {
    private long id;
    private String name;
    private String screenName;
    private String profileImageURL;
    private String backgroundImageURL;
    private String description;
    private int followersCount;
    private int followingCount;

    public User(long id, String name, String screenName, String profileImageURL,
                String backgroundImageURL, String description, int followersCount, int followingCount){
        this.id = id;
        this.name = name;
        this.screenName = screenName;
        this.profileImageURL = profileImageURL;
        this.backgroundImageURL = backgroundImageURL;
        this.description = description;
        this.followersCount = followersCount;
        this.followingCount = followingCount;
    }

    public static User parseUserFromJSON(JSONObject jsonObject) throws JSONException{
        User user;
        try{
            user = new User(
                    jsonObject.getLong("id"),
                    jsonObject.getString("name"),
                    jsonObject.getString("screen_name"),
                    jsonObject.getString("profile_image_url"),
                    jsonObject.getString("profile_background_image_url"),
                    jsonObject.getString("description"),
                    jsonObject.getInt("followers_count"),
                    jsonObject.getInt("friends_count")
            );
        }
        catch (JSONException e){
            Log.e("ERROR:", e.getMessage(), e);
            throw e;
        }
        return user;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getScreenName() {
        return screenName;
    }

    public String getProfileImageURL() {
        return profileImageURL;
    }

    public String getBackgroundImageURL() {
        return backgroundImageURL;
    }

    public String getDescription() {
        return description;
    }

    public int getFollowersCount() {
        return followersCount;
    }

    public int getFollowingCount() {
        return followingCount;
    }

    /*
"user": {
      "name": "OAuth Dancer",
      "profile_sidebar_border_color": "C0DEED",
      "profile_image_url": "http://a0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "created_at": "Wed Mar 03 19:37:35 +0000 2010",
      "location": "San Francisco, CA",
      "follow_request_sent": false,
      "profile_link_color": "0084B4",
      "entities": {
        "url": {
          "urls": [
            {
              "expanded_url": null,
              "url": "http://bit.ly/oauth-dancer",
              "indices": [
                0,
                26
              ],
              "display_url": null
            }
          ]
        },
        "description": null
      },
      "url": "http://bit.ly/oauth-dancer",
      "profile_image_url_https": "https://si0.twimg.com/profile_images/730275945/oauth-dancer_normal.jpg",
      "id": 119476949,
      "listed_count": 1,
      "description": "",
      "screen_name": "oauth_dancer"
    },
     */
}
