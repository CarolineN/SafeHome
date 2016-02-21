package com.example.caroline.safehome;

import android.annotation.TargetApi;


import android.app.AlarmManager;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import android.view.View;
import android. widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.model.LatLng;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class firstPage extends AppCompatActivity implements AccelerometerListener {

    MediaPlayer mySound;
    Camera camera;
    Camera.Parameters params;
    boolean isCalled = false;
    boolean messageSent = false;
    String location;
    List<EmergencyContact> emergency;
    List<Notification> notifications;
    String emergPhone;
    private PendingIntent pendingIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_page);
        new GetEmergencyDetails().execute("http://147.252.139.254:8080/restEmergency");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        new GetNotificationDetails().execute("http://147.252.138.19:8080/restnotification");
//        try {
//            Thread.sleep(1000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }


        mySound = MediaPlayer.create(this, R.raw.policesiren);
        Intent myIntent = new Intent(firstPage.this, MyService.class);

        pendingIntent = PendingIntent.getService(firstPage.this, 0, myIntent, 0);



        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);



        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5 * 1000, pendingIntent);

        Toast.makeText(firstPage.this, "Start Alarm", Toast.LENGTH_LONG).show();


    }

    public void onAccelerationChanged(float x, float y, float z) {
        // TODO Auto-generated method stub

    }

    private void call() {
        Intent in = new Intent(Intent.ACTION_CALL);
        in.setData(Uri.parse("tel:" + emergPhone));
        if (!isCalled) {
            try {
                startActivity(in);
                isCalled = true;
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onShake(float force) {
        call();
        // Do your stuff here

        // Called when Motion Detected
        Toast.makeText(getBaseContext(), "Motion detected",
                Toast.LENGTH_SHORT).show();


    }

    @Override
    public void onResume() {
        super.onResume();
        Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
                Toast.LENGTH_SHORT).show();

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isSupported(this)) {

            //Start Accelerometer Listening
            AccelerometerManager.startListening(this);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);

        alarmManager.cancel(pendingIntent);

        //Stopping the notifications

        // Tell the user about what we did.



        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onStop Accelerometer Stoped",
                    Toast.LENGTH_SHORT).show();
        }

    }

    public void sendSMSMessage(String em) {
        try {
            if (!messageSent) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(em, null, "I have hit the panic button!", null, null);
            }

        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), "Sms failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        messageSent = true;
addNotification();

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  distroy");

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();

            Toast.makeText(getBaseContext(), "onDestroy Accelerometer Stoped",
                    Toast.LENGTH_SHORT).show();
        }

    }
    private void addNotification() {


        long[] v = {500,1000};
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.web_hi_res_512)
                        .setContentTitle("Safe Home")
                        .setContentText("You have hit the panic button!")
                        .setVibrate(v);

        Intent notificationIntent = new Intent(this, firstPage.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, notificationIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(contentIntent);

        // Add as notification
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(4, builder.build());
    }


    public void location(View view) {
        Intent intent = new Intent(this, Location.class);
        startActivity(intent);
    }
    public void journey(View view){
        Intent intent = new Intent(this,Journey.class);
        startActivity(intent);
    }
    public void followers(View view){
        Intent intent = new Intent(this, AddFollowers.class);
        startActivity(intent);

    }
    public void myFollowers(View view){
        Intent intent = new Intent(this, MyFollowers.class);
        startActivity(intent);


    }

    public void playMusic(View view) {
        mySound.start();// media
        setVolumeControlStream(AudioManager.STREAM_MUSIC);// media


        camera = Camera.open();
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(params);
        camera.startPreview();
        if ((emergency.size() == 0) || (emergency == null)) {
            Toast.makeText(getApplicationContext(),
                    "There are no emergency contacts to view",
                    Toast.LENGTH_LONG).show();
        }
        SharedPreferences preferences1 = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        int idNew = preferences1.getInt("ID", 0);

        String strI = Integer.toString(idNew);
        for (EmergencyContact contact1 : emergency) {
            if (contact1.getUser_Id().equals(strI)) {
                emergPhone = contact1.getPhoneNumber();
                sendSMSMessage(emergPhone);
            }
        }



    }

    public void stopMusic(View view) {
        mySound.pause();
        params = camera.getParameters();
        params.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
        camera.setParameters(params);
        camera.stopPreview();

    }
    public class GetEmergencyDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            emergency = new ArrayList<>();
            try {
                URL url = new URL(params[0]);
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



                    int id = jsonObject.getInt("id");
                    String username = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    String phone = jsonObject.getString("phoneNumber");//original
                    String user_Id = jsonObject.getString("user_Id");

                    EmergencyContact co= new EmergencyContact(username,email,phone,user_Id);//original
                    co.setId(id);
                    emergency.add(co);//original;
                }
                //System.out.println("List Size1... " + emergency.size());
//                for (EmergencyContact contact1 : emergency) {
//                    //System.out.println(contact1.toString());
//
//                }
                return buffer.toString();

            } catch (JSONException | IOException e) {
                e.printStackTrace();
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
            return null;

        }
    }

    public class GetNotificationDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                notifications = new ArrayList<>();
                URL url = new URL(params[0]);
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
                    //System.out.println(not.toString());
                    notifications.add(not);
                }
                for (Notification n1 : notifications) {
                    System.out.println("hhhh" +n1.toString());

                }

                return buffer.toString();

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
            return null;

        }
    }

}


