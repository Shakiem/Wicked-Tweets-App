package com.shakiemsaunders.wickedTweetsApp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;
import com.shakiemsaunders.wickedTweetsApp.transformations.CircleTransform;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.shakiemsaunders.wickedTweetsApp.R.id.createdAt;
import static com.shakiemsaunders.wickedTweetsApp.R.id.name;
import static com.shakiemsaunders.wickedTweetsApp.R.id.profileImage;
import static com.shakiemsaunders.wickedTweetsApp.R.id.tweetBody;
import static com.shakiemsaunders.wickedTweetsApp.R.id.userName;

public class TweetsArrayAdapter extends ArrayAdapter<Tweet> {

    public TweetsArrayAdapter(Context context, List<Tweet> tweets) {
        super(context, R.layout.item_tweet, tweets);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tweet tweet = getItem(position);
        TweetHolder holder;

        if(convertView == null){
            holder = new TweetHolder();
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tweet, parent, false);
            holder.profileImage = (ImageView) convertView.findViewById(profileImage);
            holder.userName = (TextView) convertView.findViewById(userName);
            holder.name = (TextView) convertView.findViewById(name);
            holder.createdAt = (TextView) convertView.findViewById(createdAt);
            holder.tweetBody = (TextView) convertView.findViewById(tweetBody);

            convertView.setTag(holder);
        }
        else{
            holder = (TweetHolder)convertView.getTag();
        }


        holder.name.setText(tweet.getUser().getName());
        holder.userName.setText("@" + tweet.getUser().getScreenName());
        holder.tweetBody.setText(tweet.getBody());
        holder.createdAt.setText(Tweet.getRelativeTimeAgo(tweet.getCreatedAt()));
        holder.profileImage.setImageResource(android.R.color.transparent);
        Picasso.with(getContext()).load(tweet.getUser()
                .getProfileImageURL())
                .transform(new CircleTransform())
                .placeholder(R.drawable.default_profile_image)
                .into(holder.profileImage);

        return convertView;
    }

    private class TweetHolder{
        ImageView profileImage;
        TextView userName;
        TextView name;
        TextView createdAt;
        TextView tweetBody;
    }
}
