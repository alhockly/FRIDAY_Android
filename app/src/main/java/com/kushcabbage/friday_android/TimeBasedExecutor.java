package com.kushcabbage.friday_android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kushcabbage.friday_android.AsyncTasks.AccuweatherCurrentWeatherAsyncTask;
import com.kushcabbage.friday_android.AsyncTasks.AccuweatherForecastWeatherAsyncTask;
import com.kushcabbage.friday_android.AsyncTasks.AppUpdateAsyncTask;
import com.kushcabbage.friday_android.AsyncTasks.SongKickAyncTask;
import com.kushcabbage.friday_android.AsyncTasks.SunRiseAsyncTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;


public class TimeBasedExecutor extends BroadcastReceiver {

    //TODO check internet connection

    IModifyUI modifyUI;
    IUpdateApp updateApp;

    String githubUpdateURL = "https://github.com/alhockly/FRIDAY_Android/raw/master/build.apk"; //TODO move this somewhere else

    LocalDateTime lastWeather = LocalDateTime.now();
    LocalDateTime lastDaily = LocalDateTime.now();
    LocalDateTime lastHourly = LocalDateTime.now();

    LocalDateTime tomorrowMidnight;

    boolean isDailySynced = false;

    public TimeBasedExecutor(IModifyUI UI, IUpdateApp aUpdateApp) {
        modifyUI = UI;
        updateApp = aUpdateApp;
        LocalTime midnight = LocalTime.MIDNIGHT;
        LocalDate today = LocalDate.now();

        tomorrowMidnight = LocalDateTime.of(today, midnight).plusDays(1);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("Debug", "minute++");
        modifyUI.updateTimeDisplay();

        LocalDateTime now = LocalDateTime.now();

        if(now.isAfter(tomorrowMidnight) && !isDailySynced){
            isDailySynced = true;
            executeDailyTasks();
            lastDaily = now;
        }

        if (ChronoUnit.HOURS.between(lastHourly, now) >= 1) {
            lastDaily = now;
            new AppUpdateAsyncTask(context, updateApp).execute(githubUpdateURL);
        }

        if (ChronoUnit.DAYS.between(lastDaily, now) >= 1) {
            lastDaily = now;
            executeDailyTasks();
        }

        if (ChronoUnit.MINUTES.between(lastWeather, now) > 40) {
            new AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap.get(Util.ACCUWEATHER_APIKEY_NAME).toString(), Util.apiKeyMap.get(Util.ACCUWEATHER_LOCATIONKEY_NAME).toString(), modifyUI).execute();
            lastWeather = now;
        }
    }

    void onStartTasks() {
        modifyUI.updateTimeDisplay();
        new SongKickAyncTask(modifyUI).execute();
        new AccuweatherForecastWeatherAsyncTask(Util.apiKeyMap.get(Util.ACCUWEATHER_APIKEY_NAME).toString(), Util.apiKeyMap.get(Util.ACCUWEATHER_LOCATIONKEY_NAME).toString(), modifyUI).execute();
        new AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap.get(Util.ACCUWEATHER_APIKEY_NAME).toString(), Util.apiKeyMap.get(Util.ACCUWEATHER_LOCATIONKEY_NAME).toString(), modifyUI).execute();
        new SunRiseAsyncTask(modifyUI).execute();
    }


    void executeDailyTasks() {
        new SongKickAyncTask(modifyUI).execute();
        new AccuweatherForecastWeatherAsyncTask(Util.apiKeyMap.get(Util.ACCUWEATHER_APIKEY_NAME).toString(), Util.apiKeyMap.get(Util.ACCUWEATHER_LOCATIONKEY_NAME).toString(), modifyUI).execute();
        new SunRiseAsyncTask(modifyUI).execute();
    }
}
