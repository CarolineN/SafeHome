package com.example.caroline.safehome;

import android.annotation.TargetApi;


import android.app.ActionBar;
import android.app.AlarmManager;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.hardware.Camera;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
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

import static android.app.PendingIntent.getActivity;


public class firstPage extends AppCompatActivity implements AccelerometerListener {

    MediaPlayer mySound;
    Camera camera;
    Camera.Parameters params;
    boolean isCalled = false;
    boolean messageSent = false;
    String usersID;
    EmergencyContact myContact;
    String location;
  String callit;
    String name;
    List<Notification> notifications;
    List<EmergencyContact> contacts;

    private PendingIntent pendingIntent;
    private PendingIntent intentPending;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
String string;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       setContentView(R.layout.activity_first_page);
        string=getString(R.string.IP);
        new GetEmergencyDetails().execute(string+"restjourneys");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        isGPSEnable();
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userPhone = preferences.getString("PHONE", "");
     for(EmergencyContact e:contacts){
         if(e.getUser_Id().equalsIgnoreCase(userPhone)){
             callit=e.getPhoneNumber();
             name = e.getUsername();
         }
     }




        mySound = MediaPlayer.create(this, R.raw.policesiren);
        //from here
        Intent myIntent = new Intent(firstPage.this, MyService.class);

        pendingIntent = PendingIntent.getService(firstPage.this, 0, myIntent, 0);



        AlarmManager alarmManager = (AlarmManager)getSystemService(ALARM_SERVICE);


        Calendar calendar = Calendar.getInstance();

        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.add(Calendar.SECOND, 10);

        //alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 5 * 1000, pendingIntent);
        // to here
        Intent myIntent1 = new Intent(firstPage.this, MyServiceOne.class);

        intentPending = PendingIntent.getService(firstPage.this, 0, myIntent1, 0);



        AlarmManager alarmManager1 = (AlarmManager)getSystemService(ALARM_SERVICE);


        Calendar calendar1 = Calendar.getInstance();

        calendar1.setTimeInMillis(System.currentTimeMillis());

        calendar1.add(Calendar.SECOND, 10);



