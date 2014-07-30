package com.xoverto.carparks.app;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
/**
 * Created by andrew on 07/07/2014.
 */
public class CarParkProvider extends ContentProvider {

    public static final Uri CONTENT_URI = Uri.parse("content://com.xoverto.carparkprovider/carparks");

    // Column names
    public static final String KEY_ID = "_id";
    public static final String KEY_CAR_PARK_ID = "car_park_id";
    public static final String KEY_NAME = "name";
    public static final String KEY_UPDATED = "updated";
    public static final String KEY_LOCATION_LAT = "latitude";
    public static final String KEY_LOCATION_LNG = "longitude";
    public static final String KEY_OCCUPANCY = "occupancy";
    public static final String KEY_OCCUPANCY_PERCENTAGE = "occupancy_percentage";
    public static final String KEY_SPACES = "spaces";

    // Create the constants used to differentiate between the different URI requests
    private static final int CAR_PARKS = 1;
    private static final int CAR_PARK_ID = 2;

    private static final UriMatcher uriMatcher;

    // Allocate the UriMatcher object, where a URI ending in 'carparks' will correspond to a request for all carparks,
    // and 'carparks' with a trailing '/[rowID]' will represent a single carpark row.

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI("com.xoverto.carparkprovider", "carparks", CAR_PARKS);
        uriMatcher.addURI("com.xoverto.carparkprovider", "carparks/#", CAR_PARK_ID);
    }

    CarParkDatabaseHelper dbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();

        dbHelper = new CarParkDatabaseHelper(context, CarParkDatabaseHelper.DATABASE_NAME, null, CarParkDatabaseHelper.DATABASE_VERSION);

        return true;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case CAR_PARKS: return "vnd.android.cursor.dir/vnd.com.xoverto.carparks";
            case CAR_PARK_ID: return "vnd.android.cursor.item/vnd.com.xoverto.carparks";
            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sort) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(CarParkDatabaseHelper.CAR_PARK_TABLE);

        // If this is a row query, limit the result set to the passed in row
        switch (uriMatcher.match(uri)) {
            case CAR_PARK_ID: qb.appendWhere(KEY_ID + "=" + uri.getPathSegments().get(1));
                break;
            default: break;
        }

        // If no sort order is specified, sort by name
        String orderBy;
        if(TextUtils.isEmpty(sort)) {
            orderBy = KEY_NAME;
        } else {
            orderBy = sort;
        }

        // Apply the query to the underlying database
        Cursor c = qb.query(database,
                projection,
                selection, selectionArgs,
                null, null,
                orderBy);

        // Register the contexts ContentResolver to be notified if the cursor result set changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        // Return a cursor to the query result
        return c;
    }

    @Override
    public Uri insert(Uri _uri, ContentValues _initialValues) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new row. The call to the database.insert will return the row number if it is successful.
        long rowID = database.insert(CarParkDatabaseHelper.CAR_PARK_TABLE, "car_park", _initialValues);

        // Return a URI to the newly inserted row on success.
        if(rowID > 0) {
            Uri uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            getContext().getContentResolver().notifyChange(CONTENT_URI, null);
            return uri;
        }

        throw new SQLException("Failed to insert row into " + _uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case CAR_PARKS:
                count = database.delete(CarParkDatabaseHelper.CAR_PARK_TABLE, where, whereArgs);
                break;

            case CAR_PARK_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.delete(CarParkDatabaseHelper.CAR_PARK_TABLE, KEY_ID + "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default: throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[]whereArgs) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        int count;
        switch (uriMatcher.match(uri)) {
            case CAR_PARKS:
                count = database.update(CarParkDatabaseHelper.CAR_PARK_TABLE, values, where, whereArgs);
                break;

            case CAR_PARK_ID:
                String segment = uri.getPathSegments().get(1);
                count = database.update(CarParkDatabaseHelper.CAR_PARK_TABLE, values, KEY_ID + "=" + segment + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""), whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return count;
    }

    // Helper class for opening, creating and managing database version control
    private static class CarParkDatabaseHelper extends SQLiteOpenHelper {
        private static final String TAG = "CarParkProvider";
        private static final String DATABASE_NAME = "car_parks.db";
        private static final int DATABASE_VERSION = 1;
        private static final String CAR_PARK_TABLE = "car_parks";
        private static final String DATABASE_CREATE = "create table " + CAR_PARK_TABLE + " ("
                + KEY_ID + " integer primary key autoincrement, "
                + KEY_CAR_PARK_ID + " TEXT,"
                + KEY_NAME + " TEXT, "
                + KEY_UPDATED + " INTEGER, "
                + KEY_LOCATION_LAT + " FLOAT, "
                + KEY_LOCATION_LNG + " FLOAT, "
                + KEY_OCCUPANCY + " INTEGER, "
                + KEY_OCCUPANCY_PERCENTAGE + " REAL, "
                + KEY_SPACES + " INTEGER);";

        // The underlying database
        private SQLiteDatabase carParkDB;

        public CarParkDatabaseHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {

            super(context, name, factory, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + " which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS " + CAR_PARK_TABLE);
            onCreate(db);
        }


    }
}
