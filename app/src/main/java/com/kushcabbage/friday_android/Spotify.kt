package com.kushcabbage.friday_android

import android.app.Activity
import android.content.Intent
import com.kushcabbage.friday_android.AsyncTasks.SpotifyGetNowPlaying
import com.spotify.sdk.android.auth.AuthorizationClient
import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class Spotify(activity: Activity) {

    var redirectUrl = "http://localhost/"
    var act= activity


   fun getNowPlaying(accessToken : String){

        SpotifyGetNowPlaying(accessToken).execute()
   }




}