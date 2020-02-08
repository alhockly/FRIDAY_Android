package com.example.friday_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class TimeBroadcastReceiver extends BroadcastReceiver {

    IModifyUI modifyUI;


    LocalDateTime lastWeather;
    LocalDateTime lastSongkickFetch;

    public TimeBroadcastReceiver(IModifyUI UI) {
        modifyUI = UI;

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

            new AccuweatherAsyncTask(Util.apiKeyMap.get(Util.ACCUWEATHER_APIKEY_NAME).toString(), Util.apiKeyMap.get(Util.ACCUWEATHER_LOCATIONKEY_NAME).toString(),modifyUI).execute();
            lastWeather = now;
        }

        if (ChronoUnit.MINUTES.between(lastSongkickFetch, now)>1440){
            new SongKickAyncTask(modifyUI).execute();
            lastSongkickFetch = now;
        }

    }
}
