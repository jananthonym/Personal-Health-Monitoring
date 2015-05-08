package com.trainer.g14.g_trainer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import sqlite.helper.DatabaseHelper2;
import sqlite.model.History;
/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class trainer extends ActionBarActivity {
    String routine; //routine name
    private Button cancel; //cancel button
    private Button skip; //skip button
    private Button next; //next button

    private static final String TAG = trainer.class.getSimpleName();

    private TextView textView3; // exercise text view
    private TextView textView2; // title text view

    private List<exercise> regiment; //list of exercise for selected routine
    private int index=0; //index in the list

    private double Calories=0; //calories burned

    private DatabaseHelper2 db; //db obj

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db=new DatabaseHelper2(getApplicationContext()); //get db
        setContentView(R.layout.activity_trainer);
        textView3 = (TextView) findViewById(R.id.exercise);
        textView2 = (TextView) findViewById(R.id.name);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        routine = intent.getStringExtra("rt"); //get routine name

        regiment = workout.workout; //get exercise list

        //setup buttons and listeners
        cancel = (Button) findViewById(R.id.cancel);
        next = (Button) findViewById(R.id.next);
        skip = (Button) findViewById(R.id.skip);
        final Intent intent2 = new Intent(this, MainActivity.class);
        //go to main activity
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(intent2);
            }
        });
        // go to next exercise
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index!=regiment.size()) {
                    updateExercise(1);

                    //increment calories
                    if (index - 1 > -1) {
                        Calories = getCalories(regiment.get(index - 1).getName(),
                                regiment.get(index - 1).getReps(),
                                regiment.get(index - 1).getSets());
                        saveWorkout();
                    }
                }
            }
        });
        // skip to next exercise
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(index!=regiment.size()) {
                    updateExercise(1);
                }
            }
        });

        updateExercise(0);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Closing Database"); //close db
        db.closeDB();
    }

    private void updateExercise(int i){
        index+=i; //increment list index
        //display next exercise
        if(index!=regiment.size()) {
            // Create the text view
            textView3.setText(regiment.get(index).getName()
                    + " Reps: " + regiment.get(index).getReps()
                    + " Sets: " + regiment.get(index).getSets());

            //show pic of exercise
            ImageView picture = (ImageView) findViewById(R.id.pic);
            Drawable pic = getPic(regiment.get(index).getName());
            picture.setImageDrawable(pic);
        }
        //end of routine
        else {
            textView3.setText("");
            textView2.setText("Workout Complete");
            ImageView picture = (ImageView) findViewById(R.id.pic);
            Drawable[] drawables = new Drawable[]{
                    getResources().getDrawable(R.drawable.done),
            };
            picture.setImageDrawable(drawables[0]);
        }

    }

    //save routine and calories into history
    private void saveWorkout(){
        //check if history exists for today or not
        dates D = new dates();
        List<History> hlist = db.getAllHistory();
        History current = hlist.get(hlist.size()-1);
        if(D.compareDates(current.getDate(),D.getToday())==0){ //modify existing history
            if(!current.getRoutine().contains(routine)){
                if(current.getRoutine().equals(""))
                    current.setRoutine(routine);
                else
                    current.setRoutine(current.getRoutine()+", "+routine);
            }
        }else{
            current=new History(0,0,routine,0,D.getToday()); //create a new history
            db.createHistory(current);
        }
        current.setcaloriesOut(current.getcaloriesOut()+Calories); //set the calories
        db.updateHistory(current);
    }

    //get calories for exercise, reps, and sets
    public double getCalories(String exercise, int r, int s){
        int reps = r*s;
        switch (exercise){
            case "Push Ups":
                return 0.825*reps;
            case "Sit Ups":
                return 0.7*reps;
            case "Squats":
                return .5*reps;
            case "Step Ups":
                return .6*reps;
            case "Leg Curls":
                return .7*reps;
            case "Leg Press":
                return .8*reps;
            case "Leg Extensions":
                return .8*reps;
            case "Flat Bench Press":
                return .8*reps;
            case "Incline Bench Press":
                return .9*reps;
            case "Flat Bench Dumbbell Flys":
                return .8*reps;
            case "Incline Bench Dumbbell Flys":
                return .9*reps;
            case "Decline Bench Press":
                return .9*reps;
        }
        return 0;
    }

    //get the corresponding picture
    public Drawable getPic(String exercise){
        Drawable[] drawables;
        switch (exercise){
            case "Push Ups":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.pushups)
                };
                return drawables[0];
            case "Sit Ups":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.situp)
                };
                return drawables[0];
            case "Squats":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.squats)
                };
                return drawables[0];
            case "Step Ups":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.stepups)
                };
                return drawables[0];
            case "Leg Curls":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.legcurls)
                };
                return drawables[0];
            case "Leg Press":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.legpress)
                };
                return drawables[0];
            case "Leg Extensions":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.legextensions)
                };
                return drawables[0];
            case "Flat Bench Press":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.flatbenchpress)
                };
                return drawables[0];
            case "Incline Bench Press":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.inclinebenchpress)
                };
                return drawables[0];
            case "Flat Bench Dumbbell Flys":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.flatbenchflys)
                };
                return drawables[0];
            case "Incline Bench Dumbbell Flys":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.inclinebenchflys)
                };
                return drawables[0];
            case "Decline Bench Press":
                drawables = new Drawable[]{
                        getResources().getDrawable(R.drawable.declinebenchpress)
                };
                return drawables[0];
        }
        return null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_trainer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
