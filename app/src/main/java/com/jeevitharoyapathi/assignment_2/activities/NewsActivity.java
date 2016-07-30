package com.jeevitharoyapathi.assignment_2.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import com.jeevitharoyapathi.assignment_2.R;
import com.jeevitharoyapathi.assignment_2.adapters.ArticleArrayAdapter;
import com.jeevitharoyapathi.assignment_2.models.Article;
import com.jeevitharoyapathi.assignment_2.models.SearchModel;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class NewsActivity extends AppCompatActivity {

    public static final String SEARCH = "search";
    public static final int SEARCH_REQUEST = 121;
    public static final String TAG = NewsActivity.class.getSimpleName();
    String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    EditText etQuery;
    GridView gvResults;
    Button btnSearch;

    ArrayList<Article> articles;
    ArticleArrayAdapter articleArrayAdapter;
    SearchModel mSearchModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setLogo(R.drawable.nytimes);
        setSupportActionBar(toolbar);
        setUpViews();

    }

    public void setUpViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (GridView) findViewById(R.id.gvResults);
        btnSearch = (Button) findViewById(R.id.btnSearch);
        articles = new ArrayList<>();
        articleArrayAdapter = new ArticleArrayAdapter(this, articles);
        gvResults.setAdapter(articleArrayAdapter);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                // create an intent to display the article
//                Intent intent = new Intent(getApplicationContext(), ArticleActivity.class);
//                //get article to display
//                Article article = (Article) parent.getItemAtPosition(position);
//                //pass article into intent
//                intent.putExtra("url", article.getWebUrl());
//                //launch the activity
//                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    mSearchModel = data.getParcelableExtra(SEARCH);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // perform query here

                // workaround to avoid issues with some emulators and keyboard devices firing twice if a keyboard enter is used
                // see https://code.google.com/p/android/issues/detail?id=24599
                searchView.clearFocus();

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItem filterItem = menu.findItem(R.id.settings);
        filterItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(getApplicationContext(), "Filter item clicked", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivityForResult(intent, SEARCH_REQUEST);
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    public void onArticleSearch(View view) {
        String query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL;
        RequestParams params = new RequestParams();
        params.put("api-key", "a1734ed3d6684ce2b370229df108119d");
        params.put("page", 0);
        params.put("q", query);
        if (mSearchModel != null) {
            params.put("begin_date", mSearchModel.getBeginDate());
            if (mSearchModel.getCategories() != null && !mSearchModel.getCategories().isEmpty()) {
                String categories = "";
                for (String string : mSearchModel.getCategories()) {
                    categories = categories + '"' + string + '"';
                }

            }
            params.put("sort", mSearchModel.getSortOrder());
        }
        Log.d(TAG, "URL sent is - " + url);
        client.get(url, params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                JSONArray articleJsonResults = null;
                try {
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    articleArrayAdapter.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        });

    }
}
