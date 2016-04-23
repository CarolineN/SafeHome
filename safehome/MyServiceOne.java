package com.example.caroline.safehome;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;

import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.IBinder;

import android.os.StrictMode;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class MyServiceOne extends Service {
    List<Message> messages;
    List<Message> n1s;
    String string;
    String da;

    @Override

    public void onCreate() {

// TODO Auto-generated method stub


    }


    @Override

    public IBinder onBind(Intent intent) {

// TODO Auto-generated method stub

        Toast.makeText(this, "MyAlarmService.onBind()", Toast.LENGTH_LONG).show();

        return null;

    }


    @Override

    public void onDestroy() {

// TODO Auto-generated method stub

        super.onDestroy();

        Toast.makeText(this, "MyAlarmService.onDestroy()", Toast.LENGTH_LONG).show();

    }


    @Override

    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        string = getString(R.string.IP);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            messages = new ArrayList<>();
            n1s = new ArrayList<>();
            URL url = new URL(string + "restEmergency");
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5000);
            connection.connect();
            InputStream stream = connection.getInputStream();
            reader = new BufferedReader(new InputStreamReader(stream));
            StringBuffer buffer = new StringBuffer();
            String line = "";

            while ((line = reader.readLine()) != null) {
                buffer.append(line);
            }
            String finalJson = buffer.toString();
            JSONArray jsonArray = new JSONArray(finalJson);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                //System.out.println(jsonObject);
                int id = jsonObject.getInt("id");
                String myPhone = jsonObject.getString("myPhone");
                String message = jsonObject.getString("message");
                String followerNum = jsonObject.getString("followerNum");//original
                String date = jsonObject.getString("date");
                String time = jsonObject.getString("time");

                Message co= new Message(myPhone,message,followerNum,date,time);//original


                System.out.println(co.toString());
                messages.add(co);
            }
        } catch (JSONException | IOException e) {
            System.out.println(e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String phone1 = preferences.getString("PHONE", "");
        SimpleDateFormat d = new SimpleDateFormat("HH:mm");

        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            Date today = dateFormat.parse(dateFormat.format(new Date()));
            da = dateFormat.format(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        String t = d.format(calendar.getTime());

        for (Message n1 : messages) {
            if (n1.getFollowerNum().equalsIgnoreCase(phone1)&& n1.getDate().equals(da)) {
                n1s.add(n1);
                Message not = n1s.get(n1s.size() - 1);
System.out.println(not.getTime() + " compared to" + t);
                String n=not.getMyPhone();
                System.out.println("the number" + n);
                if (not.getTime().equals(t)) {


                    long[] v = {500, 1000};
                    NotificationCompat.Builder builder =
                            new NotificationCompat.Builder(this)
                                    .setSmallIcon(R.drawable.web_hi_res_512)
                                    .setContentTitle("New Message")
                                    .setContentText(not.getMessage() + "\n" + not.getFollowerNum())
                                    .setVibrate(v);

                    Intent notificationIntent = new Intent(this,  Messenger.class);
                    notificationIntent.putExtra("followerNum",n);
                    PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);

                    // Add as notification
                    NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                    manager.notify(9999, builder.build());
                }
            }
        }
    }




    @Override

    public boolean onUnbind(Intent intent) {

// TODO Auto-generated method stub

        Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();

        return super.onUnbind(intent);

    }


}