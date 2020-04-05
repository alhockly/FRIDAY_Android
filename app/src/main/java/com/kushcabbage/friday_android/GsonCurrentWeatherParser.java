package com.kushcabbage.friday_android;

public class GsonCurrentWeatherParser  {


    public TemperatureObj Temperature;
    int WeatherIcon;

    class TemperatureObj {
        TemperatureDetails Metric;

    }

    class TemperatureDetails{
        float Value;
    }


}
