package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;

public class EmergContact extends AppCompatActivity {
    List<User> users;
    TextView text;
    EditText name1, email1, phone1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emergency);
        new GetUserDetails().execute("http://147.252.139.254:8080/restusers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tester, menu);
        return true;
    }
    public void onClick(View v) {

        if ((users.size() == 0) || (users == null)) {
            Toast.makeText(getApplicationContext(),
                    "There are no users to view",
                    Toast.LENGTH_LONG).show();
        } else {
            SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
            String userName = preferences.getString("NAME", "");
            name1 = (EditText) findViewById(R.id.nameTextView);
            email1 = (EditText) findViewById(R.id.emailTextView);
            phone1 = (EditText) findViewById(R.id.phoneTextView);
            String nameText = name1.getText().toString();
            String emailText = email1.getText().toString();
            String phoneText = phone1.getText().toString();


            for (User user1 : users) {
                if (user1.getName().equalsIgnoreCase(userName)) {

                    int id=user1.getId();
                    String usersID=String.valueOf(id);

                    new MyDownloadTask().execute(usersID, nameText,emailText,phoneText);
                }
            }
        }
        Intent intent = new Intent(this, firstPage.class);
        startActivity(intent);
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
    public class GetUserDetails extends AsyncTask<String, String, String> {

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
            String url = "http://147.252.139.254:8080/create_emergency";
            String result = "fail";


            String userID = params[0];
            String name = params[1];
            String email = params[2];
            String phone = params[3];

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);

                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();

                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("phoneNumber", phone));
                postParameters.add(new BasicNameValuePair("user_Id", userID));
                postParameters.add(new BasicNameValuePair("username",name));
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
}


