package com.kushcabbage.friday_android;

import java.util.List;

public class GsonWeatherForecastParser extends GsonBase {


    List<dailyForecast> DailyForecasts;


    class dailyForecast{
        String Link;
        DayNightClass Day;
        DayNightClass Night;
        TemperatureClass Temperature;
    }


    class DayNightClass {
        int Icon;
        String IconPhrase;

    }

    class TemperatureClass{
        MinMaxTempClass Minimum;
        MinMaxTempClass Maximum;
    }

    class MinMaxTempClass{
        float Value;
    }
}
