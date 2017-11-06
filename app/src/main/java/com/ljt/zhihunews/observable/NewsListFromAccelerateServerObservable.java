package com.ljt.zhihunews.observable;

import android.util.Log;

import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.support.Constants;

import java.util.List;

import rx.Observable;

import static com.ljt.zhihunews.observable.Helper.getHtml;
import static com.ljt.zhihunews.observable.Helper.toNewsListObservable;

/**
 * Created by Administrator on 2017/10/6/006.
 * 业务类 从服务器拿数据
 */

public class NewsListFromAccelerateServerObservable {
    public static String TAG= NewsListFromAccelerateServerObservable.class.getSimpleName();
    public static Observable<List<DailyNews>> ofDate(String date){
        Log.d(TAG,TAG+" "+"---->>> date= "+date);
      return   toNewsListObservable(getHtml(Constants.Urls.ZHIHU_DAILY_PURIFY_BEFORE, date));
    }
}
