package com.stevecavallin.cipchat;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Steve on 29/07/14.
 */
public class DataContentProvider extends ContentProvider {
    public static final Uri CONTENT_URI_MESSAGES = Uri.parse("content://com.stevecavallin.cipchat.provider/messaggi");
    public static final Uri CONTENT_URI_USERS = Uri.parse("content://com.stevecavallin.cipchat.provider/utenti");

    private static final int MESSAGES_ALLROWS = 1;
    private static final int MESSAGES_SINGLE_ROW = 2;
    private static final int USERS_ALLROWS = 3;
    private static final int USERS_SINGLE_ROW = 4;
    private static final int JOIN_TABLES = 5;

    private DatabaseHelper dbHelper;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.stevecavallin.cipchat.provider", "messaggi", MESSAGES_ALLROWS);
        uriMatcher.addURI("com.stevecavallin.cipchat.provider", "messaggi/#", MESSAGES_SINGLE_ROW);
        uriMatcher.addURI("com.stevecavallin.cipchat.provider", "utenti", USERS_ALLROWS);
        uriMatcher.addURI("com.stevecavallin.cipchat.provider", "utenti/#", USERS_SINGLE_ROW);
        uriMatcher.addURI("com.stevecavallin.cipchat.provider","*",JOIN_TABLES);
    }

    public static final String COL_ID = "_id";

    public static final String TABLE_MESSAGGI = "Messaggi";
    public static final String COL_TESTO = "Testo";
    public static final String COL_DEST = "Destinatario";
    public static final String COL_DATAORA = "Dataora";
    public static final String COL_RICEVUTO = "Ricevuto";

    public static final String TABLE_UTENTI = "Utenti";
    public static final String COL_NUMERO = "Numero";
    public static final String COL_NOME = "Nome";
    public static final String COL_IMMAGINE = "Immagine";

    @Override
    public boolean onCreate() {
        dbHelper = new DatabaseHelper(getContext());
        Log.i("TAGGG","ma che affari");
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        switch(uriMatcher.match(uri)) {
            case MESSAGES_ALLROWS:
            case USERS_ALLROWS:
                qb.setTables(getTableName(uri));
                break;

            case MESSAGES_SINGLE_ROW:
            case USERS_SINGLE_ROW:
                qb.setTables(getTableName(uri));
                qb.appendWhere("_id = " + uri.getLastPathSegment());
                break;
            case JOIN_TABLES:
                qb.setTables(TABLE_MESSAGGI+","+TABLE_UTENTI);
                qb.setDistinct(true);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
        Cursor asd = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        int i = 0;
        Log.i("TAG", asd.getColumnNames().toString());
        for (asd.moveToFirst(); !asd.isAfterLast(); asd.moveToNext()) {
            String[] temp = new String[asd.getColumnCount()];
            for (i = 0; i < temp.length; i++) {
                temp[i] = asd.getString(i);
            }
            Log.i("TAG", temp.toString());
        }


        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id;
        switch(uriMatcher.match(uri)) {
            case MESSAGES_ALLROWS:
                id = db.insertOrThrow(TABLE_MESSAGGI, null, contentValues);
                break;

            case USERS_ALLROWS:
                id = db.insertOrThrow(TABLE_UTENTI, null, contentValues);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        Uri insertUri = ContentUris.withAppendedId(uri, id);
        getContext().getContentResolver().notifyChange(insertUri, null);

        return insertUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case MESSAGES_ALLROWS:
            case USERS_ALLROWS:
                count = db.delete(getTableName(uri), selection, selectionArgs);
                break;

            case MESSAGES_SINGLE_ROW:
            case USERS_SINGLE_ROW:
                count = db.delete(getTableName(uri), "_id = ?", new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int count;
        switch(uriMatcher.match(uri)) {
            case MESSAGES_ALLROWS:
            case USERS_ALLROWS:
                count = db.update(getTableName(uri), contentValues, selection, selectionArgs);
                break;

            case MESSAGES_SINGLE_ROW:
            case USERS_SINGLE_ROW:
                count = db.update(getTableName(uri), contentValues, "_id = ?", new String[]{uri.getLastPathSegment()});
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }


    private String getTableName(Uri uri) {
        switch(uriMatcher.match(uri)) {
            case MESSAGES_ALLROWS:
            case MESSAGES_SINGLE_ROW:
                return TABLE_MESSAGGI;

            case USERS_ALLROWS:
            case USERS_SINGLE_ROW:
                return TABLE_UTENTI;
        }
        return null;
    }


    public class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "Message_database.db";
        private static final int DATABASE_VERSION = 2;
        private static final String DATABASE_CREATE1 ="CREATE TABLE Utenti( _id integer primary key autoincrement, Numero VARCHAR(14) unique, Nome VARCHAR(30) NOT NULL, Immagine BLOB); ";
        private static final String DATABSE_CREATE2 = "CREATE TABLE Messaggi( _id INTEGER PRIMARY KEY AUTOINCREMENT, Testo VARCHAR(255) NOT NULL, Destinatario VARCHAR(14) NOT NULL, Dataora DATETIME NOT NULL, Ricevuto BOOL, FOREIGN KEY(Destinatario) REFERENCES Utenti(Numero));";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(DATABASE_CREATE1);
            sqLiteDatabase.execSQL(DATABSE_CREATE2);
            Log.i("TAG", "database creato");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i2) {
            // Questo metodo viene chiamato durante l'upgrade del database, ad esempio quando viene incrementato il numero di versione
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Utenti;");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS Messaggi;");
            onCreate(sqLiteDatabase);
        }
    }

}
