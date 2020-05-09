package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import com.google.gson.Gson
import com.kushcabbage.friday_android.IModifyUI
import com.kushcabbage.friday_android.Util
import com.kushcabbage.friday_android.gsonParsers.GsonLocationApi
import com.kushcabbage.friday_android.gsonParsers.GsonSunriseApiParser
import okhttp3.OkHttpClient
import okhttp3.Request
import java.text.SimpleDateFormat
import java.util.*
import kotlin.time.ExperimentalTime
import kotlin.time.hours
import kotlin.time.minutes

class SunRiseAsyncTask(iModifyUI: IModifyUI) : AsyncTask<Void, Void, String>() {

    var modUI = iModifyUI

    var sunriseApiBaseUrl = "https://api.sunrise-sunset.org/json"
    var ipLocationBaseUrl = "https://api.ipgeolocation.io/ipgeo?apiKey="

    lateinit var sunriseJson : GsonSunriseApiParser

    override fun doInBackground(vararg params: Void?): String {

        val client = OkHttpClient()

        if(Util.LocationLat==0.0f || Util.LocationLon==0.0f){
            val request: Request = Request.Builder().url(ipLocationBaseUrl+Util.apiKeyMap.get(Util.IPLOCATION_APIKEY_NAME)).build()

            try {
                val response = client.newCall(request).execute()
                val jsonString = response.body!!.string()
                var locationJson = Gson().fromJson(jsonString, GsonLocationApi::class.java)

                Util.LocationLat = locationJson.latitude.toFloat()
                Util.LocationLon = locationJson.longitude.toFloat()
            }catch (e : Exception){
            }
        }

        var sunriseUrl = sunriseApiBaseUrl+"?lat="+Util.LocationLat+"&lng="+Util.LocationLon
        val request: Request = Request.Builder().url(sunriseUrl).build()

        try {
            val response = client.newCall(request).execute()
            val jsonString = response.body!!.string()

            sunriseJson = Gson().fromJson(jsonString, GsonSunriseApiParser::class.java)
        }catch (e : Exception){
        }
        return ""
    }


    @ExperimentalTime
    override fun onPostExecute(result: String?) {
        super.onPostExecute(result)
        var sunrise = sunriseJson.results.sunrise
        var sunset = sunriseJson.results.sunset

        val pattern = "HH:mm:ss a"
        val apiDateFormat = SimpleDateFormat(pattern)
        val sunriseDate: Date = apiDateFormat.parse(sunrise)
        val sunsetDate: Date = apiDateFormat.parse(sunset)

        var timeFormat = SimpleDateFormat("HH:mm a")
        sunrise= timeFormat.format(sunriseDate)
        sunset = timeFormat.format(sunsetDate)
        modUI.refreshSunriseSet(sunrise,sunset)


    }
}