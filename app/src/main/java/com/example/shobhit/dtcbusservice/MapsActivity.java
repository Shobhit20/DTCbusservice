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
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    LocationManager locationManager;
    private static final int LOCATION_REQUEST = 1234;
    private AutoCompleteTextView init_location, terminate_location;
    private LatLng origin_latlng, dest_latlng;
    String MY_API_KEY = "AIzaSyCUDyOqzhN4ig5poW4GhizcfHWcVYJAzwk";
    String start_location, end_location;
    int index_init_location, index_end_location;

    private boolean check = false;
    private boolean init_bool = false;
    ArrayList<String> list = new ArrayList<String>();
    ArrayList<LatLng> lat_lng_route = new ArrayList<>();
    int search_init = 0;
    int search_terminal = 0;
    int track_token;
    Marker moving_marker;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        init_location = (AutoCompleteTextView) findViewById(R.id.init_location);
        terminate_location = (AutoCompleteTextView) findViewById(R.id.terminate_location);


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
            lat_lng_route.add(latLng);
            list.add(parts[0]);

        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, list);
        init_location.setAdapter(adapter);


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
                    if (search_init > 0){
                        mMap.clear();
                        init_bool =false;
                    }
                    String init_text = init_location.getText().toString();
                    for (int i=0;i<list.size();i++) {
                        if (list.get(i).equals(init_text)) {
                            index_init_location = i;
                            break;
                        }
                    }
                    Log.e("Array", String.valueOf(index_init_location));

                    start_location = init_text;
                    origin_latlng = geolocate(init_text);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1, list.subList(index_init_location, list.size()));

                    terminate_location.setAdapter(adapter);
                    init_bool = true;
                    search_init ++;
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
                    if (search_init > 0){
                        mMap.clear();
                    }
                    String terminate_text = terminate_location.getText().toString();
                    end_location = terminate_text;
                    dest_latlng = geolocate(terminate_text);
                    Log.e("Location", dest_latlng.toString() + origin_latlng.toString());
                    pathfinder();
                    for (int i=0;i<list.size();i++) {
                        if (list.get(i).equals(end_location)) {
                            index_end_location = i;
                            break;
                        }
                    }
                    Log.e("dfds", dest_latlng.toString());
                    fareprice_check();
                    plot_intermediate();
                    search_terminal++;

                }
                return false;
            }
        });
    }

    private void plot_intermediate() {
        for(int i=index_init_location; i < index_end_location; i++){
            mMap.addMarker(new MarkerOptions().position(lat_lng_route.get(i)).title(list.get(i)));
        }
    }

    private void fareprice_check() {
        RelativeLayout details = (RelativeLayout) findViewById(R.id.details);
        details.setVisibility(View.VISIBLE);
        TextView start_id = (TextView) findViewById(R.id.start);
        TextView end_id = (TextView) findViewById(R.id.end);
        Button fare = (Button) findViewById(R.id.fare);
        start_id.setText("Start\n" + start_location);
        end_id.setText("End\n" + end_location);
        int diff = index_end_location - index_init_location;
        if(diff <= 10){
            fare.setText("Fare is 5 Rs");
        }else if(diff > 10 && diff<=20){
            fare.setText("Fare is 10 Rs");
        }else if(diff > 20 && diff<=30){
            fare.setText("Fare is 15 Rs");
        }else{
            fare.setText("Fare is 20 Rs");
        }
        fare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(MapsActivity.this, qrcode_scanner.class), 73);
            }
        });


        Button farechart = (Button) findViewById(R.id.checkfare);
        farechart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), fareprice.class);
                intent.putExtra("map", list);
                intent.putExtra("start", index_init_location);
                intent.putExtra("end", index_end_location);
                startActivity(intent);

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 73){
            if (resultCode == RESULT_OK){
                boolean result = data.getBooleanExtra("paid", false);
                Log.e("The value is", String.valueOf(result));
                if(result){
                    Button fare = (Button) findViewById(R.id.fare);
                    fare.setText("Paid 20 Rs");
                    fare.setEnabled(false);
                    TextView start_id = (TextView) findViewById(R.id.start);
                    TextView end_id = (TextView) findViewById(R.id.end);
                    start_id.setVisibility(View.INVISIBLE);
                    end_id.setVisibility(View.INVISIBLE);
                    Button farechart = (Button) findViewById(R.id.checkfare);
                    farechart.setText("Live Tracking");
                    mMap.clear();
                    track_token = index_init_location;
                    mMap.addMarker(new MarkerOptions().position(lat_lng_route.get(index_init_location)).title("Boarding - "+list.get(index_init_location)));

                    mMap.addMarker(new MarkerOptions().position(lat_lng_route.get(index_end_location)).title("Dropping - " + list.get(index_end_location)));

                    final Timer timer = new Timer("MyTimer");//create a new Timer
                    TimerTask timerTask = new TimerTask() {

                        @Override
                        public void run() {
                            if (track_token < index_end_location){
                                track();
                            }
                            else{
                                finish();
                                overridePendingTransition(0, 0);
                                startActivity(getIntent());
                                overridePendingTransition(0, 0);
                                timer.cancel();
                                timer.purge();
                            }


                        }
                    };




                    timer.scheduleAtFixedRate(timerTask, 5000, 5000);


                    farechart.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(v.getContext(), livetracking.class);
                            intent.putExtra("map", list);
                            intent.putExtra("start", track_token);
                            intent.putExtra("end", index_end_location);
                            startActivity(intent);
                        }
                    });

                }
            }
        }
    }

    private void track() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                // Your code to run in GUI thread here
                track_token++;
                if(track_token > index_init_location + 1){
                    moving_marker.remove();
                }
                moving_marker = mMap.addMarker(new MarkerOptions().icon(BitmapDescriptorFactory.fromResource
                        (R.drawable.bus_show)).position(lat_lng_route.get(track_token)).title("Curr - "+list.get(track_token)));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng_route.get(track_token), 15f));
                Toast.makeText(getApplicationContext(), "Reached " +list.get(track_token), Toast.LENGTH_LONG).show();
            }
        });
    }


    private LatLng geolocate(String search_text){
        int i;
        for (i=0;i<list.size();i++) {
            if (list.get(i).equals(search_text)) {
                break;
            }
        }
        if(init_bool != true){
            mMap.addMarker(new MarkerOptions().position(lat_lng_route.get(i)).title("Boarding - " +start_location ));
        }
        else{
            mMap.addMarker(new MarkerOptions().position(lat_lng_route.get(i)).title("Dropping - " +end_location ));
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lat_lng_route.get(i), 12f));

        return lat_lng_route.get(i);
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
                //Toast.makeText(getApplicationContext(), "Direction not found", Toast.LENGTH_SHORT).show();

            }
        }
    }
}


