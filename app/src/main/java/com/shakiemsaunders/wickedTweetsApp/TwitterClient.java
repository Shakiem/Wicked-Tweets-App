package com.shakiemsaunders.wickedTweetsApp;

import android.content.Context;

import com.codepath.oauth.OAuthBaseClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.scribe.builder.api.Api;
import org.scribe.builder.api.TwitterApi;

/*
 * 
 * This is the object responsible for communicating with a REST API. 
 * Specify the constants below to change the API being communicated with.
 * See a full list of supported API classes: 
 *   https://github.com/fernandezpablo85/scribe-java/tree/master/src/main/java/org/scribe/builder/api
 * Key and Secret are provided by the developer site for the given API i.e dev.twitter.com
 * Add methods for each relevant endpoint in the API.
 * 
 * NOTE: You may want to rename this object based on the service i.e TwitterClient or FlickrClient
 * 
 */
public class TwitterClient extends OAuthBaseClient {
	public static final Class<? extends Api> REST_API_CLASS = TwitterApi.class;
	public static final String REST_URL = "https://api.twitter.com/1.1"; // Change this, base API URL
	public static final String REST_CONSUMER_KEY = "csFrzTTl7jV2roB8cNPw4MYzj";
	public static final String REST_CONSUMER_SECRET = "gWlsoQFSU8Uf5bF2QjZhBHq0JWy4w7vc6mG0NImFz4Gy7fWMyi";
	public static final String REST_CALLBACK_URL = "oauth://wickedTweets";

	public TwitterClient(Context context) {
		super(context, REST_API_CLASS, REST_URL, REST_CONSUMER_KEY, REST_CONSUMER_SECRET, REST_CALLBACK_URL);
	}

	public void getHomeTimeLine(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		//specify the request params
		RequestParams params = new RequestParams();
		params.put("count", 100);
		params.put("since_id", 1);

		//execute request
		getClient().get(apiUrl, params, handler);
	}

	public void getUserTimeLine(String screenName, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/user_timeline.json");

		//specify the request params
		RequestParams params = new RequestParams();
		params.put("count", 100);
		params.put("screen_name", screenName);

		//execute request
		getClient().get(apiUrl, params, handler);
	}

	public void postToTimeLine(String tweetStatus, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/update.json");

		RequestParams params = new RequestParams();
		params.put("status", tweetStatus);

		getClient().post(apiUrl,params, handler);
	}

	public void getUserInfo(AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("account/verify_credentials.json");

		getClient().get(apiUrl, new RequestParams(), handler);
	}

	public void getMoreTweets(long maxID, AsyncHttpResponseHandler handler){
		String apiUrl = getApiUrl("statuses/home_timeline.json");

		//specify the request params
		RequestParams params = new RequestParams();
		params.put("count", 25);
		params.put("max_id", maxID);

		//execute request
		getClient().get(apiUrl, params, handler);
	}

	public void getMentionsTimeLine(JsonHttpResponseHandler handler) {
		String apiUrl = getApiUrl("statuses/mentions_timeline.json");

		//specify the request params
		RequestParams params = new RequestParams();
		params.put("count", 100);

		//execute request
		getClient().get(apiUrl, params, handler);
	}

}
