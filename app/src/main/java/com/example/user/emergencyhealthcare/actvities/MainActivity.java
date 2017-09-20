package com.example.user.emergencyhealthcare.actvities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.user.emergencyhealthcare.R;
import com.example.user.emergencyhealthcare.database.adapter.MainAdapter;
import com.example.user.emergencyhealthcare.database.DBHelper;
import com.example.user.emergencyhealthcare.model.Data;
import com.example.user.emergencyhealthcare.model.Hospital;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private RecyclerView recyclerView;
    private MainAdapter mAdapter;
    Button sendSms;
    String username;
    TextView name, currentLocation;
    GoogleMap googleMap;
    DBHelper db = new DBHelper(this);
    String econtact1, econtact2, id;
    ArrayList<Hospital> list = new ArrayList<>();
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    String location;
    ProgressDialog progressDialog;
    double latitude, longitude;
    ArrayList<Hospital> finalList;
    private static int flag = 0;
    private static int flag1 = 0;
    LocationManager locationManager;
    ActionBar actionBar;
    ConnectivityManager connectivity;
    Button blood_group;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                try {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("0000000000", null, "hai dude how r u", null, null);
                    Toast.makeText(getApplicationContext(), "SMS Sent!",
                            Toast.LENGTH_LONG).show();
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again later!",
                            Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:0000000000" ));
                startActivity(intent);


            }
        });


        actionBar = getSupportActionBar();
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(flag1 ==0) {
            loadJsonFromAssests();
            flag1=1;
        }

        if(!isConnectingToInternet())
        {
            Toast.makeText(MainActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


        googleMap = ((SupportMapFragment) (getSupportFragmentManager().findFragmentById(R.id.map))).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        googleMap.getUiSettings().setCompassEnabled(true);
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        if (googleMap != null) {
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

            if(googleMap.isMyLocationEnabled()!= true)
            {
                showSettingsAlert();
                googleMap.setMyLocationEnabled(true);
            }

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        googleMap = mapFragment.getMap();
        mapFragment.getMapAsync(this);

    }

    public boolean isConnectingToInternet()
    {
        connectivity = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }

    public void showSettingsAlert() {
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

            // Setting Dialog Title
            alertDialog.setTitle("GPS is settings");

            // Setting Dialog Message
            alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");

            // On pressing Settings button
            alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });

            // on pressing cancel button
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            // Showing Alert Message
            alertDialog.show();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        username = getIntent().getStringExtra("USERNAME");


            getUserContactDetails();


        currentLocation = (TextView) findViewById(R.id.main_currentlocation);
        sendSms = (Button) findViewById(R.id.main_send_sms);
        sendSms.setOnClickListener(this);
        blood_group=(Button) findViewById(R.id.main_bloodgroup_details);

        blood_group.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, com.example.user.emergencyhealthcare.actvities.blood_group.class));
            }
        });

        name = (TextView) findViewById(R.id.home_username);
        name.setText(username);
        recyclerView = (RecyclerView) findViewById(R.id.main_recycler);

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }
    public static float getDistance(double startLati, double startLongi, double goalLati, double goalLongi){
        float[] resultArray = new float[99];
        Location.distanceBetween(startLati, startLongi, goalLati, goalLongi, resultArray);
        return resultArray[0];
    }

    public void getUserContactDetails() {
        db.open();
        Cursor c = db.getDetails(username);
        if (c.moveToFirst()) {

            econtact1 = c.getString(c.getColumnIndex(DBHelper.ECONTACT1));
            econtact2 = c.getString(c.getColumnIndex(DBHelper.ECONTACT2));
        } else
            Toast.makeText(getBaseContext(), "No contact found",
                    Toast.LENGTH_LONG).show();
        db.close();
    }

    @Override
    public void onClick(View v) {
        SmsManager smsManager = SmsManager.getDefault();
        // Send a text based SMS

        //     String number = econtact1;
        smsManager.sendTextMessage(econtact1, null, "Emergency Healthcare...", null, null);
        smsManager.sendTextMessage(econtact2, null, "Sending sms...", null, null);
        Toast.makeText(getApplicationContext(), "SMS sent.", Toast.LENGTH_LONG).show();
    }






    @Override
    public void onMapReady(GoogleMap googleMap) {

        //    googleMap.addMarker(new MarkerOptions().position(new LatLng(12.9317, 77.6227)).title("Marker"));
    }


    public void loadJsonFromAssests() {
        StringBuffer sb = new StringBuffer();
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(getAssets().open("jsonData.json")));
            String temp;
            while ((temp = br.readLine()) != null) {
                sb.append(temp);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String jsonArray = sb.toString();
        try {
            JSONObject jsonObjMain = new JSONObject(jsonArray);
            JSONArray jArray = jsonObjMain.getJSONArray("hospitals");
            JSONObject obj;
            Hospital details;

            for (int i = 0; i < jArray.length(); i++) {
                details = new Hospital();
                obj = jArray.getJSONObject(i);
                details.setName(obj.getString("Name"));
                details.setPhoneno(obj.getString("Number"));
                details.setLatitude(obj.getDouble("Latitude"));
                details.setLongitude(obj.getDouble("Longitude"));
                if (obj.getString("Ambulance") == "Y") {
                    details.setFlag(true);
                } else {
                    details.setFlag(false);
                }
                list.add(details);

            }
            Data.list = list;
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {

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
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//        Log.e("LOCATION", mLastLocation.toString());
        String location;
        if(mLastLocation!=null)
        {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try
            {
                List<android.location.Address> addresses = geocoder.getFromLocation(mLastLocation.getLatitude(),mLastLocation.getLongitude(),1);
                if(addresses.size()>0)
                {
                    location = addresses.get(0).getLocality() +" , "+addresses.get(0).getSubLocality()+" , "+addresses.get(0).getFeatureName();
                    currentLocation.setText(location);
                    Log.e("LOCATION", location);
                    latitude = addresses.get(0).getLatitude();
                    longitude = addresses.get(0).getLongitude();
                    Log.e("LATITUDE", String.valueOf(latitude));
                    Log.e("LONGITUDE", String.valueOf(longitude));

                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude),
                            13));

                    if (latitude != 0 && longitude != 0) {
                        Hospital item;
                        finalList = new ArrayList<>();
                        for (int i = 0; i < Data.list.size(); i++) {
                            item = new Hospital();
                            item = Data.list.get(i);
                            float distance = getDistance(latitude, longitude, item.getLatitude(), item.getLongitude());
                            if (distance <= 3000) {
                                item.setDistance(distance);
                                finalList.add(item);
                            }
                        }
                        finalList = sortList(finalList);
                        if(finalList != null)
                        {
                            Hospital list;
                            for(int i=0; i<finalList.size(); i++)
                            {
                                list = new Hospital();
                                list = finalList.get(i);
                                googleMap.addMarker(new MarkerOptions().position(new LatLng(list.getLatitude(), list.getLongitude()))
                                        .title(list.getName()));
                            }
                        }

                        mAdapter = new MainAdapter(this, finalList, latitude, longitude, username);
                        recyclerView.setAdapter(mAdapter);
                    }

                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

        }
    }

    private ArrayList<Hospital> sortList(ArrayList<Hospital> finalList) {
        ArrayList<Hospital> list = finalList;

        int len = list.size();
        for(int i =0 ; i < len ; i++) {
            for (int j = i + 1; j < len; j++) {
                Hospital hospital1 = list.get(i);
                Hospital hospital2 = list.get(j);
                Hospital temp = new Hospital();
                if (hospital1.getDistance() > hospital2.getDistance()) {
                    temp = hospital1;
                    list.remove(i);
                    list.add(i, hospital2);
                    list.remove(j);
                    list.add(j, temp);

                }
            }
        }

        return list;
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onBackPressed() {
       doExitApp();
    }


    private long exitTime = 0;

    public void doExitApp() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(this, "Press again to exit", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
