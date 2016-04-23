package com.example.caroline.safehome;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
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




public class MainActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<User> users;
    List<Notification> notifications;
    TextView text;
    private View loginFormView;
    private View progressView;
    String string;
    private TextView signUpTextView;
    private EditText editText1;
    private EditText editText2;
    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3)
        {
            Button b = (Button) findViewById(R.id.email_sign_in_button);
            b.setBackgroundColor(Color.GRAY);
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            checkFieldsForEmptyValues();
        }

        @Override
        public void afterTextChanged(Editable editable) {
            Button b = (Button) findViewById(R.id.email_sign_in_button);
            b.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#184e58")));
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        string=getString(R.string.IP);
        new GetUserDetails().execute(string + "restusers");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        getSupportActionBar().hide();
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        signUpTextView.setPaintFlags(signUpTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(signUpTextView, Linkify.ALL);

        progressView = findViewById(R.id.login_progress);
        editText1 = (EditText) findViewById(R.id.nameText);
        editText2 = (EditText) findViewById(R.id.passwordText);

        //set listeners
        editText1.addTextChangedListener(textWatcher);
        editText2.addTextChangedListener(textWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();
        if(!isNetworkAvailable()){
            isInternet();
        }

    }
    private  void checkFieldsForEmptyValues(){
        Button b = (Button) findViewById(R.id.email_sign_in_button);

        String name = ((EditText) findViewById(R.id.nameText)).getText().toString();
        String password = ((EditText) findViewById(R.id.passwordText)).getText().toString();

        if(name.equals("") && password.equals(""))
        {
            b.setEnabled(false);

        }

        else if(!name.equals("")&&password.equals("")){
            b.setEnabled(false);

        }

        else if(!name.equals("")&&password.equals(""))
        {
            b.setEnabled(false);
            
        }

        else
        {
            b.setEnabled(true);
        }
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
public void isInternet(){
    final AlertDialog.Builder builder = new AlertDialog.Builder(
            MainActivity.this);
    builder.setTitle("Safe Home");
    builder.setMessage("Turn on Wifi");
    builder.setPositiveButton("Enable Internet",
            new DialogInterface.OnClickListener() {
                @Override
                public void onClick(
                        final DialogInterface dialogInterface,
                        final int i) {
                    startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                    MainActivity.this.onRestart();
                }
            });
    builder.setNegativeButton("Continue", null);
    builder.create().show();
}

    public void onClick(View v) {

        String name = ((TextView) findViewById(R.id.nameTextView)).getText().toString();
        String pass = ((TextView) findViewById(R.id.passwordText)).getText().toString();
        String em = ((TextView) findViewById(R.id.emailTextView)).getText().toString();
        String home = ((TextView) findViewById(R.id.HomeTextView)).getText().toString();
        String phone1 = ((TextView) findViewById(R.id.phoneTextView)).getText().toString();
        new MyDownloadTask().execute(name, pass, em, home, phone1);

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString("ADDRESS",home);
        editor.putString("NAME", name);
        editor.putString("EMAIL", em);
        editor.putString("PASSWORD", pass);
        editor.putString("PHONE", phone1);

        editor.commit();
        Intent intent = new Intent(this,EmergContact.class);
        startActivity(intent);
    }
public void register(View v){
    Intent intent = new Intent(this,Register.class);
    startActivity(intent);
}

    public void onLogin(View v) {
        if ((users.size() == 0) || (users == null)) {
            Toast.makeText(getApplicationContext(),
                    "There are no users to view",
                    Toast.LENGTH_LONG).show();
        } else {


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

                    ((EditText) findViewById(R.id.nameText)).setText("");
                    ((EditText) findViewById(R.id.passwordText)).setText("");
                }
                // text.append(user1.toString());

            }
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
                connection.setConnectTimeout(20000);
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

            String url = string+"create_user";
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