package com.example.friday_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;


public class TimeBasedExecutor extends BroadcastReceiver {

    IModifyUI modifyUI;
    IUpdateApp updateApp;


    LocalDateTime lastWeather = LocalDateTime.now();
    LocalDateTime lastSongkickFetch = LocalDateTime.now();
    LocalDateTime lastDaily = LocalDateTime.now();
    LocalDateTime lastHourly = LocalDateTime.now();

    public TimeBasedExecutor(IModifyUI UI, IUpdateApp aUpdateApp) {
        modifyUI = UI;
        updateApp = aUpdateApp;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Debug","minute++");
        modifyUI.updateTimeDisplay();

        LocalDateTime now = LocalDateTime.now();

        if(ChronoUnit.HOURS.between(lastHourly, now)>=1){
            lastDaily=now;
            new AppUpdateAsyncTask(context,updateApp).execute("https://github.com/alhockly/FRIDAY_Android/raw/master/build.apk");
        }

        if(ChronoUnit.DAYS.between(lastDaily, now)>=1){
            lastDaily = now;
            new SongKickAyncTask(modifyUI).execute();
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
