package com.example.caroline.safehome;

import android.content.Context;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;

import android.os.AsyncTask;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class Location extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    ArrayList<LatLng> markerPoints;//directions onto map code
    private GoogleApiClient mGoogleApiClient;
    private TextView addressText;
    LatLng homeAddress;
    public static final String TAG = Location.class.getSimpleName();
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    private boolean messageSent = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);
        addressText = (TextView) findViewById(R.id.addressText);

        //Directions onto Map
        markerPoints = new ArrayList<LatLng>();


        setUpMapIfNeeded();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                .setFastestInterval(1 * 1000); // 1 second, in milliseconds


    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
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

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                // setUpMap();
            }
        }
    }


//    private void setUpMap() {
//        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
//
//    }

    @Override
    public void onConnected(Bundle bundle) {
        android.location.Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (location == null) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } else {
            handleNewLocation(location);
        }
    }

    private void handleNewLocation(android.location.Location location) {
        Log.d(TAG, location.toString());
        SharedPreferences preferences = getSharedPreferences("LOGIN_INFO", Context.MODE_PRIVATE);
        //String userName= preferences.getString("NAME","");
        String address = preferences.getString("ADDRESS", "");
        LatLng test = setAddressToLatLng(address);
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();
        LatLng latLng = new LatLng(currentLatitude, currentLongitude);
        // LatLng athboy = new LatLng(53.623633, -6.915637);// HERE!!! this needs to be taken from the shared preference
        //mMap.moveCamera(yourLocation);// old code//
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(currentLatitude, currentLongitude), 11));//new code
        getMyLocationAddress(currentLatitude, currentLongitude);
        setDistance(currentLatitude, currentLongitude, test);//adding the homelocation to the set distance
        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("I am here!");//HERE!!! below I am adding the athboy LatLng to the map. should be from sharedpreferences
        MarkerOptions option1 = new MarkerOptions().position(test).title("Home").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
        mMap.addMarker(option1);
        mMap.addMarker(options);//HERE!!! Polyline between user address and current location
       // Polyline line = mMap.addPolyline(new PolylineOptions().add(new LatLng(test.latitude, test.longitude), new LatLng(latLng.latitude, latLng.longitude)).width(5).color(Color.BLACK));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                .zoom(14)                   // Sets the zoom
                .bearing(20)                // Sets the orientation of the camera to east
                .tilt(40)                   // Sets the tilt of the camera to 30 degrees
                .build();                   // Creates a CameraPosition from the builder
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

        String url = getDirectionsUrl(latLng, test);

        DownloadTask downloadTask = new DownloadTask();

        // Start downloading json data from Google Directions API
        downloadTask.execute(url);

    }






        @Override
        public void onConnectionSuspended(int i) {
            Log.i(TAG, "Location services suspended. Please reconnect.");
        }

        @Override
        public void onConnectionFailed(ConnectionResult connectionResult) {
            if (connectionResult.hasResolution()) {
                try {
                    // Start an Activity that tries to resolve the error
                    connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            } else {
                Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
            }
        }

        public void setDistance(double lon, double lat, LatLng test) {
            //  LatLng homeLocation = new LatLng(53.623633, -6.915637);//HERE!!!!! should be from shared preference aswell!!
            LatLng location = new LatLng(lon, lat);
            double locationLat = location.latitude;
            double locationLon = location.longitude;
            // double homeLat = homeLocation.latitude;
            // double homeLon = homeLocation.longitude;
            calcDist(test, location);
        }



        public void calcDist(LatLng homeLocation, LatLng location) {
            double earthRadius = 6371.00;
            double dLat = Math.toRadians(location.latitude - homeLocation.latitude);
            double dLng = Math.toRadians(location.longitude - homeLocation.longitude);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                    Math.cos(Math.toRadians(homeLocation.latitude)) * Math.cos(Math.toRadians(location.latitude)) *
                            Math.sin(dLng / 2) * Math.sin(dLng / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            System.out.println("Disatnce is..." + c);
            DecimalFormat f = new DecimalFormat("##.00");
            TextView homeText = (TextView) findViewById(R.id.homeText);
            double value = earthRadius * c;

            homeText.setText("" + f.format(value) + "km" + "" + " from home." + "\n");
            if (value <= 1.00) {
                sendSMSMessage();
            }
        }

        public void sendSMSMessage() {//HERE!! add location and distance from home to the text
            try {//HERE!! should also take emergency contact from saved place
                if (!messageSent) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("0862057325", null, "Hi, I am less than 1km from home", null, null);
                }

            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Sms failed", Toast.LENGTH_LONG).show();
                e.printStackTrace();
            }
            messageSent = true;

        }

        // homeText.setText(distanceInMeters.toString());
//    mMap.addMarker(new MarkerOptions().position(reportLocation).title(r.getReportCategory()));


        @Override
        public void onLocationChanged(android.location.Location location) {
            handleNewLocation(location);
        }

        public void getMyLocationAddress(double lon, double lat) {

            Geocoder geocoder = new Geocoder(this, Locale.ENGLISH);

            try {

                //Place your latitude and longitude
                List<Address> addresses = geocoder.getFromLocation(lon, lat, 1);

                if (addresses != null) {

                    Address fetchedAddress = addresses.get(0);
                    StringBuilder strAddress = new StringBuilder();

                    for (int i = 0; i < fetchedAddress.getMaxAddressLineIndex(); i++) {
                        strAddress.append(fetchedAddress.getAddressLine(i)).append("\n");
                    }

                    addressText.setText(strAddress.toString());

                } else
                    addressText.setText("No location found..!");

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getApplicationContext(), "Could not get address..!", Toast.LENGTH_LONG).show();
            }
        }
    //FROM HERE CAROLINE
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

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
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
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();
            String distance = "";
            String duration = "";

            if(result.size()<1){
                Toast.makeText(getBaseContext(), "No Points", Toast.LENGTH_SHORT).show();
                return;
            }

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    if(j==0){    // Get distance from the list
                        distance = (String)point.get("distance");
                        continue;
                    }else if(j==1){ // Get duration from the list
                        duration = (String)point.get("duration");
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



            // Drawing polyline in the Google Map for the i-th route
            mMap.addPolyline(lineOptions);
        }
    }



}



