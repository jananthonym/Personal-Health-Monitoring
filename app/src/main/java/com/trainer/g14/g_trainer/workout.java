package com.trainer.g14.g_trainer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import sqlite.model.Routine;
import sqlite.model.Exercise;
import sqlite.model.RepSet;
import sqlite.helper.DatabaseHelper;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;


/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class workout extends Fragment implements View.OnClickListener{
    private View view;
    private ListView listView; //list view of routines
    private View lastSelectedView=null;
    private DatabaseHelper db; //db obj
    private String routine=null; //routine name

    public static List<exercise> workout = new ArrayList<exercise>(); //list of exercises for selected routine

    private static final String TAG = workout.class.getSimpleName();

    private OnFragmentInteractionListener mListener;

    public static workout newInstance() {
        workout fragment = new workout();
        return fragment;
    }

    public workout() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_workout, container, false);
        //set up start button
        Button start = (Button) view.findViewById(R.id.start);
        start.setOnClickListener(this);
        // setup list view of routines
        listView = (ListView) view.findViewById(R.id.routines);
        listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        //get routines from db and store names of routines into another list
        List<Routine> elist = db.getAllRoutines();
        List<String> values = new ArrayList<String>();
        for(Routine q:elist){
            values.add(q.getName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //clear last selected
                listView.setItemChecked(position, true);
                clearSelection();
                lastSelectedView=view;
                view.setBackgroundColor(getResources().getColor(R.color.pressed_color));

                // item position in list view
                int itemPosition     = position;

                //set routine name
                routine    = (String) listView.getItemAtPosition(position);

                // Show Alert
                Log.i(TAG, "Position: " +itemPosition+"  ListItem: " +routine);

            }

        });
        return view;
    }

    public void clearSelection(){
        if(lastSelectedView != null) lastSelectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
    }

    @Override
    public void onClick(View v) {
        //start the trainer class and pass it the name of the selected routine
        if(routine!=null) {
            List<Exercise> exercises = db.getAllExerciesByRoutine(routine);
            List<RepSet> repSets = db.getAllRepSetsByRoutine(routine);
            workout.clear(); //make sure list is empty
            for(int q = 0; q<exercises.size(); q++){ //add each exercise,rep,sets into list
                workout.add(new exercise(exercises.get(q).getName(), repSets.get(q).getSets(), repSets.get(q).getReps()));
            }
            Intent intent = new Intent(getActivity(), trainer.class);
            intent.putExtra("rt", routine);
            startActivity(intent); //start trainer class
        }
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        db=new DatabaseHelper(getActivity()); //get the db
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "Closing Database"); //close db
        db.closeDB();
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Closing Database"); //close db
        db.closeDB();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
