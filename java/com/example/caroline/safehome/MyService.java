package com.example.caroline.safehome;


/**
 * Created by Caroline on 2/16/2016.
 */

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
import java.util.ArrayList;
import java.util.List;


public class MyService extends Service {
    List<Notification> notifications;


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

//        GetNotificationDetails task = new GetNotificationDetails();
//        task.execute("http://147.252.138.19:8080/restnotification");
        Toast.makeText(this, "MyAlarmService.onCreate()", Toast.LENGTH_LONG).show();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {
            notifications = new ArrayList<>();
            URL url = new URL("http://147.252.139.254:8080/restnotification");
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
                String followerNum = jsonObject.getString("followerNum");
                String userName = jsonObject.getString("usersName");
                String message = jsonObject.getString("message");
                String time = jsonObject.getString("time");
                //System.out.println(id);
                //System.out.println(followerNum);
                Notification not = new Notification(followerNum, userName, message, time);
                System.out.println(not.toString());
                notifications.add(not);
            }
//            for (Notification n1 : notifications) {
//                System.out.println("hhhh" +n1.toString());
//
//            }



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

        for (Notification n1 : notifications) {// Joe isnt anyones followers thats why this doesnt work signed in as joe
            if(n1.getFollowerNum().equalsIgnoreCase(phone1)){
                System.out.println("1" + n1.getMessage());
                System.out.println("2" +n1.getTime());

            }
        }





//Works with a notification :)
//        Toast.makeText(this, "MyAlarmService.onStart()", Toast.LENGTH_LONG).show();
        //System.out.println("Do this....., do that");
//        long[] v = {500,1000};
//        NotificationCompat.Builder builder =
//                new NotificationCompat.Builder(this)
//                        .setSmallIcon(R.drawable.web_hi_res_512)
//                        .setContentTitle("Safe Home")
//                        .setContentText("You have hit the panic button!")
//                        .setVibrate(v);
//
//        Intent notificationIntent = new Intent(this, firstPage.class);
//        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(contentIntent);
//
//        // Add as notification
//        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        manager.notify(4, builder.build());

    }


    @Override

    public boolean onUnbind(Intent intent) {

// TODO Auto-generated method stub

        Toast.makeText(this, "MyAlarmService.onUnbind()", Toast.LENGTH_LONG).show();

        return super.onUnbind(intent);

    }


}



