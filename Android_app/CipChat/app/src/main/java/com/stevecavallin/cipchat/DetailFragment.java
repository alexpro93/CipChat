package com.stevecavallin.cipchat;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Steve on 22/07/14.
 */
public class DetailFragment extends Fragment implements Animation.AnimationListener,LoaderManager.LoaderCallbacks<Cursor>{

    public ProvinceCursorAdapter adapter;
    private ListView listView;
    private EditText chatText;
    public static String destinatario;
    private static String MITTENTE;
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    String SENDER_ID = "666166344255";
    static final String TAG = "GCMDemo";
    Animation animSideDown;
    static boolean animato;
    public static boolean attivo;
    private String[] projection=new String[]{DataContentProvider.COL_ID,DataContentProvider.COL_TESTO,DataContentProvider.COL_DATAORA,DataContentProvider.COL_RICEVUTO};

    //Quando invio il messaggio animazione slide dal basso verso l'alto vedi http://www.androidhive.info/2013/06/android-working-with-xml-animations/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        detailFragment=this;
        MITTENTE=PreferenceManager.getDefaultSharedPreferences(getActivity()).getString("Mittente","");
        Log.i("MITTENTE",MITTENTE);
        View view = inflater.inflate(R.layout.detail_fragment,container,false);
        listView = (ListView) view.findViewById(R.id.chat_list);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        Button buttonSend = (Button) view.findViewById(R.id.post_button);
        destinatario= sharedPreferences.getString("destinatario", "");
        adapter=new ProvinceCursorAdapter(getActivity(),null);
        listView.setAdapter(adapter);
        chatText=(EditText)view.findViewById(R.id.chat_content);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                sendChatMessage();
            }
        });
        listView.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        loadConversation();
        //to scroll the list view to bottom on data change
        /*adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listView.setSelection(adapter.getCount() - 1);
            }
        });*/
        // load the animation
        animSideDown = AnimationUtils.loadAnimation(getActivity(),
                R.anim.slide_down);

        // set animation listener
        animSideDown.setAnimationListener(this);
        //listView.startAnimation(animSideDown);
        return view;
    }

    private void sendChatMessage(){
        final String testo=chatText.getText().toString().trim();
        if(!testo.isEmpty()) {
            saveMessageOnLocalDatabase(testo, destinatario);
            new AsyncTask<Void, Void, String>() {
                @Override
                protected String doInBackground(Void... params) {
                    String msg = "";
                    try {
                        msg = ServerUtilities.send(getActivity(),testo, destinatario ,MITTENTE);

                        gcm= GoogleCloudMessaging.getInstance(getActivity());
                        Bundle data = new Bundle();
                        data.putString("testo", testo);
                        data.putString("destinatario", destinatario);
                        data.putString("my_action","com.google.android.gcm.demo.app.ECHO_NOW");
                        String id = Integer.toString(msgId.incrementAndGet());
                        gcm.send(SENDER_ID + "@gcm.googleapis.com", id, data);

                    } catch (IOException ex) {
                        msg = ex.getMessage();
                    }
                    return msg;
                }

                @Override
                protected void onPostExecute(String msg) {
                    if (!TextUtils.isEmpty(msg)) {
                        Log.i(TAG,msg);
                    }
                }
            }.execute(null, null, null);
            long dataoralong=Calendar.getInstance().getTime().getTime();
            animato=true;
        }
        chatText.setText("");
    }

    private void saveMessageOnLocalDatabase(String testo, String destinatario){
        ContentValues values=new ContentValues();
        values.put(DataContentProvider.COL_TESTO,testo);
        values.put(DataContentProvider.COL_DEST,destinatario);
        values.put(DataContentProvider.COL_RICEVUTO,false);
        values.put(DataContentProvider.COL_DATAORA,Calendar.getInstance().getTime().getTime());
        getActivity().getContentResolver().insert(DataContentProvider.CONTENT_URI_MESSAGES, values);
        Log.i(TAG, "Messaggio inserito su db locale");

    }

    public void setDestintario(String dest){
        destinatario=dest;
    }



    public static String getDate(long milliSeconds, String dateFormat)
    {
        // Create a DateFormatter object for displaying date in specified format.
        DateFormat formatter = new SimpleDateFormat(dateFormat);

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(milliSeconds);

        return formatter.format(calendar.getTime());
    }

    public void loadConversation(){
        if(destinatario!=null) {
            Log.i(TAG,"destinatario non nullo, bene");
            getLoaderManager().initLoader(0,null,this);


            listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(final AdapterView<?> adapterView, final View view, final int i, long l) {
                    Log.i(TAG, "long click");
                /*final DataProvider dp = new DataProvider(getActivity());
                dp.open();
                final Cursor c = dp.getConversation(destinatario);
                c.moveToFirst();*/
                    //PROVVISORIO--->È BRUTTO
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Cancellare?").setTitle("Cancella");
                    builder.setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            ProvinceCursorAdapter p=(ProvinceCursorAdapter)listView.getAdapter();
                            getActivity().getContentResolver().delete(DataContentProvider.CONTENT_URI_MESSAGES, DataContentProvider.COL_ID + "=?", new String[]{p.getCursor().getString(p.getCursor().getColumnIndex("_id"))});
                        }
                    });
                    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            builder.create().cancel();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();


                    return false;
                }
            });
        }


    }

    public static DetailFragment detailFragment;

    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),DataContentProvider.CONTENT_URI_MESSAGES,projection,DataContentProvider.COL_DEST+"=?",new String[]{destinatario},DataContentProvider.COL_DATAORA);
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> cursorLoader, Cursor cursor) {
        if(cursor!=null && cursor.moveToFirst()) {
            adapter.swapCursor(cursor);
            Log.i(TAG,"cursore non nullo, va bene!");
        }
        else if(cursor!=null && !cursor.moveToFirst())Log.i(TAG,""+cursor.getCount());
        else Log.i(TAG,"tutto nullo");
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }

    public class ProvinceCursorAdapter extends CursorAdapter
    {
        public ProvinceCursorAdapter(Context context, Cursor c)
        {
            super(context, c,0);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent)
        {
            return LayoutInflater.from(context).inflate(
                    R.layout.single_message, null);
        }
        LinearLayout singleMessageContainer;
        @Override
        public void bindView(View view, Context context, Cursor cursor)
        {
            Log.i(TAG,"blindView");

            singleMessageContainer = (LinearLayout) view.findViewById(R.id.singleMessageContainer);
            String testo = cursor.getString(cursor.getColumnIndex("Testo"));
            String dataora = getDate(cursor.getLong(cursor.getColumnIndex("Dataora")),"dd/MM/yyyy HH:mm");
            ChatMessage chatMessageObj = new ChatMessage(cursor.getInt(cursor.getColumnIndex("Ricevuto"))>0,testo,dataora);
            TextView messageView = (TextView) view.findViewById(R.id.singleMessage);
            messageView.setText(chatMessageObj.message + "\n" + chatMessageObj.dataora, TextView.BufferType.SPANNABLE);
            Spannable span = (Spannable) messageView.getText();
            span.setSpan(new ForegroundColorSpan(Color.parseColor("#ABCDEF")), chatMessageObj.message.length() , messageView.getText().length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            span.setSpan(new RelativeSizeSpan(0.6f), chatMessageObj.message.length() , messageView.getText().length(),
                    Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            messageView.setBackgroundResource(chatMessageObj.left ? R.drawable.fumetto_ric : R.drawable.fumetto_inv);
            singleMessageContainer.setGravity(chatMessageObj.left ? Gravity.LEFT : Gravity.RIGHT);
            if(cursor.isLast() && animato) {
                messageView.startAnimation(animSideDown);
                animato=false;
            }
        }
    }
    /*@Override
    public void onConfigurationChanged(Configuration configuration){
        super.onConfigurationChanged(configuration);
        loadConversation();
        Log.i(TAG, "rotation!");
    }*/

    @Override
    public void onResume(){
        attivo=true;
        super.onResume();
    }
    @Override
    public void onPause(){
        super.onPause();
        attivo=false;

    }


    /*@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.detail , menu);
    }*/
}
