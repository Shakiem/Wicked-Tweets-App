package com.shakiemsaunders.wickedTweetsApp.fragments;

import android.app.Dialog;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.shakiemsaunders.wickedTweetsApp.R;
import com.shakiemsaunders.wickedTweetsApp.TwitterApplication;
import com.shakiemsaunders.wickedTweetsApp.TwitterClient;
import com.shakiemsaunders.wickedTweetsApp.activities.TimeLineActivity;
import com.shakiemsaunders.wickedTweetsApp.models.Tweet;
import com.shakiemsaunders.wickedTweetsApp.models.User;
import com.shakiemsaunders.wickedTweetsApp.transformations.CircleTransform;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


public class ComposeTweetFragment extends DialogFragment  {
    private EditText tweetTextBox;
    private Button tweetButton;
    private Button cancelButton;
    private ImageView profileImage;
    private TextView userScreenName;
    private TextView userName;
    private TextView charsLeft;
    private User user;

    public ComposeTweetFragment() {
        // Empty constructor is required for DialogFragment
        // Make sure not to add arguments to the constructor
        // Use `newInstance` instead as shown below
    }

    public static ComposeTweetFragment newInstance(User user) {
        ComposeTweetFragment frag = new ComposeTweetFragment();
        Bundle args = new Bundle();
        args.putSerializable("user", user);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_compose_tweet, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Get field from view
        tweetTextBox = (EditText) view.findViewById(R.id.tweetTextBox);
        profileImage = (ImageView) view.findViewById(R.id.currentUserProfileImage);
        userName = (TextView) view.findViewById(R.id.currentUserName);
        userScreenName = (TextView) view.findViewById(R.id.currentUserScreenName);
        charsLeft = (TextView) view.findViewById(R.id.charsLeft);
        tweetButton = (Button) view.findViewById(R.id.tweetButton);
        cancelButton = (Button) view.findViewById(R.id.cancelButton);

        user  = (User) getArguments().getSerializable("user");


        if(user != null)
        {
            userScreenName.setText("@" + user.getScreenName());
            userName.setText(user.getName());
            Picasso.with(getContext()).load(user.getProfileImageURL())
                    .transform(new CircleTransform())
                    .placeholder(R.drawable.default_profile_image)
                    .into(profileImage);
        }

        // Show soft keyboard automatically and request focus to field
        tweetTextBox.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        tweetTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                int charactersLeft  = 140 - charSequence.length();
                charsLeft.setText(Integer.toString(charactersLeft));
            }

            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        charsLeft.setText("140");


        tweetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String tweetText = tweetTextBox.getText().toString();
                TwitterClient client = TwitterApplication.getRestClient();
                client.postToTimeLine(tweetText, new JsonHttpResponseHandler(){
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Toast.makeText(getContext(), "Success! Tweet posted.", Toast.LENGTH_SHORT).show();
                        Tweet tweet = Tweet.parseTweetFromJSON(response);
                        ((TimeLineActivity)getActivity()).updateTimeLine(tweet);
                        getDialog().cancel();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        Toast.makeText(getContext(), "Could not post tweet at this time...", Toast.LENGTH_SHORT).show();
                        getDialog().cancel();
                    }
                });
            }
        });




        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().cancel();
            }
        });
    }

    public void onResume() {

        // Store access variables for window and blank point
        Window window = getDialog().getWindow();
        Point size = new Point();

        // Store dimensions of the screen in `size`
        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        // Set the width of the dialog proportional to 75% of the screen width
        window.setLayout((int) (size.x * 0.90), WindowManager.LayoutParams.WRAP_CONTENT);
        window.setGravity(Gravity.CENTER);

        // Call super onResume after sizing
        super.onResume();
    }

    @Override

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);

        // request a window without the title
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
}
