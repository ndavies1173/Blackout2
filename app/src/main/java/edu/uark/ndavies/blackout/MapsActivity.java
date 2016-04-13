package edu.uark.ndavies.blackout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ViewFlipper;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class MapsActivity extends FragmentActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener, OnMapReadyCallback {

    private GoogleMap mMap;
    ViewFlipper viewFlipper;// = (ViewFlipper) findViewById(R.id.viewFlipper);
    private float lastX;
    private EditText user;
    private GoogleApiClient mGoogleApiClient;
    Location mLastLocation, currentLocation;
    String mLatitudeText, mLongitudeText;
    int MY_PERMISSIONS_REQUEST_FINE_LOCATION, MY_PERMISSIONS_REQUEST_COARSE_LOCATION;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates = true;
    // Location location;
    // LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    //Intent intent = getIntent();
    // private String USER = intent.getStringExtra("EXTRA_SESSION_ID");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        user = (EditText) findViewById(R.id.user_email);

        // Create an instance of GoogleAPIClient.
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }

        // Check Permissions
        while (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
        }


        final Button tracking = (Button) findViewById(R.id.track);
        tracking.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // onLocationChanged(location);
                /*if(locationManager != null){
                    if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                        Location loc = locationManager.getLastKnownLocation(provider);
                        locationManager.requestLocationUpdates(provider, 120000, 10000, locationListener);
                    }
                }*/
                startLocationUpdates();
            }
        });

        final Button friends = (Button) findViewById(R.id.friends);
        friends.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FriendsList.class));
            }
        });


    }//End on Create

   /* @Override
    public void onProviderEnabled(String string) {
    }

    @Override
    public void onProviderDisabled(String string) {
    }*/

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
        UpdateUI();
    }

    private void UpdateUI(){
        LatLng current = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(current).title("Current Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
    }

    /*@Override
    public void onStatusChanged(String string, int num, Bundle bundle) {
    }
*/
    @Override
    public void onConnected(Bundle bundle) {
        if(mGoogleApiClient != null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_FINE_LOCATION);
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
            } else {
                mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                        mGoogleApiClient);
                if (mLastLocation != null) {
                    mLatitudeText = (String.valueOf(mLastLocation.getLatitude()));
                    mLongitudeText = (String.valueOf(mLastLocation.getLongitude()));
                }
            /*if (mRequestingLocationUpdates) {
                startLocationUpdates();
            }*/
            }
        }
        else{
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            createLocationRequest();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /*LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
            .addLocationRequest(mLocationRequest);

    PendingResult<LocationSettingsResult> result =
            LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                    builder.build());*/


    protected void startLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }



    //Handles swipes for changing views NOT WORKING CURRENTLY
//    public boolean onTouchEvent(MotionEvent touchevent){
//
//        switch (touchevent.getAction())
//        {
//            // when user first touches the screen to swap
//            case MotionEvent.ACTION_DOWN:
//            {
//                lastX = touchevent.getX();
//                break;
//            }
//            case MotionEvent.ACTION_UP:
//            {
//                float currentX = touchevent.getX();
//
//                // if left to right swipe on screen
//                if (lastX < currentX)
//                {
//                    viewFlipper.setDisplayedChild(1);
//                }
//
//                // if right to left swipe on screen
//                if (lastX > currentX)
//                {
//                    viewFlipper.setDisplayedChild(0);
//                }
//
//            }
//        }
//
//        return false;
//    }
    //Returns to default view on back pressed. If already on default view exits activity.
    /*@Override
    public void onBackPressed()
    {
        if(viewFlipper.getDisplayedChild() == 0){
            super.onBackPressed();
        }
        else {
            viewFlipper.setDisplayedChild(0);
        }
    }*/
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

        /*LatLng Fayetteville = new LatLng(36.0655409, -94.1731129);
        mMap.addMarker(new MarkerOptions().position(Fayetteville).title("Marker in Fayettevile"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(Fayetteville, 15));*/
    }


}


