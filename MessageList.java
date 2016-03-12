package com.example.caroline.safehome;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import java.util.ArrayList;
import java.util.List;

public class MessageList extends AppCompatActivity {
    List<User> users;
    boolean isCalled = false;
    List<Follower>followers;
    List<Follower>follower2;
    List<String>names;
    ListView listView;
    String name;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;



    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        new GetUsers().execute("http://192.168.0.7:8080/restusers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String name = preferences.getString("NAME","");
        follower2 = new ArrayList<>();
        names = new ArrayList<>();
        String e = "";
        String e1= "";
        for (User user1 : users) {
            if (user1.getName().equalsIgnoreCase(name)) {

                for(Follower f1: user1.getFollowers()){
                    follower2.add(f1);
                }

                for(Follower follower:follower2){
                    e= follower.getUsername();
                    e1=follower.getPhoneNumber();
                    System.out.println(e + e1);
                    names.add(e + "\n"+e1);
                }
                // names.add(e);
                Log.d("MyTagGoesHere", e);
            }
        }
        //System.out.println(e);

        if(followers!=null) {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_layout, names);
            listView.setAdapter(adapter);
        }
        else {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, R.layout.row_layout, users);
            listView.setAdapter(adapter);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                             for(int i=0; i<followers.size(); i++){
                                 if(index==i){
                                     Intent appInfo = new Intent(MessageList.this, Messenger.class);
                                     appInfo.putExtra("followerNum", followers.get(i).getPhoneNumber());
                                     appInfo.putExtra("followerName",followers.get(i).getUsername());
                                     startActivity(appInfo);

                                 }
                             }

                return false;
            }


        });
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle("");
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

    private void addDrawerItems() {
        String[] osArray = { "Home","My Location", "Start Journey", "My Followers", "Add Followers", "Notifications","Emergency Contacts","Messages"};
        mAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                //The order: Location, Journey, MyFollowers, AddFollowers,Notifications, EmergencyContacts, Messages
                if(position==0){
                    Intent appInfo = new Intent(MessageList.this, firstPage.class);
                    startActivity(appInfo);
                }
                else if (position == 1) {
                    Intent appInfo = new Intent(MessageList.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(MessageList.this , Journey.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(MessageList.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(MessageList.this, AddFollowers.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(MessageList.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 6) {
                    Toast.makeText(MessageList.this, "EmergencyContacts", Toast.LENGTH_LONG).show();
                } else if (position == 7) {
                    Intent i = new Intent(MessageList.this, Messenger.class);
                    startActivity(i);
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



    public class GetUsers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            users = new ArrayList<>();
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
                    String address = jsonObject.getString("address");
                    String name = jsonObject.getString("name");
                    String password = jsonObject.getString("password");
                    String email = jsonObject.getString("email");
                    String phone = jsonObject.getString("phone");//original
                    JSONArray f = jsonObject.getJSONArray("followers");
                    if(f!=null) {

                        for (int j = 0; j < f.length(); j++) {
                            Follower f2 = new Follower();
                            String n = f.getJSONObject(j).getString("username");
                            String p = f.getJSONObject(j).getString("phoneNumber");
                            f2.setUsername(n);
                            f2.setPhoneNumber(p);
                            followers.add(f2);

                        }
                        User user = new User(id,name,password,email,address,phone);//original
                        user.setFollowers(followers);
                        users.add(user);//original;

                    }

                    else {
                        User user = new User(id, name, password, email, address, phone);//original

                        users.add(user);//original;
                    }
                }
                System.out.println("List Size1... " + users.size());
                for (User user1 : users) {
                    System.out.println(user1.toString());

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

}