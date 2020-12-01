package com.example.tp0;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class DataBaseHelper extends SQLiteOpenHelper {

    // Database Info
    private static final String DATABASE_NAME = "currencyDatabase";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    private static final String TABLE_CURRENCY = "currencyTab";

    // Currency Table Columns
    private static final String KEY_CURRENCY_ID = "id";
    private static final String KEY_CURRENCY_NAME = "currencyName";
    private static final String KEY_CURRENCY_RATE = "currencyRate";

    private static DataBaseHelper sInstance;

    /*
     * Get the previous database context or create a new database
     * @param context    context of our app
     */
    public static synchronized DataBaseHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new DataBaseHelper(context.getApplicationContext());
        }
        return sInstance;
    }
    

    /*
     * Create a new database with the context app
     * @param context    context of our app
     */
    public DataBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /*
     * Called when the database connection is being configured
     * @param SQLiteDatabase    the concerne database
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }


    /*
     * Called when the database is created for the FIRST time
     * If a database already exists on disk with the same DATABASE_NAME, this method will NOT be called
     * @param SQLiteDatabase    the concerne database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CURRENCY_TABLE = "CREATE TABLE " + TABLE_CURRENCY +
                "(" +
                KEY_CURRENCY_ID + " INTEGER PRIMARY KEY," + // Define a primary key
                KEY_CURRENCY_NAME + " TEXT," +
                KEY_CURRENCY_RATE + " TEXT" +
                ")";
        db.execSQL(CREATE_CURRENCY_TABLE);
    }


    /*
     * Called when upgrade the database
     * @param SQLiteDatabase    the concerne database
     * @param oldVersion        Use to compare database version
     * @param newVersion        Use to compare database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Drop old table and recreate it
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);
            onCreate(db);
        }
    }

    /*
     * Add or update a currency in the database
     * @param currency    the currency name
     * @param rate        the currency rate
     */
    public long addOrUpdateCurrency(String currency, String rate) {
        SQLiteDatabase db = getWritableDatabase();
        long currencyId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_CURRENCY_NAME, currency);
            values.put(KEY_CURRENCY_RATE, rate);

            // First try to update the user in case the user already exists in the database
            // This assumes userNames are unique
            int rows = db.update(TABLE_CURRENCY, values, KEY_CURRENCY_NAME + "= ?", new String[]{currency});

            // Check if update succeeded
            if (rows == 1) {
                // Get the primary key of the user we just updated
                String usersSelectQuery = String.format("SELECT %s FROM %s WHERE %s = ?",
                        KEY_CURRENCY_ID, TABLE_CURRENCY, KEY_CURRENCY_NAME);
                Cursor cursor = db.rawQuery(usersSelectQuery, new String[]{String.valueOf(currency)});
                try {
                    if (cursor.moveToFirst()) {
                        currencyId = cursor.getInt(0);
                        db.setTransactionSuccessful();
                    }
                } finally {
                    if (cursor != null && !cursor.isClosed()) {
                        cursor.close();
                    }
                }
            } else {
                // user with this userName did not already exist, so insert new user
                currencyId = db.insertOrThrow(TABLE_CURRENCY, null, values);
                db.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e("Tag database : ", "Error while trying to add or update user");
        } finally {
            db.endTransaction();
        }
        return currencyId;
    }


    /*
     * Get the database list of currency into a Hashmap
     * @return HashMapCurrency    the currency hashmap from database
     */
    public HashMap<String, String> getAllCurrency() {
        HashMap<String, String> HashMapCurrency = new HashMap<String, String>();

        // Select all data from currencyDatabase
        String POSTS_SELECT_QUERY = String.format("SELECT * FROM %s ", TABLE_CURRENCY);

        // Move into all data from database and add it to the hashmap
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(POSTS_SELECT_QUERY, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor.getColumnIndex(KEY_CURRENCY_NAME));
                    String rate = cursor.getString(cursor.getColumnIndex(KEY_CURRENCY_RATE));
                    HashMapCurrency.put(name,rate);
                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("Tag database : ", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return HashMapCurrency;
    }

}
