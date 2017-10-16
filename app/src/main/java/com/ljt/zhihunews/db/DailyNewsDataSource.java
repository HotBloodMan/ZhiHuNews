package com.ljt.zhihunews.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.gson.GsonBuilder;
import com.ljt.zhihunews.bean.DailyNews;
import com.ljt.zhihunews.support.Constants;

import java.util.List;

import static android.R.attr.data;

/**
 * Created by Administrator on 2017/10/5/005.
 */

public final class DailyNewsDataSource {
    private SQLiteDatabase database;
    private DBHelper dbHelper;
    private String[] allColumns={
            DBHelper.COLUMN_ID,
            DBHelper.COLUMN_DATE,
            DBHelper.COLUMN_CONTENT
    };

    public DailyNewsDataSource(Context context) {
       dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException{
        database=dbHelper.getWritableDatabase();
    }
    public List<DailyNews> insertDailyNewList(String date,String content){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DATE,date);
        values.put(DBHelper.COLUMN_CONTENT,content);

        long insertId = database.insert(DBHelper.TABLE_NAME, null, values);
        Cursor cursor = database.query(DBHelper.TABLE_NAME,
                allColumns, DBHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToNext();
        List<DailyNews> newList = cursorToNewsList(cursor);
        cursor.close();
        return newList;
    }

    public void updateNewsList(String date,String content){
        ContentValues values = new ContentValues();
        values.put(DBHelper.COLUMN_DATE,date);
        values.put(DBHelper.COLUMN_CONTENT,content);
        database.update(DBHelper.TABLE_NAME,values
                ,DBHelper.COLUMN_DATE+"="+date,null);
    }
    public void insertOrUpdateNewsList(String date,String content){
        if(newsOfTheDay(date) !=null){
            updateNewsList(date,content);
        }else{
            insertDailyNewList(date,content);
        }
    }
    public List<DailyNews> newsOfTheDay(String date){
        Cursor cursor=database.query(DBHelper.TABLE_NAME,
                allColumns,DBHelper.COLUMN_DATE+" = "+data,null,null,null,null);
        cursor.moveToFirst();
        List<DailyNews> newsList = cursorToNewsList(cursor);
        cursor.close();
        return newsList;
    }
    private List<DailyNews> cursorToNewsList(Cursor cursor){
        if(cursor !=null && cursor.getCount() >0){
           return new GsonBuilder().create()
                   .fromJson(cursor.getString(2)
                           , Constants.Types.newsListType);
        }else{
            return null;
        }
    }

}
