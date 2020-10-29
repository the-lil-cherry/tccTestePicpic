package com.example.testepicpic.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public abstract class DayChangedBroadcastReceiver extends BroadcastReceiver {

    private Date date = new Date();
    private final DateFormat dateFormat = new SimpleDateFormat("yyMMdd", Locale.getDefault());

    public abstract void onDayChanged();

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        Date currentDate = new Date();

        if(action != null && !isSameDay(currentDate) && (action.equals(Intent.ACTION_TIME_CHANGED) ||
                action.equals(Intent.ACTION_TIMEZONE_CHANGED)
                || action.equals(Intent.ACTION_TIME_TICK))) {

            date = currentDate;

            onDayChanged();

        }

    }

    public boolean isSameDay(Date currentDate) {
        return dateFormat.format(currentDate).equals(dateFormat.format(date));
    }

    public static IntentFilter getIntentFilter() {

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        intentFilter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter.addAction(Intent.ACTION_DATE_CHANGED);

        return intentFilter;
    }

}