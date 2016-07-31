package com.jeevitharoyapathi.assignment_2.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.jeevitharoyapathi.assignment_2.R;
import com.jeevitharoyapathi.assignment_2.adapters.NewsAdapter;
import com.jeevitharoyapathi.assignment_2.fragments.SettingsDialogFragment;
import com.jeevitharoyapathi.assignment_2.models.Article;
import com.jeevitharoyapathi.assignment_2.utils.RecyclerInsetsDecoration;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import cz.msebera.android.httpclient.Header;

public class NewsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener,
        SettingsDialogFragment.SettingsChangeListener {

    public static final String SEARCH = "search";
    public static final int SEARCH_REQUEST = 121;
    public static final String TAG = NewsActivity.class.getSimpleName();
    String BASE_URL = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvResult)
    RecyclerView gvResults;
    @BindView(R.id.text_empty_state_description)
    TextView mEmptyDescription;

    ArrayList<Article> mArticles;
    NewsAdapter mNewsAdapter;
    private String mQuery = "Today's News";
    private static SharedPreferences mSharedPreferences = null;

    @Override
    public boolean navigateUpTo(Intent upIntent) {
        return super.navigateUpTo(upIntent);
    }

    ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);
        setSupportActionBar(mToolbar);
        setUpViews();
    }

    public void setUpViews() {
        mArticles = new ArrayList<>();
        mSharedPreferences = getDefaultSharedPreferences();
        mNewsAdapter = new NewsAdapter(this, mArticles);
        mNewsAdapter.setOnClickListener(this);
        gvResults.setAdapter(mNewsAdapter);
        gvResults.addItemDecoration(new RecyclerInsetsDecoration(this));
        gvResults.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        getNewsInfo(mQuery);

    }

    private SharedPreferences getDefaultSharedPreferences() {
        if (mSharedPreferences == null) {
            mSharedPreferences = getSharedPreferences(SettingsDialogFragment.SETTINGS, Context.MODE_PRIVATE);
        }
        return mSharedPreferences;
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
                searchView.clearFocus();
                mQuery = query;
                getNewsInfo(query);
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
                android.app.DialogFragment editDialogFragment = SettingsDialogFragment.newInstance(NewsActivity.this);
                editDialogFragment.show(getFragmentManager().beginTransaction(), "Settings");
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void getNewsInfo(String query) {
        AsyncHttpClient client = new AsyncHttpClient();
        String url = BASE_URL;

        RequestParams params = new RequestParams();
        mSharedPreferences = getDefaultSharedPreferences();
        params.put("api-key", "a1734ed3d6684ce2b370229df108119d");
        params.put("page", 0);
        params.put("q", query);
        if (mSharedPreferences.getLong("Date", 0) != 0)
            params.put("begin_date", getDate(mSharedPreferences.getLong("Date", 0)));
        Set<String> set = mSharedPreferences.getStringSet("Categories", null);
        if (set != null && !set.isEmpty()) {
            List<String> cat = new ArrayList<String>(set);
            String categories = "news_desk:(";
            for (String string : cat) {
                categories = categories + '"' + string + '"';
            }
            categories = categories + ")";
            params.put("fq", categories);
        }

        params.put("sort", mSharedPreferences.getString("Sort", "Oldest"));
        client.get(url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try {
                    mArticles.clear();
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    mArticles.addAll(Article.fromJSONArray(articleJsonResults));
                    if (mArticles.isEmpty()) {
                        mEmptyDescription.setVisibility(View.VISIBLE);
                        gvResults.setVisibility(View.GONE);
                    } else {
                        mEmptyDescription.setVisibility(View.GONE);
                        gvResults.setVisibility(View.VISIBLE);
                    }
                    mNewsAdapter.notifyDataSetChanged();
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

    @Override
    public void onItemClick(Article article, int type) {
        Intent intent = new Intent(this, NewsDetailsActivity.class);
        startActivity(intent);

    }

    @Override
    public void onSettingsChanged() {
        getNewsInfo(mQuery);
    }

    public static String getDate(long date) {
        if (date == 0)
            return "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }
}
