package com.mg.cuco;

import android.support.v4.content.WakefulBroadcastReceiver;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import android.content.SharedPreferences;

import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by Michael on 5/24/2015.
 */
public class DeviceBootReceiver extends BroadcastReceiver {

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    int hrInterval, minInterval, savedhour, savedminute;
    String txtTime;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */

            SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, context.MODE_PRIVATE);
            hrInterval = prefs.getInt("HourInteger", 1);
            minInterval = prefs.getInt("MinuteInteger",1);
            //savedhour = prefs.getInt("savedhour",0);
            //savedminute = prefs.getInt("savedminute",5);
            txtTime = prefs.getString("DailyTime",null);

            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            String settime = txtTime;

            Pattern p = Pattern.compile("(\\d+):(\\d+)");

            Matcher m = p.matcher(settime);

            if (m.find()) {
                savedhour = Integer.parseInt(m.group(1));
                savedminute = Integer.parseInt(m.group(2));
            }

            Toast.makeText(context,"V2",Toast.LENGTH_LONG).show();

            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(System.currentTimeMillis());
            calendar.set(Calendar.HOUR_OF_DAY, savedhour);
            calendar.set(Calendar.MINUTE, savedminute);
            calendar.set(Calendar.SECOND, 0);

            //Toast.makeText(this, String.valueOf(calendar.getTimeInMillis()),Toast.LENGTH_LONG).show();
            //Toast.makeText(context, "Time: " + calendar.getTimeInMillis(), Toast.LENGTH_LONG).show();
            //Toast.makeText(context, savedhour + " " + savedminute + " " + hrInterval + " " + minInterval,Toast.LENGTH_LONG).show();

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = hrInterval*60*60*1000 + minInterval*60*1000;
            manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), interval, pendingIntent);





        }

    }


}
