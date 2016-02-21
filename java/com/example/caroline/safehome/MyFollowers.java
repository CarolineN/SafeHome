package com.example.caroline.safehome;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
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

public class MyFollowers extends AppCompatActivity {
    List<User> users;

    List<Follower>followers;
    List<Follower>follower2;
    List<String>names;
    ListView listView;
    String name;


    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followers);

        new GetUsers().execute("http://147.252.139.254:8080/restusers");
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
      for (User user1 : users) {
          if (user1.getName().equalsIgnoreCase(name)) {

              for(Follower f1: user1.getFollowers()){
                  follower2.add(f1);
              }

              for(Follower follower:follower2){
                 e= follower.getUsername();
                  System.out.println(e);
                  names.add(e);
              }
             // names.add(e);
              Log.d("MyTagGoesHere", e);
          }
      }
        //System.out.println(e);

        if(followers!=null) {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, names);
            listView.setAdapter(adapter);
        }
        else {
            listView = (ListView) findViewById(R.id.list);
            ArrayAdapter<User> adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_1, users);
            listView.setAdapter(adapter);
        }

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            public boolean onItemLongClick(AdapterView<?> arg0, View v,
                                           int index, long arg3) {

                Toast.makeText(MyFollowers.this, listView.getItemAtPosition(index).toString() + " Do you want to delete?", Toast.LENGTH_LONG).show();
                return false;

            }
        });
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
                            f2.setUsername(n);
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

