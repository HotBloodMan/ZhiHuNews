package com.ljt.zhihunews.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ljt.zhihunews.R;
import com.ljt.zhihunews.fragment.PrefsFragment;


/*
*
* 偏好设置
* */
public class PrefsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_frame, new PrefsFragment())
                .commit();;
    }
}
