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
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddFollowers extends AppCompatActivity {
    List<User> users;
    List<User>users1;
    List<String> names;
    ListView listView;
    String name;
    boolean isCalled = false;
    List<Follower>followers;
    List<Follower>followers1;
    String eAddress;
    String phone;
    String home;
    String userPhone;
    String string;
    List<EmergencyContact> contacts;
    String callit;
    boolean messageSent = false;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;
    FloatingActionButton floaty;
    String ne;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_followers);
        string = getString(R.string.IP);

        new GetUsers().execute(string + "restusers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new GetFollowers().execute(string + "restfollowers");
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
        String userName = preferences.getString("NAME", "");
        for(EmergencyContact e:contacts){
            if(e.getUser_Id().equalsIgnoreCase(userPhone)){
                callit=e.getPhoneNumber();
                ne = e.getUsername();
            }
        }



        followers1 = new ArrayList<>();
        users1 = new ArrayList<>();

//       for(Follower f:followers){
//           if(f.getUserPhone().equalsIgnoreCase(phone2)){
//               followers1.add(f);
//           }
//       }

            names = new ArrayList<>();
            for (User user1 : users) {
                if (!user1.getName().equalsIgnoreCase(userName)) {
                    name = user1.getName();
                    eAddress = user1.getEmail();
                    phone = user1.getPhone();
                    home = user1.getAddress();
                    names.add(name + "\n" + eAddress + "\n" + phone + "\n" + home);
                }
            }

//        else if(followers1.size()!=0||followers1!=null) {
//            names = new ArrayList<>();
//            for (int i = 0; i < followers1.size(); i++) {
//                for (User u : users1) {
//                    if (!u.getName().equalsIgnoreCase(followers1.get(i).getUsername()) && !u.getName().equals(userName)) {
//                        name = u.getName();
//                        eAddress = u.getEmail();
//                        phone = u.getPhone();
//                        home = u.getAddress();
//                        names.add(name + "\n" + eAddress + "\n" + phone + "\n" + home);
//                    }
//                }
//            }
//        }
        Set<String> uniqueList = new HashSet<String>(names);
        names = new ArrayList<String>(uniqueList); //let GC will doing free memory
            if (names != null) {
                listView = (ListView) findViewById(R.id.list);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.row_item_1, R.id.textViewFlightNo, names) {
                    @Override
                    public View getView(final int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent);
                        TextView text = (TextView) view.findViewById(R.id.textViewFlightNo);

                        Button deleteImageView = (Button) view.findViewById(R.id.btn_share);
                        deleteImageView.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View v) {
                                String follower;
                                follower = listView.getItemAtPosition(position).toString();
                                System.out.println("Before I call addfollower");
                                addFollower(follower);
                                Toast.makeText(AddFollowers.this, listView.getItemAtPosition(position).toString() + " Added as a follower", Toast.LENGTH_LONG).show();
                                Intent appInfo = new Intent(AddFollowers.this, MyFollowers.class);
                                startActivity(appInfo);
                            }
                        });


                        return view;


                    }
                };
                listView.setAdapter(adapter);
            } else {
                listView = (ListView) findViewById(R.id.list);
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

                    Toast.makeText(AddFollowers.this, listView.getItemAtPosition(index).toString() + " Added as a follower", Toast.LENGTH_LONG).show();
                    return false;

                }
            });
            mDrawerList = (ListView) findViewById(R.id.navList);
            mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            addDrawerItems();
            setupDrawer();
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setTitle("Add Followers");
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
                    Intent appInfo = new Intent(AddFollowers.this, firstPage.class);
                    startActivity(appInfo);
                }
                else if (position == 1) {
                    Intent appInfo = new Intent(AddFollowers.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(AddFollowers.this, timeInterval.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(AddFollowers.this, MyFollowers.class);
                    startActivity(i);
                } else if (position == 4) {
                    Intent i = new Intent(AddFollowers.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(AddFollowers.this, ViewEmergency.class);
                    otherEmergency();

                } else if (position == 6) {
                    Intent i = new Intent(AddFollowers.this, MessageList.class);
                    startActivity(i);
                }
                else if(position ==7){
                    Intent i = new Intent(AddFollowers.this, MainActivity.class);
                    startActivity(i);
                }
                else if(position == 8){
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
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(AddFollowers.this);

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
        Toast.makeText(AddFollowers.this,
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
        String phone = split[2];
        String home = split[3];

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String userName = preferences.getString("NAME", "");
        for(User user2:users){
            if(user2.getName().equalsIgnoreCase(userName)){
                userPhone = user2.getPhone();
            }
        }
        System.out.println("Name:" + name);
        System.out.println("Email:" + email);

        new MyDownloadTask().execute(name,email, phone, home, userPhone);



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
            BufferedReader inBuffer = null;
            String url =string+"create_follower";
            String result = "fail";

            String name = params[0];
            String email = params[1];
            String phone = params[2];
            String address = params[3];
            String userPhone = params[4];


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("homeLocation", address));
                postParameters.add(new BasicNameValuePair("phoneNumber", phone));
                postParameters.add(new BasicNameValuePair("userPhone", userPhone));
                postParameters.add(new BasicNameValuePair("username", name));

                UrlEncodedFormEntity formEntity =
                        new UrlEncodedFormEntity(postParameters);

                request.setEntity(formEntity);
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
