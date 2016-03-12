package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
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
import java.util.Arrays;
import java.util.List;

public class AddFollowers extends AppCompatActivity {
    List<User> users;
    List<String> names;
    ListView listView;
    String name;
    String eAddress;
    int id;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
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

//names.add("you");
        names= new ArrayList<>();
       for(User user1:users){
            name = user1.getName();
           eAddress= user1.getEmail();

           names.add(name + "\n" + eAddress);

       }
        if(names!=null) {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_layout, names);
            listView.setAdapter(adapter);
        }
        else
        { listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, R.layout.row_layout, users);
            listView.setAdapter(adapter);
        }
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {
                String follower;
                follower = listView.getItemAtPosition(index).toString();
                System.out.println("Before I call addfollower");
                addFollower(follower);

                Toast.makeText(AddFollowers.this,listView.getItemAtPosition(index).toString() + " Added as a follower", Toast.LENGTH_LONG).show();
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
    private void addDrawerItems() {
        String[] osArray = { "Home","My Location", "Start Journey", "My Followers", "Add Followers", "Notifications","Emergency Contacts","Messages"};
        mAdapter = new ArrayAdapter<String>(this, R.layout.row_layout, osArray);
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                //The order: Location, Journey, MyFollowers, AddFollowers,Notifications, EmergencyContacts, Messages
                if (position == 0) {
                    Intent appInfo = new Intent(AddFollowers.this, firstPage.class);
                    startActivity(appInfo);
                } else if (position == 1) {
                    Intent appInfo = new Intent(AddFollowers.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(AddFollowers.this, Journey.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(AddFollowers.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(AddFollowers.this, AddFollowers.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(AddFollowers.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 6) {
                    Toast.makeText(AddFollowers.this, "EmergencyContacts", Toast.LENGTH_LONG).show();
                } else if (position == 7) {
                    Intent i = new Intent(AddFollowers.this, MessageList.class);
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
        getMenuInflater().inflate(R.menu.menu_followers, menu);
        return true;
    }


    public void addFollower(String follower) {
        String[] split = follower.split("\\s+");
        String name = split[0];
        String email = split[1];

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userName = preferences.getString("NAME", "");
        for(User user2:users){
            if(user2.getName().equalsIgnoreCase(userName)){
                id = user2.getId();
            }
        }
        System.out.println("Name:" + name);
        System.out.println("Email:" + email);
        if ((users.size() == 0) || (users == null)) {
            Toast.makeText(getApplicationContext(),
                    "There are no users to view",
                    Toast.LENGTH_LONG).show();
        } else {
            for (User user1 : users) {
                if (user1.getName().equalsIgnoreCase(name) && user1.getEmail().equals(email)) {
                        String name1 = user1.getName();
                        String address1 = user1.getAddress();
                        String email1 = user1.getEmail();
                        String password1 =user1.getPassword();
                         String phone1= user1.getPhone();

                    String userId = Integer.toString(id);
                    System.out.println("Before async is CALLED");
                    new MyDownloadTask().execute(userId,name1, address1, email1, password1, phone1);
                } else {
                    System.out.println("Error");
                }


            }
        }
    }
    public class GetUsers extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            users = new ArrayList<>();
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

                    User user = new User(id,name,password,email,address,phone);//original
                    users.add(user);//original;
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
            private class MyDownloadTask extends AsyncTask<String, String, String> {
                @Override
                protected String doInBackground(String... params) {
                    System.out.println("In do in background");

                    BufferedReader inBuffer = null;
                    String url = "http://192.168.0.7:8080/update_user";
                    String result = "fail";

                    String id = params[0];
                    String name = params[1];
                    String address = params[2];
                    String email = params[3];
                    String password = params[4];
                    String phone = params[5];
                    JSONObject json = new JSONObject();
                    try {
                        json.put("ID", id);
                        json.put("Name",name);
                        json.put("Address", address);
                        json.put("Email",email);
                        json.put("Password",password);
                        json.put("Phone",phone);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    System.out.println(json);

                    try {
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost request = new HttpPost(url);

                        StringEntity param = new StringEntity(json.toString());
                        //System.out.println(param.toString());// this does work. why is it null on the server side
                        request.setEntity(param);
                        HttpResponse httpResponse = httpClient.execute(request);
                        inBuffer = new BufferedReader(new InputStreamReader(
                                httpResponse.getEntity().getContent()));

                        StringBuffer stringBuffer = new StringBuffer("");
                        String line = "";
                        String newLine = System.getProperty("line.separator");
                        while ((line = inBuffer.readLine()) != null) {
                            stringBuffer.append(line + newLine);
                        }
                        inBuffer.close();
                        result = stringBuffer.toString();
                    } catch (Exception e) {
                        result = e.getMessage();
                    } finally {
                        if (inBuffer != null) {
                            try {
                                inBuffer.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    return result;
                }
            }
}
