package com.jeevitharoyapathi.assignment_2.activities;

import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import com.jeevitharoyapathi.assignment_2.R;
import com.jeevitharoyapathi.assignment_2.adapters.NewsAdapter;
import com.jeevitharoyapathi.assignment_2.api.ApiService;
import com.jeevitharoyapathi.assignment_2.fragments.SettingsDialogFragment;
import com.jeevitharoyapathi.assignment_2.models.Article;
import com.jeevitharoyapathi.assignment_2.models.ArticleResponse;
import com.jeevitharoyapathi.assignment_2.utils.EndlessRecyclerViewScrollListener;
import com.jeevitharoyapathi.assignment_2.utils.NetworkUtils;
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

public class NewsActivity extends AppCompatActivity implements NewsAdapter.OnItemClickListener,
        SettingsDialogFragment.SettingsChangeListener {
    private static int mPage = 0;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.rvResult)
    RecyclerView gvResults;
    @BindView(R.id.text_empty_state_description)
    TextView mEmptyDescription;

    ArrayList<Article> mArticles;
    NewsAdapter mNewsAdapter;
    private String mQuery = "education";
    private static SharedPreferences mSharedPreferences = null;
    private Call<ArticleResponse> mCall;
    private Retrofit mRetrofit;

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
        gvResults.setLayoutManager(staggeredGridLayoutManager);
        mRetrofit = new Retrofit.Builder()
                .baseUrl("https://api.nytimes.com/svc/search/v2/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        gvResults.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                mPage = page;
                getNewsInfo(mQuery);
            }
        });
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
                mPage = 0;
                mArticles.clear();
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
        mArticles.clear();
        mPage=0;
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
        if (!NetworkUtils.isNetworkAvailable(this)) {
            Toast.makeText(NewsActivity.this,
                    "Network Not Available", Toast.LENGTH_SHORT).show();
            handleEmptyResult();
        }
        mSharedPreferences = getDefaultSharedPreferences();
        String categories = getCategories();
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
                if (isResultSucess(response)) {
                    final ArticleResponse result = response.body();
                    mArticles.addAll(result.getResult().getArticles());
                    mEmptyDescription.setVisibility(View.GONE);
                    gvResults.setVisibility(View.VISIBLE);
                }
                else
                {
                    handleEmptyResult();
                }
                mNewsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(final Call<ArticleResponse> call, final Throwable t) {
                Toast.makeText(NewsActivity.this, "Error Ocurred while downloading news" + call.toString(), Toast.LENGTH_SHORT).show();
                Log.e(NewsActivity.class.getSimpleName(), t.getMessage());
            }
        });
    }

    private boolean isResultSucess(Response<ArticleResponse> response) {
        if (!response.isSuccessful()) {
            return false;
        }
        final ArticleResponse result = response.body();
        if (result == null || result.getResult() == null
                || result.getResult().getArticles() == null
                || result.getResult().getArticles().isEmpty()) {
            return false;
        }
        return true;
    }

    @Nullable
    private String getCategories() {
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
        return categories;
    }

    private void handleEmptyResult() {
        mArticles.clear();
        mEmptyDescription.setVisibility(View.VISIBLE);
        gvResults.setVisibility(View.GONE);
    }
}
