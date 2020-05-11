package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import com.kushcabbage.friday_android.Util
import okhttp3.OkHttpClient
import okhttp3.Request

class GetLatLongAsyncTask : AsyncTask<Void, Void, String>() {

    var baseUri = "https://api.ipgeolocation.io/ipgeo?apiKey="

    override fun doInBackground(vararg params: Void?): String {


        val client = OkHttpClient()
        val request: Request = Request.Builder().url(baseUri + Util.apiKeyMap.get(Util.IPLOCATION_APIKEY_NAME)).build()

        try {
            val response = client.newCall(request).execute()
            val jsonString = response.body!!.string()
        } catch (e: Exception) {

        }



        return ""
    }
}