package com.ljt.zhihunews.fragment;


import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ljt.zhihunews.MyApplicaion;
import com.ljt.zhihunews.R;
import com.ljt.zhihunews.activity.BaseActivity;
import com.ljt.zhihunews.adapter.NewsAdapter;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.observable.NewsListFromAccelerateServerObservable;
import com.ljt.zhihunews.observable.NewsListFromDatabaseObservable;
import com.ljt.zhihunews.observable.NewsListFromZhihuObservable;
import com.ljt.zhihunews.support.Constants;
import com.ljt.zhihunews.task.SaveNewsListTask;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewsListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener
,Observer<List<DailyNews>> {

    public static String TAG=NewsListFragment.class.getSimpleName();
    private String date;
    private List<DailyNews> newsList=new ArrayList<>();

    private boolean isToday;
    private boolean isRefreshed=false;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private NewsAdapter newsAdapter;

    public NewsListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,TAG+" "+"---->>>onCreate ");
        if(savedInstanceState==null){
            Bundle bundle = getArguments();
            date = bundle.getString(Constants.BundleKeys.DATE);
            isToday=bundle.getBoolean(Constants.BundleKeys.IS_FIRST_PAGE);
        //旋转时 Fragment 不需要重新创建
            setRetainInstance(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG,TAG+" "+"---->>>onCreateView ");
        View view = inflater.inflate(R.layout.fragment_news_list, container, false);
        assert  view !=null;
        RecyclerView mRecyclerView = (RecyclerView) view.findViewById(R.id.news_list);
        mRecyclerView.setHasFixedSize(!isToday);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(llm);
        Log.d(TAG,TAG+" onCreateView() "+"---->>> newsList="+newsList);
        newsAdapter = new NewsAdapter(newsList);
        mRecyclerView.setAdapter(newsAdapter);

        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.color_primary);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG,TAG+" "+"---->>>onResume() ");
        NewsListFromDatabaseObservable.ofDate(date)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        Log.d(TAG,TAG+" "+"---->>>setUserVisibleHint() ");
        refreshIf(shouldRefreshOnVisibilityChange(isVisibleToUser));
    }

    private void refreshIf(boolean prerequisite) {
        if(prerequisite){
            Log.d(TAG,TAG+" "+"---->>>refreshIf ");
            doRefresh();
        }
    }

    private boolean shouldRefreshOnVisibilityChange(boolean isVisibleToUser) {
        Log.d(TAG,TAG+" "+"---->>>shouldRefreshOnVisibilityChange ");
        return isVisibleToUser && shouldAutoRefresh() && !isRefreshed;
    }

    private boolean shouldAutoRefresh() {
        Log.d(TAG,TAG+" "+"---->>>shouldAutoRefresh ");
        return MyApplicaion.getSharedPreferences()
                .getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_AUTO_REFRESH, true);
    }

    @Override
    public void onRefresh() {
        Log.d(TAG,TAG+" "+"---->>>onRefresh ");
        doRefresh();
    }

    private void doRefresh() {
        Log.d(TAG,TAG+" "+"---->>>doRefresh ");
        getNewsListObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this);
        if(mSwipeRefreshLayout !=null){
            mSwipeRefreshLayout.setRefreshing(true);
        }
    }
    private Observable<List<DailyNews>> getNewsListObservable(){
        Log.d(TAG,TAG+" "+"---->>>getNewsListObservable ");
        if(shouldSubscribeToZhihu()){
            Log.d(TAG,TAG+" getNewsListObservable"+"---->>>111 ");
            return NewsListFromZhihuObservable.ofDate(date);
        }else{
            Log.d(TAG,TAG+" getNewsListObservable"+"---->>>222 ");
            return NewsListFromAccelerateServerObservable.ofDate(date);
        }
    }
    private boolean shouldSubscribeToZhihu(){
        Log.d(TAG,TAG+" "+"---->>>shouldSubscribeToZhihu ");
        return isToday || !shouldUseAccelerateServer();
    }
    private boolean shouldUseAccelerateServer() {
        Log.d(TAG,TAG+" "+"---->>>shouldUseAccelerateServer ");
        return MyApplicaion.getSharedPreferences()
                .getBoolean(Constants.SharedPreferencesKeys.KEY_SHOULD_USE_ACCELERATE_SERVER, false);
    }

    @Override
    public void onNext(List<DailyNews> dailyNewses) {
        Log.d(TAG,TAG+" onNext()"+"---->>>dailyNewses= "+dailyNewses);
        this.newsList=dailyNewses;
    }

    @Override
    public void onCompleted() {
        Log.d(TAG,TAG+" "+"---->>>onCompleted() ");
        isRefreshed=true;

        mSwipeRefreshLayout.setRefreshing(false);
        newsAdapter.updateNewsList(newsList);
        new SaveNewsListTask(newsList);
    }

    @Override
    public void onError(Throwable e) {
        Log.d(TAG,TAG+" "+"---->>>onError(Throwable e) ");
        mSwipeRefreshLayout.setRefreshing(false);
        if(isAdded()){
            ((BaseActivity)getActivity()).showSnackbar(R.string.network_error);
        }
    }
}
