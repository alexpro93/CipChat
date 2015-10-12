package com.stevecavallin.cipchat;

import android.app.Activity;
import android.content.ContentUris;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by Steve on 07/08/14.
 */
public class UtentiTelefonoFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    int mNum;
    SimpleCursorAdapter mAdapter;
    String[] projectionsContacts=new String[]{ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone._ID};
    private boolean created=true;

    /**
     * When creating, retrieve this instance's number from its arguments.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mNum = getArguments() != null ? getArguments().getInt("num") : 1;

    }

    /**
     * The Fragment's UI is just a simple text view showing its
     * instance number.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_contact_item, container, false);

        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){
        created=true;
        Log.i("contacts_fragment", "onActivityCreated");
        getActivity().getSupportLoaderManager().initLoader(1, null, this);
        mAdapter = new SimpleCursorAdapter(getActivity(), R.layout.single_user, null, projectionsContacts, new int[]{R.id.text_nome, R.id.text_numero, R.id.imageView}, android.support.v4.widget.SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                //if(cursor !=null) {

                if (view.getId() == R.id.imageView) {
                    ImageView image = (ImageView) view;
                    byte[] byteArr = openPhoto(cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID)));
                    if (byteArr==null){
                        image.setImageBitmap(BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.no_profile_picture));
                    }
                    else image.setImageBitmap(BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length));
                } else if (view.getId() == R.id.text_nome) {
                    TextView t = (TextView) view;
                    t.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)).replace("\n"," "));
                } else {
                    TextView t = (TextView) view;
                    t.setText(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)).replace("\n"," "));
                }
                //}
                return true;
            }
        };
        mAdapter.setViewBinder(viewBinder);
        setListAdapter(mAdapter);
        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), ContactsContract.CommonDataKinds.Phone.CONTENT_URI,projectionsContacts,null,null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        if(mAdapter!=null){
            mAdapter.swapCursor(cursor);
        }

        else Log.i("adapter contactslist","adapter null");
        Log.i("contacts_fragment","load finished, count: "+cursor.getCount());
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    public byte[] openPhoto(long contactId) {
        Uri contactUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, contactId);
        Uri photoUri = Uri.withAppendedPath(contactUri, ContactsContract.Contacts.Photo.CONTENT_DIRECTORY);
        Cursor cursor = getActivity().getContentResolver().query(photoUri,
                new String[]{ContactsContract.Contacts.Photo.PHOTO}, null, null, null);
        if (cursor == null) {
            return null;
        }
        try {
            if (cursor.moveToFirst()) {
                return cursor.getBlob(0);
            }
        } finally {
            cursor.close();
        }
        return null;
    }



}
