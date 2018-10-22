package official.kyou.top10app;

import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

public class ParseApps {
    private static final String TAG = "ParseApps";
    // Storing our rssfeed entry objects in arraylist
    private ArrayList<RssEntry> applications;

    public ParseApps() {
        // initialize arraylist
        this.applications = new ArrayList<>();
    }

    public ArrayList<RssEntry> getApplications() {
        return applications;
    }
    // Method to begin parsing data
    public boolean parse(String xmlData) {
        boolean status = true;
        RssEntry currentEntry = null;
        boolean inEntry = false;
        boolean getImage = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = xpp.getName();
                switch(eventType) {
                    case XmlPullParser.START_TAG:
//                        Log.d(TAG, "parse: Starting tag for " + tagName);
                        // only interested in the entry tag so once we find entry begin and create new instance of RssEntry Class
                        if("entry".equalsIgnoreCase(tagName)){
                            inEntry = true;
                            currentEntry = new RssEntry();
                        }
                        else if(("image".equalsIgnoreCase(tagName)) && inEntry){
                            String imageResolution = xpp.getAttributeValue(null,"height");
                            if(imageResolution != null){
                                getImage = "53".equalsIgnoreCase(imageResolution);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        // Store text whenever new text becomes available but it doesn't do anything with it until it hits the End Tag
                        textValue = xpp.getText();
                        break;
                    case XmlPullParser.END_TAG:
//                        Log.d(TAG, "parse: Ending tag for " + tagName);
                        // If we do get an End Tag event we check to make sure we are in an entry
                        if(inEntry){
                            if("entry".equalsIgnoreCase(tagName)){
                                applications.add(currentEntry);
                                inEntry = false;
                            }
                            // We are in so text the values for our entry, put the test case first because tagName.equals can return null
                            else if("name".equalsIgnoreCase(tagName)){
                                currentEntry.setName(textValue);
                            }
                            else if("artist".equalsIgnoreCase(tagName)){
                                currentEntry.setArtist(textValue);
                            }
                            else if("releaseDate".equalsIgnoreCase(tagName)){
                                currentEntry.setReleaseDate(textValue);
                            }
                            else if("summary".equalsIgnoreCase(tagName)){
                                currentEntry.setSummary(textValue);
                            }
                            else if("image".equalsIgnoreCase(tagName)){
                                if(getImage) {
                                    currentEntry.setImageURL(textValue);
                                }
                            }
                        }
                        break;
                    default:
                        // Nothing to do here
                }
                // Keep looping for interesting events (the stuff we need) until we hit the End Tag
                eventType = xpp.next();
            }
            // Traverse arrayList to see our data
//            for(RssEntry app: applications){
//                Log.d(TAG, "********************");
//                Log.d(TAG, app.toString());
//            }
        }
        catch (Exception e) {
            status = false;
            e.printStackTrace();
        }
        return status;
    }
}
