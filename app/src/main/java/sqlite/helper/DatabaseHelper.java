package sqlite.helper;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import sqlite.model.Exercise;
import sqlite.model.RepSet;
import sqlite.model.Routine;

public class DatabaseHelper extends SQLiteOpenHelper {

    //
    private static String DB_PATH = "data/data/com.database.trainer.g14.q_trainer/databases/";
    private static String DB_NAME = "Workouts";

    // Logcat tag
    private static final String LOG = DatabaseHelper.class.getName();

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "routineManager";

    // Table Names
    private static final String TABLE_EXERCISE = "exercises";
    private static final String TABLE_ROUTINE = "routines";
    private static final String TABLE_REPSET = "repsets";
    private static final String TABLE_EXERCISE_ROUTINE_REPSET = "err";

    // Common column names
    private static final String KEY_ID = "id";
    //private static final String KEY_CREATED_AT = "created_at";

    // NOTES Table - column nmaes
    private static final String KEY_EXERCISE = "exercise";
    //private static final String KEY_STATUS = "status";

    // TAGS Table - column names
    private static final String KEY_ROUTINE_NAME = "routine_name";

    // REPSET Table
    private static final String KEY_REPS = "reps";
    private static final String KEY_SETS = "sets";

    // NOTE_TAGS Table - column names
    private static final String KEY_EXERCISE_ID = "exercise_id";
    private static final String KEY_ROUTINE_ID = "routine_id";
    private static final String KEY_REPSET_ID = "repset_id";


    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_EXERCISE = "CREATE TABLE " + TABLE_EXERCISE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_EXERCISE
            + " TEXT" + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_ROUTINE = "CREATE TABLE " + TABLE_ROUTINE
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_ROUTINE_NAME + " TEXT"
            + ")";

    // Tag table create statement
    private static final String CREATE_TABLE_REPSET = "CREATE TABLE " + TABLE_REPSET
            + "(" + KEY_ID + " INTEGER PRIMARY KEY," + KEY_REPS + " INTEGER,"
            + KEY_SETS + " INTEGER" + ")";

