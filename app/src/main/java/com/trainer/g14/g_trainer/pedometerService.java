package com.trainer.g14.g_trainer;

import android.app.Service;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;

import java.util.List;

import sqlite.helper.DatabaseHelper2;
import sqlite.model.History;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class pedometerService extends Service implements OnDataPointListener {
    private DatabaseHelper2 db; //db obj
    private boolean listener=false; //bool to know is a listener was already registered
    private static final String TAG = pedometerService.class.getSimpleName();

    //google fit api stuff
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;
    private static final int REQUEST_OAUTH = 1;
    private static GoogleApiClient mClient = null;

    @Override
    public void onCreate() {
        Log.i(TAG, "Pedo Service started");
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        buildFitnessClient(); //build Fit api stuff
        mClient.connect(); //connect the api

        addListener(); //add a step listener
        db=new DatabaseHelper2(getApplicationContext()); //get the db
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);


        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        removeListener(); //remove the listener
        Log.i(TAG, "Closing Database");
        db.closeDB(); //close the db

        if (mClient.isConnected()) {
            mClient.disconnect(); //diconnect the fit api
        }

        // Tell the user we stopped.
        Log.i(TAG, "Pedo Service Stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //add step listener
    private void addListener(){
        if(listener) return;
        Log.i(TAG, "adding listener");
        Fitness.SensorsApi.add(
                mClient,
                new SensorRequest.Builder()
                        .setDataType(DataType.TYPE_STEP_COUNT_DELTA) // steps since last data point
                        .build(),
                this).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(Status status) {
                if (status.isSuccess()) {
                    Log.i(TAG, "Listener registered!");
                } else {
                    Log.i(TAG, "Listener not registered.");
                }
            }
        });
        listener = true; //set bool to true
    }

    //remove step listener
    private void removeListener() {
        if(!listener || !mClient.isConnected()) return;
        Fitness.SensorsApi.remove(
                mClient,
                this)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            Log.i(TAG, "Listener was removed!");
                        } else {
                            Log.i(TAG, "Listener was not removed.");
                        }
                    }
                });
        listener=false; //set bool to false
    }

    //data point listener
    @Override
    public void onDataPoint(DataPoint dataPoint) {
        final int steps = dataPoint.getValue(Field.FIELD_STEPS).asInt(); //get steps since last data point
        Log.i(TAG, "Detected :"+steps);
        List<History> hlist=db.getAllHistory();
        History current = hlist.get(hlist.size()-1); //get most recent history
        dates d = new dates();
        if(d.compareDates(current.getDate(),d.getToday())==0){ //there is a history for today, use that
            current.setSteps(current.getSteps()+steps);
            current.setcaloriesOut(current.getcaloriesOut() + 0.05*steps);
            db.updateHistory(current);
        }else{ //else, create a new one for today
            current=new History(0,0,"",steps,d.getToday());
            current.setcaloriesOut(current.getcaloriesOut() + 0.05*steps);
            db.createHistory(current);
        }

    }


    protected void onSaveInstanceState(Bundle outState) {
        //super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(this)
                .addApi(Fitness.SENSORS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                        //.addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.

                            }

                            @Override
                            public void onConnectionSuspended(int i) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    Log.i(TAG, "Connection lost.  Cause: Network Lost.");
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    Log.i(TAG, "Connection lost.  Reason: Service Disconnected");
                                }

                            }
                        }
                )

                .build();
    }
}
