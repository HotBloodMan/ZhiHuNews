package com.ljt.zhihunews.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ljt.zhihunews.R;
import com.ljt.zhihunews.support.Constants;
import com.squareup.timessquare.CalendarPickerView;

import java.util.Calendar;
import java.util.Date;

/*
* 含有日历的Activity
* */
public class PickDateActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        layoutResID=R.layout.activity_pick_date;
        super.onCreate(savedInstanceState);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Calendar nextDay = Calendar.getInstance();
        nextDay.add(Calendar.DAY_OF_YEAR,1);

        CalendarPickerView calendarPickerView = (CalendarPickerView) findViewById(R.id.calendar_view);
        assert calendarPickerView != null;
        calendarPickerView.init(Constants.Dates.birthday,nextDay.getTime())
                .withSelectedDate(Calendar.getInstance().getTime());
        calendarPickerView.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_YEAR, 1);

                Intent intent = new Intent(PickDateActivity.this, SingleDayNewsActivity.class);
                intent.putExtra(Constants.BundleKeys.DATE,
                        Constants.Dates.simpleDateFormat.format(calendar.getTime()));
                startActivity(intent);
            }
            @Override
            public void onDateUnselected(Date date) {

            }
        });
        calendarPickerView.setOnInvalidDateSelectedListener(date -> {
            if(date.after(new Date())){
                showSnackbar(R.string.not_coming);
            }else{
                showSnackbar(R.string.not_born);
            }
        });

    }
}
