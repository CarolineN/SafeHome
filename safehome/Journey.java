package com.example.caroline.safehome;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.ShareApi;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.ui.IconGenerator;

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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Journey extends FragmentActivity implements
        LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = "LocationActivity";
    private  long INTERVAL;
    private  long FASTEST_INTERVAL;
    public String distance = "";
    public String duration = "";
    LocationRequest mLocationRequest;
    GoogleApiClient mGoogleApiClient;
    Location mCurrentLocation;
    String reportDate="";
    public Context context;
    private BroadcastReceiver receiver;
    String mLastUpdateTime;
    ArrayList<LatLng> markerPoints;
    String ves= "";
    String location1="";
    TextView tvDistanceDuration;
    GoogleMap googleMap;

    LatLng homeAddress;
    boolean messageSent = false;
    LatLng tester;
    //List<EmergencyContact> emergency;
    EditText destination;
   // List<User> users;
    boolean justOnce=false;
    boolean firstTime = false;
    //List<Follower>follower2;
    public List<String>names;
    List<Follower>followers;
    String string;




    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        string=getString(R.string.IP);
        new GetFollowers().execute(string+"restfollowers");
        try{
            Thread.sleep(1000);
        }catch( InterruptedException e){
            e.printStackTrace();
        }
        Log.d(TAG, "onCreate ...............................");
        //show error dialog if GoolglePlayServices not available
        if (!isGooglePlayServicesAvailable()) {
            finish();
        }
        Intent mIntent = getIntent();
        INTERVAL = mIntent.getExtras().getLong("in");
        FASTEST_INTERVAL = mIntent.getExtras().getLong("in1");
        createLocationRequest(INTERVAL, FASTEST_INTERVAL);
        this.context=context;
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        String phone = preferences.getString("PHONE", "");
        String address = preferences.getString("ADDRESS","");


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();

        setContentView(R.layout.activity_journey);
        tvDistanceDuration = (TextView) findViewById(R.id.tv_distance_time);
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();
        googleMap.getUiSettings().setZoomControlsEnabled(true);

        EditText myTextBox = (EditText) findViewById(R.id.et_location);
        myTextBox.setText(address + "");
        names = new ArrayList<>();
        context=this;
       String e="";
                for(Follower follower:followers){
                    if(follower.getUserPhone().equalsIgnoreCase(phone)){
                        e = follower.getPhoneNumber();
                        //System.out.println(e);
                        names.add(e);
                    }


        }


        //Broadcast receiver
        receiver  = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //Add current time

                Calendar rightNow = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("h:mm:ss a");
                String strDate = sdf.format(rightNow.getTime());
                ves = intent.getStringExtra("activity") + "\n";
            }
        };

        //Filter the Intent and register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction("ImActive");
        registerReceiver(receiver, filter);
        // Setting a custom info window adapter for the google map
        googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {// Use default InfoWindow frame
            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            // Defines the contents of the InfoWindow
            @Override
            public View getInfoContents(Marker arg0) {
                SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
                String userName = preferences.getString("NAME", "");

                // Getting view from the layout file info_window_layout
                View v = getLayoutInflater().inflate(R.layout.info_window_layout, null);

                // Getting the position from the marker
                LatLng latLng = arg0.getPosition();

                // Getting reference to the TextView to set latitude
                TextView address = (TextView) v.findViewById(R.id.address);

                TextView name = (TextView) v.findViewById(R.id.name);

                //TextView activity = (TextView) v.findViewById(R.id.name);
                long atTime = mCurrentLocation.getTime();
                mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));

                address.setText("Location:" + getMyLocationAddress(latLng.latitude, latLng.longitude));
                //speed.setText("Speed:" + (mCurrentLocation.getSpeed() * 3.6));
                name.setText(userName);

                return v;
            }
        });

    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart fired ..............");
        mGoogleApiClient.connect();
    }
    protected void createLocationRequest(long interval, long otherInterval) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(interval);
        mLocationRequest.setFastestInterval(otherInterval);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop fired ..............");
        mGoogleApiClient.disconnect();
        Log.d(TAG, "isConnected ...............: " + mGoogleApiClient.isConnected());
    }

    private boolean isGooglePlayServicesAvailable() {
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (ConnectionResult.SUCCESS == status) {
            return true;
        } else {
            GooglePlayServicesUtil.getErrorDialog(status, this, 0).show();
            return false;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Intent i = new Intent(this, ActivityRecognitionIntentService.class);
        PendingIntent mActivityRecongPendingIntent = PendingIntent
                .getService(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(mGoogleApiClient, 0, mActivityRecongPendingIntent);
        Log.d(TAG, "onConnected - isConnected ...............: " + mGoogleApiClient.isConnected());
        startLocationUpdates();
    }

    protected void startLocationUpdates() {
        PendingResult<Status> pendingResult = LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
        Log.d(TAG, "Location update started ..............: ");
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Connection failed: " + connectionResult.toString());
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Firing onLocationChanged..............................................");
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        //-----
        addMarker();
    }

    public String getMyLocationAddress(double lon, double lat) {

        Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);
        String address = "";

        try {

            //Place your latitude and longitude
            List<Address> addresses = geocoder.getFromLocation(lon, lat, 1);

            if (addresses != null) {

                Address fetchedAddress = addresses.get(0);
                StringBuilder strAddress = new StringBuilder();

                for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                    strAddress.append(fetchedAddress.getAddressLine(i)).append(",");
                }
                address = strAddress.toString();
            }


        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
        }
        return address;
    }
    public void destination(View view){
        destination=(EditText)findViewById(R.id.et_location);
        location1 = destination.getText().toString();
        if(location1!=null){
            tester =setAddressToLatLng(location1);
            googleMap.addMarker(new MarkerOptions().position(tester));
            destination.setText("");
            SharedPreferences preferences = getSharedPreferences("Journey", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("DESTINATION", location1);
            editor.commit();
            LatLng new1= new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());


            String url = getDirectionsUrl(new1, tester);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);



        }

    }
    private LatLng setAddressToLatLng(String address) {//Brand new method to try out!!
        Geocoder myCoder = new Geocoder(getApplicationContext());
        List<Address> address1;
        try {
            address1 = myCoder.getFromLocationName(address, 5);
            if (address1 == null) {
                Log.d("In if", "Address is null");
            }
            Address location = address1.get(0);
            String lat = "" + location.getLatitude();
            String lng = "" + location.getLongitude();
            homeAddress = new LatLng(location.getLatitude(), location.getLongitude());

        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Error with address", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
        return homeAddress;

    }


    private void addMarker() {
        MarkerOptions options = new MarkerOptions();
        IconGenerator iconFactory = new IconGenerator(this);
        iconFactory.setStyle(IconGenerator.STYLE_PURPLE);

        options.icon(BitmapDescriptorFactory.fromBitmap(iconFactory.makeIcon(mLastUpdateTime)));
        options.anchor(iconFactory.getAnchorU(), iconFactory.getAnchorV());

        LatLng currentLatLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        options.position(currentLatLng);
        Marker mapMarker = googleMap.addMarker(options);

        long atTime = mCurrentLocation.getTime();
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date(atTime));
        mapMarker.showInfoWindow();
        Log.d(TAG, "Marker added.............................");
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng,
                10));
        Log.d(TAG, "Zoom done.............................");
        //Changes from here

        SharedPreferences preferences = getSharedPreferences("Journey", Context.MODE_PRIVATE);
        //String userName= preferences.getString("NAME","");
        String loc = preferences.getString("DESTINATION", "");
        tester =setAddressToLatLng(loc);
        if(justOnce) {


            String url = getDirectionsUrl(currentLatLng, tester);

            DownloadTask downloadTask = new DownloadTask();

            // Start downloading json data from Google Directions API
            downloadTask.execute(url);

        }
        justOnce=true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        Log.d(TAG, "Location update stopped .......................");
    }
    private String getDirectionsUrl(LatLng origin,LatLng dest){

        // Origin of route
        String str_origin = "origin="+origin.latitude+","+origin.longitude;

        // Destination of route
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        // Sensor enabled
        String sensor = "sensor=false";

        // Building the parameters to the web service
        String parameters = str_origin+"&"+str_dest+"&"+sensor;

        // Output format
        String output = "json";

        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        return url;
    }


    /** A method to download json data from url */
    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creating an http connection to communicate with url
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }

    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
           //this.context = context.getApplicationContext();
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
//            String distance = "";
//            String duration = "";

            if (result.size() < 1) {
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for (int i = 0; i < result.size(); i++) {
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    if (j == 0) {    // Get distance from the list
                        distance = (String) point.get("distance");
                        continue;
                    } else if (j == 1) { // Get duration from the list
                        duration = (String) point.get("duration");
                        continue;
                    }

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }
                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(2);
                lineOptions.color(Color.RED);
            }
