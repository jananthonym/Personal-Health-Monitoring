package com.trainer.g14.g_trainer;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.List;

import sqlite.helper.DatabaseHelper2;
import sqlite.model.History;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class reminderService extends Service {
    private NotificationManager mNM; //notification manager
    private DatabaseHelper2 db; //db obj
    private static final String TAG = reminderService.class.getSimpleName();

    @Override
    public void onCreate() {
        Log.i(TAG, "Service started");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "Received start id " + startId + ": " + intent);

        new Thread(new Runnable() { //create new thread
            @Override
            public void run() {
                while(true){
                    int count =0;
                    db=new DatabaseHelper2(getApplicationContext()); //get obj
                    Log.i(TAG, "Checking if should notify");

                    //get settings
                    SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    boolean notify = SP.getBoolean("notifications_new_message", true);
                    //check user wants notifications
                    if(!notify){
                        try {
                            db.closeDB();
                            // Sleep for 1 hour
                            Thread.sleep(60*60*1000);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "sleep failure");
                        }
                        continue;
                    }

                    //get how many days a week user wants to notify
                    //if it wasn't set, notify by default
                    String days = SP.getString("days_list", "-1");
                    int day = Integer.parseInt(days);
                    Log.d(TAG, "days a week: "+day);
                    if(day==-1) {
                        Log.d(TAG, "wasn't set");
                        showNotification();
                        db.closeDB();
                        try {
                            // Sleep for 1 hour
                            Thread.sleep(60*60*1000);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "sleep failure");
                        }
                        continue;
                    }

                    //get latest history
                    dates q = new dates();
                    List<History> hlist = db.getAllHistory();
                    int i = hlist.size();
                    // if brand new user, notify
                    if(i==0){
                        Log.d(TAG, "no history");
                        showNotification();
                        db.closeDB();
                        try {
                            // Sleep for 1 hour
                            Thread.sleep(60*60*1000);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "sleep failure");
                        }
                        continue;
                    }

                    //check if worked out today
                    int hIndex = hlist.size()-1;
                    History latest=hlist.get(hIndex);
                    if(q.compareDates(latest.getDate(), q.getToday())==0)
                        if(!latest.getRoutine().equals("")) {
                            Log.d(TAG, "already worked out");
                            db.closeDB();
                            try {
                                // Sleep for 1 hour
                                Thread.sleep(60*60*1000);
                            } catch (InterruptedException e) {
                                Log.d(TAG, "sleep failure");
                            }
                            continue;
                        }

                    //Check if user's weekly goal is met
                    hlist = db.getAllHistory();
                    hIndex = hlist.size()-1;
                    latest=hlist.get(hIndex); //get latest history
                    //get int representation of today's position in the week 0-6
                    int y = q.getWorkoutsWeek(latest.getDate());
                    Log.d(TAG, "day of week "+y);
                    //check all the days before today
                    for(int z=hIndex-1; z!=-1; z--){
                        History prev = hlist.get(z);
                        //check if prev history is at most y days ago
                        int h = (int) q.compareDates(prev.getDate(), latest.getDate());
                        Log.d(TAG, "diff "+h);
                        if(h>y) break;
                        //check if a workout was done in prev history
                        if(prev.getRoutine().equals("")) break;
                        // a workout was done within the week, increment count
                        count++;
                    }
                    Log.d(TAG, "count "+count+" set day: "+day);
                    //if user hasn't met goal, notify
                    if(count<day){
                        Log.d(TAG, count+"<"+day);
                        showNotification();
                        db.closeDB();
                        try {
                            // Sleep for 1 hour
                            Thread.sleep(60*60*1000);
                        } catch (InterruptedException e) {
                            Log.d(TAG, "sleep failure");
                        }
                        continue;
                    }

                    db.closeDB();


                    try {
                        // Sleep for 1 hour
                        Thread.sleep(60*60*1000);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "sleep failure");
                    }
                }
            }
        }).start();
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        // Cancel the persistent notification.
        //mNM.cancel(0);
        Log.i(TAG, "db closed");
        db.closeDB(); //close db
        Log.i(TAG, "Service Stopped");
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //show the notification telling user to workout
    private void showNotification() {
        long[] pattern= {0,0,0,0}; //no vibrate
        //get ringtone and vibrate settings
        SharedPreferences SP = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String ringtone = SP.getString("notifications_new_message_ringtone", "content://settings/system/notification_sound");
        boolean vib = SP.getBoolean("notifications_new_message_vibrate", true);
        if(vib){ //if user wants vibrate then change the pattern
            pattern = new long[] {200,300,200,300};
        }
        mNM = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        //create intent to start Main Activity when notification is selected
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent notifyPendingIntent =
                PendingIntent.getActivity(
                        this,
                        0,
                        resultIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        //build the notification
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.iconbmp);
        NotificationCompat.Builder noti = new NotificationCompat.Builder(this);
        noti.setContentTitle("It's time to workout!") //title
                .setContentText("Touch To Workout") //subtitle
                .setContentIntent(notifyPendingIntent) //intent
                .setAutoCancel(true) //go away when touched
                .setSound(Uri.parse(ringtone)) //set ringtone
                .setVibrate(pattern) //set vibrate
                .setSmallIcon(R.drawable.icon); //set icon
                //.setLargeIcon(bm);


        Log.i(TAG, "Showing notification");
        mNM.notify(0, noti.build()); //show the notification
    }

}