    // todo_tag table create statement
    private static final String CREATE_TABLE_EXERCISE_ROUTINE_REPSET = "CREATE TABLE "
            + TABLE_EXERCISE_ROUTINE_REPSET + "(" + KEY_ID + " INTEGER PRIMARY KEY,"
            + KEY_EXERCISE_ID + " INTEGER," + KEY_ROUTINE_ID + " INTEGER,"
            + KEY_REPSET_ID + " INTEGER" + ")";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    public void onCreate(SQLiteDatabase db) {
        if(!checkDataBase()) {
            // creating required tables
            db.execSQL(CREATE_TABLE_EXERCISE);
            db.execSQL(CREATE_TABLE_ROUTINE);
            db.execSQL(CREATE_TABLE_REPSET);
            db.execSQL(CREATE_TABLE_EXERCISE_ROUTINE_REPSET);
        }
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ROUTINE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPSET);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXERCISE_ROUTINE_REPSET);

        // create new tables
        onCreate(db);
    }

    // Check if the database exist to avoid re-copy the data
    private boolean checkDataBase(){

        SQLiteDatabase checkDB = null;

        try{
            String path = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.OPEN_READONLY);
        }catch(SQLiteException e){
            // database don't exist yet.
            e.printStackTrace();
        }

        if(checkDB != null){
            checkDB.close();
        }

        return checkDB != null ? true : false;
    }

    //Open the database
    /*public boolean open() {
        try {
            String myPath = DB_PATH + DB_NAME;
            db = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
            return true;
        } catch(SQLException sqle) {
            db = null;
            return false;
        }
    }*/

    // ------------------------ "todos" table methods ----------------//

    /**
     * Creating a todo
     */
    public long createExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE, exercise.getName());

        // insert row
        long exercise_id = db.insert(TABLE_EXERCISE, null, values);

        return exercise_id;
    }

    /**
     * get single todo
     */
    public Exercise getExercise(long exercise_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISE + " WHERE "
                + KEY_ID + " = " + exercise_id;

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        Exercise td = new Exercise();
        td.setId(c.getInt(c.getColumnIndex(KEY_ID)));
        //td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
        //td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

        return td;
    }

    /**
     * getting all todos
     * */
    public List<Exercise> getAllExercises() {
        List<Exercise> exercises = new ArrayList<Exercise>();
        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Exercise td = new Exercise();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                //td.setNote((c.getString(c.getColumnIndex(KEY_TODO))));
                //td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                exercises.add(td);
            } while (c.moveToNext());
        }

        return exercises;
    }

    /**
     * getting all todos under single tag
     * */
    public List<Exercise> getAllExerciesByRoutine(String routine_name) {
        List<Exercise> exercises = new ArrayList<Exercise>();

        String selectQuery = "SELECT  * FROM " + TABLE_EXERCISE + " td, "
                + TABLE_ROUTINE + " tg, " + TABLE_EXERCISE_ROUTINE_REPSET + " tt WHERE tg."
                + KEY_ROUTINE_NAME + " = '" + routine_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_ROUTINE_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_EXERCISE_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Exercise td = new Exercise();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setName(c.getString(c.getColumnIndex(KEY_EXERCISE)));
                //td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                exercises.add(td);
            } while (c.moveToNext());
        }

        return exercises;
    }

    /**
     * getting todo count
     */
    public int getExerciseCount() {
        String countQuery = "SELECT  * FROM " + TABLE_EXERCISE;
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
    public int updateExercise(Exercise exercise) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE, exercise.getName());
        //values.put(KEY_STATUS, todo.getStatus());

        // updating row
        return db.update(TABLE_EXERCISE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(exercise.getId()) });
    }

    /**
     * Deleting a todo
     */
    public void deleteExercise(long exercise_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE, KEY_ID + " = ?",
                new String[] { String.valueOf(exercise_id) });
    }

    // ------------------------ "tags" table methods ----------------//

    /**
     * Creating tag
     */
    public long createRoutine(Routine routine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_NAME, routine.getName());
        //values.put(KEY_CREATED_AT, getDateTime());

        // insert row
        long tag_id = db.insert(TABLE_ROUTINE, null, values);

        return tag_id;
    }

    /**
     * getting all tags
     * */
    public List<Routine> getAllRoutines() {
        List<Routine> tags = new ArrayList<Routine>();
        String selectQuery = "SELECT * FROM " + TABLE_ROUTINE;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Routine t = new Routine();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setName(c.getString(c.getColumnIndex(KEY_ROUTINE_NAME)));

                // adding to tags list
                tags.add(t);
            } while (c.moveToNext());
        }
        return tags;
    }

    /**
     * getting all todos under single tag
     * */
    public List<RepSet> getAllRepSetsByRoutine(String routine_name) {
        List<RepSet> repSets = new ArrayList<RepSet>();

        String selectQuery = "SELECT * FROM " + TABLE_REPSET + " td, "
                + TABLE_ROUTINE + " tg, " + TABLE_EXERCISE_ROUTINE_REPSET + " tt WHERE tg."
                + KEY_ROUTINE_NAME + " = '" + routine_name + "'" + " AND tg." + KEY_ID
                + " = " + "tt." + KEY_ROUTINE_ID + " AND td." + KEY_ID + " = "
                + "tt." + KEY_REPSET_ID;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RepSet td = new RepSet();
                td.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                td.setReps(c.getInt(c.getColumnIndex(KEY_REPS)));
                td.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));
                //td.setCreatedAt(c.getString(c.getColumnIndex(KEY_CREATED_AT)));

                // adding to todo list
                repSets.add(td);
            } while (c.moveToNext());
        }

        return repSets;
    }

    /**
     * Updating a tag
     */
    public int updateRoutine(Routine routine) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_ROUTINE_NAME, routine.getName());

        // updating row
        return db.update(TABLE_ROUTINE, values, KEY_ID + " = ?",
                new String[] { String.valueOf(routine.getId()) });
    }

    /**
     * Deleting a tag
     */
    public void deleteRoutine(Routine routine, boolean should_delete_all_exercises) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if todos under this tag should also be deleted
        if (should_delete_all_exercises) {
            // get all todos under this tag
            List<Exercise> allRoutineExercies = getAllExerciesByRoutine(routine.getName());

            // delete all todos
            for (Exercise exercise : allRoutineExercies) {
                // delete todo
                deleteExercise(exercise.getId());
            }
        }

        // now delete the tag
        db.delete(TABLE_ROUTINE, KEY_ID + " = ?",
                new String[] { String.valueOf(routine.getId()) });
    }

    // ------------------------ "repset" table methods ----------------//

    public long createRepSet(RepSet repset){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REPS, repset.getReps());
        values.put(KEY_SETS, repset.getSets());

        // insert row
        long repset_id = db.insert(TABLE_REPSET, null, values);

        return repset_id;
    }

    /**
     * getting all tags
     * */
    public List<RepSet> getAllRepSets() {
        List<RepSet> repset = new ArrayList<RepSet>();
        String selectQuery = "SELECT  * FROM " + TABLE_REPSET;

        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                RepSet t = new RepSet();
                t.setId(c.getInt((c.getColumnIndex(KEY_ID))));
                t.setReps(c.getInt(c.getColumnIndex(KEY_REPS)));
                t.setSets(c.getInt(c.getColumnIndex(KEY_SETS)));

                // adding to tags list
                repset.add(t);
            } while (c.moveToNext());
        }
        return repset;
    }

    /**
     * Updating a tag
     */
    public int updateRepSet(RepSet repset) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_REPS, repset.getReps());
        values.put(KEY_SETS, repset.getSets());

        // updating row
        return db.update(TABLE_REPSET, values, KEY_ID + " = ?",
                new String[] { String.valueOf(repset.getId()) });
    }

    /**
     * Deleting a tag
     */
    public void deleteRepSet(long repSet_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REPSET, KEY_ID + " = ?",
                new String[] { String.valueOf(repSet_id) });
    }

    // ------------------------ "todo_tags" table methods ----------------//

    /**
     * Creating todo_tag
     */
    public long createExerciseRoutine(long exercise_id, long routine_id, long repset_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_ID, exercise_id);
        values.put(KEY_ROUTINE_ID, routine_id);
        values.put(KEY_REPSET_ID, repset_id);

        long id = db.insert(TABLE_EXERCISE_ROUTINE_REPSET, null, values);

        return id;
    }

    /**
     * Updating a todo tag
     */
    public int updateExerciseRoutine(long id, long exercise_id, long repset_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_EXERCISE_ID, exercise_id);
        values.put(KEY_REPSET_ID, repset_id);

        // updating row
        return db.update(TABLE_EXERCISE_ROUTINE_REPSET, values, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
    }

    /**
     * Deleting a todo tag
     */
    public void deleteExerciseRoutine(long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_EXERCISE_ROUTINE_REPSET, KEY_ID + " = ?",
                new String[] { String.valueOf(id) });
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