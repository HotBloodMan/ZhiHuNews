package com.ljt.zhihunews.observable;

import android.util.Log;

import com.ljt.zhihunews.MyApplicaion;
import com.ljt.zhihunews.bean.DailyNews;

import java.util.List;

import rx.Observable;

/**
 * Created by Administrator on 2017/10/6/006.
 */

public class NewsListFromDatabaseObservable {
    public static String TAG=NewsListFromDatabaseObservable.class.getSimpleName();
    public static Observable<List<DailyNews>> ofDate(String date){
        Log.d(TAG,TAG+" "+"---->>>date "+date);
        return Observable.create(subscriber -> {
            List<DailyNews> newsList = MyApplicaion.getDataSource().newsOfTheDay(date);
            if(newsList !=null){
                Log.d(TAG,TAG+" "+"---->>>newsList= "+newsList.size());
                subscriber.onNext(newsList);
            }
            subscriber.onCompleted();
        });
    }
}
