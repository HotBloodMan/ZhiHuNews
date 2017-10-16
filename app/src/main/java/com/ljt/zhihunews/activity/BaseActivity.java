package com.ljt.zhihunews.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.ljt.zhihunews.R;


/**
 * Created by Administrator on 2017/10/4/004.
 */

public class BaseActivity extends AppCompatActivity {

    public static String TAG=BaseActivity.class.getSimpleName();
    private CoordinatorLayout mCoordinatorLayout;
    protected int layoutResID= R.layout.activity_base;
    protected Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layoutResID);

        mToolBar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolBar);
        mCoordinatorLayout= (CoordinatorLayout) findViewById(R.id.coordinator_layout);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void showSnackbar(int resId){
        Snackbar.make(mCoordinatorLayout,resId,Snackbar.LENGTH_SHORT).show();
    }
}
