package com.example.caroline.safehome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ViewNotification extends ActionBarActivity {
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    List<String>names;
    List<String>f;
    boolean isCalled = false;
    ListView listView;
    String reportDate="";
    List<EmergencyContact> contacts;
    String callit;
    String name;
    boolean messageSent = false;
    List<Notification> notifications;

    List<Notification> n1s;
    View v;
    String string;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_notification);
        string=getString(R.string.IP);
        new GetNotifications().execute(string+"restnotification");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new GetEmergencyDetails().execute(string+"restjourneys");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userPhone = preferences.getString("PHONE", "");
        for(EmergencyContact e:contacts){
            if(e.getUser_Id().equalsIgnoreCase(userPhone)){
                callit=e.getPhoneNumber();
                name = e.getUsername();
            }
        }
        mDrawerList = (ListView) findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Notifications");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
        Button b = (Button) findViewById(R.id.allNotifications);
        b.callOnClick();

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
                    Intent appInfo = new Intent(ViewNotification.this, firstPage.class);
                    startActivity(appInfo);
                }
                else if (position == 1) {
                    Intent appInfo = new Intent(ViewNotification.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(ViewNotification.this, timeInterval.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(ViewNotification.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(ViewNotification.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(ViewNotification.this, ViewEmergency.class);
                    otherEmergency();

                } else if (position == 6) {
                    Intent i = new Intent(ViewNotification.this, MessageList.class);
                    startActivity(i);
                }
                else if(position ==7){
                    Intent i = new Intent(ViewNotification.this, MainActivity.class);
                    startActivity(i);
                }
                else if(position == 8){
                    playMusic();
                }

            }


        });
    }

    public void todayNotifications(View v){// These two methods are my newest changes here, errors could be in here

        names = new ArrayList<>();
        f = new ArrayList<>();
        n1s = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String phone1 = preferences.getString("PHONE", "");

        for (Notification n1 : notifications) {
            if (n1.getFollowerNum().equalsIgnoreCase(phone1)) {
                n1s.add(n1);

            }
        }


        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date today = dateFormat.parse(dateFormat.format(new Date()));
            reportDate = dateFormat.format(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println("fgfgfgfgf" + n1s.toString());
        for(Notification n: n1s){
            if(n.getDate().equals(reportDate)) {
                String name = n.getUsersName();
                String message = n.getMessage();
                String time = n.getTime();

                if(message.equalsIgnoreCase("Finished their journey")){
                    names.add(name + "      " + time + "\n" + message);
                }
                else{
                    String location = message.substring(message.indexOf("Location:") + 9, message.length());
                    String userDestination = message.substring(message.indexOf("Destination:") + 12, message.indexOf("Distance:"));
                    String distance = message.substring(message.indexOf("Distance:") + 9, message.indexOf("Current"));

                    String started=message.substring(0, message.indexOf("Destination"));
                    names.add(name + "     " + time +"\n" + started + "\n" + "Current Location: " + location + "\n" + "Destination:" + userDestination + "\n" + "Distance:" + distance);

                }
            }

        }
        if(n1s!=null) {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item,R.id.textViewFlightNo, names);
            listView.setAdapter(adapter);
        }
        else {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_layout, f);
            listView.setAdapter(adapter);
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                final String follower;
                follower = listView.getItemAtPosition(index).toString();

                String loc = follower.substring(follower.indexOf("Location:") + 9, follower.indexOf("Destination"));
                String name = follower.substring(0, follower.indexOf(" "));
                String destination = follower.substring(follower.indexOf("Destination:") + 12, follower.indexOf("Distance:"));
                //String distancefollower.substring(follower.indexOf("Distance:")+9, follower.length()));

                Intent appInfo = new Intent(ViewNotification.this, NotificationExtended.class);
                appInfo.putExtra("location", loc);
                appInfo.putExtra("name", name);
                appInfo.putExtra("destination", destination);
                startActivity(appInfo);
                return false;
            }


        });

    }
    public void otherEmergency(){

//        startActivity(intent);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ViewNotification.this);

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
    public void playMusic() {
        String message="I'm in danger!";
        sendSMSMessage(callit,message);
        Toast.makeText(ViewNotification.this,
                "Emergency Contact Contacted", Toast.LENGTH_LONG).show();

    }

    public void allNotifications(View v){
        names = new ArrayList<>();
        f = new ArrayList<>();
        n1s = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String phone1 = preferences.getString("PHONE", "");

        for (Notification n1 : notifications) {
            if (n1.getFollowerNum().equalsIgnoreCase(phone1)) {
                n1s.add(n1);

            }
        }

        for(Notification n: n1s){

                String name = n.getUsersName();
                String message = n.getMessage();
                String time = n.getTime();
                System.out.println(time);
                //names.add(name + "     " + time + "\n" + message);
            if(message.equalsIgnoreCase("Finished their journey")){
                names.add(name + "      " + time + "\n" + message);
            }
            else{
                String location = message.substring(message.indexOf("Location:") + 9, message.length());
                String userDestination = message.substring(message.indexOf("Destination:") + 12, message.indexOf("Distance:"));
                String distance = message.substring(message.indexOf("Distance:") + 9, message.indexOf("Current"));

                String started=message.substring(0, message.indexOf("Destination"));
                names.add(name + "     " + time +"\n" + started + "\n" + "Current Location: " + location + "\n" + "Destination:" + userDestination + "\n" + "Distance:" + distance);


        }

    }
    if(n1s!=null) {
        listView = (ListView) findViewById(R.id.list);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item,R.id.textViewFlightNo, names);
        listView.setAdapter(adapter);
    }
        else {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_layout, f);
            listView.setAdapter(adapter);
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                final String follower;
                follower = listView.getItemAtPosition(index).toString();

                String loc=follower.substring(follower.indexOf("Location:")+9 , follower.indexOf("Destination"));
                String name=follower.substring(0, follower.indexOf(" "));
                String destination =follower.substring(follower.indexOf("Destination:")+12,follower.indexOf("Distance:"));
                //String distancefollower.substring(follower.indexOf("Distance:")+9, follower.length()));

                Intent appInfo = new Intent(ViewNotification.this, NotificationExtended.class);
                appInfo.putExtra("location", loc);
                appInfo.putExtra("name", name);
                appInfo.putExtra("destination",destination);
                startActivity(appInfo);
                return false;
            }



        });

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_notification, menu);
        return true;
    }

    public class GetNotifications extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            notifications = new ArrayList<>();
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

                        //System.out.println(jsonObject);
                        int id = jsonObject.getInt("id");
                        String followerNum = jsonObject.getString("followerNum");
                        String userName = jsonObject.getString("usersName");
                        String message = jsonObject.getString("message");
                        String time = jsonObject.getString("time");
                        String date = jsonObject.getString("date");
                        //System.out.println(id);
                        //System.out.println(followerNum);
                        Notification not = new Notification(followerNum, userName, message, time,date);
                        System.out.println(not.toString());
                        notifications.add(not);
                }
                System.out.println("Notification... " + notifications.size());
                for (Notification notification1 : notifications) {
                    System.out.println(notification1.toString());

                }
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

