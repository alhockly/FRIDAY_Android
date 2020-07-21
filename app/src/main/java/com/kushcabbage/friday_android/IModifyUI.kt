package com.kushcabbage.friday_android

import android.content.Context

import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser
import com.kushcabbage.friday_android.gsonParsers.GsonWeatherForecastParser

interface IModifyUI {

    var context: Context

    fun updateTimeDisplay()

    fun refreshForecastDisplay(jsonObject: GsonWeatherForecastParser)

    fun refreshCurrentWeatherDisplay(currentWeatherjsonObj: GsonCurrentWeatherParser)

    fun refreshSongKickDisplay(jsonObject: GsonSongKickParser)

    fun refreshSunriseSet(sunrise: String, sunset: String)

    fun showException(exceptionText: String)
}
