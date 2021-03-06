package com.ljt.zhihunews.activity;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ljt.zhihunews.R;
import com.ljt.zhihunews.fragment.NewsListFragment;
import com.ljt.zhihunews.support.Constants;

import java.text.DateFormat;
import java.util.Calendar;

public class MainActivity extends BaseActivity {
    private static final int PAGE_COUNT = 7;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID=R.layout.activity_main;
        super.onCreate(savedInstanceState);

        TabLayout tabs = (TabLayout) findViewById(R.id.main_pager_tabs);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_pager);
        assert tabs != null;
        assert viewPager != null;
        /*setOffscreenPageLimit 方法设置的默认值是1.这个设置的值有两层含义:
         一是 ViewPager 会预加载几页; 二是 ViewPager 会缓存 2*n+1 页(n为设置的值).*/
        viewPager.setOffscreenPageLimit(PAGE_COUNT);

        MainPagerAdapter adapter = new MainPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setupWithViewPager(viewPager);

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab_pick_date);
        assert floatingActionButton != null;
        floatingActionButton.setOnClickListener(v->preparIntent(PickDateActivity.class));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_settings:
                return preparIntent(PrefsActivity.class);
            case R.id.action_go_to_search:
                return preparIntent(SearchActivity.class);
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean preparIntent(Class clazz){
        startActivity(new Intent(MainActivity.this,clazz));
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main,menu);
        return true;
    }

    /*
    * PagerAdapter 的实现将只保留当前页面，当页面离开视线后，就会被消除，
    * 释放其资源；而在页面需要显示时，生成新的页面(就像 ListView 的实现一样)。
    * 这么实现的好处就是当拥有大量的页面时，不必在内存中占用大量的内存。
    * */
    private class MainPagerAdapter extends FragmentStatePagerAdapter{

        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /*
        * 生成新的 Fragment 对象
        * */
        @Override
        public Fragment getItem(int i) {
            Bundle bundle = new Bundle();
            Fragment newsFragment = new NewsListFragment();

            Calendar dateToGetUrl = Calendar.getInstance();
            dateToGetUrl.add(Calendar.DAY_OF_YEAR,1 - i);
            String date = Constants.Dates.simpleDateFormat.format(dateToGetUrl.getTime());
            Log.d(TAG,TAG+" "+"---->>>date= "+date);

            bundle.putString(Constants.BundleKeys.DATE,date);
            bundle.putBoolean(Constants.BundleKeys.IS_FIRST_PAGE,i == 0);
            bundle.putBoolean(Constants.BundleKeys.IS_SINGLE,false);

            newsFragment.setArguments(bundle);
            return newsFragment;
        }

        @Override
        public int getCount() {
            return PAGE_COUNT;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Calendar displayDate = Calendar.getInstance();
            displayDate.add(Calendar.DAY_OF_YEAR,-position);
            return  (position == 0 ? getString(R.string.zhihu_daily_today) + " " : "")
                    + DateFormat.getDateInstance().format(displayDate.getTime());
        }
    }

}
