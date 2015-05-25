package com.mg.cuco;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        String[] country = {"CAD","USD","EUR","CNY","JPY","GBP","HKD","BTC"};

        Spinner TCurr = (Spinner) findViewById(R.id.TCurr);
        Spinner FCurr = (Spinner) findViewById(R.id.FCurr);
        Button start = (Button) findViewById(R.id.start);
        TextView final_amount = (TextView) findViewById(R.id.final_amount);
        EditText exchange = (EditText) findViewById(R.id.exchange);

        ArrayAdapter<String> countryArrayAdapter =
                new ArrayAdapter<String>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item, country);

        FCurr.setAdapter(countryArrayAdapter);
        TCurr.setAdapter(countryArrayAdapter);

        //end of main method
    }

    public void startOnClick(View v){

        Button start = (Button) v;
        Spinner TCurr = (Spinner) findViewById(R.id.TCurr);
        Spinner FCurr = (Spinner) findViewById(R.id.FCurr);
        TextView final_amount = (TextView) findViewById(R.id.final_amount);
        EditText exchange = (EditText) findViewById(R.id.exchange);
        //Toast.makeText(this,"clicked777",Toast.LENGTH_LONG).show();

        String exchange2 = exchange.getText().toString();


        String fromCurrency = FCurr.getSelectedItem().toString();
        String toCurrency = TCurr.getSelectedItem().toString();

        String link = "https://www.google.com/finance/converter?a="+exchange2+"&from="+fromCurrency+"&to="+toCurrency;

        Log.i("WTF",link);

        if (TextUtils.isEmpty(exchange2)){

            Toast.makeText(this,"Please enter a valid number",Toast.LENGTH_LONG).show();
            return;

        }


        if (fromCurrency.equals(toCurrency))
        {
            Toast.makeText(this,"Please choose two different currencies",Toast.LENGTH_LONG).show();
            return;
        }

        new GetWebpageTask().execute(link);


        //https://www.google.com/finance/converter?a={0}&from={1}&to={2}&meta={3}", amount, fromCurrency, toCurrency, Guid.NewGuid().ToString())
    }

    public void sendMessage(View view){

        Button sendMessage = (Button) view;
        Intent intent = new Intent(this, Notifications.class);
        startActivity(intent);
    }


        private String getWebsite(String address) {

        StringBuffer stringBuffer = new StringBuffer();

        BufferedReader reader = null;

        try{
            URL url = new URL(address);
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";

            while((line=reader.readLine())!=null){
                stringBuffer.append(line);
            }

        }

        catch (IOException e){
            e.printStackTrace();
        }finally{
            if(reader!=null){
                try{
                    reader.close();
                } catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return stringBuffer.toString();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class GetWebpageTask extends AsyncTask<String,Void, String> {
        @Override
        protected void onPostExecute(String result) {

            //Log.i("Currency", result);

            //for (int i = 0; i<result.length(); i+= 1024)
            //Log.i("test", result.substring(i, 1024));

           Pattern p = Pattern.compile("class=bld>(\\d+.\\d+)");
            //Pattern p = Pattern.compile("testing_no_find(\\d+.\\d+)");
           //Pattern p = Pattern.compile("(w3.org)");
           Matcher m = p.matcher(result);

            TextView final_amount = (TextView) findViewById(R.id.final_amount);

            if (m.find()) {
                String result2 = m.group(1);
                final_amount.setText(result2);
            }
            else {
                Toast.makeText(MainActivity.this,"Unexpected error",Toast.LENGTH_LONG).show();
                return;
            }


            super.onPostExecute(result);

            //"<span class=\"?bld\"?>([^<]+)</span>")[0].Groups[1].Value;
        }

        @Override
        protected String doInBackground(String... url){

            return getWebsite(url[0]);



        }

    }

}

