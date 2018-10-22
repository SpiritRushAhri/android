/*
    Andrew Fancett
    10-18-18
    Use an rssfeed to display a list of top 10 or top 25, from the apple appstore with the options given in the menu
*/

package official.kyou.top10app;

import android.os.AsyncTask;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
//import android.widget.ArrayAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ListView listApps;
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private String feedCachedUrl = "None";
    public static final String STATE_URL = "feedUrl";
    public static final String STATE_LIMIT = "feedLimit";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listApps = (ListView) findViewById(R.id.xmlListView);

        // Check for device rotation
        if(savedInstanceState != null){
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_LIMIT);
        }

        downloadUrl(String.format(feedUrl, feedLimit));

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feeds_menu, menu);
        if(feedLimit == 10) {
            menu.findItem(R.id.mnu10).setChecked(true);
        }
        else {
            menu.findItem(R.id.mnu25).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch(id){
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;
            case R.id.mnuPaid:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;
            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;
            case R.id.mnu10:
            case R.id.mnu25:
                if(!item.isChecked()){
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                }
                break;
            case R.id.mnuRefresh:
                feedCachedUrl = "None";
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        downloadUrl(String.format(feedUrl, feedLimit));
        return true;
    }

    // Save state so when device is rotated it doesn't need to redownload the xml feed
    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putString(STATE_URL, feedUrl);
        outState.putInt(STATE_LIMIT, feedLimit);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    private void downloadUrl(String feedUrl){
        // Check to see if the url is the same so we don't redownload the feed again wasting mobile data
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)){
            Log.d(TAG, "downloadUrl: starting Asynctask");
            DownloadData downloadData = new DownloadData();
            // Starts the new thread right away regardless of main activity finishing or not
            downloadData.execute(feedUrl);
            feedCachedUrl = feedUrl;
            Log.d(TAG, "downloadUrl: done");
        }
        else {
            Log.d(TAG, "downloadUrl: Url not changed");
        }

    }

    // Only main will ever use this, so no need for a separate class file
    // First parameter = Info we prove will be a String
    // Second parameter = Normally used when displaying a progress bar, in our case data is small so no time to display progress
    // Third parameter = The result we want to get back is String
    private class DownloadData extends AsyncTask<String, Void, String> {
        private static final String TAG = "DownloadData";

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
//            Log.d(TAG, "onPostExecute: parameter is " + s);
            ParseApps parseApps = new ParseApps();
            parseApps.parse(s);
            // Send apps to array adapter
            // First parameter =
            // Second parameter = resource containing the textview that the array adapter needs to put the data into
            // Third parameter = the list of objects to display
//            ArrayAdapter<RssEntry> arrayAdapter = new ArrayAdapter<>(MainActivity.this,R.layout.list_item,parseApps.getApplications());
//            listApps.setAdapter(arrayAdapter);
            FeedAdapter<RssEntry> feedAdapter = new FeedAdapter<>(MainActivity.this,R.layout.list_record,parseApps.getApplications());
            listApps.setAdapter(feedAdapter);
        }

        // Does the processing on another thread and not the main UI thread
        @Override
        protected String doInBackground(String... strings) {
//            Log.d(TAG, "doInBackground: starts with " + strings[0]);
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null) {
                // log e is an actual error and not just for debugging
                Log.e(TAG, "doInBackground: Error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlPath) {
            StringBuilder xmlResult = new StringBuilder();
            // Access a stream of data from site
            try {
                URL url = new URL(urlPath);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                int response = connection.getResponseCode();
                Log.d(TAG, "downloadXML: The response code was " + response);
                // URL exists and we are ready to read in data
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                int charsRead;
                char[] inputBuffer = new char[500];
                // Keep going until the end of the stream of data
                while(true) {
                    charsRead = reader.read(inputBuffer);
                    if(charsRead < 0) {
                        break;
                    }
                    if(charsRead > 0) {
                        xmlResult.append(String.copyValueOf(inputBuffer,0,charsRead));
                    }
                }
                reader.close();
                // Return the built string
                return xmlResult.toString();
            }
            catch (MalformedURLException e) {
                Log.e(TAG, "downloadXML: Invalid URL " + e.getMessage());
            }
            catch (IOException e) {
                Log.e(TAG, "downloadXML: IO Exception reading data: " + e.getMessage());
            }
            catch (SecurityException e) {
                Log.e(TAG, "downloadXML: Security Exception. Needs permission?" + e.getMessage());
                //e.printStackTrace();
            }
            return null;
        }
    }
}
