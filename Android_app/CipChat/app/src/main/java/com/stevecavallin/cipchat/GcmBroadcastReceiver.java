package com.stevecavallin.cipchat;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;
import android.widget.EditText;

/**
 * Created by Steve on 06/07/14.
 */
public class GcmBroadcastReceiver extends WakefulBroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("TAG","onReceive!" );
        Bundle extras = intent.getExtras();
        if(extras==null)Log.i("TAG","null");
        else{
            Log.i("TAG",""+extras.get("dataKey"));
            //MyActivity.setText(""+extras.get("dataKey"));
        }
        // Explicitly specify that GcmIntentService will handle the intent.
        ComponentName comp = new ComponentName(context.getPackageName(), GcmIntentService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
    }
}
