package com.hovar.auxilium;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;

import android.util.Log;
import android.widget.Toast;


import com.google.android.gms.location.LocationResult;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class MyLocationService extends BroadcastReceiver {

    public static final String ACTION_PROCESS_UPDATE="com.hovar.auxilium.UPDATE_LOCATION";
    public static final String ACTION_AUTHOR_UPDATE="com.hovar.auxilium.UPDATE_AUTHOR";
    SharedPreferences sharedpreferences;

    public static boolean testt = false;
    public static String User_ID="";

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent!=null){
            sharedpreferences = context.getSharedPreferences("customTestPref", Context.MODE_PRIVATE);
            testt = sharedpreferences.getBoolean("testt",false);
            User_ID = sharedpreferences.getString("User_ID","");
            Log.i("TESTING","NON NULL intent");
            Log.i("TESTING uid",User_ID);
            final String action = intent.getAction();
            Log.i("TESTING","finally should go in "+testt);
            if(ACTION_PROCESS_UPDATE.equals(action) && testt){
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("locations").child(User_ID);
                LocationResult result = LocationResult.extractResult(intent);
                Log.i("results","RESULT INCOMING");
                if(result!=null){
                    Location location = result.getLastLocation();
                    String location_string = new StringBuilder("Lat: "+location.getLatitude())
                            .append(" , Long: ")
                            .append(location.getLongitude())
                            .append(" ,alt: ")
                            .append(location.getAltitude())
                            .toString();
                    Log.i("LOC",location_string);
                    myRef.child("Latitude").setValue(location.getLatitude());
                    myRef.child("Longitude").setValue(location.getLongitude());
                    myRef.child("Altitude").setValue(location.getAltitude());
                    myRef.child("lastUpdatedAt").setValue(location.getTime());
                    try{
                        MainActivity.getInstance().updateTextView(location_string);
                        //myRef.setValue(location_string);
                        myRef.child("offline").setValue(false);
                    }catch (Exception ex){
                        Log.i("offline","working");
                        Toast.makeText(context, location_string, Toast.LENGTH_SHORT).show();
                        myRef.child("offline").setValue(true);
                    }
                }
            }
            if(ACTION_AUTHOR_UPDATE.equals(action)){
                Log.i("TESTING",intent.getStringExtra("TEST_VAL"));
                Log.i("TESTING","IT WORKS OH YEAH");
                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putBoolean("testt",true);
                edit.putString("User_ID",intent.getStringExtra("TEST_VAL"));
                edit.apply();
                //testt=true;
            }
        }
    }
}
