package com.example.mszhapa.booklistingapp;

import android.app.LoaderManager;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Book>> {

    private static final String LOG_TAG = MainActivity.class.getName();

    /**
     * URL for book data from the Google Books API
     */

    private static final String BOOK_REQUEST_URL = "https://www.googleapis.com/books/v1/volumes?q=";

    private static final int BOOK_LOADER_ID = 1;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    private String mBookParameter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        updateInfo();

        // Find a reference to the {@link ListView} in the layout
        ListView bookListView = (ListView) findViewById(R.id.list);

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);


        // Create a new adapter that takes an empty list of earthquakes as input
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        bookListView.setAdapter(mAdapter);

        Button searchButton = (Button) findViewById(R.id.button);
        EditText editText = (EditText) findViewById(R.id.editText);
        mBookParameter = editText.getText().toString().trim();

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAdapter.clear();

                updateInfo();

            }
        });
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // Find the current earthquake that was clicked on
                Book currentBook = mAdapter.getItem(position);

                // Convert the String URL into a URI object (to pass into the Intent constructor)
                Uri bookUri = Uri.parse(currentBook.getUrl());

                // Create a new intent to view the earthquake URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, bookUri);

                // Send the intent to launch a new activity
                startActivity(websiteIntent);
            }
        });
    }

    private void updateInfo() {

        EditText bookTitle = (EditText) findViewById(R.id.editText);
        String title = bookTitle.getText().toString();
        title = title.replace(" ", "+");
        String uriString = BOOK_REQUEST_URL + title;
        Bundle args = new Bundle();
        args.putString("Uri", uriString);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();

            loaderManager.initLoader(BOOK_LOADER_ID, args, MainActivity.this);
            if (loaderManager.getLoader(BOOK_LOADER_ID).isStarted()) {
                //restart it if there's one
                getLoaderManager().restartLoader(BOOK_LOADER_ID, args, MainActivity.this);
            }
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
            // Update empty state with no connection error message
            mEmptyStateTextView.setText("no esta internet");
        }
    }

    //        public void checkOnStart() {
//
//            // Get a reference to the ConnectivityManager to check state of network connectivity
//            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//            // Get details on the currently active default data network
//            final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//            // If there is a network connection, fetch data
//            if (networkInfo != null && networkInfo.isConnected()) {
//                // Get a reference to the LoaderManager, in order to interact with loaders.
//                LoaderManager loaderManager = getLoaderManager();
//
//                // Initialize the loader. Pass in the int ID constant defined above and pass in null for
//                // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
//                // because this activity implements the LoaderCallbacks interface).
//                loaderManager.initLoader(BOOK_LOADER_ID, null, MainActivity.this);
//            } else {
//                // Otherwise, display error
//                // First, hide loading indicator so error message will be visible
//                View loadingIndicator = findViewById(R.id.loading_indicator);
//                loadingIndicator.setVisibility(View.GONE);
//
//                mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
//                // Update empty state with no connection error message
//                mEmptyStateTextView.setText("no esta internet");
//            }
//        }
//
//    public void checkOnSearch() {
//
//        // Get a reference to the ConnectivityManager to check state of network connectivity
//        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        // Get details on the currently active default data network
//        final NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
//        // If there is a network connection, fetch data
//        if (networkInfo != null && networkInfo.isConnected()) {
//            // Get a reference to the LoaderManager, in order to interact with loaders.
//            LoaderManager loaderManager = getLoaderManager();
//
//            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
//            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
//            // because this activity implements the LoaderCallbacks interface).
//
//            loaderManager.restartLoader(BOOK_LOADER_ID, null, MainActivity.this);
//        } else {
//            // Otherwise, display error
//            // First, hide loading indicator so error message will be visible
//            View loadingIndicator = findViewById(R.id.loading_indicator);
//            loadingIndicator.setVisibility(View.GONE);
//
//            mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
//            // Update empty state with no connection error message
//            mEmptyStateTextView.setText("no esta internet");
//        }
//
//    }
    @Override
    public Loader<List<Book>> onCreateLoader(int i, Bundle args) {

        return new BookLoader(this, args.getString("Uri"));
    }

    @Override
    public void onLoadFinished(Loader<List<Book>> loader, List<Book> books) {
        // Hide loading indicator because the data has been loaded
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // Set empty state text to display "No earthquakes found."
        mEmptyStateTextView.setText(R.string.no_books);

        // Clear the adapter of previous earthquake data
        mAdapter.clear();

        // If there is a valid list of {@link Book}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (books != null && !books.isEmpty()) {
            mAdapter.addAll(books);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Book>> loader) {
        // Loader reset, so we can clear out our existing data.
        mAdapter.clear();

    }

}