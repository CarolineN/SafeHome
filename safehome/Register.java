package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
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

/**
 * Created by Caroline on 4/9/2016.
 */
public class Register extends ActionBarActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    List<User> users;
    List<Notification> notifications;
    TextView text;
    String string;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private TextView signUpTextView;
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
        setContentView(R.layout.register);
        string=getString(R.string.IP);
        signUpTextView = (TextView) findViewById(R.id.signUpTextView);
        signUpTextView.setPaintFlags(signUpTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        Linkify.addLinks(signUpTextView, Linkify.ALL);

        editText2 = (EditText) findViewById(R.id.passwordTextView);
        editText1 = ((EditText) findViewById(R.id.nameTextView));

        editText3= ((EditText) findViewById(R.id.emailTextView));
        editText4= ((EditText) findViewById(R.id.HomeTextView));
        editText5 = ((EditText) findViewById(R.id.phoneTextView));
        editText1.addTextChangedListener(textWatcher);
        editText2.addTextChangedListener(textWatcher);
        editText3.addTextChangedListener(textWatcher);
        editText4.addTextChangedListener(textWatcher);
        editText5.addTextChangedListener(textWatcher);

        // run once to disable if empty
        checkFieldsForEmptyValues();

        getSupportActionBar().hide();



    }
    private  void checkFieldsForEmptyValues(){
        Button b = (Button) findViewById(R.id.email_sign_in_button);

        String name = ((TextView) findViewById(R.id.nameTextView)).getText().toString();
        String pass = ((TextView) findViewById(R.id.passwordTextView)).getText().toString();
        String em = ((TextView) findViewById(R.id.emailTextView)).getText().toString();
        String home = ((TextView) findViewById(R.id.HomeTextView)).getText().toString();
        String phone1 = ((TextView) findViewById(R.id.phoneTextView)).getText().toString();

        if(name.equals("") && pass.equals("")&& em.equals("") && home.equals("") && phone1.equals(""))
        {
            b.setEnabled(false);

        }

        else if(!name.equals("")&&pass.equals("")&& em.equals("") && home.equals("") && phone1.equals("")){
            b.setEnabled(false);

        }
        else if(name.equals("")&& !pass.equals("")&& em.equals("") && home.equals("") && phone1.equals("")){
            b.setEnabled(false);

        }
        else if(name.equals("")&&pass.equals("")&& !em.equals("") && home.equals("") && phone1.equals("")){
            b.setEnabled(false);

        }
        else if(name.equals("")&&pass.equals("")&& em.equals("") && !home.equals("") && phone1.equals("")){
            b.setEnabled(false);

        }
        else if(name.equals("")&&pass.equals("")&& em.equals("") && home.equals("") && !phone1.equals("")){
            b.setEnabled(false);

        }
        else
        {
            b.setEnabled(true);
        }
    }
public void login(View v){
    Intent intent = new Intent(this,MainActivity.class);
    startActivity(intent);
}

    public void onClick(View v) {

        String name = ((TextView) findViewById(R.id.nameTextView)).getText().toString();
        String pass = ((TextView) findViewById(R.id.passwordTextView)).getText().toString();
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
