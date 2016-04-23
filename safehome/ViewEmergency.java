package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
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
import java.util.ArrayList;
import java.util.List;

public class ViewEmergency extends AppCompatActivity {
    TextView name1, email1, phone1;
    ArrayList<EmergencyContact> contacts1;
    Boolean isCalled=false;
    String num;
    String string;

    String callit;
    boolean messageSent = false;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    String n;
    private ActionBarDrawerToggle mDrawerToggle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_emergency);
        string=getString(R.string.IP);
        new GetEmergencyDetails().execute(string+"restjourneys");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userPhone = preferences.getString("PHONE", "");
        for(EmergencyContact e1:contacts1) {
            if (e1.getUser_Id().equalsIgnoreCase(userPhone)) {

                name1 = (TextView) findViewById(R.id.nameTextView);
                email1 = (TextView) findViewById(R.id.emailTextView);
                phone1 = (TextView) findViewById(R.id.phoneTextView);
               name1.setText(e1.getUsername());
                email1.setText(e1.getEmail());
               phone1.setText(e1.getPhoneNumber());
                num=e1.getPhoneNumber();
                callit=e1.getPhoneNumber();
               n= e1.getUsername();
            }

        }

        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("Emergency Contact");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_emergency, menu);
        return true;
    }
    public void onClick(View v){
        Intent in = new Intent(Intent.ACTION_CALL);
        in.setData(Uri.parse("tel:" + num));
        if (!isCalled) {
            try {
                startActivity(in);
                isCalled = true;
            } catch (android.content.ActivityNotFoundException ex) {

            }
        }

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

    private void addDrawerItems() {
        String[] osArray = { "Home","My Location", "Start Journey", "My Followers", "Notifications","Emergency Contacts","Messages"," Log Out","Panic Button"};
        mAdapter =(new MobileArrayAdapter(this, osArray));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                //The order: Location, Journey, MyFollowers, AddFollowers,Notifications, EmergencyContacts, Messages
                if (position == 0) {
                    Intent appInfo = new Intent(ViewEmergency.this, firstPage.class);
                    startActivity(appInfo);
                } else if (position == 1) {
                    Intent appInfo = new Intent(ViewEmergency.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(ViewEmergency.this, timeInterval.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(ViewEmergency.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(ViewEmergency.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(ViewEmergency.this, ViewEmergency.class);
                    startActivity(i);

                } else if (position == 6) {
                    Intent i = new Intent(ViewEmergency.this, MessageList.class);
                    startActivity(i);
                } else if (position == 7) {
                    Intent i = new Intent(ViewEmergency.this, MainActivity.class);
                    startActivity(i);
                } else if (position == 8) {
                    playMusic();
                }

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
        sendSMSMessage(callit, message);
        Toast.makeText(ViewEmergency.this,
                "Emergency Contact Contacted", Toast.LENGTH_LONG).show();




    }
    public void message(View v){
        String message="I'm in danger!";
        sendSMSMessage(callit, message);
        Toast.makeText(ViewEmergency.this,
                n +"Warned of your danger!", Toast.LENGTH_LONG).show();
    }

    public class GetEmergencyDetails extends AsyncTask<String, String, String> {

    @Override
    protected String doInBackground(String... params) {
        HttpURLConnection connection = null;
        BufferedReader reader = null;
        contacts1 = new ArrayList<>();
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
                contacts1.add(user);//original;
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



