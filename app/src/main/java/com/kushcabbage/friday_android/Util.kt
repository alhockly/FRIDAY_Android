package com.kushcabbage.friday_android

import java.util.HashMap

object Util {

    val SPOTIFY_AUTHKEY_NAME = "SPOTIFY_AUTH"
    val ACCUWEATHER_LOCATIONKEY_NAME = "LOCATION_KEY"
    val ACCUWEATHER_APIKEY_NAME = "WEATHER_API_KEY"
    val IPLOCATION_APIKEY_NAME = "IPLOCATION_API_KEY"


    var apiKeyMap = mutableMapOf<String, String>()


    var LocationLat: Float = 0.toFloat()
    var LocationLon: Float = 0.toFloat()


}
