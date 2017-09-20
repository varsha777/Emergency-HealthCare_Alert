package com.example.user.emergencyhealthcare.actvities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.user.emergencyhealthcare.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class HospitalDetails extends AppCompatActivity {
    ImageView image;
    private GoogleApiClient mGoogleApiClient;
    GoogleMap googleMap;
    LatLng latLng;
    LocationManager lm;
    double latdest;
    double londest;
    double latitude, longitude;
    ActionBar actionBar;
    String username;
    TextView userName,distance;
    String phoneno;
    Button callAmbulance, navigate, stopNavigation;
    PolylineOptions polyLineOptions = null;
    int thickness;
    //   ToggleButton tnavigate;
    Polyline line;
    final String TAG = "HospitalDetails";
    private Location mLastLocation;
    Marker updateMarker, currentLocation, MyStaticLocation;
    private int navigation_status;
    String roundedOff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hospital_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        username = getIntent().getStringExtra("USERNAME");
        userName = (TextView) findViewById(R.id.hospdetails_username);
        userName.setText(username);
        phoneno = getIntent().getStringExtra("NUMBER");
        navigate = (Button) findViewById(R.id.hospmain_navigate);
        stopNavigation = (Button) findViewById(R.id.stop_navigation);
        distance = (TextView) findViewById(R.id.distance);

        callAmbulance = (Button) findViewById(R.id.hospmain_ambulance);

        callAmbulance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (phoneno.equals("  Not Updated")) {
                    Toast.makeText(HospitalDetails.this, "Number not updated", Toast.LENGTH_SHORT).show();
                } else {
                    Uri uri = Uri.parse("tel:" + Uri.encode(phoneno));
                    Intent intent = new Intent(Intent.ACTION_CALL, uri);
                    if (ActivityCompat.checkSelfPermission(HospitalDetails.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    startActivity(intent);
                }
            }
        });
        navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigation_status=0;
                addMarkers(2);
                thickness = 15;
                String url = getMapsApiDirectionsUrl();
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);
                navigate.setVisibility(View.GONE);
                callAmbulance.setVisibility(View.GONE);
                stopNavigation.setVisibility(View.VISIBLE);
                getCurrentLocation(1);
//               Intent navigationIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + latdest + "," + londest));
//                startActivity(navigationIntent);
//                Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
//                        Uri.parse("http://maps.google.com/maps?saddr=latitude,longitude&daddr=latdest,londest"));
//                startActivity(intent);
            }
        });

        stopNavigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                distance.setVisibility(View.GONE);
                navigation_status=1;
                googleMap.clear();
                thickness = 2;
                addMarkers(1);
                String url = getMapsApiDirectionsUrl();
                ReadTask downloadTask = new ReadTask();
                downloadTask.execute(url);
                stopNavigation.setVisibility(View.GONE);
                navigate.setVisibility(View.VISIBLE);
                callAmbulance.setVisibility(View.VISIBLE);
            }
        });
       /* navigate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(HospitalDetails.this, MapsActivity.class);
                startActivity(i);
            }
        });*/


        latitude = getIntent().getDoubleExtra("LATITUDE", latitude);
        longitude = getIntent().getDoubleExtra("LONGITUDE", longitude);
        Intent i = getIntent();
        latLng = i.getParcelableExtra("LATLNG");

        latdest = latLng.latitude;
        londest = latLng.longitude;
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        googleMap = fm.getMap();

        MarkerOptions options = new MarkerOptions();
        options.position(new LatLng(latitude, longitude));
        options.position(new LatLng(latdest, londest));
        googleMap.addMarker(options);
        thickness = 2;
        String url = getMapsApiDirectionsUrl();
        ReadTask downloadTask = new ReadTask();
        downloadTask.execute(url);


        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                13));
        addMarkers(1);
        googleMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (googleMap != null) {
            googleMap.setTrafficEnabled(true);
        }
        if (googleMap.isMyLocationEnabled() != true) {
            googleMap.setMyLocationEnabled(true);
        }
    }

    private String getMapsApiDirectionsUrl() {

        String str_origin = "origin=" + latitude + "," + longitude;
        String str_dest = "destination=" + latdest + "," + londest;

        String sensor = "sensor=false";
        String parameters = str_origin + "&" + str_dest + "&" + sensor;

        String output = "json";
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters;
        return url;
    }

    private void addMarkers(int marker_flag) {
        if (googleMap != null) {
            if (marker_flag == 1) {
                distance.setVisibility(View.GONE);
                if(updateMarker == null ) {
                    MyStaticLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))// changes the marker icon color
                            .draggable(true));
                }else {
                    updateMarker.remove();
                    MyStaticLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title("Your Location")
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))// changes the marker icon color
                            .draggable(true));
                }

            } else {
                MyStaticLocation.remove();
                currentLocation = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                        .title("Starting Point")
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))// changes the marker icon color
                        .draggable(true));

                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10));
                googleMap.animateCamera(CameraUpdateFactory.zoomTo(18),3000, null);
