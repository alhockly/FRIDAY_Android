package com.kushcabbage.friday_android

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Vibrator
import android.speech.RecognizerIntent
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil

import androidx.recyclerview.widget.LinearLayoutManager

import com.kushcabbage.friday_android.databinding.ActivityMainBinding
import com.kushcabbage.friday_android.gsonParsers.GsonCurrentWeatherParser
import com.kushcabbage.friday_android.gsonParsers.GsonSongKickParser
import com.kushcabbage.friday_android.gsonParsers.GsonWeatherForecastParser
import com.kushcabbage.friday_android.views.SongKickRecyclerSpacer

import java.io.File
import java.io.IOException
import java.util.Calendar
import java.util.Locale

import edu.cmu.pocketsphinx.Assets
import edu.cmu.pocketsphinx.Hypothesis
import edu.cmu.pocketsphinx.RecognitionListener
import edu.cmu.pocketsphinx.SpeechRecognizer
import edu.cmu.pocketsphinx.SpeechRecognizerSetup


class MainActivity : Activity(), IModifyUI, RecognitionListener, IKeyPass, IUpdateApp, IApiMVC.ViewOps {
    override lateinit var context: Context

    lateinit var iTimeBasedExecutor: TimeBasedExecutor
    lateinit var iBinding: ActivityMainBinding

    internal var songkickRecyclerViewAdapter: SongkickRecyclerViewAdapter? = null
    private var mRecognizer: SpeechRecognizer? = null
    private val mVibrator: Vibrator? = null


    private var httpServer: HttpServer? = null
    private var database: DataController? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        context = applicationContext
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        iTimeBasedExecutor = TimeBasedExecutor(this, this)
        database = DataController(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        val filter = IntentFilter(Intent.ACTION_TIME_TICK)
        registerReceiver(iTimeBasedExecutor, filter)

        iBinding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        iBinding.ParentView.keepScreenOn = true
        iBinding.SongKickList.isFocusable = false
        hideNavbar()

        //TODO move keys to gradle.properties
        SetKey(Util.ACCUWEATHER_LOCATIONKEY_NAME, "328328")
        SetKey(Util.ACCUWEATHER_APIKEY_NAME, "jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP")
        SetKey(Util.IPLOCATION_APIKEY_NAME, "9c96f50a80144c92b8d0e448a152e727")

        iTimeBasedExecutor.onStartTasks(context)

//        var spotify = Spotify()
//        SpotifyAuthAsyncTask(spotify).execute()

        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(this@MainActivity, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
        }


        try {
            httpServer = HttpServer(this, database!!)
        } catch (e: IOException) {
            e.printStackTrace()
        }

    }




    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this@MainActivity, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

    override fun onStop() {
        super.onStop()
        httpServer!!.stop()
    }

    fun hideNavbar() {
        val decorView = window.decorView
        val uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN
        decorView.systemUiVisibility = uiOptions
    }


