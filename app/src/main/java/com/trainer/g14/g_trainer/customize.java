package com.trainer.g14.g_trainer;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import sqlite.helper.DatabaseHelper;
import sqlite.model.Routine;


/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class customize extends Fragment {
    private ListView listView; //List view of routines
    private DatabaseHelper db; //database object
    private View lastSelectedView=null;
    private View view;

    private OnFragmentInteractionListener mListener;

    private static final String TAG = customize.class.getSimpleName();


    // TODO: Rename and change types and number of parameters
    public static customize newInstance() {
        customize fragment = new customize();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);*/
        return fragment;
    }

    public customize() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_customize, container, false);

        listView = (ListView) view.findViewById(R.id.routineList);
        //get all routines
        List<Routine> rlist = db.getAllRoutines();
        List<String> values = new ArrayList<String>(); //list to hold the names of the routines
        for(Routine r:rlist){
            values.add(r.getName()); //add name to values list
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

                listView.setItemChecked(position, true);
                clearSelection();
                lastSelectedView=view;
                view.setBackgroundColor(getResources().getColor(R.color.pressed_color));
                // ListView Clicked item index
                int itemPosition  = position;

                // ListView Clicked item value
                String routine    = (String) listView.getItemAtPosition(position);
                // Show Alert
                Log.i(TAG, "Position: " + position + "  ListItem: " + routine);

                //start the edit class and tell it which routine to edit
                Intent intent = new Intent(getActivity(), edit.class);
                intent.putExtra("rt", routine);
                startActivity(intent);
            }

        });
        return view;
    }

    public void clearSelection(){
        if(lastSelectedView != null) lastSelectedView.setBackgroundColor(getResources().getColor(android.R.color.transparent));
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
        db=new DatabaseHelper(getActivity()); //get database
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "Closing Database");
        db.closeDB(); //close db
        mListener = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Closing Database");
        db.closeDB(); //close db
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
