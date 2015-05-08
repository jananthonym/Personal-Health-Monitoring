package com.trainer.g14.g_trainer;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Routine;
import sqlite.model.RepSet;
import sqlite.model.Exercise;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class edit extends ActionBarActivity implements View.OnClickListener {
    private DatabaseHelper db; //database
    private List<Exercise> elist; //exercise list
    private List<RepSet> rslist; //rep set list
    private List<TextView> tvs = new ArrayList<TextView>(); //list of the text views
    private List<EditText> ets = new ArrayList<EditText>(); //list of the edit texts

    private TextView tv1,tv2,tv3,tv4,tv5,tv6,tv7,tv8,tv9,tv10; //10 text views

    private EditText et1,et2,et3,et4,et5,et6,et7,et8,et9,et10  //20 edit texts
            ,et11,et12,et13,et14,et15,et16,et17,et18,et19,et20;

    private static final String TAG = edit.class.getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        getSupportActionBar().setDefaultDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String routine = intent.getStringExtra("rt"); //get the routine name to edit

        TextView name = (TextView) findViewById(R.id.name);
        name.setText(routine);

        db=new DatabaseHelper(getApplicationContext());
        elist=db.getAllExerciesByRoutine(routine); //populate the lists
        rslist=db.getAllRepSetsByRoutine(routine);

        //get all textviews and add to list
        tv1=(TextView) findViewById(R.id.textView10);
        tv2=(TextView) findViewById(R.id.textView11);
        tv3=(TextView) findViewById(R.id.textView12);
        tv4=(TextView) findViewById(R.id.textView13);
        tv5=(TextView) findViewById(R.id.textView14);
        tv6=(TextView) findViewById(R.id.textView15);
        tv7=(TextView) findViewById(R.id.textView16);
        tv8=(TextView) findViewById(R.id.textView17);
        tv9=(TextView) findViewById(R.id.textView18);
        tv10=(TextView) findViewById(R.id.textView19);
        tvs.add(tv1);
        tvs.add(tv2);
        tvs.add(tv3);
        tvs.add(tv4);
        tvs.add(tv5);
        tvs.add(tv6);
        tvs.add(tv7);
        tvs.add(tv8);
        tvs.add(tv9);
        tvs.add(tv10);

        //get all edittexts and add to list
        et1=(EditText) findViewById(R.id.editText);
        et2=(EditText) findViewById(R.id.editText2);
        et3=(EditText) findViewById(R.id.editText3);
        et4=(EditText) findViewById(R.id.editText4);
        et5=(EditText) findViewById(R.id.editText5);
        et6=(EditText) findViewById(R.id.editText6);
        et7=(EditText) findViewById(R.id.editText7);
        et8=(EditText) findViewById(R.id.editText8);
        et9=(EditText) findViewById(R.id.editText9);
        et10=(EditText) findViewById(R.id.editText10);
        et11=(EditText) findViewById(R.id.editText11);
        et12=(EditText) findViewById(R.id.editText12);
        et13=(EditText) findViewById(R.id.editText13);
        et14=(EditText) findViewById(R.id.editText14);
        et15=(EditText) findViewById(R.id.editText15);
        et16=(EditText) findViewById(R.id.editText16);
        et17=(EditText) findViewById(R.id.editText17);
        et18=(EditText) findViewById(R.id.editText18);
        et19=(EditText) findViewById(R.id.editText19);
        et20=(EditText) findViewById(R.id.editText20);
        ets.add(et1);
        ets.add(et2);
        ets.add(et3);
        ets.add(et4);
        ets.add(et5);
        ets.add(et6);
        ets.add(et7);
        ets.add(et8);
        ets.add(et9);
        ets.add(et10);
        ets.add(et11);
        ets.add(et12);
        ets.add(et13);
        ets.add(et14);
        ets.add(et15);
        ets.add(et16);
        ets.add(et17);
        ets.add(et18);
        ets.add(et19);
        ets.add(et20);

        // set the exercise name for each textview
        for(int i=0;i<elist.size();i++){
            tvs.get(i).setText(elist.get(i).getName());
        }
        // set the already saved reps and sets into the edit texts
        int index=0;
        for(int i=0;i<rslist.size();i++){
            ets.get(index).setText(String.valueOf(rslist.get(i).getReps()));
            ets.get(index+1).setText(String.valueOf(rslist.get(i).getSets()));
            index+=2;
        }
        //make the other unused edittexts disappear
        for(int i=index; i<ets.size(); i++){
            ets.get(i).setVisibility(View.GONE);
        }

        Button save = (Button) findViewById(R.id.save);
        save.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //save everything into the db
        int index=0;
        for(int i=0;i<rslist.size();i++){
            RepSet rs = rslist.get(i);
            rs.setReps(Integer.parseInt(ets.get(index).getText().toString()));
            rs.setSets(Integer.parseInt(ets.get(index+1).getText().toString()));
            index+=2;
            db.updateRepSet(rs);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Closing Database"); //close db
        db.closeDB();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit, menu);
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
