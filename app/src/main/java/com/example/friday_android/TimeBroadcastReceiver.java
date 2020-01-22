package com.example.friday_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class TimeBroadcastReceiver extends BroadcastReceiver {

    IModifyUI modifyUI;
    IKeyPass iKeyPass;

    LocalDateTime lastWeather;
    LocalDateTime lastSongkickFetch;

    public TimeBroadcastReceiver(IModifyUI UI, IKeyPass aKeyPass) {
        modifyUI = UI;
        iKeyPass = aKeyPass;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Debug","minute++");
        modifyUI.updateTimeDisplay();


        LocalDateTime now = LocalDateTime.now();

        if (null == lastWeather) {
            lastWeather=now;
        }
        if( null == lastSongkickFetch){
            lastSongkickFetch = now;
        }

        if(ChronoUnit.MINUTES.between(lastWeather, now)>30){
            new AccuweatherAsyncTask(iKeyPass.GetKey(Util.ACCUWEATHER_APIKEY_NAME), iKeyPass.GetKey(Util.ACCUWEATHER_LOCATIONKEY_NAME),modifyUI).execute();
            lastWeather = now;
        }

        if (ChronoUnit.MINUTES.between(lastSongkickFetch, now)>1440){
            new SongKickAyncTask(modifyUI);
            lastSongkickFetch = now;
        }

    }
}
