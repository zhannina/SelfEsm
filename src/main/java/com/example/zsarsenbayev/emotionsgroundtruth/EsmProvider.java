package com.example.zsarsenbayev.emotionsgroundtruth;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.util.HashMap;

/**
 * Created by zsarsenbayev on 5/31/18.
 */

public class EsmProvider extends ContentProvider{
    public static final int ESM_DATABASE_VERSION = 1;
    public static String ESM_AUTHORITY = "com.example.zsarsenbayev.emotionsgroundtruth.selfesm";
    private static final int ESM_SENSOR_DEV = 1;
    private static final int ESM_SENSOR_DEV_ID = 2;

    public static final class EsmTable implements BaseColumns {
        private EsmTable() {
        }

        public static final Uri ESM_CONTENT_URI = Uri.parse("content://"
                + ESM_AUTHORITY + "/selfesm");
        public static final String ESM_CONTENT_TYPE = "vnd.android.cursor.dir/vnd.contextdatareading.selfesm";
        public static final String ESM_CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.contextdatareading.selfesm";

        public static final String ESM_TIMESTAMP = "timestamp";
        public static final String ESM_PERSON_COLOR = "person_color";
        public static final String ESM_ANSWER = "esm_answer";
    }

    public static String ESM_DATABASE_NAME = "selfesm.db";
    public static final String[] ESM_DATABASE_TABLES = { "selfesm" };

    public static final String[] TABLES_FIELDS = {
            EsmTable._ID + " integer primary key autoincrement,"
                    + EsmTable.ESM_TIMESTAMP + " real default 0,"
                    + EsmTable.ESM_PERSON_COLOR + " text default '',"
                    + EsmTable.ESM_ANSWER + " text default '' "
    };

    private static UriMatcher sUriMatcher = null;
    private static HashMap<String, String> sensorMap = null;
    private static DatabaseHelper databaseHelper = null;
    private static SQLiteDatabase database = null;

    private boolean initializeDB() {
        if (databaseHelper == null) {
            databaseHelper = new DatabaseHelper( getContext(), ESM_DATABASE_NAME, null, ESM_DATABASE_VERSION, ESM_DATABASE_TABLES, TABLES_FIELDS );
        }
        if( databaseHelper != null && ( database == null || ! database.isOpen() )) {
            database = databaseHelper.getWritableDatabase();
        }
        return( database != null && databaseHelper != null);
    }

    public static void resetDB(Context c ) {
        Log.d("AWARE", "Resetting " + ESM_DATABASE_NAME + "...");

        File db = new File(ESM_DATABASE_NAME);
        db.delete();
        databaseHelper = new DatabaseHelper( c, ESM_DATABASE_NAME, null, ESM_DATABASE_VERSION, ESM_DATABASE_TABLES, TABLES_FIELDS);
        if( databaseHelper != null ) {
            database = databaseHelper.getWritableDatabase();
        }
    }

    @Override
    public boolean onCreate() {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(EsmProvider.ESM_AUTHORITY, ESM_DATABASE_TABLES[0],
                ESM_SENSOR_DEV);
        sUriMatcher.addURI(EsmProvider.ESM_AUTHORITY, ESM_DATABASE_TABLES[0] + "/#",
                ESM_SENSOR_DEV_ID);

        sensorMap = new HashMap<String, String>();
        sensorMap.put(EsmTable._ID, EsmTable._ID);
        sensorMap.put(EsmTable.ESM_TIMESTAMP, EsmTable.ESM_TIMESTAMP);
        sensorMap.put(EsmTable.ESM_PERSON_COLOR, EsmTable.ESM_PERSON_COLOR);
        sensorMap.put(EsmTable.ESM_ANSWER, EsmTable.ESM_ANSWER);

        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if( ! initializeDB() ) {
            //Log.w(ESM_AUTHORITY,"Database unavailable...");
            return null;
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        switch (sUriMatcher.match(uri)) {
            case ESM_SENSOR_DEV:
                qb.setTables(ESM_DATABASE_TABLES[0]);
                qb.setProjectionMap(sensorMap);
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        try {
            Cursor c = qb.query(database, projection, selection, selectionArgs,
                    null, null, sortOrder);
            c.setNotificationUri(getContext().getContentResolver(), uri);
            return c;
        } catch (IllegalStateException e) {

            Log.e("Aware.TAG", e.getMessage());

            return null;
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case ESM_SENSOR_DEV:
                return EsmTable.ESM_CONTENT_TYPE;
            case ESM_SENSOR_DEV_ID:
                return EsmTable.ESM_CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues initialValues) {
        if( ! initializeDB() ) {
            //Log.w(ESM_AUTHORITY,"Database unavailable...");
            return null;
        }

        ContentValues values = (initialValues != null) ? new ContentValues(
                initialValues) : new ContentValues();

        switch (sUriMatcher.match(uri)) {
            case ESM_SENSOR_DEV:
                database.beginTransaction();
                long accel_id = database.insertWithOnConflict(ESM_DATABASE_TABLES[0],
                        EsmTable.ESM_PERSON_COLOR, values, SQLiteDatabase.CONFLICT_IGNORE);
                database.setTransactionSuccessful();
                database.endTransaction();
                if (accel_id > 0) {
                    Uri accelUri = ContentUris.withAppendedId(
                            EsmTable.ESM_CONTENT_URI, accel_id);
                    getContext().getContentResolver().notifyChange(accelUri, null);
                    return accelUri;
                }
                throw new SQLException("Failed to insert row into " + uri);
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        if( ! initializeDB() ) {
            //Log.w(ESM_AUTHORITY,"Database unavailable...");
            return 0;
        }
        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case ESM_SENSOR_DEV:
                database.beginTransaction();
                count = database.delete(ESM_DATABASE_TABLES[0], selection,
                        selectionArgs);
                database.setTransactionSuccessful();
                database.endTransaction();
                break;
            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        if( ! initializeDB() ) {
            //Log.w(ESM_AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch (sUriMatcher.match(uri)) {
            case ESM_SENSOR_DEV:
                database.beginTransaction();
                count = database.update(ESM_DATABASE_TABLES[0], values, selection,
                        selectionArgs);
                database.setTransactionSuccessful();
                database.endTransaction();
                break;

            default:

                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if( ! initializeDB() ) {
//            Log.w(ESM_AUTHORITY,"Database unavailable...");
            return 0;
        }

        int count = 0;
        switch ( sUriMatcher.match(uri) ) {
            case ESM_SENSOR_DEV:
                database.beginTransaction();
                for (ContentValues v : values) {
                    long id;
                    try {
                        id = database.insertOrThrow( ESM_DATABASE_TABLES[0], EsmTable.ESM_PERSON_COLOR, v );
                    } catch ( SQLException e ) {
                        id = database.replace( ESM_DATABASE_TABLES[0], EsmTable.ESM_PERSON_COLOR, v );
                    }
                    if( id <= 0 ) {
                        Log.w("Light.TAG", "Failed to insert/replace row into " + uri);
                    } else {
                        count++;
                    }
                }
                database.setTransactionSuccessful();
                database.endTransaction();
                getContext().getContentResolver().notifyChange(uri, null);
                return count;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }
}
