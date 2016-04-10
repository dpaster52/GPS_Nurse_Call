package com.example.paster52.gpsnursecall;

import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.IBinder;
import android.renderscript.ScriptGroup;
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

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
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

import com.example.paster52.gpsnursecall.ServiceCommunicator;
import com.example.paster52.gpsnursecall.SMSreceiver;


public class MapsActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener,LocationListener {

        private GoogleMap mMap; // Might be null if Google Play services APK is not available.
        private GoogleApiClient client;
        private LocationManager manager;
        public static final String TAG = MapsActivity.class.getSimpleName();
        private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
        private LocationRequest mLocationRequest;
        private ServiceCommunicator mServiceCommunicator;
        private boolean mIsBound=false;
        //public static String message;

//Todo comment on the code and add references
    private ServiceConnection mConnection = new ServiceConnection() {
        //Code found at http://developer.android.com/reference/android/app/Service.html
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // This is called when the connection with the service has been
            // established, giving us the service object we can use to
            // interact with the service.  Because we have bound to a explicit
            // service that we know is running in our own process, we can
            // cast its IBinder to a concrete class and directly access it.
            mServiceCommunicator = ((ServiceCommunicator.LocalBinder)service).getService();
            mIsBound=true;
            // Tell the user about this for our demo.
            //Toast.makeText(ScriptGroup.Binding.this,"trial",Toast.LENGTH_SHORT).show();
        }
        @Override
        public void onServiceDisconnected(ComponentName className) {
            // This is called when the connection with the service has been
            // unexpectedly disconnected -- that is, its process crashed.
            // Because it is running in our same process, we should never
            // see this happen.
            mServiceCommunicator = null;
            mIsBound=false;
            //Toast.makeText(ScriptGroup.Binding.this,"Check Status",Toast.LENGTH_SHORT).show();
        }
    };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_maps);
            setUpMapIfNeeded();
            Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
            setSupportActionBar(myToolbar);
            //comm.onCreate();
            // ATTENTION: This "addApi(AppIndex.API)"was auto-generated to implement the App Indexing API.
            // See https://g.co/AppIndexing/AndroidStudio for more information.
            client = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .addApi(AppIndex.API).build();
            mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10 * 1000)        // 10 seconds, in milliseconds
                    .setFastestInterval(1 * 1000); // 1 second, in milliseconds
            manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);


            // public static final String INBOX = "content://sms/inbox";
            // public static final String SENT = "content://sms/sent";
            // public static final String DRAFT = "content://sms/draft";
            /*Cursor cursor = getContentResolver().query(Uri.parse("content://sms/inbox"), null, null, null, null);

            if (cursor.moveToFirst()) { // must check the result to prevent exception
                do {
                    String msgData = "";
                    for (int idx = 0; idx < cursor.getColumnCount(); idx++) {
                        msgData += " " + cursor.getColumnName(idx) + ":" + cursor.getString(idx);
                    }
                    // use msgData
                } while (cursor.moveToNext());
            } else {
                // empty box, no SMS
            }*/

        }

        @Override
        protected void onResume() {
            super.onResume();
            setUpMapIfNeeded();
            client.connect();
            //this.getApplicationContext().stopService(getIntent());
        }

        @Override
        protected void onPause() {
            super.onPause();
            //this.getApplicationContext().startService(getIntent());
            //comm.startService(getIntent());
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
         * {@link MapView MapView}) will show a prompt for the user to
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
                    Toast.makeText(this, "Drop marker to place chair", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.option_Remove_Chair:
                    Toast.makeText(this, "Click on chair to be removed", Toast.LENGTH_SHORT).show();

                    break;
                case R.id.option_Go_to:
                    Log.d("Tag", "Go to the given location");
                    mMap.addMarker(new MarkerOptions().position(new LatLng(33.775449, -84.403181)).title("CRC"));
                    Toast.makeText(this, "I want to go to CRC", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=33.775449, -84.403181&mode=w"));
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

   private void doUnbindService() {
        if (mIsBound) {
            // Detach our existing connection.
            unbindService(mConnection);
            mIsBound = false;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(this, ServiceCommunicator.class);
        startService(intent);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.paster52.gpsnursecall/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);


    }

    @Override
    public void onStop() {
        super.onStop();
        if (mIsBound) {
            unbindService(mConnection);
            mIsBound = false;
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Maps Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.paster52.gpsnursecall/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }

}
