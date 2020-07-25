package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import com.kushcabbage.friday_android.DataClass
import okhttp3.*


class SpotifyRequestAccessToken(spotifyObj : DataClass.spotifyApi) : AsyncTask<Void, Void, String>() {

    var code = spotifyObj.authCode
    var spotifyData = spotifyObj

    override fun doInBackground(vararg p0: Void?): String {
        val client = OkHttpClient()

        var url = 	"https://accounts.spotify.com/api/token"
        val requestBody: RequestBody = FormBody.Builder()
               // .setType(MultipartBody.FORM)
                .add("grant_type", "authorization_code")
                .add("code", "$code")
                .add("redirect_uri", "${spotifyData.redirectUrl}")
                .add("client_id", "${spotifyData.clientID}")
                .add("client_secret", "${spotifyData.clientSecret}")
                .build()

        val request: Request = Request.Builder().url(url).post(requestBody).addHeader("cache-control", "no-cache").addHeader("Content-Type", "application/x-www-form-urlencoded").build()
                //.addHeader("Authorization", "Bearer $code")

        val response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        return jsonString
    }
}