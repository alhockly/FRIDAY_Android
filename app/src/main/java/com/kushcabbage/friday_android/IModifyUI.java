package com.kushcabbage.friday_android;

import android.content.Context;

import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser;
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser;
import com.kushcabbage.friday_android.gsonParsers.GsonWeatherForecastParser;

public interface IModifyUI {

    void updateTimeDisplay();
    void refreshWeatherDisplay(GsonWeatherForecastParser jsonObject, GsonCurrentWeatherParser currentWeatherjsonObj);
    void refreshSongKickDisplay(GsonSongKickParser jsonObject);
    Context getContext();
}
