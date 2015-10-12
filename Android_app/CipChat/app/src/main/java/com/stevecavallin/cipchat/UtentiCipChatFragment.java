package com.stevecavallin.cipchat;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.os.Bundle;
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
public class UtentiCipChatFragment extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
    int mNum;
    SimpleCursorAdapter adapter;
    String[] projections=new String[]{DataContentProvider.COL_NOME,DataContentProvider.COL_NUMERO,DataContentProvider.COL_IMMAGINE,DataContentProvider.COL_ID};
    private boolean created=true;

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
        super.onViewCreated(view,savedInstanceState);
        created=true;
        getActivity().getSupportLoaderManager().initLoader(0, null, this);
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.single_user, null, projections, new int[]{R.id.text_nome, R.id.text_numero, R.id.imageView}, android.support.v4.widget.SimpleCursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
        SimpleCursorAdapter.ViewBinder viewBinder = new SimpleCursorAdapter.ViewBinder() {

            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                //if(cursor !=null) {
                cursor.moveToFirst();
                if (view.getId() == R.id.imageView) {
                    ImageView image = (ImageView) view;
                    Log.i("Mylistfragment", "setviewvalue");
                    byte[] byteArr = cursor.getBlob(cursor.getColumnIndex("Immagine"));
                    image.setImageBitmap(BitmapFactory.decodeByteArray(byteArr, 0, byteArr.length));
                } else if (view.getId() == R.id.text_nome) {
                    TextView t = (TextView) view;
                    t.setText(cursor.getString(cursor.getColumnIndex("Nome")));
                } else {
                    TextView t = (TextView) view;
                    t.setText(cursor.getString(cursor.getColumnIndex("Numero")));
                    Log.i("TAG setviewbinder", t.getText().toString());
                }
                //}
                return true;
            }
        };
        adapter.setViewBinder(viewBinder);
        setListAdapter(adapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Log.i("FragmentList", "Item clicked: " + id);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(),DataContentProvider.CONTENT_URI_USERS,projections,null,null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        if(adapter!=null)
            adapter.swapCursor(cursor);
        else Log.i("adapter arraylist","adapter null");
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        adapter.swapCursor(null);
    }


}