//            googleMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
            }
            googleMap.addMarker(new MarkerOptions().position(new LatLng(latdest, londest))
                    .title("Hospital Location"));
        }
    }


    private class ReadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url) {
            String data = "";
            try {
                HttpConnection http = new HttpConnection();
                data = http.readUrl(url[0]);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            new ParserTask().execute(result);
        }
    }

    private class ParserTask extends
            AsyncTask<String, Integer, List<List<HashMap<String, String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(
                String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try {
                jObject = new JSONObject(jsonData[0]);
                PathJSONParser parser = new PathJSONParser();
                routes = parser.parse(jObject);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> routes) {
            ArrayList<LatLng> points = null;


            // traversing through routes
            for (int i = 0; i < routes.size(); i++) {
                points = new ArrayList<LatLng>();
                polyLineOptions = new PolylineOptions();
                List<HashMap<String, String>> path = routes.get(i);

                for (int j = 0; j < path.size(); j++) {
                    HashMap<String, String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                polyLineOptions.addAll(points);
                polyLineOptions.width(thickness);
                polyLineOptions.color(Color.BLUE);
            }

            googleMap.addPolyline(polyLineOptions);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(this, MainActivity.class);
        i.putExtra("USERNAME", username);
        startActivity(i);
        finish();
    }

    public void getCurrentLocation(int status) {

        new Thread(new Runnable() {
            public void run() {
                String filepath = null;
                try {
                    LocationManager locationManager;
                    locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (location != null) {
                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                    }
                }catch(Exception e){
                    Log.e("sa", e.getMessage());
                }
            }
        }).start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(updateMarker == null ) {
                        currentLocation.remove();
                        updateMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                .title("Starting Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))// changes the marker icon color
                                .draggable(true));
                    }else {
                        currentLocation.remove();
                        updateMarker.remove();
                        updateMarker = googleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude))
                                .title("Starting Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))// changes the marker icon color
                                .draggable(true));
                    }
                    distance.setVisibility(View.VISIBLE);
                    float[] resultArray = new float[99];
                    Location.distanceBetween(latitude, longitude, latdest, londest, resultArray);
                    double distance = resultArray[0] / 1000;
                    DecimalFormat f = new DecimalFormat("#0.00");
                    roundedOff = f.format(distance);
                    checkNavigationStatus();
                }
            }, 12000);
    }

    public void checkNavigationStatus(){
        if(navigation_status == 1) {
//            Toast.makeText(getApplicationContext(),"stopped",Toast.LENGTH_SHORT).show();
            Log.e("Navigation","Stopped");
            addMarkers(1);
        }else{
            distance.setText("Distance Left " +roundedOff+"Km");
            //Toast.makeText(getApplicationContext(),"Longitude"+String.valueOf(longitude)+"Latitude"+String.valueOf(latitude),Toast.LENGTH_SHORT).show();
            getCurrentLocation(1);
        }
    }

}