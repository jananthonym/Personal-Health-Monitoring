package sqlite.helper;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sqlite.model.Exercise;
import sqlite.model.History;

public class DatabaseHelper2 extends SQLiteOpenHelper {

    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "historyManager";

    // Table Names
    private static final String TABLE_HISTORY = "history";

    // Common column names
    private static final String KEY_ID = "id";
    private static final String KEY_ROUTINE = "routine";
    private static final String KEY_STEPS = "steps";
    private static final String KEY_CALORIESIN = "caloriesin";
    private static final String KEY_CALORIESOUT = "caloriesout";
    private static final String KEY_DATE = "date";


    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_HISTORY = "CREATE TABLE " + TABLE_HISTORY
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ROUTINE
            + " TEXT," + KEY_STEPS + " INTEGER," + KEY_CALORIESIN + " INTEGER,"
            + KEY_CALORIESOUT + " DOUBLE," + KEY_DATE + " STRING" + ")";

    public DatabaseHelper2(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
            // creating required tables
            db.execSQL(CREATE_TABLE_HISTORY);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_HISTORY);

        // create new tables
        onCreate(db);
    }

    /**
     * Creating a todo
     */
    public long createHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CALORIESIN, history.getCaloriesIn());
        values.put(KEY_CALORIESOUT, history.getcaloriesOut());
        values.put(KEY_ROUTINE, history.getRoutine());
        values.put(KEY_STEPS, history.getSteps());
        values.put(KEY_DATE, history.getDate());

        // insert row
        long history_id = db.insert(TABLE_HISTORY, null, values);

        return history_id;
    }

    /**
     * get single todo
     */
    public History getHistory(long history_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY + " WHERE "
                + KEY_ID + " = " + history_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        History td = new History();
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        td.setSteps((c.getInt(c.getColumnIndex(KEY_STEPS))));
        td.setRoutine((c.getString(c.getColumnIndex(KEY_ROUTINE))));
        td.setcaloriesIn((c.getInt(c.getColumnIndex(KEY_CALORIESIN))));
        td.setcaloriesOut((c.getDouble(c.getColumnIndex(KEY_CALORIESOUT))));
        td.setDate((c.getString(c.getColumnIndex(KEY_DATE))));

        return td;
    }

    /**
     * getting all todos
     * */
    public List<History> getAllHistory() {
        List<History> histories = new ArrayList<History>();
        String selectQuery = "SELECT  * FROM " + TABLE_HISTORY;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                History td = new History();
                td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                td.setSteps((c.getInt(c.getColumnIndex(KEY_STEPS))));
                td.setRoutine((c.getString(c.getColumnIndex(KEY_ROUTINE))));
                td.setcaloriesIn((c.getInt(c.getColumnIndex(KEY_CALORIESIN))));
                td.setcaloriesOut((c.getDouble(c.getColumnIndex(KEY_CALORIESOUT))));
                td.setDate((c.getString(c.getColumnIndex(KEY_DATE))));

                // adding to todo list
                histories.add(td);
            } while (c.moveToNext());
        }

        return histories;
    }

    /**
     * getting todo count
     */
    public int getHistoryCount() {
        String countQuery = "SELECT  * FROM " + TABLE_HISTORY;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);

        int count = cursor.getCount();
        cursor.close();

        // return count
        return count;
    }

    /**
     * Updating a todo
     */
    public int updateHistory(History history) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_CALORIESIN, history.getCaloriesIn());
        values.put(KEY_CALORIESOUT, history.getcaloriesOut());
        values.put(KEY_ROUTINE, history.getRoutine());
        values.put(KEY_STEPS, history.getSteps());
        values.put(KEY_DATE, history.getDate());

        // updating row
        return db.update(TABLE_HISTORY, values, KEY_ID + " = ?",
                new String[] { String.valueOf(history.getId()) });
    }

    /**
     * Deleting a todo
     */
    public void deleteHistory(long history_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_HISTORY, KEY_ID + " = ?",
                new String[] { String.valueOf(history_id) });
    }

    // closing database
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     * get datetime
     * */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

}
