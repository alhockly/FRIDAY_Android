package com.example.friday_android;

import java.util.List;

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
