package com.mg.cuco;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * Created by Michael on 5/21/2015.
 */
public class UpdateCurrencyService extends IntentService {

        public UpdateCurrencyService() {
        super("UpdateCurrencyService");
    }

    public static final String MY_PREFS_NAME = "MyPrefsFile";
    String exchange3;
    String fromCurrency2;
    String toCurrency2;
    String result2;

    //Intent Service has own context

    @Override
    protected void onHandleIntent(Intent workIntent) {

        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString



        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Boolean checked = prefs.getBoolean("CheckBox", false);
        exchange3 = prefs.getString("ExchangeAmount", null);
        fromCurrency2 = prefs.getString("FromCurrency", null);
        toCurrency2 = prefs.getString("ToCurrency", null);

        if (!checked){
            return;
        }


        String link = "https://www.google.com/finance/converter?a=" + exchange3 + "&from=" + fromCurrency2 + "&to=" + toCurrency2;

        String result = getWebsite(link);

        Pattern p = Pattern.compile("class=bld>(\\d+.\\d+)");

        Matcher m = p.matcher(result);

        if (m.find()) {
            result2 = m.group(1);

            /*
            Handler mHandler = new Handler(getMainLooper());
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(UpdateCurrencyService.this, exchange3 + " " + fromCurrency2 + " = " + result2 + " " + toCurrency2, Toast.LENGTH_SHORT).show();
                }
            });
            */


            NotificationCompat.Builder noti = new NotificationCompat.Builder(this)
                    .setContentTitle("Daily Currency Update")
                    .setContentText(exchange3 + " " + fromCurrency2 + " = " + result2 + " " + toCurrency2)
                    .setSmallIcon(R.mipmap.ic_launcher);
                    //.setContentIntent(pIntent);

            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

            // hide the notification after its selected
            noti.setAutoCancel(true);

            PendingIntent pIntent =
                    PendingIntent.getActivity(getApplicationContext(), 0, new Intent(), 0);
            noti.setContentIntent(pIntent);

            Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            noti.setSound(alarmSound);

            noti.setDefaults(Notification.DEFAULT_VIBRATE);

            notificationManager.notify(0, noti.build());


            Log.i("matching", m.group(1));

        }

         else {
            Toast.makeText(UpdateCurrencyService.this, "Unexpected error", Toast.LENGTH_SHORT).show();
            return;
        }


        AlarmReceiver.completeWakefulIntent(workIntent);
    }


    private String getWebsite(String address) {

        StringBuffer stringBuffer = new StringBuffer();

        BufferedReader reader = null;

        try {
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";

            while ((line = reader.readLine()) != null) {
                stringBuffer.append(line);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuffer.toString();

    }



}
