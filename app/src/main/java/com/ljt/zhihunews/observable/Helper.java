package com.ljt.zhihunews.observable;

import android.text.Html;
import android.util.Log;

import com.annimon.stream.Optional;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.support.Constants;
import com.ljt.zhihunews.support.lib.Http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOError;
import java.io.IOException;
import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Administrator on 2017/10/6/006.
 */

public class Helper {
    public static String TAG= Helper.class.getSimpleName();
    private Helper(){}

    static Observable<String> getHtml(String url) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(url));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }
/*
*
* 将url加上时间的前缀---》编码
* */
    static Observable<String> getHtml(String url, String suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                     Log.d("NewsListFragment",TAG+"  getHtml(----->>>url= "+url.toString());

                    String s = Http.get(url, suffix);
                    Log.d("NewsListFragment",TAG+" Helper getHtml(----->>> suffix s= "+s.toString());
                    subscriber.onNext(Http.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String url, int suffix) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(url, suffix));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static Observable<String> getHtml(String baseUrl, String key, String value) {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    subscriber.onNext(Http.get(baseUrl, key, value));
                    subscriber.onCompleted();
                } catch (IOException e) {
                    subscriber.onError(e);
                }
            }
        });
    }

    static <T> Observable<T> toNonempty(Observable<Optional<T>> optionalObservable) {
         Log.d(TAG,TAG+" ------------------>>> toNonempty( ");
        return optionalObservable.filter(Optional::isPresent).map(Optional::get);
    }

    static Observable<List<DailyNews>> toNewsListObservable(Observable<String> htmlObservable) {
        return htmlObservable
                .map(Helper::decodeHtml)
                .flatMap(Helper::toJSONObject)
                .flatMap(Helper::getDailyNewsJSONArray)
                .map(Helper::reflectNewsListFromJSON);
    }

    private static Observable<JSONObject> toJSONObject(String data) {
        return Observable.create(subscriber -> {
            try {
                 Log.d(TAG,TAG+"toJSONObject( ----->>> data="+data);
                subscriber.onNext(new JSONObject(data));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static Observable<JSONArray> getDailyNewsJSONArray(JSONObject dailyNewsJsonObject) {
        return Observable.create(subscriber -> {
            try {
                 Log.d(TAG,TAG+"getDailyNewsJSONArray( ----->>>dailyNewsJsonObject= "+
                         dailyNewsJsonObject);
                subscriber.onNext(dailyNewsJsonObject.getJSONArray("news"));
                subscriber.onCompleted();
            } catch (JSONException e) {
                subscriber.onError(e);
            }
        });
    }

    private static List<DailyNews> reflectNewsListFromJSON(JSONArray newsListJsonArray) {
         Log.d(TAG,TAG+"reflectNewsListFromJSON( ----->>>newsListJsonArray= "+
         newsListJsonArray);
        Gson gson = new GsonBuilder().create();
        return gson.fromJson(newsListJsonArray.toString(), Constants.Types.newsListType);
    }

    private static String decodeHtml(String in) {
         Log.d(TAG,TAG+"decodeHtml( ----->>> in= "+in);
        return Html.fromHtml(Html.fromHtml(in).toString()).toString();
    }
}
