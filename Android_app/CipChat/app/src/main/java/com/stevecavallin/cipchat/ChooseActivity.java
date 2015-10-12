package com.stevecavallin.cipchat;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

/**
 * Created by Steve on 21/07/14.
 */
public class ChooseActivity extends FragmentActivity{

    public static final String PROPERTY_REG_ID = "registration_id";
    private static final String TAG = "GCMDemo";
    private final static int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    Context context;
    // Constants
    // The authority for the sync adapter's content provider
    /*public static final String AUTHORITY = ContactsContract.AUTHORITY;
    // An account type, in the form of a domain name
    public static final String ACCOUNT_TYPE = "com.cipchat.account";
    // The account name
    public static final String ACCOUNT = "CipChat";
    // Instance fields
    Account mAccount;
    // Global variables
    // A content URI for the content provider's data table
    Uri mUri;
    // A content resolver for accessing the provider
    ContentResolver mResolver;
    public static final long MILLISECONDS_PER_SECOND = 1000L;
    public static final long SECONDS_PER_MINUTE = 60L;
    public static final long SYNC_INTERVAL_IN_MINUTES = 1440L;
    public static final long SYNC_INTERVAL =SYNC_INTERVAL_IN_MINUTES *SECONDS_PER_MINUTE *MILLISECONDS_PER_SECOND;*/


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        context=getApplicationContext();
        if (checkPlayServices()) {
            if (!CodeActivity.getGCMPreferences(context).getString(PROPERTY_REG_ID, "").equals("")) {
                Intent i = new Intent(this, MainActivity.class);
                startActivity(i);
                this.finish();

            } else {
                Intent i = new Intent(this, SplashActivity.class);
                startActivity(i);
                this.finish();
                Log.i(TAG, "Not registered");
            }
        }

    }



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
}
