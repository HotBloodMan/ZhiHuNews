package com.ljt.zhihunews.observable;

import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.support.Constants;

import java.util.List;

import rx.Observable;

import static com.ljt.zhihunews.observable.Helper.getHtml;
import static com.ljt.zhihunews.observable.Helper.toNewsListObservable;

/**
 * Created by ${JT.L} on 2017/10/16.
 */

public class NewsListFromSearchObservable {
    public static Observable<List<DailyNews>> withKeyword(String keyword){
        return toNewsListObservable(getHtml(Constants.Urls.SEARCH, "q", keyword));
    }
}
