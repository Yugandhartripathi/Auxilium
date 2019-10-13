 package com.hovar.googlelocationbackground;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseError;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

 public class MainActivity extends AppCompatActivity {

     static MainActivity instance;
     LocationRequest locationRequest;
     FusedLocationProviderClient fusedLocationProviderClient;
     TextView txt_location;
     private Button mButton;
     private EditText Name;
     private EditText Telefonnummer;
     private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0 ;

     public static MainActivity getInstance(){
         return instance;
     }
     private FirebaseAuth mAuth;
     private FirebaseAnalytics mFirebaseAnalytics;
     FirebaseDatabase mDatabase;
     private MyReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mDatabase = FirebaseDatabase.getInstance();
        mDatabase.setPersistenceEnabled(true);
        IntentFilter filter = new IntentFilter(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        mReceiver = new MyReceiver (this);
        registerReceiver(mReceiver, filter);

        Name = (EditText)findViewById(R.id.namae);
        Telefonnummer = (EditText)findViewById(R.id.telefonnummer);
        mButton = findViewById(R.id.addButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String nomine = Name.getText().toString();
                String numerus = Telefonnummer.getText().toString();
                Log.i("Button", nomine);
                Log.i("Button", "Button Clicked");
                Log.i("Button", numerus);
                // Write a message to the database
                FirebaseUser currUser = mAuth.getCurrentUser();
                if(currUser!=null) {
                    String currUID = currUser.getUid();
                    mDatabase.getReference("users").child(currUID).child("Contacts").setValue(true);
                    mDatabase.getReference("contacts").child(currUID).child(nomine).setValue(numerus);
                }
            }
        });

        txt_location = (TextView)findViewById(R.id.txt_location);
        instance = this;

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        Toast.makeText(MainActivity.this,"Location access",Toast.LENGTH_SHORT).show();
                        updateLocation();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"You must accept",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
        Dexter.withActivity(this)
                .withPermission(Manifest.permission.SEND_SMS)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        updateLocation();
                        Toast.makeText(MainActivity.this,"SMS linked",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this,"You must accept",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();
    }

     @Override
     public void onStart() {
         super.onStart();
         // Check if user is signed in (non-null) and update UI accordingly.
         FirebaseUser currentUser = mAuth.getCurrentUser();
         updateUI(currentUser);
     }

     private void updateUI(FirebaseUser user) {
        if(user==null){
            mAuth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = mAuth.getCurrentUser();
                        Toast.makeText(MainActivity.this, "Logged in"+user.getUid(), Toast.LENGTH_LONG).show();
                        updateUI(user);
                    } else {
                        updateUI(null);
                    }
                }
            });
        }
        else{
            //final String TAG = "login";
            final String uid = user.getUid();
            DatabaseReference newUser = FirebaseDatabase.getInstance()
                    .getReference("users");
            newUser.child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        Log.i("fb", snapshot.getKey());
                        MyLocationService.User_ID = snapshot.getKey();
                        Intent intentTest = new Intent(MainActivity.this, MyLocationService.class);
                        intentTest.setAction(MyLocationService.ACTION_AUTHOR_UPDATE);
                        intentTest.putExtra("TEST_VAL",snapshot.getKey());
                        sendBroadcast(intentTest);
                    } else {
                        Log.e("fb", "Not found: " + uid);
                        snapshot.getRef().getParent().child(uid).child("LKL").setValue(true);
                        snapshot.getRef().getParent().child(uid).child("Contacts").setValue(false);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.e("fb", databaseError.toString());
                }
            });
        }
     }

     private void updateLocation() {
         buildLocationRequest();

         fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
         if(ActivityCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
             return;
         }
         fusedLocationProviderClient.requestLocationUpdates(locationRequest, getPendingIntentLoc());
     }
     private PendingIntent getPendingIntentLoc(){
         Intent intent = new Intent(this, MyLocationService.class);
         intent.setAction(MyLocationService.ACTION_PROCESS_UPDATE);
         return PendingIntent.getBroadcast(this,0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
     }

     private void buildLocationRequest() {
         locationRequest = new LocationRequest();
         locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
         locationRequest.setInterval(4000);
         locationRequest.setFastestInterval(2000);
         locationRequest.setSmallestDisplacement(1f);
     }

     public void updateTextView(final String value){
         MainActivity.this.runOnUiThread(new Runnable() {
             @Override
             public void run() {
                txt_location.setText(value);
             }
         });
     }

     public void sendSMSMessage() {
         if (ContextCompat.checkSelfPermission(this,
                 Manifest.permission.SEND_SMS)
                 != PackageManager.PERMISSION_GRANTED) {
             if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                     Manifest.permission.SEND_SMS)) {
             } else {
                 ActivityCompat.requestPermissions(this,
                         new String[]{Manifest.permission.SEND_SMS},
                         MY_PERMISSIONS_REQUEST_SEND_SMS);
             }
         }
         else{
             SmsManager smsManager = SmsManager.getDefault();
             smsManager.sendTextMessage("8800210152", null, "Heuristic Time Diff Sent Trigger", null, null);
             Toast.makeText(getApplicationContext(), "SMS sent. Help On The Way!", Toast.LENGTH_LONG).show();
         }
     }

 }
