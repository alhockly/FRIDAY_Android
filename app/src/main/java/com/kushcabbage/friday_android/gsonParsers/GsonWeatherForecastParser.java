package com.kushcabbage.friday_android.gsonParsers;

import java.util.List;

public class GsonWeatherForecastParser {


    public List<dailyForecast> DailyForecasts;


    public class dailyForecast {
        String Link;
        DayNightClass Day;
        DayNightClass Night;
        public TemperatureClass Temperature;

        public float minTemp() {
            return Temperature.Minimum.Value;
        }

        public float maxTemp() {
            return Temperature.Maximum.Value;
        }
    }


    class DayNightClass {
        int Icon;
        String IconPhrase;

    }

    class TemperatureClass {
        MinMaxTempClass Minimum;
        MinMaxTempClass Maximum;
    }

    class MinMaxTempClass {
        float Value;
    }
}
