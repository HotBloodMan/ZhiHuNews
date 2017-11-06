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
        /*
        *当数据库不存在时，系统会自动生成一个数据库
        * 第一次调用，SQLiteOpenHelper会缓存已获得的SQLiteDatabase实例。
        * 多次调用getReadXX/getWriteXX得到的都是同一个SQLiteDatabase实例。
        * */
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
        /*
        * 1 表名 2要更新的数据 3 更新的条件 4 更新需要的参数
        * */
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
        //查数据库表TABLE_NAME
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
