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
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

public class MyFollowers extends AppCompatActivity {

    boolean isCalled = false;
    List<Follower>followers;
    List<Follower>follower2;
    List<String>names;
    ListView listView;
    String name;
    List<EmergencyContact> contacts;
    String callit;
    boolean messageSent = false;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    FloatingActionButton floaty;
    String ne;
String string;


    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetEmergencyDetails().execute(string + "restjourneys");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setContentView(R.layout.activity_followers);
        string=getString(R.string.IP);
        new GetFollowers().execute(string+"restfollowers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userPhone = preferences.getString("PHONE", "");
        String userName = preferences.getString("NAME", "");
        for(EmergencyContact e:contacts){
            if(e.getUser_Id().equalsIgnoreCase(userPhone)){
                callit=e.getPhoneNumber();
                ne=e.getUsername();
            }
        }
        follower2 = new ArrayList<>();
        names = new ArrayList<>();

        for(Follower f:followers){
            if(f.getUserPhone().equalsIgnoreCase(userPhone)){
                String e = f.getUsername();
                String e1=f.getPhoneNumber();
                names.add(e + "\n" + e1);

            }
        }


        if(followers!=null) {
            listView = (ListView) findViewById(R.id.listMy);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_item_2,R.id.textViewFlightNo, names){
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    View view = super.getView(position, convertView, parent);
                    TextView text = (TextView) view.findViewById(R.id.textViewFlightNo);

                    Button deleteImageView = (Button) view.findViewById(R.id.btn_message);
                    deleteImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final String follower;
                            follower = listView.getItemAtPosition(position).toString();
                            String[] split = follower.split("\\s+");
                            String name = split[0];
                            String phone = split[1];

                            Intent appInfo = new Intent(MyFollowers.this, Messenger.class);
                            appInfo.putExtra("followerNum", phone);
                            appInfo.putExtra("followerName",name);
                            startActivity(appInfo);
                            Toast.makeText(MyFollowers.this, listView.getItemAtPosition(position).toString() + "Go To Messages", Toast.LENGTH_LONG).show();
                        }
                    });
                    Button callImageView =(Button)view.findViewById(R.id.btn_call);
                    callImageView.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            final String follower;
                            follower = listView.getItemAtPosition(position).toString();
                            callFollower(follower);
                            Toast.makeText(MyFollowers.this,listView.getItemAtPosition(position).toString() +  " Called", Toast.LENGTH_LONG).show();
                        }
                    });


                    return view;


                }
            };


            listView.setAdapter(adapter);
        }
        else {
            listView = (ListView) findViewById(R.id.listMy);
            ArrayAdapter<Follower> adapter = new ArrayAdapter<Follower>(this, R.layout.row_layout, followers);
            listView.setAdapter(adapter);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                final String follower;
                follower = listView.getItemAtPosition(index).toString();
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyFollowers.this);
                alertDialogBuilder.setMessage("Call Follower?");

                alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(MyFollowers.this, "You clicked yes button", Toast.LENGTH_LONG).show();
                        callFollower(follower);
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MyFollowers.this,"You clicked no",Toast.LENGTH_LONG).show();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return false;
            }


        });
        mDrawerList = (ListView)findViewById(R.id.navListMy);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("My Followers");
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
    }
    public void callFollower(String follower) {
        String[] split = follower.split("\\s+");
        String name = split[0];
        String phone = split[1];
        Intent in = new Intent(Intent.ACTION_CALL);
        in.setData(Uri.parse("tel:" + phone));

        try {
            startActivity(in);
            isCalled = true;
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(getApplicationContext(), "yourActivity is not founded", Toast.LENGTH_SHORT).show();
        }

    }
    public void add(View v){
        Intent intent = new Intent(this, AddFollowers.class);
        startActivity(intent);

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
                if (position == 0) {
                    Intent appInfo = new Intent(MyFollowers.this, firstPage.class);
                    startActivity(appInfo);
                } else if (position == 1) {
                    Intent appInfo = new Intent(MyFollowers.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(MyFollowers.this, timeInterval.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(MyFollowers.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(MyFollowers.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                   otherEmergency();

                } else if (position == 6) {
                    Intent i = new Intent(MyFollowers.this, MessageList.class);
                    startActivity(i);
                } else if (position == 7) {
                    Intent i = new Intent(MyFollowers.this, MainActivity.class);
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
        getMenuInflater().inflate(R.menu.menu_followers, menu);
        return true;
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
    public void otherEmergency(){

//        startActivity(intent);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MyFollowers.this);

        // set title
        alertDialogBuilder.setTitle("Emergency Contact");

        // set dialog message
        alertDialogBuilder
                .setMessage(ne)
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
    public void playMusic() {
        String message="I'm in danger!";
        sendSMSMessage(callit, message);
        Toast.makeText(MyFollowers.this,
                "Emergency Contact Contacted", Toast.LENGTH_LONG).show();




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



    public class GetFollowers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            followers = new ArrayList<>();


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
                    String address = jsonObject.getString("homeLocation");
                    String name = jsonObject.getString("username");
                    String phone = jsonObject.getString("phoneNumber");
                    String email = jsonObject.getString("email");
                    String userPhone = jsonObject.getString("userPhone");//original

                    Follower follower = new Follower(name,email,address,phone,userPhone);//original
                    followers.add(follower);




                }

                for (Follower f1 : followers) {
                    System.out.println(f1.toString());

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
            contacts= new ArrayList<>();
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