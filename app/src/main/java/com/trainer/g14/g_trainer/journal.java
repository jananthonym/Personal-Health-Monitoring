package com.trainer.g14.g_trainer;

import android.app.Activity;
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
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import sqlite.helper.DatabaseHelper2;
import sqlite.model.History;


/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class journal extends Fragment implements View.OnClickListener{
    private ListView listView; //list view of histories
    private DatabaseHelper2 db=null; //db
    private View lastSelectedView=null;
    private TextView stats; // text view of stats from a history
    private List<History> hlist; //list of histories
    private Button save; //save button
    private History current = null; //current history
    private TextView calin; //calories in text view

    private OnFragmentInteractionListener mListener;

    private static final String TAG = journal.class.getSimpleName();

    // TODO: Rename and change types and number of parameters
    public static journal newInstance() {
        journal fragment = new journal();

        return fragment;
    }

    public journal() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_journal, container, false);

        save = (Button) view.findViewById(R.id.save); //set up save button
        save.setOnClickListener(this);

        calin = (TextView) view.findViewById(R.id.calin); //calories in text view

        listView = (ListView) view.findViewById(R.id.historyList); //list view
        //listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        stats = (TextView) view.findViewById(R.id.result); //text view of stats
        stats.setText("Results from Selected Date: ");

        hlist = db.getAllHistory(); //get all histories
        List<String> values = new ArrayList<String>(); //list of dates from each history
        for(int q=hlist.size()-1; q>-1; q--){
            values.add(hlist.get(q).getDate());
        }
        final int size=hlist.size()-1;
        //display all the dates in the list view
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        // ListView Item Click Listener
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //clear previous selection
                listView.setItemChecked(position, true);
                clearSelection();
                lastSelectedView=view;
                view.setBackgroundColor(getResources().getColor(R.color.pressed_color));

                // the list displays the items in reverse order so calculate the real index
                int itemPosition  = size-position;

                // get the history object and display it's stats
                String history    = (String) listView.getItemAtPosition(position);
                displayStats(itemPosition);
                current = hlist.get(itemPosition);
                // Show Alert
                Log.i(TAG, "Position: " + position + "  ListItem: " + history);


            }

        });


        return view;
    }

    @Override
    public void onClick(View v) {
        //save the user input of calories in to the selected history
        if(current!=null && !calin.getText().toString().contains("Calories")) {
            Log.i(TAG, "Saving Calories in");
            current.setcaloriesIn(Integer.parseInt(calin.getText().toString()));
            db.updateHistory(current);
        }
    }

    //display the stats of the selected history
    private void displayStats(int index){
        History ayy=hlist.get(index);
        calin.setText("Calories In: "+hlist.get(index).getCaloriesIn());
        stats.setText("Results from Selected Date: "
                + "\nRoutine: "+ayy.getRoutine()
                + "\nCalories Burned: " + new DecimalFormat("#.##").format(ayy.getcaloriesOut())
                + "\nSteps Taken: " + ayy.getSteps());
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
        db=new DatabaseHelper2(getActivity()); //get db
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
