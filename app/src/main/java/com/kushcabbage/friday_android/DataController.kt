package com.kushcabbage.friday_android

import android.content.Context
import com.google.gson.Gson
import com.kushcabbage.friday_android.AsyncTasks.AccuweatherCurrentWeatherAsyncTask
import com.kushcabbage.friday_android.AsyncTasks.SpotifyRequestAccessToken
import com.spotify.sdk.android.auth.AuthorizationResponse
import java.net.URI
import kotlin.reflect.KClass
import kotlinx.serialization.*
import kotlinx.serialization.json.*

class DataController(UiModify: IModifyUI, spotifyObj : Spotify) : IApiMVC.DataControllerOps {


        var spotifyPrefsKey = "PREFS_SPOTIFY"
        var userPrefsKey = "PREFS_DATA"
        override var spotifyData = DataClass.spotifyApi(null,null,null,null,null, spotifyObj.redirectUrl)


    var iModifyUI = UiModify
    var spotify = spotifyObj

    init{

        var spotify = spotifyObjFromPrefs()
        if(spotify!= null){
            spotifyData = spotify
        }
    }



    fun spotifyObjFromPrefs() : DataClass.spotifyApi? {
        var userPrefs =iModifyUI.context.getSharedPreferences(userPrefsKey,Context.MODE_PRIVATE)
        var json = userPrefs.getString(spotifyPrefsKey,null)
        return Gson().fromJson(json, DataClass.spotifyApi::class.java)
    }

    fun saveSpotifyDataToPrefs(){
        var prefs =iModifyUI.context.getSharedPreferences(userPrefsKey,Context.MODE_PRIVATE).edit()
        prefs.putString(spotifyPrefsKey, Gson().toJson(spotifyData))
        prefs.commit()
    }

//    fun <T : Any> readSerializableFromPrefs(key : String, clazz : KClass<T> ) : KClass<T>? {
////        var prefs =iModifyUI.context.getSharedPreferences(userPrefsKey,Context.MODE_PRIVATE)
////        var json = prefs.getString(key,"")
////        return Gson().fromJson(json,clazz::class)
//    }

    ///API Interface
    override fun refreshCurrentTemp() {
        AccuweatherCurrentWeatherAsyncTask(Util.apiKeyMap[Util.ACCUWEATHER_APIKEY_NAME].toString(), Util.apiKeyMap[Util.ACCUWEATHER_LOCATIONKEY_NAME].toString(), iModifyUI).execute()
    }


    override fun setSpotifyClientID(token: String) {
        spotifyData.clientID = token
        saveSpotifyDataToPrefs()
    }

    override fun setSpotifyClientSecret(token: String) {
        spotifyData.clientSecret = token
        saveSpotifyDataToPrefs()
    }

    override fun setSpotifyAuthCode(token: String) {
        spotifyData.authCode = token
        saveSpotifyDataToPrefs()
        SpotifyRequestAccessToken(spotifyData).execute()
    }

    override fun setSpotifyAccessToken(token: String) {
        spotifyData.accessToken = token
        saveSpotifyDataToPrefs()
    }



    override fun requestSpotifyDisplayUpdate() {
        if(spotifyData.accessToken != null) {
            spotify.getNowPlaying(spotifyData.accessToken!!)
        }
    }


    //////////////////
}