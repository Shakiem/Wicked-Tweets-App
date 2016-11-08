package com.shakiemsaunders.wickedTweetsApp.activities;

import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.TwitterApplication;
import com.shakiemsaunders.wickedTweetsApp.TwitterClient;
import com.shakiemsaunders.wickedTweetsApp.fragments.UserTimeLineFragment;
import com.shakiemsaunders.wickedTweetsApp.models.User;
import com.shakiemsaunders.wickedTweetsApp.transformations.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ProfileActivity extends AppCompatActivity {
    private TwitterClient client;
    private User user;
    private ImageView backgroundImage;
    private ImageView profileImage;
    private TextView name;
    private TextView screenName;
    private TextView description;
    private TextView followersCount;
    private TextView followingsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        String screenName = null;
        user = (User) getIntent().getSerializableExtra("user");
        if(user == null) {
            client = TwitterApplication.getRestClient();
            client.getUserInfo(new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    try {
                        user = User.parseUserFromJSON(response);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ProfileActivity.this, "Unable to load user profile at this time...", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    populateProfileHeader(user);
                }
            });
        }
        else{
            populateProfileHeader(user);
            screenName = user.getScreenName();
        }

        if(savedInstanceState == null){
            UserTimeLineFragment userTimeLineFragment = UserTimeLineFragment.newInstance(screenName);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, userTimeLineFragment);
            fragmentTransaction.commit();
        }
    }

    private void populateProfileHeader(User user) {
        name = (TextView) findViewById(R.id.name);
        screenName = (TextView) findViewById(R.id.screenName);
        description = (TextView) findViewById(R.id.description);
        followersCount = (TextView) findViewById(R.id.followersCount);
        followingsCount = (TextView) findViewById(R.id.followingCount);
        profileImage = (ImageView) findViewById(R.id.userProfileImage);
        backgroundImage = (ImageView) findViewById(R.id.profileBackgroundImage);

        name.setText(user.getName());
        screenName.setText("@" + user.getScreenName());
        description.setText(user.getDescription());
        followingsCount.setText(new Integer(user.getFollowingCount()).toString());
        followersCount.setText(new Integer(user.getFollowersCount()).toString());

        backgroundImage.setImageResource(0);
        profileImage.setImageResource(0);
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getSize(size);
        Picasso.with(this).load(user.getBackgroundImageURL())
                .placeholder(R.color.orange)
                .resize(size.x, 0).centerCrop()
                .into(backgroundImage);

        Picasso.with(this).load(user.getProfileImageURL())
                .transform(new CircleTransform())
                .placeholder(R.drawable.default_profile_image)
                .into(profileImage);

    }


}
