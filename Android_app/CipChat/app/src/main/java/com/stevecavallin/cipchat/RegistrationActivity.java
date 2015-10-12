package com.stevecavallin.cipchat;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

import java.io.IOException;
import java.util.Random;


public class RegistrationActivity extends ActionBarActivity {
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "666166344255";
    private static final int MAX_ATTEMPTS = 5;
    private static final int BACKOFF_MILLI_SECONDS = 2000;
    private static final Random random = new Random();
    private String phonenumb;
    private String nickname;
    public static String regid;
    private String emailtext;
    GoogleCloudMessaging gcm;
    static final String TAG = "GCMDemo";
    private Context context;
    EditText phone;
    EditText nick;
    EditText email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        context = getApplicationContext();
        phone=(EditText)findViewById(R.id.editText);
        nick=(EditText)findViewById(R.id.editText2);
        email=(EditText)findViewById(R.id.editText3);
        Button reg = (Button) findViewById(R.id.button);
        reg.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Log.i(TAG,"click");
            phonenumb = phone.getText().toString().replace("+","%2B");
            nickname = nick.getText().toString();
            emailtext = email.getText().toString();
            if(phonenumb.isEmpty() || nickname.isEmpty() || emailtext.isEmpty()){
                Toast.makeText(getApplicationContext(),"Riempi tutti i campi!",Toast.LENGTH_SHORT).show();
            }
            else {
                checkAll();
            }
            }
        });
    }

    private void changeActivity(){
        Intent i = new Intent(this,CodeActivity.class);
        i.putExtra("Nickname",nickname);
        i.putExtra("Email",emailtext);
        i.putExtra("Numero",phonenumb);
        i.putExtra("regid",regid);
        startActivity(i);
        finish();
    }

    private void registerOnServer(){
        new AsyncTask<Void, Void, String>() {
            boolean done=true;
            @Override
            protected String doInBackground(Void... voids) {
                String msg="";
                try {
                    Log.i(TAG, "passo per di qui???");
                    msg = ServerUtilities.sendRegistrationIdToBackend(getApplicationContext(),regid,phonenumb,nickname,emailtext);
                    Log.i(TAG,msg);

                } catch (IOException ex) {
                    done=false;
                    msg = ex.getMessage();
                    Log.i(TAG,msg);
                    progress.dismiss();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(),"Impossibile connettersi al server! Controlla la connessione di rete e riprova.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                return msg;
            }
            @Override
            protected void onPostExecute(String status) {
                if(done) {
                    changeActivity();
                    progress.dismiss();
                }

            }

        }.execute(null,null,null);
    }

    private void checkAll(){
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
                registerInBackground();
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
        Log.i(TAG,"Reg id: "+regid);
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


    ProgressDialog progress;
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        progress=new ProgressDialog(this);
        progress.setTitle("Loading");
        progress.setMessage("Registrazione in corso...");
        progress.setCanceledOnTouchOutside(false);
        progress.setCancelable(false);
        progress.show();

        new AsyncTask<Void, Void, Boolean>() {
            boolean done=true;
            @Override
            protected Boolean doInBackground(Void... params) {
                long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                        if (gcm == null) {
                            gcm = GoogleCloudMessaging.getInstance(context);
                        }
                        regid = gcm.register(SENDER_ID);

                        // You should send the registration ID to your server over HTTP,
                        // so it can use GCM/HTTP or CCS to send messages to your app.
                        // ServerUtilities.register(Common.getPreferredEmail(), regid);
                        //ServerUtilities.sendRegistrationIdToBackend(context,regid,phonenumb,nickname);

                        return Boolean.TRUE;

                    } catch (IOException ex) {
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + ex);
                        if (i == MAX_ATTEMPTS) {
                            progress.dismiss();
                            done=false;
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(getApplicationContext(), "Impossibile connettersi al server! Controlla la connessione di rete e riprova.", Toast.LENGTH_SHORT).show();
                                }

                            });
                            break;
                        }
                        try {
                            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            // Activity finished before we complete - exit.
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                        }
                        // increase backoff exponentially
                        backoff *= 2;
                    }
                }
                return Boolean.FALSE;
            }

            @Override
            protected void onPostExecute(Boolean status) {
                if(done) {
                    registerOnServer();
                }
            }
        }.execute(null, null, null);

    }





}
