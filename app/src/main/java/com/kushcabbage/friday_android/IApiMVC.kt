package com.kushcabbage.friday_android

import java.net.URI

interface IApiMVC {


    interface ViewOps {
        fun requestDisplayOnOff(isOn: Boolean)
        fun requestSetExceptionText(text : String)
    }


    interface DataControllerOps {
        fun refreshCurrentTemp()
        fun setSpotifyClientID(token : String)
        fun setSpotifyClientSecret(token : String)
        fun setSpotifyAuthCode(token : String)
        fun setSpotifyAccessToken(token : String)
        fun requestSpotifyDisplayUpdate()
        var spotifyData : DataClass.spotifyApi
    }
}