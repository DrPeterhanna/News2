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

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewsActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = NewsActivity.class.getName();

    private static final int NEWS_LOADER_ID = 1;
    final String BASIC_URL_SEGMENT="https://content.guardianapis.com/search";
    private static final String USGS_REQUEST_URL = "https://content.guardianapis.com/search?&show-tags=contributor&api-key=7a2c42cd-4829-4541-8a83-3aaaa2594a28";
    private static final int Article_LOADER_ID = 1;
    ProgressBar mProgressBar;
    private NewsAdapter mAdapter;
    TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news_activity);
        ListView NewsListView = (ListView) findViewById(R.id.list);

        mProgressBar = (ProgressBar) findViewById(R.id.loading_indicator);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        NewsListView.setEmptyView(mEmptyStateTextView);

        mAdapter = new NewsAdapter(this, new ArrayList<News>());

        NewsListView.setAdapter(mAdapter);

        NewsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                News currentNews = mAdapter.getItem(position);

                Uri newsUrl = Uri.parse(currentNews.getmUrl());

                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, newsUrl);

                if (websiteIntent.resolveActivity(getPackageManager()) != null) {

                    startActivity(websiteIntent);

                }
            }
        });

        // Checking the internet connection before initializing the loader
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert manager != null;
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        if(networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager  = getLoaderManager();
            loaderManager.initLoader(NEWS_LOADER_ID, null, this);

        } else {
            mProgressBar.setVisibility(View.GONE);
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }
    }

    @Override
    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {
       Uri.Builder builder = Uri.parse(BASIC_URL_SEGMENT).buildUpon();
       builder.appendQueryParameter("txq", "out:JSON");
       builder.appendQueryParameter("show-tags", "contributor");
       builder.appendQueryParameter("api-key", "7a2c42cd-4829-4541-8a83-3aaaa2594a28");
       String myUrl = builder.build().toString();
        return new NewsLoader(this, myUrl);
    }

    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> earthquakes) {
        mAdapter.clear();
        if(earthquakes == null) {
            return;
        }
        mAdapter.addAll(earthquakes);
        mProgressBar.setVisibility(View.GONE);
        mEmptyStateTextView.setText(R.string.no_news);
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }
}