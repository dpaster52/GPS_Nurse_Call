package com.example.paster52.gpsnursecall;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by Paster52 on 3/29/2016.
 */
public class SMSreceiver extends BroadcastReceiver {
    private final String TAG = this.getClass().getSimpleName();
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
                    strMessage += "SMS from " + strMsgSrc + " : " + strMsgBody;

                    Log.i(TAG, strMessage);
                    Log.d(TAG, strMessage);
                }

            }

    }
}
