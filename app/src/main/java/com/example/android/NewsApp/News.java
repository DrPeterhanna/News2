
package com.example.android.NewsApp;

/**
 * An {@link News} object contains information related to a single Article.
 */
public class News {

    /** title of the Article */
    private String mTitle;

    /** Location of the Article */
    private String mSection;

    /** publication Time of the Article */
    private String mTime;

    /** Website URL of the Article */
    private String mUrl;

    /** Website author of the Article */
    private String mAuthor;


    /**
     * Constructs a new {@link News} object.
     *
     * @param title  of the Article
     * @param author  of the Article
     * @param section Article section
     * @param time Article time
     * @param url is the website URL to find more details about the Article
     */
    public News(String title, String section, String time, String url, String author) {
        mTitle = title;
        mSection = section;
        mTime = time;
        mUrl = url;
        mAuthor = author;
    }

    /**
     * Returns the Title of the Article.
     */
    public String getmTitle() {
        return mTitle;
    }

    /**
     * Returns the section of the Article.
     */
    public String getmSection() {
        return mSection;
    }

    /**
     * Returns the time of the earthquake.
     */
    public String getmTime() {
        return mTime;
    }

    /**
     * Returns the website URL to find more information about the earthquake.
     */
    public String getmUrl() {
        return mUrl;
    }

    public String getmAuthor() {
        return mAuthor;
    }
}
