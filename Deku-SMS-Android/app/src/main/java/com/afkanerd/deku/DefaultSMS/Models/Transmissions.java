package com.afkanerd.deku.DefaultSMS.Models;

import android.app.PendingIntent;
import android.telephony.SmsManager;
import android.widget.Toast;

import java.util.ArrayList;

public class Transmissions {

    private static final short DATA_TRANSMISSION_PORT = 8200;
    public static void sendTextSMS(String destinationAddress, String text,
                             PendingIntent sentIntent, PendingIntent deliveryIntent,
                             Integer subscriptionId) throws Exception {

        if (text == null || text.isEmpty())
            return;

        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);

        try {
            ArrayList<String> dividedMessage = smsManager.divideMessage(text);
            if (dividedMessage.size() < 2)
                smsManager.sendTextMessage(destinationAddress, null, text, sentIntent, deliveryIntent);
            else {
                ArrayList<PendingIntent> sentPendingIntents = new ArrayList<>();
                ArrayList<PendingIntent> deliveredPendingIntents = new ArrayList<>();

                for (int i = 0; i < dividedMessage.size() - 1; i++) {
                    sentPendingIntents.add(null);
                    deliveredPendingIntents.add(null);
                }

                sentPendingIntents.add(sentIntent);
                deliveredPendingIntents.add(deliveryIntent);

                smsManager.sendMultipartTextMessage(destinationAddress, null,
                        dividedMessage, sentPendingIntents, deliveredPendingIntents);
            }
        } catch (Exception e) {
            throw new Exception(e);
        }

    }

    public static void sendDataSMS(String destinationAddress, byte[] data,
                                     PendingIntent sentIntent, PendingIntent deliveryIntent,
                                     Integer subscriptionId) throws Exception {
        if (data == null)
            return;

        SmsManager smsManager = SmsManager.getSmsManagerForSubscriptionId(subscriptionId);
        try {
            smsManager.sendDataMessage(
                    destinationAddress,
                    null,
                    DATA_TRANSMISSION_PORT,
                    data,
                    sentIntent,
                    deliveryIntent);
        } catch (Exception e) {
            throw new Exception(e);
        }
    }
}
