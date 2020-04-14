package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import com.kushcabbage.friday_android.Util
import okhttp3.OkHttpClient
import okhttp3.Request
import java.lang.Exception



class SunRiseAsyncTask : AsyncTask<Void, Void, String>() {

    var baseUrl = "https://api.sunrise-sunset.org/"
    var ipLocationBaseUrl = "https://api.ipgeolocation.io/ipgeo?apiKey="



    override fun onPreExecute() {
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: Void?): String {



        if(Util.LocationLat==null || Util.LocationLon==null){

            val client = OkHttpClient()
            val request: Request = Request.Builder().url(ipLocationBaseUrl+Util.apiKeyMap.get(Util.IPLOCATION_APIKEY_NAME)).build()

            try {
                val response = client.newCall(request).execute()
                val jsonString = response.body!!.string()
            }catch (e : Exception){

            }

        }

//        val client = OkHttpClient()
//        val request: Request = Request.Builder().url(url).build()
//
//        try {
//            val response = client.newCall(request).execute()
//            val jsonString = response.body!!.string()
//        }catch (e : Exception){
//
//        }

        return ""
    }



}