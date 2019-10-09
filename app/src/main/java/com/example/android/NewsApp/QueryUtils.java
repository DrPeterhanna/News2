/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.NewsApp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@SuppressWarnings("ALL")
class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getName();
    @SuppressLint("StaticFieldLeak")
    private static Context context;

    // News API keys
    private static final String API_KEY_RESPONSE = "response";
    private static final String API_KEY_RESULTS = "results";
    private static final String API_KEY_SECTION = "sectionName";
    private static final String API_KEY_PUBLISHED_DATE = "webPublicationDate";
    private static final String API_KEY_TITLE = "webTitle"; // same key used for news title and author name
    private static final String API_KEY_WEBURL = "webUrl";
    private static final String API_KEY_TAGS = "tags";


    /**
     * This is a private constructor and only meant to hold static variables and methods,
     * which can be accessed directly from the class name Utils
     */
    private QueryUtils() {
    }

    /**
     * Query the USGS dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchArticleData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> news = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return news;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }


    /**
     * Return a list of {@link News} objects retrieved from parsing a JSON response.
     */
    private static List<News> extractFeatureFromJson(String newsJSON) {

        if (TextUtils.isEmpty(newsJSON)) {
            return null;
        }

        List<News> newsItems = new ArrayList<>();

        try {

            JSONObject baseJsonResponse;            // JSON Object for the data retrieved from API request
            JSONObject jsonResults;                 // JSON results fetched
            JSONArray newsArray;                    // Array of News Items
            JSONObject currentNewsItem;             // JSON object for current news item in the newsArray
            String newsTitle = "";                  // News Title
            String newsSection = "";                // News Section
            String newsDate = "";                   // Published Date
            String newsUrl = "";                    // Web URL of the news item
            JSONArray tagsArray;                    // Array of tags
            JSONObject newsTag;                     // JSON Object for news tags - first element in tagsArray
            String newsAuthor = "";                 // Author of the news item - obtained from newsTags


            baseJsonResponse = new JSONObject(newsJSON);
            jsonResults = baseJsonResponse.getJSONObject(API_KEY_RESPONSE);

            if (jsonResults.has(API_KEY_RESULTS)) {
                newsArray = jsonResults.getJSONArray(API_KEY_RESULTS);

                for (int i = 0; i < newsArray.length(); i++) {
                    currentNewsItem = newsArray.getJSONObject(i);

                    if (currentNewsItem.has(API_KEY_TITLE)) {
                        newsTitle = currentNewsItem.getString(API_KEY_TITLE);
                    }

                    if (currentNewsItem.has(API_KEY_SECTION)) {
                        newsSection = currentNewsItem.getString(API_KEY_SECTION);
                    }

                    if (currentNewsItem.has(API_KEY_PUBLISHED_DATE)) {
                        newsDate = currentNewsItem.getString(API_KEY_PUBLISHED_DATE);
                    }

                    if (currentNewsItem.has(API_KEY_WEBURL)) {
                        newsUrl = currentNewsItem.getString(API_KEY_WEBURL);
                    }

                    if (currentNewsItem.has(API_KEY_TAGS)) {
                        tagsArray = currentNewsItem.getJSONArray(API_KEY_TAGS);

                        if (tagsArray.length() > 0) {
                            for (int j = 0; j < 1; j++) {
                                newsTag = tagsArray.getJSONObject(j);
                                if (newsTag.has(API_KEY_TITLE)) {
                                    newsAuthor = newsTag.getString(API_KEY_TITLE);
                                }
                            }
                        }
                    }

                    // Create a new {@link NewsItem} object with parameters obtained from JSON response
                    News newsItem = new News(
                            newsTitle,
                            newsSection,
                            newsDate,
                            newsAuthor,
                            newsUrl
                    );

                    // Add the new {@link NewsItem} object to the list of news items
                    newsItems.add(newsItem);
                }
            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the Article JSON results", e);
        }

        // Return the list of newsItems
        return newsItems;
    }
}