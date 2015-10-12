package com.stevecavallin.cipchat;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.provider.Contacts;
import android.provider.ContactsContract;
import android.telephony.PhoneNumberUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by Steve on 29/07/14.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {
    // Global variables
    // Define a variable to contain a content resolver instance
    ContentResolver mContentResolver;
    ConnectivityManager cm =(ConnectivityManager)getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
    NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

    /**
     * Set up the sync adapter
     */
    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        mContentResolver = context.getContentResolver();
    }

    /**
     * Set up the sync adapter. This form of the
     * constructor maintains compatibility with Android 3.0
     * and later platform versions
     */
    public SyncAdapter(Context context,boolean autoInitialize,boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void onPerformSync(Account account, Bundle bundle, String s, ContentProviderClient contentProviderClient, SyncResult syncResult) {
        String response[];
        Log.i("TAG","onPerformSync");
        if(activeNetwork.getType()==ConnectivityManager.TYPE_WIFI || bundle.getBoolean("onChange")){
            try {
                Cursor c=contentProviderClient.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER},null,null,null);
                String[] numbers=new String[c.getCount()];
                if(c.moveToFirst()) {
                    Log.i("TAG","movetofirst");
                    for (int i = 0; c.moveToNext(); i++) {
                        numbers[i] = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replaceAll("\\s+","");
                    }

                    c.close();
                    if(!CodeActivity.getMittente(getContext()).equals("")) {
                        Log.i("TAG","mittente not empty");
                        String response1=ServerUtilities.checkContacts(getContext(), numbers, PreferenceManager.getDefaultSharedPreferences(getContext()).getString("Mittente", ""));
                        Log.i("SyncAdapter response",response1);
                        response=response1.split("<br>");
                        for (int j=0;j<response.length;j+=2){
                            ContentValues values=new ContentValues();
                            values.put(DataContentProvider.COL_NUMERO,response[j]);
                            values.put(DataContentProvider.COL_NOME,getContactName(response[j]));

                            /*if(!response[j+1].equals("NULL")) {
                                values.put(DataContentProvider.COL_IMMAGINE, response[j + 1].getBytes());
                            }*/
                            //else {
                                Bitmap imm= BitmapFactory.decodeResource(getContext().getResources(), R.drawable.no_profile_picture);
                                values.put(DataContentProvider.COL_IMMAGINE, bitmapToByteArray(imm));
                            //}

                            getContext().getContentResolver().insert(DataContentProvider.CONTENT_URI_USERS, values);
                        }
                    }
                }
                Log.i("TAG","onPerformSync done!");
                Log.i("TAG",""+getContext().getContentResolver().query(DataContentProvider.CONTENT_URI_USERS,new String[]{DataContentProvider.COL_NUMERO},null,null,null).getCount());

            } catch (RemoteException e) {
                e.printStackTrace();
                Log.i("Exception", "exception onPerformSync");
            } catch (IOException e) {
                Log.i("Exception", "exception onPerformSync: IOException");
            }
        }
    }

    public String getContactName(final String phoneNumber)
    {
        Uri uri;
        String[] projection;

        uri = Uri.parse("content://com.android.contacts/phone_lookup");
        projection = new String[] { "display_name" };
        uri = Uri.withAppendedPath(uri, Uri.encode(phoneNumber));
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);

        String contactName = "";

        if (cursor.moveToFirst())
        {
            contactName = cursor.getString(0);
        }

        cursor.close();

        return contactName;
    }

    public static byte[] bitmapToByteArray(Bitmap bitmap) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        return baos.toByteArray();

    }

}
