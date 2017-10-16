package com.ljt.zhihunews;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.ljt.zhihunews.db.DailyNewsDataSource;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

/**
 * Created by Administrator on 2017/10/5/005.
 */

public class MyApplicaion extends Application {
public static String TAG= MyApplicaion.class.getSimpleName();
    private static MyApplicaion applicaion;
    private static DailyNewsDataSource dataSource;
    public static MyApplicaion getInstance(){
        return applicaion;
    };
    public static DailyNewsDataSource getDataSource(){
        return dataSource;
    }

     public static void initImageLoader(Context context){
         ImageLoaderConfiguration config=new ImageLoaderConfiguration.Builder(context)
                 .denyCacheImageMultipleSizesInMemory()
                 .threadPriority(Thread.NORM_PRIORITY-2)
                 .diskCacheFileNameGenerator(new Md5FileNameGenerator())
                 .tasksProcessingOrder(QueueProcessingType.LIFO)
                 .build();
         ImageLoader.getInstance().init(config);
     }
    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG,TAG+" "+"---->>>onCreate() ");
        applicaion=this;

        initImageLoader(getApplicationContext());

        dataSource = new DailyNewsDataSource(getApplicationContext());
        dataSource.open();
    }

    public static SharedPreferences getSharedPreferences(){
        return PreferenceManager.getDefaultSharedPreferences(applicaion);
    }

}
