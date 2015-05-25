package com.mg.cuco; /**
 * Created by Michael on 5/17/2015.
 */


import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.content.SharedPreferences.Editor;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.app.AlarmManager;
import android.app.PendingIntent;


public class Notifications extends ActionBarActivity {

    Button btnTimePicker;
    TextView txtTime;
    public static final String MY_PREFS_NAME = "MyPrefsFile";
    private int mHour, mMinute, savedhour, savedminute;
    int hourOfDay;
    int minute;
    int hrInterval;
    int minInterval;
    int iCurrentSelectionF, iCurrentSelectionT;
    String beforeStringEx, beforeStringTime, beforeHrInt, beforeMinInt;


    //because the spinners and amount boxes can be changed at anytime, these strings NEED to be reupdated every time an event is triggered

    //findviewbyid TCurr and FCurr spinners can't be here since they aren't fully created/defined

    Spinner TCurr2;
    Spinner FCurr2;
    EditText exchangetwo;


    String exchange3;
    String fromCurrency2;
    String toCurrency2;

    private PendingIntent pendingIntent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.activity_notifications);





        btnTimePicker = (Button) findViewById(R.id.btnTimePicker);


        txtTime = (TextView) findViewById(R.id.txtTime);


        String[] country = {"CAD", "USD", "EUR", "CNY", "JPY", "GBP", "HKD", "BTC"};



        Spinner TCurr2 = (Spinner) findViewById(R.id.TCurr2);
        Spinner FCurr2 = (Spinner) findViewById(R.id.FCurr2);
        EditText exchangetwo = (EditText) findViewById(R.id.exchangetwo);
        final EditText hrInt = (EditText) findViewById(R.id.hrInt);
        final EditText minInt = (EditText) findViewById(R.id.minInt);

        ArrayAdapter<String> countryArrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item, country);

        FCurr2.setAdapter(countryArrayAdapter);
        TCurr2.setAdapter(countryArrayAdapter);

        CheckBox setbox = (CheckBox) findViewById(R.id.setbox);

        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Boolean checked = prefs.getBoolean("CheckBox",false);
        setbox.setChecked(checked);
        txtTime.setText(prefs.getString("DailyTime", null));
        exchangetwo.setText(prefs.getString("ExchangeAmount", null));
        FCurr2.setSelection(prefs.getInt("FromPosition", 0));
        TCurr2.setSelection(prefs.getInt("ToPosition", 0));
        hrInt.setText(prefs.getString("HourInterval", null));
        minInt.setText(prefs.getString("MinuteInterval", null));


        btnTimePicker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Button btnTimePicker = (Button) v;

                // Process to get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);

                // Launch Time Picker Dialog
                TimePickerDialog tpd = new TimePickerDialog(Notifications.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(android.widget.TimePicker view, int hourOfDay, int minute) {
                        // Display Selected time in textbox


                        if(hourOfDay < 10 && minute<10){
                            txtTime.setText("0" + hourOfDay + ":" + "0" + minute);
                        }

                        else if (hourOfDay < 10) {
                            txtTime.setText("0" + hourOfDay + ":" + minute);
                        }

                        else if (minute < 10) {
                            txtTime.setText(hourOfDay + ":" + "0" + minute);
                        }
                        else {
                            txtTime.setText(hourOfDay + ":" + minute);
                        }

                        savedhour = hourOfDay;
                        savedminute = minute;

                    }
                }, mHour, mMinute, false);
                tpd.show();
            }
        });

        iCurrentSelectionF = FCurr2.getSelectedItemPosition();

        FCurr2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (iCurrentSelectionF != i) {
                    CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                    setbox.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("CheckBox", false);
                    editor.apply();
                    Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                    cancel();

                }
                iCurrentSelectionF = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });



        iCurrentSelectionT = TCurr2.getSelectedItemPosition();

        TCurr2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (iCurrentSelectionT != i) {
                    CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                    setbox.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("CheckBox", false);
                    editor.apply();
                    Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                    cancel();
                }
                iCurrentSelectionT = i;
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                return;
            }
        });


        beforeStringEx = exchangetwo.getText().toString();

            exchangetwo.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    EditText exchangetwo = (EditText) findViewById(R.id.exchangetwo);
                    if (!exchangetwo.getText().toString().equals(beforeStringEx)) {
                        CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                        setbox.setChecked(false);
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("CheckBox", false);
                        editor.apply();
                        Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                        cancel();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });


        beforeStringTime = txtTime.getText().toString();


            txtTime.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    if (!txtTime.getText().toString().equals(beforeStringTime)) {
                        CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                        setbox.setChecked(false);
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putBoolean("CheckBox", false);
                        editor.apply();
                        Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                        pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                        cancel();
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });

        beforeHrInt = hrInt.getText().toString();


        hrInt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!hrInt.getText().toString().equals(beforeHrInt)) {
                    CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                    setbox.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("CheckBox", false);
                    editor.apply();
                    Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                    cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        beforeMinInt = minInt.getText().toString();


        minInt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if (!minInt.getText().toString().equals(beforeMinInt)) {
                    CheckBox setbox = (CheckBox) findViewById(R.id.setbox);
                    setbox.setChecked(false);
                    SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                    editor.putBoolean("CheckBox", false);
                    editor.apply();
                    Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
                    pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
                    cancel();
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


    }


    public void sendBack(View b) {

        Button sendBack = (Button) b;

        super.onBackPressed();


    }



    public void onsetboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();



        CheckBox setbox = (CheckBox) findViewById(R.id.setbox);

        //case R.id.setbox:
        if (checked) {
            // Put some meat on the sandwich
            //Toast.makeText(this, "Checked Box", Toast.LENGTH_LONG).show();

            Spinner TCurr2 = (Spinner) findViewById(R.id.TCurr2);
            Spinner FCurr2 = (Spinner) findViewById(R.id.FCurr2);
            EditText exchangetwo = (EditText) findViewById(R.id.exchangetwo);
            EditText hrInt = (EditText) findViewById(R.id.hrInt);
            EditText minInt = (EditText) findViewById(R.id.minInt);


            if(TextUtils.isEmpty(minInt.getText().toString()) || TextUtils.isEmpty(hrInt.getText().toString())){
                Toast.makeText(this, "Please enter a valid time interval", Toast.LENGTH_LONG).show();
                setbox.toggle();
                return;
            }

            exchange3 = exchangetwo.getText().toString();
            fromCurrency2 = FCurr2.getSelectedItem().toString();
            toCurrency2 = TCurr2.getSelectedItem().toString();
            minInterval = Integer.parseInt(minInt.getText().toString());
            hrInterval = Integer.parseInt(hrInt.getText().toString());

            Log.i("Exchange", exchange3);

            String test_time = txtTime.getText().toString();

            if(TextUtils.isEmpty(test_time)){

                Toast.makeText(this, "Please select a valid start time", Toast.LENGTH_LONG).show();
                setbox.toggle();
                return;
            }


            if (minInterval<0 || hrInterval<0 || minInterval+hrInterval==0) {

                Toast.makeText(this, "Please enter a valid time interval", Toast.LENGTH_LONG).show();
                setbox.toggle();
                return;

            }

            if (TextUtils.isEmpty(exchange3)) {

                Toast.makeText(this, "Please enter a valid number", Toast.LENGTH_LONG).show();
                setbox.toggle();
                return;

            }


            if (fromCurrency2.equals(toCurrency2)) {
                Toast.makeText(this, "Please choose two different currencies", Toast.LENGTH_LONG).show();
                setbox.toggle();
                return;
            }


            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("ExchangeAmount", exchange3);
            editor.putString("FromCurrency", fromCurrency2);
            editor.putString("ToCurrency", toCurrency2);
            editor.putString("DailyTime", txtTime.getText().toString());
            editor.putBoolean("CheckBox", checked);
            editor.putInt("FromPosition", FCurr2.getSelectedItemPosition());
            editor.putInt("ToPosition", TCurr2.getSelectedItemPosition());
            editor.putString("HourInterval", hrInt.getText().toString());
            editor.putString("MinuteInterval", minInt.getText().toString());
            editor.putInt("HourInteger", hrInterval);
            editor.putInt("MinuteInteger", minInterval);
            editor.putInt("savedhour",savedhour);
            editor.putInt("savedminute",savedminute);
            editor.apply();

            /* Retrieve a PendingIntent that will perform a broadcast */
            Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);

            startAt10();


        } else {
            //Toast.makeText(this, "Unchecked Box", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putBoolean("CheckBox", checked);
            editor.apply();
            Intent alarmIntent = new Intent(Notifications.this, AlarmReceiver.class);
            pendingIntent = PendingIntent.getBroadcast(Notifications.this, 0, alarmIntent, 0);
            cancel();
            // clears all Shared Preferences
            //this.getSharedPreferences(MY_PREFS_NAME, 0).edit().clear().apply();


        }
    }


    public void cancel() {
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(pendingIntent);
        //Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();

    }


    public void startAt10() {
        Log.i("WTF2", "WTF WHY RUNNING?");
        //Toast.makeText(Notifications.this, "before alarmManager", Toast.LENGTH_LONG).show();
        AlarmManager manager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //Toast.makeText(Notifications.this, "after alarmManager", Toast.LENGTH_LONG).show();
        //int interval = 1000 * 60 * 60 * 24;
        int interval = hrInterval*60*60*1000 + minInterval*60*1000;
        Log.i("Interval",Integer.toString(interval));

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, savedhour);
        calendar.set(Calendar.MINUTE, savedminute);
        calendar.set(Calendar.SECOND, 0);



        String k = Integer.toString(savedhour);
        String w = Integer.toString(savedminute);

        Log.i("mHour",k);
        Log.i("mMinute", w);

        /* Repeating on every 20 minutes interval */
        manager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);
        //Toast.makeText(Notifications.this, "start at 10 fully ran", Toast.LENGTH_LONG).show();
    }





    // add in reboot set alarm ability
    // change to actual 24 hour interval AND remove test toasts
    // add in selectable interval feature? general fine tuning
    //end of Notifications class
}


