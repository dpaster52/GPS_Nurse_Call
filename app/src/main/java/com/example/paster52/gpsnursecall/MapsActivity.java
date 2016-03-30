package com.example.paster52.gpsnursecall;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        private GoogleApiClient client;
        private LocationManager manager;
        public static final String TAG = MapsActivity.class.getSimpleName();
        private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        private LocationRequest mLocationRequest;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);

            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            manager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

            // public static final String INBOX = "content://sms/inbox";
            // public static final String SENT = "content://sms/sent";
            // public static final String DRAFT = "content://sms/draft";
            Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String msgData = "";
                    for(int idx=0;idx<cursor.getColumnCount();idx++)
                    {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    }
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                // empty box, no SMS
            }

        }

        @Override
        protected void onResume() {
            super.onResume();
            setUpMapIfNeeded();
            client.connect();
        }

        @Override
        protected void onPause() {
            super.onPause();
            if (client.isConnected()) {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
                client.disconnect();
        }


        /**
         * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
         * installed) and the map has not already been instantiated.. This will ensure that we only ever
         * call {@link #setUpMap()} once when {@link #mMap} is not null.
         * <p/>
         * If it isn't installed {@link SupportMapFragment} (and
         * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
         * install/update the Google Play services APK on their device.
         * <p/>
         * A user can return to this FragmentActivity after following the prompt and correctly
         * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
         * have been completely destroyed during this process (it is likely that it would only be
         * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
         * method in {@link #onResume()} to guarantee that it will be called.
         */
        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
                // Try to obtain the map from the SupportMapFragment.
                mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                        .getMap();
                // Check if we were successful in obtaining the map.
                if (mMap != null) {
                    setUpMap();
                }
            }
        }

        /**
         * This is where we can add markers or lines, add listeners or move the camera. In this case, we
         * just add a marker near Africa.
         * <p/>
         * This should only be called once and when we are sure that {@link #mMap} is not null.
         */
        private void setUpMap() {

            mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        }

        @Override
        public boolean onCreateOptionsMenu(Menu menu) {
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.options_menu, menu);
            return true;
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle item selection
            switch (item.getItemId()) {
                case R.id.option_Add_Chair:
                    Toast.makeText(this, "I pressed the add button", Toast.LENGTH_SHORT);
                    break;
                case R.id.option_Remove_Chair:
                    Toast.makeText(this, "I pressed the remove button", Toast.LENGTH_SHORT);

                    break;
                case R.id.option_Go_to:
                    Log.d("Tag", "Go to the given location");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(33.789234,-84.405327)).title("CRC"));
                    Toast.makeText(this, "I pressed the remove button", Toast.LENGTH_SHORT);
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=33.789234,-84.405327&mode=w"));
                    startActivity(i);
                    break;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Location location = LocationServices.FusedLocationApi.getLastLocation(client);
            if (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(client, mLocationRequest, this);
            }
            else {
                handleNewLocation(location);
            }
        }

        @Override
        public void onConnectionSuspended(int i) {

        }

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
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
        private void handleNewLocation(Location location) {
            Log.d(TAG, location.toString());
            double currentLatitude = location.getLatitude();
            double currentLongitude = location.getLongitude();
            LatLng latLng = new LatLng(currentLatitude, currentLongitude);
            mMap.setMyLocationEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng,17));
            MarkerOptions options = new MarkerOptions()
                    .position(latLng)
                    .title("I am here!");
            mMap.addMarker(options);




        }

        @Override
        public void onLocationChanged(Location location) {
            handleNewLocation(location);
        }
}
