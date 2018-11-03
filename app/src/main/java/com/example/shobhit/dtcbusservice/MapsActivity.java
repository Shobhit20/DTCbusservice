package com.example.shobhit.dtcbusservice;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int LOCATION_REQUEST = 1234;
    private EditText init_location, terminate_location;
    private LatLng origin_latlng, dest_latlng;
    String MY_API_KEY = "AIzaSyCUDyOqzhN4ig5poW4GhizcfHWcVYJAzwk";
    String start_location, end_location;

    private boolean check = false;
    ArrayList<String> list = new ArrayList<String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init_location = (EditText) findViewById(R.id.init_location);
        terminate_location = (EditText) findViewById(R.id.terminate_location);


        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST);
        }
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latlng = new LatLng(latitude, longitude );
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String strlocality = addresses.get(0).getLocality() + ", ";
                        String strcountry = addresses.get(0).getCountryName();
                        if (check == false){
                            mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource
                                    (R.drawable.curr_location)).position(latlng).title(strlocality + strcountry));
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 12f));
                            check = true;

                        }



                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });

        }
        else if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();
                    LatLng latlng = new LatLng(latitude, longitude );
                    Geocoder geocoder = new Geocoder(getApplicationContext());
                    try {
                        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                        String strlocality = addresses.get(0).getLocality() + ", ";
                        String strcountry = addresses.get(0).getCountryName();
                        mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource
                                (R.drawable.curr_location)).position(latlng).title(strlocality + strcountry));
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 15f));


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        }



    }


    private void locate() {
        Scanner Scan = new Scanner(getResources().openRawResource(R.raw.route73));
        while(Scan.hasNextLine()){
            String Line = Scan.nextLine();
            String[] parts = Line.split(":");
            String[] lat_lng = parts[1].split(",");
            Log.e(lat_lng[0].trim(), lat_lng[1].trim());
            LatLng latLng = new LatLng(Double.parseDouble(lat_lng[0].trim()), Double.parseDouble(lat_lng[1].trim()));
            mMap.addMarker(new MarkerOptions().position(latLng).title(parts[0]));
            list.add(parts[0]);

        }

    }

    private void pathfinder() {
        String url = getRequestUrl(origin_latlng, dest_latlng);
        Log.e("Url", url);
        TaskRequestDirections taskRequestDirections = new TaskRequestDirections();
        taskRequestDirections.execute(url);

    }

    private String getRequestUrl(LatLng origin, LatLng destination){
        String str_org = "origin=" + origin.latitude+","+origin.longitude;
        String str_dest = "destination=" + destination.latitude+","+destination.longitude;
        String sensor = "sensor=false";
        String mode = "mode=driving";
        String param = str_org + "&" + str_dest +"&"+ sensor +"&"+mode;
        String output="json";
        String url = "https://maps.googleapis.com/maps/api/directions/"+ output +"?"+ param+ "&key=" + MY_API_KEY;
        return url;

    }

    private String requestDirection(String reqUrl) throws IOException {
        String responseString = "";
        InputStream inputStream = null;
        HttpURLConnection httpURLConnection = null;
        try{
            URL url = new URL(reqUrl);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.connect();
            inputStream = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuffer stringBuffer = new StringBuffer();
            String line = "";
            while ((line = bufferedReader.readLine())!=null){
                stringBuffer.append(line);
            }
            responseString = stringBuffer.toString();
            bufferedReader.close();
            inputStreamReader.close();

        } catch (MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(inputStream !=null){
                inputStream.close();
            }
            httpURLConnection.disconnect();
        }
        return responseString;
    }

    private void init(){
        init_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction()==KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    String init_text = init_location.getText().toString();
                    start_location = init_text;
                    origin_latlng = geolocate(init_text);
                }
                return false;
            }
        });
    }

    private void terminate(){
        terminate_location.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || event.getAction()==KeyEvent.ACTION_DOWN
                        || event.getAction() == KeyEvent.KEYCODE_ENTER){
                    String terminate_text = terminate_location.getText().toString();
                    end_location = terminate_text;
                    dest_latlng = geolocate(terminate_text);
                    Log.e("Location", dest_latlng.toString() + origin_latlng.toString());
                    pathfinder();
                    Log.e("dfds", dest_latlng.toString());
                    fareprice_check();
                }
                return false;
            }
        });
    }

    private void fareprice_check() {
        RelativeLayout details = (RelativeLayout) findViewById(R.id.details);
        details.setVisibility(View.VISIBLE);
        TextView start_id = (TextView) findViewById(R.id.start);
        TextView end_id = (TextView) findViewById(R.id.end);
        TextView fare = (TextView) findViewById(R.id.fare);
        start_id.setText("Start\n" + start_location);
        end_id.setText("End\n" + end_location);
        fare.setText("Fare - X rs");


        Button farechart = (Button) findViewById(R.id.checkfare);
        farechart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), fareprice.class);
                intent.putExtra("map", list);
                intent.putExtra("start", start_location);
                intent.putExtra("end", end_location);
                startActivity(intent);

            }
        });
    }

    private LatLng geolocate(String search_text){

        Geocoder geocoder = new Geocoder(MapsActivity.this);
        List<Address> list_addresses = new ArrayList<>();
        try {
            list_addresses = geocoder.getFromLocationName(search_text, 1);


        }catch (IOException e){
            Log.e("Exception", "Error in fetching");
        }
        Address address = list_addresses.get(0);

        LatLng latlng_init = new LatLng(address.getLatitude(), address.getLongitude() );
        mMap.addMarker(new MarkerOptions().position(latlng_init).title("Start"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng_init, 15f));

        return latlng_init;
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        locate();
        init();
        terminate();

    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case LOCATION_REQUEST:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    Toast.makeText(this, "Permission already granted", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.

                }
                break;
        }

    }

    public class TaskRequestDirections extends AsyncTask<String, Void, String>{

        @Override
        protected String doInBackground(String... strings){
            String responseString = "";
            try {
                responseString = requestDirection(strings[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return responseString;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            TaskParser taskParser= new TaskParser();
            taskParser.execute(s);

        }
    }

    public class TaskParser extends AsyncTask<String, Void, List<List<HashMap<String, String>>> >{

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jsonObject = null;
            List<List<HashMap<String, String>>> routes = null;
            try {
                jsonObject = new JSONObject(strings[0]);
                DirectionsJSONParser directionsJSONParser = new DirectionsJSONParser();
                routes = directionsJSONParser.parse(jsonObject);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists){
            ArrayList points = null;
            PolylineOptions polylineOptions = null;
            for (List<HashMap<String, String>> path : lists){
                points = new ArrayList();
                polylineOptions = new PolylineOptions();

                for (HashMap<String, String> point : path){
                    double lat = Double.parseDouble(point.get("lat"));
                    double lon = Double.parseDouble(point.get("lng"));
                    points.add(new LatLng(lat, lon));
                }
                polylineOptions.addAll(points);
                polylineOptions.width(15);
                polylineOptions.color(Color.BLUE);
                polylineOptions.geodesic(true);

            }
            if (polylineOptions!=null){
                mMap.addPolyline(polylineOptions);

            }
            else {
                Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();

            }
        }
    }
}


