package com.kushcabbage.friday_android.AsyncTasks

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.AsyncTask

import com.kushcabbage.friday_android.IUpdateApp

import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.net.HttpURLConnection
import java.net.URL

class AppUpdateAsyncTask(private val context: Context, internal var iUpdateApp: IUpdateApp) : AsyncTask<String, Void, Void>() {

    internal var saveDir = "/sdcard/"
    internal var saveFileName = "update.apk"
    internal var downloadedVersionName: String? = null

    override fun doInBackground(vararg strings: String): Void? {
        var input: InputStream? = null
        var output: OutputStream? = null
        var connection: HttpURLConnection? = null
        try {
            if (strings.size == 0) {
                println("Download link not provided to AsyncTask")
                throw NullPointerException()
            }
            val url = URL(strings[0])
            connection = url.openConnection() as HttpURLConnection
            connection.connect()

            // expect HTTP 200 OK, so we don't mistakenly save error report
            // instead of the file
            if (connection.responseCode != HttpURLConnection.HTTP_OK) {
                return null
            }

            // this will be useful to display download percentage
            // might be -1: server did not report the length
            val fileLength = connection.contentLength

            // download the file
            val outputFile = File(saveDir + saveFileName)
            if (outputFile.exists()) {
                outputFile.delete()
            }
            input = connection.inputStream
            output = FileOutputStream(outputFile)

            val data = ByteArray(4096)
            var total: Long = 0
            var count: Int
            while (true) {
                // allow canceling with back button
                count =  input!!.read(data)
                if(count == -1){break}
                if (isCancelled) {
                    input!!.close()
                    return null
                }
                total += count.toLong()
                output.write(data, 0, count)
            }

            try {
                output?.close()
                input?.close()
            } catch (ignored: IOException) {
            }

            connection?.disconnect()


            val pm = context.packageManager
            val fullPath = saveDir + saveFileName
            val info = pm.getPackageArchiveInfo(fullPath, 0)
            println("VersionCode : " + info.versionCode + ", VersionName : " + info.versionName)
            downloadedVersionName = info.versionName

        } catch (e: NullPointerException) {
            e.printStackTrace()
            return null
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }


        return null
    }


    override fun onPostExecute(aVoid: Void) {


        if (downloadedVersionName != null) {
            try {
                val pInfo = context.packageManager.getPackageInfo(context.packageName, 0)
                val installedVersion = pInfo.versionName

                if (java.lang.Float.parseFloat(installedVersion) < java.lang.Float.parseFloat(downloadedVersionName!!)) {
                    iUpdateApp.installApp(File(saveDir + saveFileName))
                }


            } catch (e: PackageManager.NameNotFoundException) {
                e.printStackTrace()
            }

        }

    }
}


