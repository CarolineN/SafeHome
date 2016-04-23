package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class Messenger  extends ActionBarActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private ImageButton sendBtn;
    List<Follower>followers;

    private ChatAdapter adapter;
    String reportDate="";
    private ArrayList<Message> chatHistory;
private String followerNum;
        private String fName;
    List<Message> messages;
   // DBHelper mydb;
String string;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ArrayAdapter<String> mAdapter;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        string=getString(R.string.IP);
        new GetEmergencyDetails().execute(string+"restEmergency");
        try {
        Thread.sleep(1000);
       } catch (InterruptedException e) {
            e.printStackTrace();
        }
        new GetFollowers().execute(string+"restfollowers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent mIntent = getIntent();
        followerNum = mIntent.getExtras().getString("followerNum");
       // followerName=mIntent.getExtras().getString("followerName");
        for(Follower f:followers){
            if(f.getPhoneNumber().equals(followerNum)){
                fName = f.getUsername();
            }
        }
        System.out.println("Tester" + followerNum);

        setContentView(R.layout.activity_messenger);
        //getSupportActionBar().setTitle(followerName);
       // mydb = new DBHelper(this);
        initControls();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setTitle(fName);
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        addDrawerItems();
        setupDrawer();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_messenger, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_cart:
                Toast.makeText(this, "The home button selected", Toast.LENGTH_SHORT).show();
                break;

        }
        return true;
    }
    private void addDrawerItems() {
        String[] osArray = { "Home","My Location", "Start Journey", "My Followers", "Notifications","Emergency Contacts","Messages"};
        mAdapter =(new MobileArrayAdapter(this, osArray));
        mDrawerList.setAdapter(mAdapter);
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Toast.makeText(MainActivity.this, "Time for an upgrade!", Toast.LENGTH_SHORT).show();
                //The order: Location, Journey, MyFollowers, AddFollowers,Notifications, EmergencyContacts, Messages
                if (position == 0) {
                    Intent appInfo = new Intent(Messenger.this, firstPage.class);
                    startActivity(appInfo);
                } else if (position == 1) {
                    Intent appInfo = new Intent(Messenger.this, Location.class);
                    startActivity(appInfo);
                } else if (position == 2) {
                    Intent i = new Intent(Messenger.this, Journey.class);
                    startActivity(i);
                } else if (position == 3) {
                    Intent i = new Intent(Messenger.this, MyFollowers.class);
                    startActivity(i);

                } else if (position == 4) {
                    Intent i = new Intent(Messenger.this, ViewNotification.class);
                    startActivity(i);
                } else if (position == 5) {
                    Intent i = new Intent(Messenger.this, ViewEmergency.class);
                    startActivity(i);
                } else if (position == 6) {
                    Intent i = new Intent(Messenger.this, Messenger.class);
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


    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (ImageButton) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
       // companionLabel.setText(followerName);// Hard Coded
        loadDummyHistory();


    }
    public void buttonClicked(View v){
        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String number = preferences.getString("PHONE", "");
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        try {
            Date today = dateFormat.parse(dateFormat.format(new Date()));
            reportDate = dateFormat.format(today);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar now2 = Calendar.getInstance();
        SimpleDateFormat d = new SimpleDateFormat("HH:mm");
        String t = d.format(now2.getTime());
        Message chatMessage = new Message(number, messageText, followerNum, reportDate,t);
        new MyDownloadTask().execute(number, messageText, followerNum,reportDate,t);


        messageET.setText("");
        displayMessage(chatMessage);

    }

    public void displayMessage(Message message) {
        adapter.add(message);
        adapter.notifyDataSetChanged();
        scroll();
    }

    private void scroll() {
        messagesContainer.setSelection(messagesContainer.getCount() - 1);
    }

    private void loadDummyHistory() {
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        //String userName= preferences.getString("NAME","");
        String phone = preferences.getString("PHONE", "");
        chatHistory = new ArrayList<Message>();


        for(Message m:messages){
            if((m.getMyPhone().equalsIgnoreCase(phone) && m.getFollowerNum().equals(followerNum)) || (m.getMyPhone().equals(followerNum) && m.getFollowerNum().equals(phone))){
                chatHistory.add(m);
            }

        }
        //chatHistory.add(msg);


        adapter = new ChatAdapter(Messenger.this, new ArrayList<Message>());
        messagesContainer.setAdapter(adapter);

        for (int i = 0; i < chatHistory.size(); i++) {
            Message message = chatHistory.get(i);
            displayMessage(message);
        }
    }
    private class MyDownloadTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader inBuffer = null;
            String url = string+"create_emergency";
            String result = "fail";

            String myPhone = params[0];
            String message = params[1];
            String followerNum = params[2];
            String date= params[3];
            String time=params[4];


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("myPhone", myPhone));
                postParameters.add(new BasicNameValuePair("message", message));
                postParameters.add(new BasicNameValuePair("followerNum", followerNum));
                postParameters.add(new BasicNameValuePair("date",date));
                postParameters.add(new BasicNameValuePair("time",time));



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
    public class GetEmergencyDetails extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;
            messages = new ArrayList<>();
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
                    String myPhone = jsonObject.getString("myPhone");
                    String message = jsonObject.getString("message");
                    String followerNum = jsonObject.getString("followerNum");//original
                    String date = jsonObject.getString("date");
                    String time = jsonObject.getString("time");

                    Message co= new Message(myPhone,message,followerNum,date,time);//original
                    co.setId(id);
                    messages.add(co);//original;
                }
                System.out.println("List Size1... " + messages.size());
                for (Message contact1 : messages) {
                    System.out.println(contact1.toString());

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

}


