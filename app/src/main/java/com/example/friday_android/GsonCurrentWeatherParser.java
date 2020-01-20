package com.example.friday_android;

import java.util.List;

public class GsonCurrentWeatherParser  {


    Temperature Temperature;

    class Temperature{
        TemperatureDetails Metric;

    }

    class TemperatureDetails{
        float Value;
    }


}
