package com.trainer.g14.g_trainer;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import sqlite.helper.DatabaseHelper2;
import sqlite.model.History;


/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class home extends Fragment{

    private OnFragmentInteractionListener mListener;

    private static final String TAG = home.class.getSimpleName(); //for Log()

    private static DatabaseHelper2 db; //db obj
    private List<History> hlist; //history list
    private static TextView count, last; //textviews

    private static Thread update; //steps update thread


    public static home newInstance() {
        home fragment = new home();
        return fragment;
    }

    public home() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    public void onResume() {
        super.onResume();
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String name = SP.getString("name_text", "");
        //Display users name
        TextView textView = (TextView) getActivity().findViewById(R.id.welcome);
        textView.setText("Welcome "+name+"!");

        //create new thread to constantly update the text view to step count
        //every 5 seconds
        update = new Thread() {
            @Override
            public void run() {
                while (!Thread.interrupted()) { //stop when thread is interrupted
                    if(Thread.interrupted()) return;
                    int oldstep = 0;
                    List<History> hlist = db.getAllHistory();
                    History current = hlist.get(hlist.size() - 1);
                    dates d = new dates();
                    if(d.compareDates(current.getDate(), d.getToday())==0) {
                        final int step = current.getSteps();
                        if (step != oldstep) {
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    //update textview
                                    count = (TextView) getActivity().findViewById(R.id.count);
                                    count.setText("" + step);
                                }
                            });
                            oldstep = step;
                        }
                    }else{//else no steps for today yet
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //update textview
                                count = (TextView) getActivity().findViewById(R.id.count);
                                count.setText("" + 0);
                            }
                        });
                    }
                    try {
                        // Sleep for 5 seconds
                        Thread.sleep(5 * 1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Interrupt");
                        break;
                    }
                }
                return;
            }
        };
        update.start(); //start the thread

        //display whether google fit api is connected or not
        if(MainActivity.connected){
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView connect = (TextView) getActivity().findViewById(R.id.textView9);
                    connect.setText("Google Fit Status: Connected");
                }
            });
        }else{
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    TextView connect = (TextView) getActivity().findViewById(R.id.textView9);
                    connect.setText("Google Fit Status: Disconnected");
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setupDB setup=new setupDB(getActivity().getApplicationContext());
        setup.setup();
        final View view = inflater.inflate(R.layout.fragment_home, container, false);

        //find out when was the last time user worked out
        last = (TextView) view.findViewById(R.id.last);
        hlist = db.getAllHistory();
        History latest;
        int i = hlist.size();
        if(i==0){ //history database is empty
            last.setText("You haven't worked out yet");
        }else {
            do {
                i--;
                latest = hlist.get(i);
            } while (latest.getRoutine().equals("") && i != 0); //history contains no workout, continue;
            if (i != 0) {
                dates d = new dates();
                String date = latest.getDate();
                String today = d.getToday();
                long days = d.compareDates(date, today);
                if (days == 0)
                    last.setText("You're Last Workout Was Today");
                else
                    last.setText("You Last Worked Out " + days + " Days Ago");
            } else {
                last.setText("You haven't worked out yet");
            }
        }

        return view;
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
    public void onPause() {
        super.onPause();
        Log.i(TAG, "Closing Database"); //close db
        db.closeDB();
        update.interrupt(); //interrupt thread
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    //keep screen in portrait
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isVisibleToUser) {
            Activity a = getActivity();
            if(a != null) a.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
    }

}
