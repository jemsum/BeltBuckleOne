package uk.co.jemsum.helloworld;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/* JS: these were the original imports
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;*/

public class MainActivity extends ActionBarActivity {
	
	private TextView tweetDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
        
        tweetDisplay = (TextView)findViewById(R.id.tweet_txt);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    
    public void searchTwitter(View view)
    {
    	//EditText searchTxt = (EditText)findViewById(R.id.search_edit);
    	//String searchTerm = searchTxt.getText().toString(); 
    	tweetDisplay = (TextView)findViewById(R.id.tweet_txt);
    	
//    	if(searchTerm.length()>0)
//    	{
    		try
    		{
//    			String encodedSearch = URLEncoder.encode(searchTerm, "UTF-8");
 //   			String searchURL = "http://search.twitter.com/search.json?q="+encodedSearch;
    			DownloadTwitterTask downloadTT = new DownloadTwitterTask();
    			downloadTT.execute("");
    		}
    		catch(Exception e)
    		{ 
    		    tweetDisplay.setText("Whoops - something went wrong!");
    		    e.printStackTrace(); 
    		}
//    	}
//    	else
//    	{
//    	    tweetDisplay.setText("Enter a search query!");
//    	}
    	
    }
    
    // Uses an AsyncTask to download a Twitter user's timeline
    //TODO make it asynchronous
    private class DownloadTwitterTask extends AsyncTask<String, Void, String> 
    {
    	final static String CONSUMER_KEY = "jgKlAQBYqiNheMZBCsV6lUZ81";
    	final static String CONSUMER_SECRET = "E7TTshX3ikqvyrEGY3dhtnLgesEqRYBLhjz89BoISmHuEtUNVQ";
    	final static String TwitterTokenURL = "https://api.twitter.com/oauth2/token";
    	final static String TwitterTrendsURL = "https://api.twitter.com/1.1/trends/place.json?id=1";

    	private String getTwitterStream(String searchTerm) {
    		String results = null;

    		// Step 1: Encode consumer key and secret
    		try {
    			// URL encode the consumer key and secret
    			String urlApiKey = URLEncoder.encode(CONSUMER_KEY, "UTF-8");
    			String urlApiSecret = URLEncoder.encode(CONSUMER_SECRET, "UTF-8");

    			// Concatenate the encoded consumer key, a colon character, and the
    			// encoded consumer secret
    			String combined = urlApiKey + ":" + urlApiSecret;

    			// Base64 encode the string
    			String base64Encoded = Base64.encodeToString(combined.getBytes(), Base64.NO_WRAP);

    			// Step 2: Obtain a bearer token
    			HttpPost httpPost = new HttpPost(TwitterTokenURL);
    			httpPost.setHeader("Authorization", "Basic " + base64Encoded);
    			httpPost.setHeader("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
    			httpPost.setEntity(new StringEntity("grant_type=client_credentials"));

    			String rawAuthorization = getResponseBody(httpPost);
    			Gson gson = new Gson();
    			Authenticated auth = gson.fromJson(rawAuthorization, Authenticated.class);//jsonToAuthenticated(rawAuthorization);

    			// Applications should verify that the value associated with the
    			// token_type key of the returned object is bearer
    			if (auth != null && auth.token_type.equals("bearer")) {

    				// Step 3: Authenticate API requests with bearer token
    				HttpGet httpGet = new HttpGet(TwitterTrendsURL);

    				// construct a normal HTTPS request and include an Authorization
    				// header with the value of Bearer <>
    				httpGet.setHeader("Authorization", "Bearer " + auth.access_token);
    				httpGet.setHeader("Content-Type", "application/json");
    				// update the results with the body of the response
    				results = getResponseBody(httpGet);
    			}
    		} 
    		catch (UnsupportedEncodingException ex) 
    		{
    		} catch (IllegalStateException ex1) {
    		}
    		return results;
    	}
    	
    	private String getResponseBody(HttpRequestBase request) {
			StringBuilder sb = new StringBuilder();
			try {

				DefaultHttpClient httpClient = new DefaultHttpClient(new BasicHttpParams());
				HttpResponse response = httpClient.execute(request);
				int statusCode = response.getStatusLine().getStatusCode();
				String reason = response.getStatusLine().getReasonPhrase();

				if (statusCode == 200) {

					HttpEntity entity = response.getEntity();
					InputStream inputStream = entity.getContent();

					BufferedReader bReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"), 8);
					String line = null;
					while ((line = bReader.readLine()) != null) {
						sb.append(line);
					}
				} else {
					sb.append(reason);
				}
			} catch (UnsupportedEncodingException ex) {
			} catch (ClientProtocolException ex1) {
			} catch (IOException ex2) {
			}
			return sb.toString();
		}

		@Override
		protected String doInBackground(String... params)
		{
			return getTwitterStream(params[0]);
		}
		
		@Override
		protected void onPostExecute(String result)
		{
			result = result.substring(1, result.length() - 1);
			Data data = new Gson().fromJson(result, Data.class);
			StringBuilder allTrends = new StringBuilder();
			for (Trend t : data.getTrends())
			{
				allTrends.append(t.name);
				allTrends.append("\n");
			}
			tweetDisplay.setText(allTrends.toString()); 
		}
    }
    
    public class Authenticated 
    {
    	String token_type;
    	String access_token;
    }
}
