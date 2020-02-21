package com.kushcabbage.friday_android;

import android.content.Context;

public interface IModifyUI {

    void updateTimeDisplay();
    void refreshWeatherDisplay(GsonWeatherForecastParser jsonObject, GsonCurrentWeatherParser currentWeatherjsonObj);
    void refreshSongKickDisplay(GsonSongKickParser jsonObject);
    Context getContext();
}