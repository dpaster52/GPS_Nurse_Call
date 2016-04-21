package com.example.paster52.gpsnursecall;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.List;

/**
 * Created by Paster52 on 3/29/2016.
 */
public class SMSreceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
    private NotificationManager mNM;
    private int NOTIFICATION = R.string.local_service_started;
    @Override
    public void onReceive(Context context, Intent intent) {


            Bundle extras = intent.getExtras();
            String strMessage = "";
            if (extras != null) {
                Object[] smsextras = (Object[]) extras.get("pdus");

                for (int i = 0; i < smsextras.length; i++) {
                    SmsMessage smsmsg = SmsMessage.createFromPdu((byte[]) smsextras[i]);
                    String strMsgBody = smsmsg.getMessageBody().toString();
                    String strMsgSrc = smsmsg.getOriginatingAddress();
                    if(strMsgSrc.contains("+12296305667")){

                        strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;
                        /*for(String info:strMsgBody.split(",")) {
                            if (info.contains("")) {
                            }
                        }*/
                        //Log.i(TAG, strMessage);
                        /*ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
                        List <ActivityManager.RunningTaskInfo> tasks =activityManager.getRunningTasks(Integer.MAX_VALUE);
                        for(ActivityManager.RunningTaskInfo task : tasks)
                        {
                            if(task.baseActivity.getPackageName().contains("Google"))
                            {

                            }
                        }*/
                        ActivityManager am = (ActivityManager) context.getSystemService(Activity.ACTIVITY_SERVICE);
                        String packageName = am.getRunningTasks(1).get(0).topActivity.getPackageName();
                        if(packageName.contains("Google"))
                        {

                        }
                        else
                        {
                            Log.d(TAG, strMessage);
                            //showNotification(context, intent, strMsgBody);
                            String[] message = strMsgBody.split(",");
                            MapsActivity.positionList.push(message[1]+","+message[0]);
                            if(MapsActivity.positionList.size()<2)
                            {
                                Intent googleIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + message[1] + "," + message[0] + "&mode=w"));
                                //googleIntent.setClassName("com.example.paster52.gpsnursecall","com.example.paster52.gpsnursecall.MapsActivity");
                                googleIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(googleIntent);
                            }
                        }
                    }


                }

            }

    }
    //Todo try to fix if time permits
    private void showNotification(Context context, Intent intent,String lat_long) {
        // In this sample, we'll use the same text for the ticker and the expanded notification
        CharSequence text = context.getText(R.string.local_service_started);

        // The PendingIntent to launch our activity if the user selects this notification
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0,
                new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=33.775449, -84.403181&mode=w")), 0);

        // Set the info for the views that show in the notification panel.
        Notification notification = new Notification.Builder(context)
                .setTicker(text)  // the status text
                .setSmallIcon(R.mipmap.ic_penguin)
                .setWhen(System.currentTimeMillis())  // the time stamp
                .setContentTitle(context.getString(R.string.local_service_label))  // the label of the entry
                .setContentText(text)  // the contents of the entry
                .setContentIntent(contentIntent)  // The intent to send when the entry is clicked
                .build();

        // Send the notification.
        mNM.notify(NOTIFICATION, notification);
        Log.d(TAG,"finish");
    }
}
