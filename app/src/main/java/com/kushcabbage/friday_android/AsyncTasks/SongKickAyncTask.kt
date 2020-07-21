package com.kushcabbage.friday_android.AsyncTasks

import android.os.AsyncTask
import android.util.Log

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.kushcabbage.friday_android.IModifyUI
import com.kushcabbage.friday_android.exceptions.RequestsExceededException
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser

import java.io.IOException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

class SongKickAyncTask(internal var modifyUI: IModifyUI) : AsyncTask<Void, Void, Void>() {

    internal var username = "alex-hockly"
    internal var apiKey = "vFJgueGeeIk9C2rV"

    internal var url = "https://api.songkick.com/api/3.0/users/$username/calendar.json?reason=tracked_artist&apikey=$apiKey"

    internal var js: GsonSongKickParser? = null

    internal var currentDate: Date

    init {
        currentDate = Date()
    }

    override fun doInBackground(vararg p0: Void?): Void? {
        val client = OkHttpClient()
        val request = Request.Builder().url(url).build()

        try {
            val response = client.newCall(request).execute()
            val jsonString = response.body!!.string()


            js = Gson().fromJson(jsonString, GsonSongKickParser::class.java)

            for (cal in js!!.getevents()) {

                if (cal.getEvent().start.datetime != null) {
                    val format = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                    try {
                        cal.event.start.dateobj = format.parse(cal.event.start.datetime)

                    } catch (e: ParseException) {
                        e.printStackTrace()
                    }

                }

                cal.event.featuredArtists = " + "
                var ii = 0
                for (p in cal.event.performance) {
                    if (cal.event.performance.size == 1) {
                        cal.event.featuredArtists = ""
                        break
                    }  //bail if no features
                    if (ii == 0) {
                        ii++
                        continue
                    }

                    if (ii > 1) {
                        cal.event.featuredArtists += ", " + p.displayName      //dont show comma for first feature
                    } else {
                        cal.event.featuredArtists += p.displayName
                    }
                    if (ii > 2) {
                        break
                    }        //max num of features
                    ii++
                }

            }

            val iterator = js!!.getevents().iterator()
            while (iterator.hasNext()) {
                val cal = iterator.next()
                if (cal.event.start.dateobj != null) {
                    if (cal.event.start.dateobj.before(currentDate)) {
                        iterator.remove()
                    }
                }
            }

            Log.d("Debug", jsonString)
            if (js!!.resultsPage == null) {
                throw RequestsExceededException()
            }


        } catch (e: RequestsExceededException) {
            e.printStackTrace()
        } catch (e: JsonSyntaxException) {
            Log.d("Debug", "json parse failed")
            e.printStackTrace()
        } catch (e: IOException) {
            Log.d("Debug", "HTTP GET failed - probably offline")
            e.printStackTrace()
        }

        return null
    }

    override fun onPostExecute(aVoid: Void?) {
        if (js != null) {
            modifyUI.refreshSongKickDisplay(js!!)
        }
    }
}
