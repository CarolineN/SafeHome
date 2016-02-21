package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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




public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<User> users;
    List<Notification> notifications;
    TextView text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetUserDetails().execute("http://147.252.139.254:8080/restusers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    public void onClick(View v) {

        String name = ((TextView) findViewById(R.id.nameTextView)).getText().toString();
        String pass = ((TextView) findViewById(R.id.PasswordTextView)).getText().toString();
        String em = ((TextView) findViewById(R.id.emailTextView)).getText().toString();
        String home = ((TextView) findViewById(R.id.HomeTextView)).getText().toString();
        String phone1 = ((TextView) findViewById(R.id.phoneTextView)).getText().toString();
        new MyDownloadTask().execute(name, pass, em, home, phone1);

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ADDRESS",home);
        editor.putString("NAME", name);
        editor.putString("EMAIL", em);
        editor.putString("PASSWORD",pass);
        editor.putString("PHONE", phone1);
        editor.commit();
        Intent intent = new Intent(this,EmergContact.class);
        startActivity(intent);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SignUpFragment(), "Login");
        adapter.addFragment(new RegisterFragment(), "Register");

        viewPager.setAdapter(adapter);
    }
    public void onLogin(View v) {
        if ((users.size() == 0) || (users == null)) {
            Toast.makeText(getApplicationContext(),
                    "There are no users to view",
                    Toast.LENGTH_LONG).show();
        } else {

            text = (TextView) findViewById(R.id.errorText);
            String name = ((EditText) findViewById(R.id.nameText)).getText().toString();
            String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();
            for (User user1 : users) {
                if (user1.getName().equalsIgnoreCase(name) && user1.getPassword().equals(password)) {
                 //   text.setText("Correct");
                    Intent intent = new Intent(this,firstPage.class);
                    startActivity(intent);
                    SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putInt("ID",user1.getId());
                    editor.putString("ADDRESS",user1.getAddress());
                    editor.putString("NAME",user1.getName());
                    editor.putString("EMAIL",user1.getEmail());
                    editor.putString("PASSWORD",user1.getPassword());
                    editor.putString("PHONE", user1.getPhone());

                    editor.commit();
                    break;
                } else {
                    text.setText("Incorrect UserName");
                    ((EditText) findViewById(R.id.nameText)).setText("");
                    ((EditText) findViewById(R.id.passwordText)).setText("");
                }
                // text.append(user1.toString());

            }
        }
    }



    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
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
            String url = "http://147.252.139.254:8080/create_user";
            String result = "fail";

            String name = params[0];
            String password = params[1];
            String email = params[2];
            String address = params[3];
            String phone = params[4];


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("name", name));
                postParameters.add(new BasicNameValuePair("password", password));
                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("address", address));
                postParameters.add(new BasicNameValuePair("phone", phone));

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