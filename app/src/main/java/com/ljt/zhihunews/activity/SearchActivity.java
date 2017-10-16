package com.ljt.zhihunews.activity;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RelativeLayout;

import com.ljt.zhihunews.R;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.fragment.SearchNewsFragment;
import com.ljt.zhihunews.observable.NewsListFromSearchObservable;
import com.ljt.zhihunews.observable.NewsListFromZhihuObservable;
import com.ljt.zhihunews.widget.IzzySearchView;

import java.util.ArrayList;
import java.util.List;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class SearchActivity extends BaseActivity implements Observer<List<DailyNews>> {

    private IzzySearchView searchView;
    private ProgressDialog dialog;

    private Subscription searchSubscription;
    private List<DailyNews> newsList = new ArrayList<>();
    private SearchNewsFragment searchNewsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initDialog();
        searchNewsFragment = new SearchNewsFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_frame,searchNewsFragment)
                .commit();
    }


    private void initView() {
        searchView = new IzzySearchView(this);
        searchView.setQueryHint(getResources().getString(R.string.search_hint));
        searchView.setOnQueryTextListener(query -> {
         dialog.show();
          searchView.clearFocus();
          searchSubscription= NewsListFromSearchObservable.withKeyword(query)
                  .observeOn(AndroidSchedulers.mainThread())
                  .subscribeOn(Schedulers.io())
                  .doOnSubscribe(this::onSubscribe)
                  .doOnUnsubscribe(this::onUnsubscribe)
                  .subscribe(this);
            return true;
        });
        RelativeLayout relative = new RelativeLayout(this);
        relative.addView(searchView);
        mToolBar.addView(relative);
        setSupportActionBar(mToolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void initDialog() {
        dialog=new ProgressDialog(SearchActivity.this);
        dialog.setMessage(getString(R.string.searching));
        dialog.setCancelable(true);
        dialog.setOnCancelListener(dialog1 -> {
            if(searchSubscription !=null && !searchSubscription.isUnsubscribed()){
                searchSubscription.unsubscribe();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        searchNewsFragment=null;
        super.onDestroy();
    }

    private void onSubscribe(){
        dialog.show();
    }
    private void onUnsubscribe(){
        dialog.dismiss();
    }

    @Override
    public void onCompleted() {
        dialog.dismiss();
        searchNewsFragment.updateContent(newsList);
    }

    @Override
    public void onError(Throwable e) {
        dialog.dismiss();
        showSnackbar(R.string.no_result_found);
    }

    @Override
    public void onNext(List<DailyNews> dailyNewses) {
        this.newsList=dailyNewses;
    }
}