    override fun updateTimeDisplay() {
        val cal = Calendar.getInstance()
        var hour = cal.get(Calendar.HOUR).toString()
        if (hour == "0") {
            hour = hour.replace("0", "12")
        }
        var minutes = cal.get(Calendar.MINUTE).toString()
        if (minutes.length < 2) {
            minutes = "0$minutes"
        }
        iBinding.TimeDisplay.text = "$hour:$minutes"
        val date = cal.get(Calendar.DATE)
        val month = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())
        iBinding.DateDisplay.text = "$date $month"
    }

    override fun refreshForecastDisplay(jsonObject: GsonWeatherForecastParser) {
        if (jsonObject != null) {
            try {
                val maxTemp = jsonObject.DailyForecasts[0].maxTemp()
                val minTemp = jsonObject.DailyForecasts[0].minTemp()
                iBinding.TempRange.text = "$minTemp°C/$maxTemp°C"

            } catch (e: Exception) {     //development builds be like
                e.printStackTrace()
            }

        }
    }

    override fun refreshCurrentWeatherDisplay(currentWeatherjsonObj: GsonCurrentWeatherParser) {
        if (currentWeatherjsonObj != null) {
            try {
                iBinding.CurrentTemp.text = currentWeatherjsonObj.Temperature.Metric.Value.toString() + "°C"
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }
    }


    override fun refreshSongKickDisplay(jsonObject: GsonSongKickParser) {

        iBinding.SongKickList.layoutManager = LinearLayoutManager(applicationContext)

        if (songkickRecyclerViewAdapter == null) {
            songkickRecyclerViewAdapter = SongkickRecyclerViewAdapter(jsonObject, this)
            iBinding.SongKickList.addItemDecoration(SongKickRecyclerSpacer())
            iBinding.SongKickList.adapter = songkickRecyclerViewAdapter
        }

        songkickRecyclerViewAdapter!!.setCalenderEntries(jsonObject.getevents())
        songkickRecyclerViewAdapter!!.notifyDataSetChanged()

    }

    override fun showException(exceptionText: String) {
        iBinding.exceptionTextview.text = exceptionText

    }


    override fun installApp(apkFile: File) {
        apkFile.setReadable(true, false)
        val intent = Intent(Intent.ACTION_INSTALL_PACKAGE)
        intent.addCategory("android.intent.category.DEFAULT")
        val fileUri = FileProvider.getUriForFile(context, context.applicationContext.packageName + ".provider", apkFile)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive")
        startActivity(intent)
    }


    override fun SetKey(Name: String, Key: String) {
        Util.apiKeyMap[Name] = Key
    }

    override fun GetKey(Name: String): String? {

        return if (Util.apiKeyMap.containsKey(Name)) {
            Util.apiKeyMap[Name]!!.toString()
        } else {
            null
        }
    }


//    fun getContext(): Context {
//        return applicationContext
//    }


    override fun onResume() {
        super.onResume()
        hideNavbar()
        registerReceiver(iTimeBasedExecutor, IntentFilter(Intent.ACTION_TIME_TICK))
        //CMUsetup();
    }

    /**
     * Stop the recognizer.
     * Since cancel() does trigger an onResult() call,
     * we cancel the recognizer rather then stopping it.
     */
    override fun onPause() {
        super.onPause()
        if (mRecognizer != null) {
            mRecognizer!!.removeListener(this)
            mRecognizer!!.cancel()
            mRecognizer!!.shutdown()
            Log.d(LOG_TAG, "PocketSphinx Recognizer was shutdown")
        }
        unregisterReceiver(iTimeBasedExecutor)
    }

    /**
     * Setup the Recognizer with a sensitivity value in the range [1..100]
     * Where 1 means no false alarms but many true matches might be missed.
     * and 100 most of the words will be correctly detected, but you will have many false alarms.
     */


    /////CMU Sphinx stuff
    private fun CMUsetup() {
        try {
            val assets = Assets(this@MainActivity)
            val assetDir = assets.syncAssets()
            mRecognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(File(assetDir, "models/en-us-ptm"))
                    .setDictionary(File(assetDir, "models/lm/words.dic"))
                    .setKeywordThreshold(java.lang.Float.valueOf("1.e-" + 2 * sensitivity))
                    .recognizer
            mRecognizer!!.addKeyphraseSearch(WAKEWORD_SEARCH, getString(R.string.wake_word))
            mRecognizer!!.addListener(this)
            mRecognizer!!.startListening(WAKEWORD_SEARCH)
            Log.d(LOG_TAG, "... listening")
            Toast.makeText(context, "....Listening", Toast.LENGTH_LONG)
        } catch (e: IOException) {
            Log.e(LOG_TAG, e.toString())
            Toast.makeText(context, "Couldn't get microphone", Toast.LENGTH_LONG)
        }

    }

    //
    // RecognitionListener Implementation
    //

    override fun onBeginningOfSpeech() {
        Log.d(LOG_TAG, "Beginning Of Speech")

    }

    override fun onEndOfSpeech() {
        Log.d(LOG_TAG, "End Of Speech")

    }

    override fun onPartialResult(hypothesis: Hypothesis?) {
        if (hypothesis != null) {
            val text = hypothesis.hypstr
            Log.d(LOG_TAG, "on partial: $text")
            if (text == getString(R.string.wake_word)) {


                startActivity(Intent(this, MainActivity::class.java))
                Toast.makeText(context, "DETECTED HOTWORD!", Toast.LENGTH_SHORT)
                ///////////////
                //                Intent intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
                //                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                //                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
                //                startActivityForResult(intent, 1);
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
        Log.d("Debug", result.toString())

    }

    override fun onResult(hypothesis: Hypothesis?) {
        if (hypothesis != null) {
            Log.d(LOG_TAG, "on Result: " + hypothesis.hypstr + " : " + hypothesis.bestScore)

        }
    }

    override fun onError(e: Exception) {
        Log.e(LOG_TAG, "on Error: $e")
    }

    override fun onTimeout() {
        Log.d(LOG_TAG, "on Timeout")
    }


    override fun requestDisplayOnOff(isOn: Boolean) {
        if (isOn) {
            iBinding.root.visibility = View.VISIBLE
        } else {
            iBinding.root.visibility = View.INVISIBLE
        }
    }

    override fun requestSetExceptionText(text: String) {
        showException(text)
    }

    override fun refreshSunriseSet(sunrise: String, sunset: String) {
        iBinding.SunRiseSet.text = "$sunrise | $sunset"
    }

    companion object {

        ///CMU SPHINX
        private val WAKEWORD_SEARCH = "WAKEWORD_SEARCH"
        private val PERMISSIONS_REQUEST_CODE = 5
        private val sensitivity = 25
        private val LOG_TAG = MainActivity::class.java.name
    }
}
