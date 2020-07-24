package com.kushcabbage.friday_android

import Spotify
import com.kushcabbage.friday_android.AsyncTasks.AccuweatherCurrentWeatherAsyncTask
import java.net.URI

class DataController(UiModify: IModifyUI, spotifyObj : Spotify = Spotify()) : IApiMVC.DataControllerOps {

    companion object{
        var spotifyData = DataClass.spotifyApi(null,null,null,null,null)
    }

    init{
        //TODO read from prefs into companion objects
    }

    var iModifyUI = UiModify
    var spotifyObj = spotifyObj

    override fun refreshCurrentTemp() {

        AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME].toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME].toString(), iModifyUI).execute()
    }

    override fun setSpotifyClientID(token: String) {
        spotifyData.clientID = token
    }

    override fun setSpotifyClientSecret(token: String) {
        spotifyData.clientSecret = token
    }

    override fun setSpotifyAuthCode(token: String) {
        spotifyData.authCode = token
    }

    override fun requestSpotifyAuthURI(): URI? {
        if(spotifyData.clientID != null && spotifyData.clientSecret != null){
            return spotifyObj.getAuthPageUrl(spotifyData.clientID!!, spotifyData.clientSecret!!)
        }
        return null
    }


}