//
            Calendar nowish = Calendar.getInstance();
            SimpleDateFormat df = new SimpleDateFormat("HH:mm");
            String[] splits = distance.split(" ");
            String firstWord = splits[0];
            double value = Double.parseDouble(firstWord);
            double time = (value / 5);
            int min = (int) Math.round(time * 60);
            nowish.add(Calendar.MINUTE, min);
            String time1 = df.format(nowish.getTime());
            String[] animals = duration.split(" ");
            String first = animals[0];
            Calendar now = Calendar.getInstance();
            int foo = Integer.parseInt(first);
            now.add(Calendar.MINUTE, foo);

            String time2 = df.format(now.getTime());
            tvDistanceDuration.setText("Distance:" + distance + ", Duration:" + duration + "\n" + ", Arrival Time(Car):" + time2 + "" + "Arrival Time(Walking)" + time1);
            //}
            SharedPreferences preferences = getSharedPreferences("Journey", Context.MODE_PRIVATE);
            //String userName= preferences.getString("NAME","");
            String loc1 = preferences.getString("DESTINATION", "");
            String messageText;
            if (ves.equalsIgnoreCase("In Vehicle")) {
                messageText =
                        "Destination:"  +loc1 + "\n" + "Distance:" + distance + "\n"  + "Duration:" + duration +
                                "\n" + "Arrival Time(Car):" + time2 + "\n" +"Movement:In Car";

            }
            else if(ves.equalsIgnoreCase("On Foot")){
                messageText ="Destination:" +loc1 + "\n"  +"Distance:" + distance +
                        "\n" + "Arrival Time(Walking):" + time1 + "\n" +"Movement:Walking";
            }
            else{
                messageText ="Destination:" +loc1 + "\n" + "Distance:" + distance +
                        "\n" +  "Arrival Time(Car):" + time2 + "\n" +  "Arrival Time(Walking):" + time1 + "\n" +"Movement:Still";
            }
            googleMap.addPolyline(lineOptions);

            LatLng latLng = new LatLng(mCurrentLocation.getLatitude(),mCurrentLocation.getLongitude());
            String message;
            if(!firstTime) {
            message = "Journey: Started!" + "Destination:" + " " + loc1 + " "+ "Distance:" + " " + distance + " " + "Current Location:" + getMyLocationAddress(latLng.latitude, latLng.longitude);
                }
            else{
            message ="Destination:" + " " + loc1 + " " + "Distance:" + " " + distance + " " + "Current Location:" + getMyLocationAddress(latLng.latitude, latLng.longitude);
            }
            SharedPreferences preferences1 = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
            int idNew = preferences1.getInt("ID", 0);
            String usersName = preferences1.getString("NAME", "");




            //sendSMSMessage(emergPhone,messageText);// taken out cos the messages are driving me mad at the moment!!!!
            for (int j = 0; j < names.size(); j++){// names is an arraylist of follower numbers
                String fPhone = names.get(j);
                Calendar now2 = Calendar.getInstance();
                SimpleDateFormat d = new SimpleDateFormat("HH:mm");
                String t = d.format(now2.getTime());
                Log.d("Tester", fPhone + " " + t + ""  + message + " " + usersName);
                System.out.println("reaching");
                new MyDownloadTask().execute(fPhone, message, t,usersName);
            }
            String[] split = distance.split("\\s+");
            String nmber = split[0];
            double foo1 = Double.parseDouble(nmber);
            if (foo1 <= 1.00) {
                System.out.println("hhhhhhhhhhhh" + " it is less than 1 km");
                for (int j = 0; j < names.size(); j++){// names is an arraylist of follower numbers
                    String fPhone = names.get(j);
                    Calendar now2 = Calendar.getInstance();
                    SimpleDateFormat d = new SimpleDateFormat("HH:mm");
                    String t = d.format(now2.getTime());
                    String m="Finished their journey";
                    Log.d("Tester", fPhone + " " + t + ""  + m + " " + usersName);
                    System.out.println("reaching");
                    new MyDownloadTask().execute(fPhone, m, t,usersName);
                }

                context.startActivity(new Intent(context, PostToWall.class));
            }
            firstTime=true;
        }
        // public void sendSMSMessage(String number, String message) {
