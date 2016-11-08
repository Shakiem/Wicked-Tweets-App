package com.shakiemsaunders.wickedTweetsApp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.astuetz.PagerSlidingTabStrip;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.TwitterApplication;
import com.shakiemsaunders.wickedTweetsApp.TwitterClient;
import com.shakiemsaunders.wickedTweetsApp.fragments.ComposeTweetFragment;
import com.shakiemsaunders.wickedTweetsApp.fragments.HomeTimeLineFragment;
import com.shakiemsaunders.wickedTweetsApp.fragments.MentionsTimeLineFragment;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;
import com.shakiemsaunders.wickedTweetsApp.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import cz.msebera.android.httpclient.Header;

public class TimeLineActivity extends AppCompatActivity {
    private TwitterClient client;
    private ViewPager viewPager;
    private PagerSlidingTabStrip tabStrip;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_line);
        Toolbar toolbar = (Toolbar)findViewById(R.id.tweetToolbar);
        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPager.setAdapter(new TweetsPagerAdapter(getSupportFragmentManager()));

        tabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
        tabStrip.setViewPager(viewPager);

        client = TwitterApplication.getRestClient();
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
            case R.id.action_profile:
                showProile();
            default:
                return super.onOptionsItemSelected(item);
        }
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

    private void showProile(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
    }

    public void updateTimeLine(Tweet tweet){
        if(tweet == null)
            return;
        else {
            List<Fragment> fragments = getSupportFragmentManager().getFragments();
            for (Fragment fragment : fragments) {
                if(fragment instanceof HomeTimeLineFragment){
                    ((HomeTimeLineFragment)fragment).updateTimeLine(tweet);
                    break;
                }
            }
        }
    }

    //returns the order of the fragments in the viewPager
    public class TweetsPagerAdapter extends FragmentPagerAdapter{
        private String tabTitles[] = {"Home", "Mentions"};

        public TweetsPagerAdapter(FragmentManager manager){
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if(position == 0)
                return new HomeTimeLineFragment();
            else if (position == 1){
                return new MentionsTimeLineFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return tabTitles.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }
    }

}
