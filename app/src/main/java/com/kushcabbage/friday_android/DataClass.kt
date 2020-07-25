package com.kushcabbage.friday_android



class DataClass {

    @kotlinx.serialization.Serializable
    data class spotifyApi(var clientID: String?, var clientSecret: String?, var authCode: String?, var accessToken: String?, var refreshToken: String?, var redirectUrl : String?)
}