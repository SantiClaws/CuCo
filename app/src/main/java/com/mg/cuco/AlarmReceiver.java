package com.mg.cuco;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.EditText;
import android.widget.Spinner;
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
 * Created by Michael on 5/20/2015.
 */

public class AlarmReceiver extends WakefulBroadcastReceiver {




    @Override
    public void onReceive(Context context, Intent intent) {



        Log.i("WTF", "wtf why is it running?");
        Intent service = new Intent(context, UpdateCurrencyService.class);
        startWakefulService(context, service);


    }


}