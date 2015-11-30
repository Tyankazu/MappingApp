package com.example.b1012234.mappingapp;



import android.location.Location;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import android.location.LocationListener;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.lang.String.valueOf;


public class MainActivity extends ActionBarActivity implements LocationListener{

    LocationManager mLocationManager = null;
    private static final String LOG_TAG ="Location";
    private SimpleDateFormat sdf;
    BufferedWriter bw;
    Date date;
    private String path;
    //ボタンの判定
    private int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLocationManager =(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        // クリックイベントを取得したいボタン
        Button Start_bt = (Button) findViewById(R.id.btnstart);
        Button Stop_bt = (Button) findViewById(R.id.btnstop);

        // クリックリスナーを登録
        Start_bt.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "start", Toast.LENGTH_SHORT).show();
                System.out.println("startclick");
                count++;
                System.out.println("start:" + count);
                sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");//時刻の出力フォーマット作成
            }
        });
        Stop_bt.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "stop", Toast.LENGTH_SHORT).show();
                System.out.println("stopclick");
                count++;
                try {
                    bw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.println("stop:" + count);
            }
        });
        date = new Date();//現在時刻の取得
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Location lastLocation = mLocationManager.getLastKnownLocation
                (LocationManager.GPS_PROVIDER);
        updateDisplayedInfo(lastLocation);
        //位置情報更新要求(リスナの登録)
        mLocationManager.requestLocationUpdates
                (LocationManager.GPS_PROVIDER, 0, 0, this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        //リスナの解除
        mLocationManager.removeUpdates(this);
    }

    public void updateDisplayedInfo(Location location){
        if (location == null){
            Log.e(LOG_TAG, "location is null.");
            return;
        }
        //緯度の表示更新
        TextView lat_value = (TextView)findViewById(R.id.latitude);
        lat_value.setText(Double.toString(location.getLatitude()));
        //経度の表示更新
        TextView lon_value = (TextView)findViewById(R.id.longitude);
        lon_value.setText(Double.toString(location.getLongitude()));
        //スピードの表示更新
        TextView spd_value = (TextView)findViewById(R.id.speed);
        spd_value.setText(Float.toString(location.getSpeed()));
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

    @Override
    public void onLocationChanged(Location location) {
        Log.d(LOG_TAG, "onLocationChanged has been called.");
        //緯度、経度、標高の更新
        updateDisplayedInfo(location);

        Log.v("----------", "----------");
        Log.v("Latitude", String.valueOf(location.getLatitude()));
        Log.v("Longitude", String.valueOf(location.getLongitude()));
        Log.v("Speed", String.valueOf(location.getSpeed()));

        Calendar time = Calendar.getInstance();
        int year = time.get(time.YEAR);
        int month = time.get(time.MONTH);
        int day = time.get(time.DAY_OF_MONTH);
        int hour = time.get(time.HOUR_OF_DAY);
        int minute = time.get(time.MINUTE);
        int second = time.get(time.SECOND);
        int ms = time.get(time.MILLISECOND);

        String nowtime = valueOf(year) + "/" +
                valueOf(month + 1) + "/" + valueOf(day) + "_" + valueOf(hour) + ":"
                + valueOf(minute) + ":" + valueOf(second) + ":" + valueOf(ms);
        System.out.println(nowtime);

        if (count % 2 != 0) {
            String fileName = sdf.format(date) + "gps" + ".csv";
            path = Environment.getExternalStorageDirectory() + "/" + fileName;
            File file = new File(path);
            file.getParentFile().mkdir();

            String write_int = nowtime + "," +
                    String.valueOf(location.getLatitude()) + "," +
                    String.valueOf(location.getLongitude()) + "," +
                    String.valueOf(location.getSpeed()) + "\n";
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(file, true);
                OutputStreamWriter writer = new OutputStreamWriter(fos);
                bw = new BufferedWriter(writer);
                bw.write(write_int);
                bw.flush();

                System.out.println("save");
            } catch (UnsupportedEncodingException k) {
                k.printStackTrace();
            } catch (FileNotFoundException k) {
                String message = k.getMessage();
                k.printStackTrace();
            } catch (IOException k) {
                String message = k.getMessage();
                k.printStackTrace();
            }
        }
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
