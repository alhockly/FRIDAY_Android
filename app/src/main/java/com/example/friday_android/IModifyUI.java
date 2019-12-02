package com.example.friday_android;

import android.content.Context;

public interface IModifyUI {

    void updateTimeDisplay();
    void updateWeather();
    void refreshWeatherDisplay(GsonWeatherParser jsonObject);
    void refreshSongKickDisplay(GsonSongKickParser jsonObject);
    void refreshSongKickData();
    Context getContext();
}
