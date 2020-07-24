package com.kushcabbage.friday_android

import com.spotify.sdk.android.auth.AuthorizationRequest
import com.spotify.sdk.android.auth.AuthorizationResponse


class Spotify {

    var redirectUrl = "localhost"

    init{

    }

    fun getAuthenticationRequest(type: AuthorizationResponse.Type,clientId : String): AuthorizationRequest? {
        return AuthorizationRequest.Builder(clientId, type, redirectUrl)
                .setShowDialog(false)
                .setScopes(arrayOf("user-read-email"))
                .setCampaign("your-campaign-token")
                .build()
    }
}