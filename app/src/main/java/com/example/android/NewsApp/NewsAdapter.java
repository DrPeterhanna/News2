
package com.example.android.NewsApp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * An {@link NewsAdapter} knows how to create a list item layout for each earthquake
 * in the data source (a list of {@link News} objects).
 *
 * These list item layouts will be provided to an adapter view like ListView
 * to be displayed to the user.
 */
public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * The part of the location string from the USGS service that we use to determine
     * whether or not there is a location offset present ("5km N of Cairo, Egypt").
     */
    String timeView;
    String dateView;
    private static final String TIME_SEPARATOR = " of ";

    /**
     * Constructs a new {@link NewsAdapter}.
     *
     * @param context of the app
     * @param news is the list of news, which is the data source of the adapter
     */
    public NewsAdapter(Context context, List<News> news) {
        super(context, 0, news);
    }

    /**
     * Returns a list item view that displays information about the article at the given position
     * in the list of article.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if ( convertView == null ){
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.article_list_item, parent, false);
        }

        // Find the article at the given position in the list of article
        News currentNews = getItem(position);

        TextView title = (TextView) listItemView.findViewById(R.id.title);
        title.setText(currentNews.getmTitle());

        TextView section = (TextView) listItemView.findViewById(R.id.section_text);
        section.setText(currentNews.getmSection());

        TextView author = (TextView) listItemView.findViewById(R.id.author_text);
        author.setText(currentNews.getmAuthor());

        // Create a new Date object from the time in milliseconds of the earthquake
        String originalTime = currentNews.getmTime();

        TextView time_View = (TextView) listItemView.findViewById(R.id.date);
        String formattedDate = formatDate(originalTime);
        time_View.setText(formattedDate);

        TextView date_View = (TextView) listItemView.findViewById(R.id.time);
        String formattedTime = formatTime(originalTime);
        date_View.setText(formattedTime);

        if (originalTime.contains(TIME_SEPARATOR)) {
            String[] parts = originalTime.split(TIME_SEPARATOR);
            this.timeView = parts[0] + TIME_SEPARATOR;
            this.dateView = parts[1];
        } else {
            this.timeView = getContext().getString(R.string.time);
            this.dateView = originalTime;
        }
        // Return the list item view that is now showing the appropriate data
        return listItemView;
    }


    /**
     * Return the formatted date string (i.e. "Mar 3, 1984") from a Date object.
     */
    private String formatDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    /**
     * Return the formatted date string (i.e. "4:30 PM") from a Date object.
     */
    private String formatTime(String time) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(time);
    }
}
