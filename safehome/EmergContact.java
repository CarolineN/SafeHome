package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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

public class EmergContact extends AppCompatActivity {
    List<User> users;
    TextView text;
    EditText name1, email1, phone1;
    String string;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
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
        setContentView(R.layout.activity_emergency);
        string=getString(R.string.IP);
        editText2 = (EditText) findViewById(R.id.phoneTextView);
        editText1 = ((EditText) findViewById(R.id.nameTextView));
        editText3 =((EditText) findViewById(R.id.emailTextView));
        editText1.addTextChangedListener(textWatcher);
        editText2.addTextChangedListener(textWatcher);
        editText3.addTextChangedListener(textWatcher);
        checkFieldsForEmptyValues();
        getSupportActionBar().setTitle("Emergency Details");

    }
    private  void checkFieldsForEmptyValues(){
        Button b = (Button) findViewById(R.id.email_sign_in_button);

        String name = ((EditText) findViewById(R.id.nameTextView)).getText().toString();
        String password = ((EditText) findViewById(R.id.emailTextView)).getText().toString();
        String email =((EditText) findViewById(R.id.phoneTextView)).getText().toString();

        if(name.equals("") && password.equals("") && email.equals(""))
        {
            b.setEnabled(false);

        }

        else if(!name.equals("")&&password.equals("") && email.equals("")){
            b.setEnabled(false);

        }

        else if(name.equals("")&& !password.equals("") && email.equals(""))
        {
            b.setEnabled(false);

        }
        else if(name.equals("")&& password.equals("") && !email.equals(""))
        {
            b.setEnabled(false);

        }

        else
        {
            b.setEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tester, menu);
        return true;
    }
    public void onClick(View v) {

            SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
            String phone = preferences.getString("PHONE", "");
            name1 = (EditText) findViewById(R.id.nameTextView);
            email1 = (EditText) findViewById(R.id.emailTextView);
            phone1 = (EditText) findViewById(R.id.phoneTextView);
            String nameText = name1.getText().toString();
            String emailText = email1.getText().toString();
            String phoneText = phone1.getText().toString();

            new MyDownloadTask().execute(nameText,emailText,phoneText, phone);
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

    private class MyDownloadTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader inBuffer = null;
            String url =string+"create_journey";
            String result = "fail";

            String name = params[0];
            String email = params[1];
            String phone = params[2];
            String userId = params[3];


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("email", email));
                postParameters.add(new BasicNameValuePair("phoneNumber", phone));
                postParameters.add(new BasicNameValuePair("user_Id", userId));
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
}



