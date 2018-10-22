package official.kyou.top10app;

/*
    Andrew Fancett
    10-22-18
    Custom adapter to display the xml feed on the screen
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class FeedAdapter<T extends RssEntry> extends ArrayAdapter {
    private static final String TAG = "FeedAdapter";
    private final int layoutResource;
    private final LayoutInflater layoutInflater;
    private List<T> applications;

    // Constructor
    public FeedAdapter(Context context,int resource, List<T> applications) {
        super(context, resource);
        this.layoutResource = resource;
        this.layoutInflater = LayoutInflater.from(context);
        this.applications = applications;
    }

    // The adapter needs to know how many items will be put on screen
    @Override
    public int getCount() {
        return applications.size();
    }
    // When the entry is scrolled off screen get view asks for the next item to display
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        // Check if view exists so we can re-use instead of constantly creating new views filling up device memory
        if(convertView == null){
            Log.d(TAG, "getView: called with null convertView");
            convertView = layoutInflater.inflate(layoutResource,parent,false);
            // Create viewHolder object and store in convertview tag
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }
        else {
            Log.d(TAG, "getView: provided a convertView");
            // isn't null so we can retrieve the tags we set
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // Since we are inflating the view, we want to find the id that is part of that view which is our constraint layout in list_record
//        TextView tvName = convertView.findViewById(R.id.tvName);
//        TextView tvArtist = convertView.findViewById(R.id.tvArtist);
//        TextView tvSummary = convertView.findViewById(R.id.tvSummary);

        T currentApp = applications.get(position);

        // Set the corresponding textviews that show on screen
        viewHolder.tvName.setText(currentApp.getName());
        viewHolder.tvArtist.setText(currentApp.getArtist());
        viewHolder.tvSummary.setText(currentApp.getSummary());

        return convertView;
    }
    // Hold the data for the textviews
    private class ViewHolder {
        final TextView tvName;
        final TextView tvArtist;
        final TextView tvSummary;

        ViewHolder(View v) {
            this.tvName = v.findViewById(R.id.tvName);
            this.tvArtist = v.findViewById(R.id.tvArtist);
            this.tvSummary = v.findViewById(R.id.tvSummary);
        }
    }
}
