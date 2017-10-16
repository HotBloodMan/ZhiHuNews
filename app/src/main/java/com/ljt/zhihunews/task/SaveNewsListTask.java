package com.ljt.zhihunews.task;

import android.os.AsyncTask;

import com.google.gson.GsonBuilder;
import com.ljt.zhihunews.MyApplicaion;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.db.DailyNewsDataSource;

import java.util.List;

/**
 * Created by Administrator on 2017/10/7/007.
 */

public class SaveNewsListTask extends AsyncTask<Void,Void,Void>{

   private List<DailyNews> newsList;

    public SaveNewsListTask(List<DailyNews> newsList) {
        this.newsList = newsList;
    }

    @Override
    protected Void doInBackground(Void... params) {
       if(newsList !=null && newsList.size()>0){

       }
        return null;
    }
    private void saveNewsList(List<DailyNews> newList){
        DailyNewsDataSource dataSource = MyApplicaion.getDataSource();
        String date = newList.get(0).getDate();

        List<DailyNews> originalData = dataSource.newsOfTheDay(date);
        if(originalData==null || ! originalData.equals(newList)){
            dataSource.insertOrUpdateNewsList(date,new GsonBuilder().create().toJson(newsList));
        }
    }
}
