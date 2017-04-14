package com.example.yfm.devicecontrol;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int INTERVAL_TIME = 2000;
    Timer timer;
    TimerTask task;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(this.getString(R.string.app_title));
    }

    @Override
    protected void onResume() {
        super.onResume();

        task = new TimerTask(){
            public void run(){
                jsonDataFromHttpAppAcvite("get", "led", null);
            }
        };
        timer = new Timer();
        timer.schedule(task, 0, INTERVAL_TIME);         //开启定时器，delay 1s后执行task
    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();                             //销毁定时器
    }

    public static String GET(String url){
        InputStream inputStream = null;
        String result = "";
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse httpResponse = httpclient.execute(new HttpGet(url));
            inputStream = httpResponse.getEntity().getContent();
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return GET(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(getBaseContext(), "Received!", Toast.LENGTH_LONG).show();

            JSONObject json = null;
            try {
                json = new JSONObject(result);
                String strValue = json.getString("value");
                if(strValue.equalsIgnoreCase("on")) {
                    setLampImageView(true);
                }else {
                    setLampImageView(false);
                }
            }catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public void jsonDataFromHttpAppAcvite(String active, String key, String value) {

        String url = "";
        if (active.equalsIgnoreCase("put")) {
            url = String.format("http://yfm1202.6655.la:9090/api/a7/control?active=put&key=%s&value=%s", key, value);
        } else {
            url = String.format("http://yfm1202.6655.la:9090/api/a7/control?active=get&key=%s", key);
        }
        new HttpAsyncTask().execute(url);
    }

    public void lamp_off_click(View v) {
        jsonDataFromHttpAppAcvite("put", "led", "off");
    }
    public void lamp_on_click(View v) {
        jsonDataFromHttpAppAcvite("put", "led", "on");
    }

    private void setLampImageView(boolean lamp_on) {
        ImageView iv =  (ImageView) findViewById(R.id.lamp_imageView);
        if(lamp_on) {
            iv.setImageResource(R.drawable.lamp_on);
        }else {
            iv.setImageResource(R.drawable.lamp_off);
        }
    }
}
