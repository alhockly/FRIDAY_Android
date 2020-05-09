package com.kushcabbage.friday_android;

import android.content.Context;

import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser;
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser;
import com.kushcabbage.friday_android.gsonParsers.GsonWeatherForecastParser;

public interface IModifyUI {

    void updateTimeDisplay();
    void refreshForecastDisplay(GsonWeatherForecastParser jsonObject);
    void refreshCurrentWeatherDisplay(GsonCurrentWeatherParser currentWeatherjsonObj);
    void refreshSongKickDisplay(GsonSongKickParser jsonObject);
    void refreshSunriseSet(String sunrise, String sunset);
    void showException(String exceptionText);
    Context getContext();
}
