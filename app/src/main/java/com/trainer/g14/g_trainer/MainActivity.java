package com.trainer.g14.g_trainer;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.fitness.Fitness;

/**
 * written by: Jan Anthony Miranda
 * tested by: Jan Anthony Miranda
 * debugged by: Jan Anthony Miranda
 */
public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks ,home.OnFragmentInteractionListener, workout.OnFragmentInteractionListener
        , stats.OnFragmentInteractionListener, customize.OnFragmentInteractionListener, journal.OnFragmentInteractionListener{
    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private static final int REQUEST_OAUTH = 1;

    /**
     *  Track whether an authorization activity is stacking over the current activity, i.e. when
     *  a known auth error is being resolved, such as showing the account chooser or presenting a
     *  consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private static final String AUTH_PENDING = "auth_state_pending";
    private boolean authInProgress = false;

    public static GoogleApiClient mClient = null;
    private static final String TAG = MainActivity.class.getSimpleName();

    //global variable to know if google api connected
    public static boolean connected=false;

    private TextView connect;

    @Override
    protected void onStart() {
        super.onStart();
        // Connect to the Fitness API
        Log.i(TAG, "Connecting...");
        mClient.connect();
        // Setup Database
        setupDB setup=new setupDB(getApplicationContext());
        setup.setup();
        //start notification service
        Intent intent = new Intent(this, reminderService.class);
        this.stopService(intent);
        this.startService(intent);
        //start pedometer service
        Intent intent2 = new Intent(this, pedometerService.class);
        this.stopService(intent2);
        this.startService(intent2);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //setup navigation drawer
        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, home.newInstance())
                .commit();

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING);
        }
        //fit api setup
        buildFitnessClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        buildFitnessClient(); //fit api stuff

    }

    //fit api stuff
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OAUTH) {
            authInProgress = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mClient.isConnecting() && !mClient.isConnected()) {
                    mClient.connect();
                }
            }
        }
    }

    //fit api stuff
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(AUTH_PENDING, authInProgress);
    }

    //navigation drawer code
    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch (position+1) {
            case 1:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, home.newInstance()).addToBackStack(null)
                        .commit();
                break;
            case 2:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, workout.newInstance()).addToBackStack(null)
                        .commit();
                break;
            case 3:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, stats.newInstance()).addToBackStack(null)
                        .commit();
                break;
            case 4:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, journal.newInstance()).addToBackStack(null)
                        .commit();
                break;
            case 5:
                fragmentManager.beginTransaction()
                        .replace(R.id.container, customize.newInstance()).addToBackStack(null)
                        .commit();
                break;
        }

    }
    //update titles
    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
            case 5:
                mTitle = getString(R.string.title_section5);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri){

    }


    /**
     *  Build a {@link GoogleApiClient} that will authenticate the user and allow the application
     *  to connect to Fitness APIs. The scopes included should match the scopes your app needs
     *  (see documentation for details). Authentication will occasionally fail intentionally,
     *  and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     *  can address. Examples of this include the user never having signed in before, or having
     *  multiple accounts on the device and needing to specify which account to use, etc.
     */
    private void buildFitnessClient() {
        // Create the Google API Client
        mClient = new GoogleApiClient.Builder(getApplicationContext())
                .addApi(Fitness.SENSORS_API)
                .addApi(Fitness.RECORDING_API)
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addScope(new Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(new Scope(Scopes.FITNESS_LOCATION_READ))
                //.addApi(Games.API).addScope(Games.SCOPE_GAMES)
                .addConnectionCallbacks(
                        new GoogleApiClient.ConnectionCallbacks() {

                            @Override
                            public void onConnected(Bundle bundle) {
                                Log.i(TAG, "Connected!!!");
                                // Now you can make calls to the Fitness APIs.
                                // Put application specific code here.
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        connect = (TextView) findViewById(R.id.textView9);
                                        if(connect != null)
                                            connect.setText("Google Fit Status: Connected");
                                    }
                                });
                                connected = true;

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
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        connect = (TextView) findViewById(R.id.textView9);
                                        connect.setText("Google Fit Status: Disconnected");
                                    }
                                });
                                connected = false;
                            }
                        }
                )
                .addOnConnectionFailedListener(
                        new GoogleApiClient.OnConnectionFailedListener() {
                            // Called whenever the API client fails to connect.
                            @Override
                            public void onConnectionFailed(ConnectionResult result) {
                                Log.i(TAG, "Connection failed. Cause: " + result.toString());
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        connect = (TextView) findViewById(R.id.textView9);
                                        connect.setText("Google Fit Status: Failed to Connect");
                                    }
                                });
                                connected = false;
                                if (!result.hasResolution()) {
                                    // Show the localized error dialog
                                    GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(),
                                            MainActivity.this, 0).show();
                                    return;
                                }
                                // The failure has a resolution. Resolve it.
                                // Called typically when the app is not yet authorized, and an
                                // authorization dialog is displayed to the user.
                                if (!authInProgress) {
                                    try {
                                        Log.i(TAG, "Attempting to resolve failed connection");
                                        authInProgress = true;
                                        result.startResolutionForResult(MainActivity.this,
                                                REQUEST_OAUTH);
                                    } catch (IntentSender.SendIntentException e) {
                                        Log.e(TAG,
                                                "Exception while starting resolution activity", e);
                                    }
                                }
                            }
                        }
                )
                .build();
    }
}