        alarmManager1.setRepeating(AlarmManager.RTC_WAKEUP, calendar1.getTimeInMillis(), 3 * 1000, intentPending);
        //Toast.makeText(firstPage.this, "Start Alarm", Toast.LENGTH_LONG).show();
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Home");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));




    }
    private void addDrawerItems() {
        String[] osArray = { "Home","My Location", "Start Journey", "My Followers", "Notifications","Emergency Contact","Messages"," Log Out","Panic Button"};
        mAdapter =(new MobileArrayAdapter(this, osArray));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                //The order: Location, Journey, MyFollowers, AddFollowers,Notifications, EmergencyContacts, Messages
                if(position==0){
                    Intent appInfo = new Intent(firstPage.this, firstPage.class);
                    startActivity(appInfo);
                }
                else if (position == 1) {
                    Intent appInfo = new Intent(firstPage.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(firstPage.this, timeInterval.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(firstPage.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(firstPage.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                    otherEmergency();

                } else if (position == 6) {
                    Intent i = new Intent(firstPage.this, MessageList.class);
                    startActivity(i);
                }
                else if(position ==7){
                    Intent i = new Intent(firstPage.this, MainActivity.class);
                    startActivity(i);
                }
                else if(position == 8){
                    playMusic();
                }

            }


        });
    }
    public void isGPSEnable(){
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean enabled = service
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (!enabled) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    firstPage.this);
            alertDialogBuilder
                    .setMessage("GPS is disabled in your device. Enable it?")
                    .setCancelable(false)
                    .setPositiveButton("Enable GPS",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int id) {
                                    Intent callGPSSettingIntent = new Intent(
                                            android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    startActivity(callGPSSettingIntent);
                                }
                            });
            alertDialogBuilder.setNegativeButton("Cancel",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = alertDialogBuilder.create();
            alert.show();

        }

    }
    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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

        // Activate the navigation drawer toggle
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void onAccelerationChanged(float x, float y, float z) {
        // TODO Auto-generated method stub

    }


    public void onShake(float force) {
    }

    @Override
    public void onResume() {
        super.onResume();
       // Toast.makeText(getBaseContext(), "onResume Accelerometer Started",
              //  Toast.LENGTH_SHORT).show();

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

        if (AccelerometerManager.isListening()) {

            //Start Accelerometer Listening
            AccelerometerManager.stopListening();
        }

    }

    public void sendSMSMessage(String em,String message) {
        try {
            if (!messageSent) {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(em, null, message, null, null);
            }

        } catch (Exception e) {
           // Toast.makeText(getApplicationContext(), "Sms failed", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        messageSent = true;


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("Sensor", "Service  distroy");

        //Check device supported Accelerometer senssor or not
        if (AccelerometerManager.isListening()) {


            AccelerometerManager.stopListening();


        }

    }




    public void location(View view) {
        Intent intent = new Intent(this, Location.class);
        startActivity(intent);
    }
    public void journey(View view){
        Intent intent = new Intent(this,timeInterval.class);
        startActivity(intent);
    }
    public void followers(View view){
        Intent intent = new Intent(this, MyFollowers.class);
        startActivity(intent);

    }
    public void otherEmergency(){

//        startActivity(intent);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(firstPage.this);

        // set title
        alertDialogBuilder.setTitle("Emergency Contact");

        // set dialog message
        alertDialogBuilder
                .setMessage(name)
                .setCancelable(false)
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        callThem();
                    }
                })
                .setNeutralButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                System.out.println("Cancel");


                            }
                        })
                .setNegativeButton("Panic MSG", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, just close
                        // the dialog box and do nothing

                        playMusic();
                    }
                });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
    public void emergency(View v){
//        Intent intent = new Intent(this, ViewEmergency.class);
//        startActivity(intent);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(firstPage.this);

        // set title
        alertDialogBuilder.setTitle("Emergency Contact");

        // set dialog message
        alertDialogBuilder
                .setMessage(name)
                .setCancelable(false)
                .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // if this button is clicked, close
                        // current activity
                        callThem();
                    }
                })
        .setNeutralButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        System.out.println("Cancel");


                    }
                })
        .setNegativeButton("Panic MSG", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // if this button is clicked, just close
                // the dialog box and do nothing

                playMusic();
            }
        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();
    }
   public void messages(View v){
       Intent intent = new Intent(this, MessageList.class);
       startActivity(intent);
   }
    public void notification(View v){
        Intent intent = new Intent(this, ViewNotification.class);
        startActivity(intent);

    }

    public void playMusic() {
        String message="I'm in danger!";
        sendSMSMessage(callit, message);
        Toast.makeText(firstPage.this,
                "Emergency Contact Contacted", Toast.LENGTH_LONG).show();



    }

    public void stopMusic(View view) {
        String message="I'm ok";
        sendSMSMessage(callit,message);
        Toast.makeText(firstPage.this,
                "Emergency Contact Contacted", Toast.LENGTH_LONG).show();

    }
    public void callThem(){
        Intent in = new Intent(Intent.ACTION_CALL);
        in.setData(Uri.parse("tel:" + callit));
        if (!isCalled) {
            try {
                startActivity(in);
                isCalled = true;
            } catch (android.content.ActivityNotFoundException ex) {

            }
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
                    String date = jsonObject.getString("date");
                //System.out.println(id);
                    //System.out.println(followerNum);
                    Notification not = new Notification(followerNum, userName, message, time, date);
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
    public class GetEmergencyDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            contacts = new ArrayList<>();
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

                    String userPhone = jsonObject.getString("user_Id");
                    String name = jsonObject.getString("username");
                    String email = jsonObject.getString("email");
                    String phone = jsonObject.getString("phoneNumber");//original

                    EmergencyContact user = new EmergencyContact(name,email,phone,userPhone);//original
                    contacts.add(user);//original;
                }
                // System.out.println("List Size1... " + users.size());
//
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


}


