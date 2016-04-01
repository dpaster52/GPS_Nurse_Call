package com.example.paster52.gpsnursecall;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Paster52 on 3/29/2016.
 */
public class ServiceCommunicator extends Service {
        private SMSreceiver mSMSreceiver;
        private IntentFilter mIntentFilter;
        private NotificationManager mNM;
        private int NOTIFICATION = R.string.local_service_started;
        private IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        ServiceCommunicator getService() {
            return ServiceCommunicator.this;
        }
        }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("LocalService", "Received start id " + startId + ": " + intent);
        return START_NOT_STICKY;
    }

        @Override
        public void onCreate()
        {
            super.onCreate();
            mNM = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

            // Display a notification about us starting.  We put an icon in the status bar.
            showNotification();

            //SMS event receiver
            mSMSreceiver = new SMSreceiver();
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction("android.provider.Telephony.SMS_RECEIVED");
            registerReceiver(mSMSreceiver, mIntentFilter);
        }

        @Override
        public void onDestroy()
        {
            super.onDestroy();

            // Unregister the SMS receiver
            unregisterReceiver(mSMSreceiver);

            mNM.cancel(NOTIFICATION);

            // Tell the user we stopped.
            Toast.makeText(this,"Service Destroyed", Toast.LENGTH_SHORT).show();
        }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private void showNotification() {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=33.775449, -84.403181&mode=w")), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(this)
                .setTicker(text)  // the status text
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(getText(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
    }



}
