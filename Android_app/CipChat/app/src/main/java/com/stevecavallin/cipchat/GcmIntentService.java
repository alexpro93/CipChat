package com.stevecavallin.cipchat;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

/**
 * Created by Steve on 09/07/14.
 */
public class GcmIntentService extends IntentService {

    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    private static final String TAG = "Intent Service";

    public GcmIntentService() {
        super("GcmIntentService");
    }

    public GcmIntentService(String name) {
        super(name);
        //setIntentRedelivery(true);
        Log.i(TAG,"costruttore");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG,"handling intent! YEAH!");
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        // The getMessageType() intent parameter must be the intent you received
        // in your BroadcastReceiver.
        String messageType = gcm.getMessageType(intent);
        if (!extras.isEmpty()) {  // has effect of unparcelling Bundle
            /*
             * Filter messages based on message type. Since it is likely that GCM
             * will be extended in the future with new message types, just ignore
             * any message types you're not interested in, or that you don't
             * recognize.
             */
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
                sendNotification("Send error: " + extras.get("dataKey"),extras.getString("mittente"),extras.getString("time"));
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
                sendNotification("Deleted messages on server: " + extras.get("dataKey"),extras.getString("mittente"),extras.getString("time"));
                // If it's a regular GCM message, do some work.
            } else if (GoogleCloudMessaging.
                    MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                // This loop represents the service doing some work.
                /*for (int i=0; i<5; i++) {
                    Log.i(TAG, "Working... " + (i + 1)
                            + "/5 @ " + SystemClock.elapsedRealtime());
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                    }
                }
                Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());*/
                // Post notification of received message.
                if(!DetailFragment.attivo) {
                    sendNotification(extras.getString("dataKey"),extras.getString("mittente"),extras.getString("time"));
                }
                else{
                    saveOnListView(extras);
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    // Put the message into a notification and post it.
    // This is just one simple example of what you might choose to do with
    // a GCM message.
    private void sendNotification(String msg,String destinatario,String time) {
        mNotificationManager = (NotificationManager)
                this.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle("GCM Notification")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setContentText(msg)
                        .setAutoCancel(true)
                .setSound(alarmSound);
        if(destinatario!=null) {
            Intent i=new Intent(this, MainActivity.class);
            i.putExtra("Destinatario",destinatario);
            i.putExtra("Animato",true);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(),0,i,PendingIntent.FLAG_CANCEL_CURRENT);

            mBuilder.setContentIntent(contentIntent);
        }
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        saveOnListView(msg,destinatario,time);
    }
    private void saveOnListView(Bundle extras){
        ContentValues values=new ContentValues();
        values.put(DataContentProvider.COL_TESTO,extras.getString("dataKey"));
        values.put(DataContentProvider.COL_DEST,extras.getString("mittente"));
        values.put(DataContentProvider.COL_RICEVUTO,true);
        values.put(DataContentProvider.COL_DATAORA,Long.parseLong(extras.getString("time")));
        getApplicationContext().getContentResolver().insert(DataContentProvider.CONTENT_URI_MESSAGES, values);
        DetailFragment.detailFragment.animato=true;
    }
    private void saveOnListView(String testo, String destinatario,String time){
        ContentValues values=new ContentValues();
        values.put(DataContentProvider.COL_TESTO,testo);
        values.put(DataContentProvider.COL_DEST,destinatario);
        values.put(DataContentProvider.COL_RICEVUTO,true);
        values.put(DataContentProvider.COL_DATAORA,Long.parseLong(time));
        getApplicationContext().getContentResolver().insert(DataContentProvider.CONTENT_URI_MESSAGES, values);
        //DetailFragment.detailFragment.animato=true;
    }
}
