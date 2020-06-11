package com.stevecavallin.cipchat;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.MergeCursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;


/**
 * Created by Steve on 22/07/14.
 */
public class MyListFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private SimpleCursorAdapter mAdapter;

    public String[] from={"Immagine","Nome","Numero"};
    private ListView lv;
    static boolean contatti=true;
    ContactsFragment Cfragment = new ContactsFragment();
    static DetailFragment Dfragment = new DetailFragment();
    public static boolean added=false;
    private Cursor[] cursors=new Cursor[2];

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        if(i==0) {
            String[] projection = new String[]{"Utenti.Nome", "Utenti.Immagine", "Utenti.Numero", "Utenti._id"};
            return new CursorLoader(getActivity(), Uri.parse("content://com.stevecavallin.cipchat.provider/*"), projection, "Utenti.Numero=Messaggi.Destinatario", null, DataContentProvider.COL_DATAORA + " DESC");
        }
        else{
            String strUriInbox = "content://sms";
            Uri uri = Uri.parse(strUriInbox);
            String[] projections=new String[]{"address"};
            return new CursorLoader(getActivity(), uri,projections,null,null,"date DESC");
        }
        //return new CursorLoader(getActivity(),DataContentProvider.CONTENT_URI_USERS,projection,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursorLoader.getId()==0){
            cursors[0]=cursor;
        }else{
            cursors[1]=cursor;
            MergeCursor mg=new MergeCursor(cursors);
            cursor.moveToFirst();
            mAdapter.swapCursor(mg);

            Log.i("TAGGG","done");
        }


    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }

    /** An interface for defining the callback method */
    public interface ListFragmentItemClickListener {
        /**
         * This method will be invoked when an item in the ListFragment is
         * clicked
         */

        void onListFragmentItemClick(View view, int position);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        lv = getListView();
        /*ContentValues values=new ContentValues();
        values.put(DataContentProvider.COL_NUMERO,"3490000000");
        values.put(DataContentProvider.COL_NOME,"Alex XD");
        Bitmap imm= BitmapFactory.decodeResource(this.getResources(), R.drawable.no_profile_picture);
        values.put(DataContentProvider.COL_IMMAGINE,SyncAdapter.bitmapToByteArray(imm));
        getActivity().getContentResolver().insert(DataContentProvider.CONTENT_URI_USERS, values);*/
        getLoaderManager().initLoader(0,null,this);
        getLoaderManager().initLoader(1,null,this);
        mAdapter = new SimpleCursorAdapter(getActivity(),R.layout.conversation_layout,null, from,new int[]{R.id.imageView,R.id.textView,R.id.hidden},SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {

                if(view.getId()==R.id.imageView) {
                    ImageView image = (ImageView) view;
                    if(cursor !=null) {
                        //Log.i("Mylistfragment","setviewvalue");
                        if(cursor.getColumnCount()==4) {
                            byte[] byteArr = cursor.getBlob(cursor.getColumnIndex("Immagine"));
                            image.setImageBitmap(BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length));
                        }
                        else{
                            Bitmap bitmap = getPictureFromNumber(cursor.getString(cursor.getColumnIndex("address")));
                            image.setImageBitmap(bitmap);
                        }

                    }
                }
                else if(view.getId()==R.id.textView){
                    TextView t= (TextView)view;
                    if(cursor.getColumnCount()==4)
                        t.setText(" "+cursor.getString(cursor.getColumnIndex("Nome")));
                    else
                        t.setText(getNameFromNumber(cursor.getString(cursor.getColumnIndex("address"))));
                }
                else{
                    TextView t= (TextView)view;
                    if(cursor.getColumnCount()==4)
                        t.setText(cursor.getString(cursor.getColumnIndex("Numero")));
                    else
                        t.setText(cursor.getString(cursor.getColumnIndex("address")));
                }
                return true;
            }
        };
        /*View view = LayoutInflater.from(getActivity().getApplication()).inflate(R.layout.conversation_layout, null);
        ImageView image = (ImageView) view.findViewById(R.id.imageView);
        viewBinder.setViewValue(image, cursor, cursor.getColumnIndex("image"));*/
        mAdapter.setViewBinder(viewBinder);
        setListAdapter(mAdapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TextView tw=(TextView)view.findViewById(R.id.hidden);
                contatti=false;
                DetailFragment.destinatario=tw.getText().toString();
                SharedPreferences.Editor ed= PreferenceManager.getDefaultSharedPreferences(getActivity()).edit();
                ed.putString("destinatario",DetailFragment.destinatario);
                ed.apply();
                Log.i("TAG detailFragment", DetailFragment.destinatario);
                if(MainActivity.mSlidingLayout.isOpen()) {
                    new AsyncTask<Void, Void, Void>() {

                        @Override
                        protected Void doInBackground(Void... voids) {
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                            Log.i("TAG", "" + contatti);
                            if (!contatti) {
                                fragmentTransaction.hide(Cfragment);
                                if(!added) {
                                    fragmentTransaction.add(R.id.content_pane, Dfragment);
                                    added=true;
                                }
                                else
                                    fragmentTransaction.show(Dfragment);
                                if (getActivity().getIntent().getStringExtra("Destinatario") != null) {
                                    DetailFragment.destinatario = getActivity().getIntent().getStringExtra("Destinatario");

                                }
                            }
                            fragmentTransaction.commit();
                            if (getActivity().getIntent().getBooleanExtra("Animato", false))
                                DetailFragment.animato = getActivity().getIntent().getBooleanExtra("Animato", true);
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void voids) {
                            MainActivity.mSlidingLayout.closePane();
                        }
                    }.execute(null, null, null);
                }
            }
        });
        //d.deleteUser("3486067125");
        //d.close();


    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        setHasOptionsMenu(true);
        return super.onCreateView(inflater, container, savedInstanceState);
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_add_user, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.add) {
            if(!contatti) {
                MyListFragment.contatti = true;
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... voids) {
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                        fragmentTransaction.hide(Dfragment);
                        fragmentTransaction.show(Cfragment);
                        fragmentTransaction.commit();
                        try {
                            Thread.sleep(300);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(Void voids) {
                        MainActivity.mSlidingLayout.closePane();
                    }
                }.execute(null, null, null);
            }
            else MainActivity.mSlidingLayout.closePane();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public Bitmap getPictureFromNumber(String number) {
        Uri phoneUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,Uri.encode(number));
        Uri photoUri = null;
        Cursor contact = getActivity().getContentResolver().query(phoneUri,
                new String[]{ContactsContract.Contacts._ID}, null, null, null);
        if (contact.moveToFirst()) {
            long userId = contact.getLong(contact.getColumnIndex(ContactsContract.Contacts._ID));
            photoUri = ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI,userId);
        }
        else{
            return BitmapFactory.decodeResource(getResources(), R.drawable.no_profile_picture);
        }
        if(photoUri!=null){
            InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(getActivity().getContentResolver(),photoUri);
            if(input!=null){
                return BitmapFactory.decodeStream(input);
            }
        } else{
            return BitmapFactory.decodeResource(getResources(), R.drawable.no_profile_picture);
        }
        return BitmapFactory.decodeResource(getResources(), R.drawable.no_profile_picture);
    }

    public String getNameFromNumber(String number){
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME},null,null,null);
        if(cursor.moveToFirst()){
            return cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }
        return number;
    }

}
