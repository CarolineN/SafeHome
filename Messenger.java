package com.example.caroline.safehome;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import java.util.Date;
import java.util.List;

public class Messenger  extends ActionBarActivity {

    private EditText messageET;
    private ListView messagesContainer;
    private Button sendBtn;
    private ChatAdapter adapter;
    private ArrayList<Message> chatHistory;
private String followerNum;
        private String followerName;
    List<Message> messages;
   // DBHelper mydb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new GetEmergencyDetails().execute("http://192.168.0.7:8080/restEmergency");
        try {
        Thread.sleep(1000);
       } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Intent mIntent = getIntent();
        followerNum = mIntent.getExtras().getString("followerNum");
        followerName=mIntent.getExtras().getString("followerName");
        System.out.println("Tester" + followerNum);
        setContentView(R.layout.activity_messenger);
       // mydb = new DBHelper(this);
        initControls();
    }

    private void initControls() {
        messagesContainer = (ListView) findViewById(R.id.messagesContainer);
        messageET = (EditText) findViewById(R.id.messageEdit);
        sendBtn = (Button) findViewById(R.id.chatSendButton);

        TextView meLabel = (TextView) findViewById(R.id.meLbl);
        TextView companionLabel = (TextView) findViewById(R.id.friendLabel);
        RelativeLayout container = (RelativeLayout) findViewById(R.id.container);
        companionLabel.setText(followerName);// Hard Coded
        loadDummyHistory();


    }
    public void buttonClicked(View v){
        String messageText = messageET.getText().toString();
        if (TextUtils.isEmpty(messageText)) {
            return;
        }

        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String number = preferences.getString("PHONE", "");

        String dateTime = DateFormat.getDateTimeInstance().format(new Date());
//Here I would fill in the message info and send to the database and try to get it to come back out
      Message chatMessage = new Message(number, messageText, followerNum, dateTime);
        new MyDownloadTask().execute(number, messageText, followerNum,dateTime);
//        mydb.insertMessage(chatMessage);

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
            if((m.getMyPhone().equalsIgnoreCase(phone) && m.getFollowerPhone().equals(followerNum)) || (m.getMyPhone().equals(followerNum) && m.getFollowerPhone().equals(phone))){
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
            String url = "http://192.168.0.7:8080/create_emergency";
            String result = "fail";

            String myPhone = params[0];
            String message = params[1];
            String followerNum = params[2];
            String dateTime= params[3];


            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("myPhone", myPhone));
                postParameters.add(new BasicNameValuePair("message", message));
                postParameters.add(new BasicNameValuePair("followerNum", followerNum));
                postParameters.add(new BasicNameValuePair("dateTime",dateTime));



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
                    String dateTime = jsonObject.getString("dateTime");

                    Message co= new Message(myPhone,message,followerNum,dateTime);//original
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

}


