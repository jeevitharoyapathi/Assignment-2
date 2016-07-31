package com.jeevitharoyapathi.assignment_2.activities;

import android.app.DialogFragment;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.jeevitharoyapathi.assignment_2.R;
import com.jeevitharoyapathi.assignment_2.adapters.NewsAdapter;
import com.jeevitharoyapathi.assignment_2.api.ApiService;
import com.jeevitharoyapathi.assignment_2.fragments.SettingsDialogFragment;
import com.jeevitharoyapathi.assignment_2.models.Article;
import com.jeevitharoyapathi.assignment_2.models.ArticleResponse;
import com.jeevitharoyapathi.assignment_2.utils.RecyclerInsetsDecoration;

import org.parceler.Parcels;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.subscriptions.CompositeSubscription;

public class NewsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener,
        SettingsDialogFragment.SettingsChangeListener {
    private static int mPage = 0;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvResult)
    RecyclerView gvResults;
    @BindView(R.id.text_empty_state_description)
    TextView mEmptyDescription;
    private CompositeSubscription mSubscription = new CompositeSubscription();
    ArrayList<Article> mArticles;
    NewsAdapter mNewsAdapter;
    private String mQuery = "education";
    private static SharedPreferences mSharedPreferences = null;
    private Call<ArticleResponse> mCall;
    private Retrofit mRetrofit;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient mClient;

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
        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, 1);
        gvResults.addItemDecoration(new RecyclerInsetsDecoration(this));
        gvResults.setLayoutManager(new StaggeredGridLayoutManager(2, 1));
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/search/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
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
                DialogFragment editDialogFragment = SettingsDialogFragment.newInstance(NewsActivity.this);
                editDialogFragment.show(getFragmentManager().beginTransaction(), "Settings");
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    private void handleArticlesObserver(ArticleResponse articles) {
        mArticles.clear();
        if (articles == null || articles.getResult() == null || articles.getResult().getArticles() == null)
            mArticles = null;
        else
            mArticles.addAll(articles.getResult().getArticles());
        if (mArticles == null || mArticles.isEmpty()) {
            mEmptyDescription.setVisibility(View.VISIBLE);
            gvResults.setVisibility(View.GONE);
        } else {
            mEmptyDescription.setVisibility(View.GONE);
            gvResults.setVisibility(View.VISIBLE);
        }
        mNewsAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(Article article, int type) {
        Intent intent = new Intent(this, NewsDetailsActivity.class);
        intent.putExtra(NewsDetailsActivity.ARTICLE, Parcels.wrap(article));
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onSettingsChanged() {
        getNewsInfo(mQuery);
    }

    public static String getDate(long date) {
        if (date == 0)
            return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        return dateFormat.format(date);
    }

    public void getNewsInfo
            (String query) {
        mSharedPreferences = getDefaultSharedPreferences();
        Set<String> set = mSharedPreferences.getStringSet("Categories", null);
        String categories = null;
        if (set != null && !set.isEmpty()) {
            List<String> cat = new ArrayList<String>(set);
            categories = "news_desk:(";
            for (String string : cat) {
                categories = categories + '"' + string + '"';
            }
            categories = categories + ")";
        }
        ApiService apiService =
                mRetrofit.create(ApiService.class);
        mCall = apiService.getArticles("a1734ed3d6684ce2b370229df108119d",
                query
                , getDate(mSharedPreferences.getLong("Date", 0))
                , categories
                , mSharedPreferences.getString("Sort", null)
                , mPage
        );
        mCall.enqueue(new Callback<ArticleResponse>() {
            @Override
            public void onResponse(final Call<ArticleResponse> call, final Response<ArticleResponse> response) {
                mArticles.clear();
                if (!response.isSuccessful()) {
                    mEmptyDescription.setVisibility(View.VISIBLE);
                    gvResults.setVisibility(View.GONE);
                    mNewsAdapter.notifyDataSetChanged();
                    return;
                }
                final ArticleResponse result = response.body();
                if (result == null || result.getResult() == null
                        || result.getResult().getArticles().isEmpty()) {
                    mEmptyDescription.setVisibility(View.VISIBLE);
                    gvResults.setVisibility(View.GONE);
                    return;
                }
                mArticles.addAll(result.getResult().getArticles());
                mEmptyDescription.setVisibility(View.GONE);
                gvResults.setVisibility(View.VISIBLE);
                mNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(final Call<ArticleResponse> call, final Throwable t) {
                Toast.makeText(NewsActivity.this, "Error Ocurred while downloading news" + call.toString(), Toast.LENGTH_SHORT).show();
                Log.e(NewsActivity.class.getSimpleName(), t.getMessage());
            }
        });
    }
}
