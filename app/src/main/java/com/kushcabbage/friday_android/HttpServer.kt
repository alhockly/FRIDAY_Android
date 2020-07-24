package com.kushcabbage.friday_android

import java.io.IOException

import fi.iki.elonen.NanoHTTPD

/*api definition
 *
 * api runs on port 8080
 *
 * e.g ipAdress:8080/api/
 *
 *  calls to manipulate the view call at /view/
 *
 *  calls to access data at /data/ using GET method to read and POST to request a write
 *
 * e.g
 *   baseUrl/api/view/display/off
 *   baseUrl/api/data/uptime
 *   /api/data/refresh/currentTemp
 *
 *
 * */

class HttpServer @Throws(IOException::class)
constructor(private val viewInterface: IApiMVC.ViewOps, private val dataInterface: IApiMVC.DataControllerOps) : NanoHTTPD(null, 8080) {
    private val TAG = "HTTPSERVER"


    init {
        start(SOCKET_READ_TIMEOUT, false)
    }

    override fun serve(session: NanoHTTPD.IHTTPSession): Response {
        val htmlOpen = "<html><body>\n"
        val htmlClose = "</body></html>\n"
        val htmlbody = ""
        val sessionUri = session.uri.toLowerCase().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (sessionUri[1] != "api") {
            return newFixedLengthResponse(htmlOpen + "hello" + htmlClose)
        } else {

            if (sessionUri!!.size > 3) {
                if (sessionUri[2] == "view") {

                    return viewApiHandler(sessionUri)
                }
                if (sessionUri[2] == "data") {
                    return dataApiHandler(sessionUri)

                }
            }
        }
        return newFixedLengthResponse("api was unable to handle your request")
    }


    fun viewApiHandler(aUri: Array<String>): Response {
        var startIndex = aUri.indexOf("view")
        when(aUri[startIndex + 1]){

            "display" ->{
                if(aUri[1] == "on"){ viewInterface.requestDisplayOnOff(true)}
                if(aUri[1] == "off"){ viewInterface.requestDisplayOnOff(false)}
            }

            "text" ->{
                if(aUri.size >= 4) {
                    var textindex = aUri.indexOf("text")
                    viewInterface.requestSetExceptionText(aUri[textindex + 1])
                    return newFixedLengthResponse("set exception text to ${aUri[2]}")
                }
            }

        }



        return newFixedLengthResponse("/display received")
    }

    fun dataApiHandler(aUri: Array<String>): Response {
        var startIndex = aUri.indexOf("data")
        when(aUri[startIndex + 1]){
            "spotify" -> {
                when(aUri[startIndex+2]){
                    "clientid" ->{
                        dataInterface.setSpotifyClientID(aUri[startIndex + 3])
                        return newFixedLengthResponse("spotify client ID set")
                    }

                    "clientsecret" ->{
                        dataInterface.setSpotifyClientSecret(aUri[startIndex + 3])
                        return newFixedLengthResponse("spotify client Secret set")
                    }

                    "authcode" ->{
                        dataInterface.setSpotifyAuthCode(aUri[startIndex + 3])
                        return newFixedLengthResponse("spotify authCode set")
                    }

                    "getauthuri" ->{
                        var uri = dataInterface.requestSpotifyAuthURI()
                        if(uri != null){
                            return newFixedLengthResponse(uri.toString())
                        }
                        return newFixedLengthResponse("client ID or client Secret is not set")

                    }




                }


            }
        }


        return newFixedLengthResponse("")
    }

    companion object {


        fun getSliceOfArray(arr: Array<String>, start: Int, end: Int): Array<String>? {

            // Get the slice of the Array
            val slice = arrayOfNulls<String>(end - start)

            // Copy elements of arr to slice
            for (i in slice.indices) {
                slice[i] = arr[start + i]
            }

            // return the slice
            if(slice is Array<String?>){
                return null
            } else {
                return slice as Array<String>
            }
        }
    }
}
