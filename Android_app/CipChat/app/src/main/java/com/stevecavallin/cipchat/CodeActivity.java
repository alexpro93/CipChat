package com.stevecavallin.cipchat;

import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.stevecavallin.cipchat.R;

import java.io.IOException;
import java.util.Random;

public class CodeActivity extends ActionBarActivity {

    EditText code;
    private String codetext,numero;
    private String nickname;
    private String emailtext;
    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String PROPERTY_APP_VERSION = "appVersion";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private String regid;
    GoogleCloudMessaging gcm;
    static final String TAG = "GCMDemo";
    private Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        checkPlayServices();
        context=getApplicationContext();
        setContentView(R.layout.activity_code);
        numero=getIntent().getStringExtra("Numero");
        nickname=getIntent().getStringExtra("Nickname");
        emailtext=getIntent().getStringExtra("Email");
        regid=getIntent().getStringExtra("regid");
        Log.i("regid ricevuto","regid: "+regid);
        code=(EditText)findViewById(R.id.editText);
        Button b=(Button)findViewById(R.id.button);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                codetext=code.getText().toString();
                next();
            }
        });
    }
     private void next() {
         final Dialog imageDiaglog= new Dialog(CodeActivity.this,R.style.PauseDialog);
         imageDiaglog.setTitle("Checking");
         imageDiaglog.show();

         new AsyncTask<Void, Void, String>() {
             @Override
             protected String doInBackground(Void... voids) {
                 try{
                     if (ServerUtilities.isCodeRight(getApplicationContext(), codetext, numero)) {
                         storeRegistrationId(getApplicationContext(),regid);
                         Intent i = new Intent(getApplicationContext(), MainActivity.class);
                         storeMittente(getApplicationContext(),numero);
                         startActivity(i);finish();
                     } else runOnUiThread(new Runnable() {
                         @Override
                         public void run() {
                             Toast.makeText(getApplicationContext(), "Codice inserito errato!", Toast.LENGTH_SHORT).show();
                         }
                     });
                 }

                 catch(IOException e){
                     Log.i("TAG","errore controllo codice");}
                 return null;
             }
             @Override
             protected void onPostExecute(String status){
                 imageDiaglog.dismiss();
                 SyncUtils.CreateSyncAccount(getApplicationContext());
             }
         }.execute(null,null,null);
     }




    // You need to do the Play Services APK check here too.
    @Override
    protected void onResume() {
        super.onResume();
        checkPlayServices();
    }

    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGCMPreferences(Context context) {
        // This sample app persists the registration ID in shared preferences, but
        // how you store the regID in your app is up to you.
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager()
                    .getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }



    /**
     * Stores the registration ID and app versionCode in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGCMPreferences(context);
        int appVersion = getAppVersion(context);
        Log.i(TAG, "Saving regId on app version " + appVersion +" regID = "+regId);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.apply();
    }
    public static void storeMittente(Context context,String mittente){
        Log.i("Mittente salvato","eccolo: "+mittente);
        final SharedPreferences prefs = getGCMPreferences(context);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Mittente",mittente);
        editor.apply();
    }
    public static String getMittente(Context context) {
        Log.i(TAG+" CodeActivity",PreferenceManager.getDefaultSharedPreferences(context).getString("Mittente", ""));
        return PreferenceManager.getDefaultSharedPreferences(context).getString("Mittente", "");
    }

}
