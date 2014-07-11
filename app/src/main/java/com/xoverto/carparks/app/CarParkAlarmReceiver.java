package com.xoverto.carparks.app;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by andrew on 11/07/2014.
 */
public class CarParkAlarmReceiver extends BroadcastReceiver {

    public static final String ACTION_REFRESH_CARPARK_ALARM = "com.xoverto.carparks.app.ACTION_REFRESH_CARPARK";

    @Override
    public void onReceive(Context context, Intent intent)  {
        Intent startIntent = new Intent(context, CarParkUpdateService.class);
        context.startService(startIntent);
    }
}
