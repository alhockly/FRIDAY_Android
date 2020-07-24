package com.kushcabbage.friday_android.AsyncTasks

import Spotify
import android.os.AsyncTask
import com.kushcabbage.friday_android.Util.SPOTIFY_AUTHKEY_NAME
import com.kushcabbage.friday_android.Util.apiKeyMap
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

class SpotifyAuthAsyncTask(spotifyObj : Spotify) : AsyncTask<Void?, String?, Void?>() {
    var url: String? = null
    var BASE_URL = "https://accounts.spotify.com/"
    var AUTH_ENDPOINT = "authorize"
    var clientID = "2c0d0c49b20c4a2cbe346f42bb6dab74"
    var clientSecret = "811e8611fafc4683b415caae2814d98b"
    var scope = "user-read-currently-playing"

    var sp = spotifyObj



    override fun doInBackground(vararg p0: Void?): Void? {

        if(sp.isAuthorised()){

        }


        url = "$BASE_URL$AUTH_ENDPOINT?client_id=$clientID&scope=$scope&response_type=code&redirect_uri=http://localhost/"
        try {
            val client = OkHttpClient()
            val request = Request.Builder().url(url!!).build()
            val response = client.newCall(request).execute()
            val jsonString = response.body!!.string()
            apiKeyMap[SPOTIFY_AUTHKEY_NAME] = "key"
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    @Throws(IOException::class)
    fun OkHttpCall(): Response {
        var response: Response? = null
        val client = OkHttpClient()
        val request = Request.Builder().url(url!!).build()
        response = client.newCall(request).execute()
        val jsonString = response.body!!.string()
        return response
    }


}