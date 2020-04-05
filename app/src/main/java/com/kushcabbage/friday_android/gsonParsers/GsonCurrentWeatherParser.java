package com.kushcabbage.friday_android.gsonParsers;

public class GsonCurrentWeatherParser  {


    public TemperatureObj Temperature;
    int WeatherIcon;

    public class TemperatureObj {
        public TemperatureDetails Metric;

    }

    public class TemperatureDetails{
        public float Value;
    }


}
