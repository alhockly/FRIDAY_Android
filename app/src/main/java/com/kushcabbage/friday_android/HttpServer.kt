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

    override fun serve(session: NanoHTTPD.IHTTPSession): NanoHTTPD.Response {
        val htmlOpen = "<html><body>\n"
        val htmlClose = "</body></html>\n"
        val htmlbody = ""
        val sessionUri = session.uri.toLowerCase().split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        if (sessionUri[1] != "api") {
            return newFixedLengthResponse(htmlOpen + "hello" + htmlClose)
        } else {
            var uriSegment = getSliceOfArray(sessionUri, 3, sessionUri.size)
            if (uriSegment != null) {
                if (sessionUri[2] == "view") {

                    return viewApiHandler(uriSegment)
                }
                if (sessionUri[2] == "data") {
                    return dataApiHandler(uriSegment)

                }
            }
        }
        return newFixedLengthResponse("")
    }


    fun viewApiHandler(aUri: Array<String>): NanoHTTPD.Response {
        if (aUri[0] == "display") {
            if (aUri[1] == "off") {
                viewInterface.displayOnOff(false)
            } else {
                viewInterface.displayOnOff(true)
            }
        }

        return newFixedLengthResponse("")
    }

    fun dataApiHandler(aUri: Array<String>): NanoHTTPD.Response {
        if (aUri[0] == "refresh") {
            if (aUri[1] == "currenttemp") {
                dataInterface.refreshCurrentTemp()
                return newFixedLengthResponse("Current temp refreshed")
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
