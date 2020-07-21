package com.kushcabbage.friday_android

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

import com.kushcabbage.friday_android.AsyncTasks.AccuweatherCurrentWeatherAsyncTask
import com.kushcabbage.friday_android.AsyncTasks.AccuweatherForecastWeatherAsyncTask
import com.kushcabbage.friday_android.AsyncTasks.AppUpdateAsyncTask
import com.kushcabbage.friday_android.AsyncTasks.SongKickAyncTask
import com.kushcabbage.friday_android.AsyncTasks.SunRiseAsyncTask

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

//TODO check internet connection
class TimeBasedExecutor(var modifyUI: IModifyUI,  var updateApp: IUpdateApp) : BroadcastReceiver() {

    internal var githubUpdateURL = "https://github.com/alhockly/FRIDAY_Android/raw/master/app/build/outputs/apk/debug/app-debug.apk" //TODO move this to data class?

    internal var lastWeather = LocalDateTime.now()
    internal var lastDaily = LocalDateTime.now()
    internal var lastHourly = LocalDateTime.now()

    internal var tomorrowMidnight: LocalDateTime

    internal var isDailySynced = false

    init {
        val midnight = LocalTime.MIDNIGHT
        val today = LocalDate.now()

        tomorrowMidnight = LocalDateTime.of(today, midnight).plusDays(1)
    }

    override fun onReceive(context: Context, intent: Intent) {
        Log.d("Debug", "minute++")
        modifyUI.updateTimeDisplay()

        val now = LocalDateTime.now()

        if (now.isAfter(tomorrowMidnight) && !isDailySynced) {
            isDailySynced = true
            lastDaily = now
            executeDailyTasks(context)
        }

        if (ChronoUnit.HOURS.between(lastHourly, now) >= 1) {
            lastHourly = now
            executeHourlyTasks(context)
        }

        if (ChronoUnit.DAYS.between(lastDaily, now) >= 1) {
            lastDaily = now
            executeDailyTasks(context)
        }

        if (ChronoUnit.MINUTES.between(lastWeather, now) > 40) {
            AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME]!!.toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME]!!.toString(), modifyUI).execute()
            lastWeather = now
        }
    }

    internal fun onStartTasks(aContext: Context) {
        modifyUI.updateTimeDisplay()
        SongKickAyncTask(modifyUI).execute()
        AccuweatherForecastWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME]!!.toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME]!!.toString(), modifyUI).execute()
        AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME]!!.toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME]!!.toString(), modifyUI).execute()
        SunRiseAsyncTask(modifyUI).execute()
        AppUpdateAsyncTask(aContext, updateApp).execute(githubUpdateURL)
    }


    internal fun executeDailyTasks(aContext: Context) {
        SongKickAyncTask(modifyUI).execute()
        AccuweatherForecastWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME]!!.toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME]!!.toString(), modifyUI).execute()
        SunRiseAsyncTask(modifyUI).execute()
    }

    internal fun executeHourlyTasks(aContext: Context) {
        AppUpdateAsyncTask(aContext, updateApp).execute(githubUpdateURL)
    }
}
