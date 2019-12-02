package com.example.friday_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimeBroadcastReceiver extends BroadcastReceiver {

    IModifyUI modifyUI;
    int minuteCounter=0;


    public TimeBroadcastReceiver(IModifyUI UI) {
        modifyUI = UI;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Debug","Time change");
        modifyUI.updateTimeDisplay();
        minuteCounter++;

        if (minuteCounter>30){
            modifyUI.updateWeather();

            minuteCounter=0;
        }

    }
}
