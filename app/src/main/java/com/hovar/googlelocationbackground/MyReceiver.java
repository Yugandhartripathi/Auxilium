package com.hovar.googlelocationbackground;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class MyReceiver extends BroadcastReceiver {
    static int countPowerOff = 0;
    private MainActivity mActivity;
    private Long iniTime, finalTime;
    //Via trial and error it was established that finalTime-iniTime value less than 5000 should be suitable so as to not affect system functions, provide ease of access to sms feature and to prevent action on external triggers

    public MyReceiver(MainActivity activity) {
        this.mActivity = activity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        //Log.v("onReceive", "Power button is pressed.");

        //Toast.makeText(context, "power button clicked", Toast.LENGTH_LONG).show();

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            if(countPowerOff==0)
            {
                iniTime = System.currentTimeMillis();
            }
            countPowerOff++;
            if (countPowerOff == 6) {
                finalTime = System.currentTimeMillis();
                countPowerOff=0;
                Log.i("times","Pressed 6 times"+(finalTime-iniTime));
                if((finalTime-iniTime)<10000)
                {
                    Log.i("times","SENT");
                    mActivity.sendSMSMessage();
                }
                else
                {
                    Toast.makeText(context, "Tap'em faster", Toast.LENGTH_LONG).show();
                }
            }
        }
        else if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            if(countPowerOff==0)
            {
                iniTime = System.currentTimeMillis();
            }
            countPowerOff++;
            if (countPowerOff == 6) {
                finalTime = System.currentTimeMillis();
                countPowerOff=0;
                Log.i("times","Pressed 6 times"+(finalTime-iniTime));
                if((finalTime-iniTime)<10000)
                {
                    mActivity.sendSMSMessage();
                }
                else
                {
                    Toast.makeText(context, "Tap'em faster", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
