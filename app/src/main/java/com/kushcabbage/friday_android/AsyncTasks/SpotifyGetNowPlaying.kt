package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import com.kushcabbage.friday_android.IModifyUI
import com.kushcabbage.friday_android.Util
import okhttp3.OkHttpClient
import okhttp3.Request

class SpotifyGetNowPlaying(accessToken : String) : AsyncTask<Void, Void, String>() {

    var token = accessToken


    override fun doInBackground(vararg p0: Void?): String {
        val client = OkHttpClient()

        var url = 	"https://api.spotify.com/v1/me/player"
        val request: Request = Request.Builder().url(url).addHeader("Authorization", "Bearer $token").addHeader("cache-control", "no-cache").build()

        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        return jsonString
    }
}