//            try {
//                if (!messageSent) {
//                    SmsManager smsManager = SmsManager.getDefault();
//                    smsManager.sendTextMessage(number, null,  " Journey Started : " +message, null, null);
//                }
//
//            } catch (Exception e) {
//                Toast.makeText(getApplicationContext(), "Sms failed", Toast.LENGTH_LONG).show();
//                e.printStackTrace();
//            }
//            messageSent = true;
//
//        }
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


    private class MyDownloadTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            BufferedReader inBuffer = null;
            String url = string+"create_notification";
            String result = "fail";

            String fPhone = params[0];
            String message = params[1];
            String time = params[2];
            String users_name = params[3];
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            try {
                Date today = dateFormat.parse(dateFormat.format(new Date()));
                reportDate = dateFormat.format(today);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("Checking" + fPhone + message + time + users_name);

            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost request = new HttpPost(url);
                List<NameValuePair> postParameters =
                        new ArrayList<NameValuePair>();
                postParameters.add(new BasicNameValuePair("followerNum", fPhone));
                postParameters.add(new BasicNameValuePair("message", message));
                postParameters.add(new BasicNameValuePair("time", time));
                postParameters.add(new BasicNameValuePair("date",reportDate));
                postParameters.add(new BasicNameValuePair("usersName", users_name));


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
