package com.kushcabbage.friday_android;

public class GsonCurrentWeatherParser  {


    TemperatureObj Temperature;
    int WeatherIcon;

    class TemperatureObj {
        TemperatureDetails Metric;

    }

    class TemperatureDetails{
        float Value;
    }


}
