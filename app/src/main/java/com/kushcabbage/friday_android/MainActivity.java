package com.kushcabbage.friday_android;

import androidx.annotation.NonNull;
import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.kushcabbage.friday_android.databinding.ActivityMainBinding;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;
import edu.cmu.pocketsphinx.SpeechRecognizerSetup;


public class MainActivity extends Activity implements IModifyUI, RecognitionListener, IKeyPass, IUpdateApp{
    TimeBasedExecutor iTimeBasedExecutor;
    ActivityMainBinding iBinding;

    String githubUpdateURL = "https://github.com/alhockly/FRIDAY_Android/raw/master/build.apk";



    SongkickRecyclerViewAdapter songkickRecyclerViewAdapter = null;

    ///CMU SPHINX
    private static final String WAKEWORD_SEARCH = "WAKEWORD_SEARCH";
    private static final int PERMISSIONS_REQUEST_CODE = 5;
    private static int sensitivity = 25;
    private SpeechRecognizer mRecognizer;
    private Vibrator mVibrator;
    private static final String LOG_TAG = MainActivity.class.getName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Util.iUpdateApp = this;
        iTimeBasedExecutor = new TimeBasedExecutor(this,this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(iTimeBasedExecutor,filter);

        iBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        iBinding.ParentView.setKeepScreenOn(true);
        iBinding.SongKickList.setFocusable(false);
        hideNavbar();

        SetKey(Util.ACCUWEATHER_LOCATIONKEY_NAME,"328328");
        SetKey(Util.ACCUWEATHER_APIKEY_NAME,"jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP");

        updateTimeDisplay();
        new SongKickAyncTask(this).execute();
        new AccuweatherAsyncTask(GetKey(Util.ACCUWEATHER_APIKEY_NAME),GetKey(Util.ACCUWEATHER_LOCATIONKEY_NAME),this).execute();

        new SpotifyAuthAsyncTask().execute();

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {

        } else {
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},PERMISSIONS_REQUEST_CODE);
        }


        try {
            new HttpServer();
        } catch (IOException e) {
            e.printStackTrace();
        }

        new AppUpdateAsyncTask(getContext(),this).execute(githubUpdateURL);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    public void hideNavbar(){
        View decorView = getWindow().getDecorView();
        int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN;
        decorView.setSystemUiVisibility(uiOptions);
    }


    @Override
    public void updateTimeDisplay() {
        Calendar cal = Calendar.getInstance();
        String hour = String.valueOf(cal.get(Calendar.HOUR));
        if (hour.equals("0")){ hour = hour.replace("0","12");}
        String minutes = String.valueOf(cal.get(Calendar.MINUTE));
        if (minutes.length()<2){
            minutes="0"+minutes;
        }
        iBinding.TimeDisplay.setText(hour+":"+minutes);
        int date = cal.get(Calendar.DATE);
        String month = cal.getDisplayName(Calendar.MONTH,Calendar.LONG,Locale.getDefault());
        iBinding.DateDisplay.setText(date+" "+month);
    }



    @Override
    public void refreshWeatherDisplay(GsonWeatherForecastParser forcastjson, GsonCurrentWeatherParser currentConditionsJson) {
        if (forcastjson != null) {
            try {
                float maxTemp = forcastjson.DailyForecasts.get(0).Temperature.Maximum.Value;
                float minTemp = forcastjson.DailyForecasts.get(0).Temperature.Minimum.Value;
                iBinding.TempRange.setText(minTemp + "°C/" + maxTemp + "°C");

            } catch (Exception e) {     //development builds be like
                e.printStackTrace();
            }
        }

        if (currentConditionsJson != null) {
            try {
                iBinding.CurrentTemp.setText(String.valueOf(currentConditionsJson.Temperature.Metric.Value) + "°C");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void refreshSongKickDisplay(final GsonSongKickParser jsonObject) {
        iBinding.SongKickList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        if (songkickRecyclerViewAdapter == null) {
            songkickRecyclerViewAdapter = new SongkickRecyclerViewAdapter(jsonObject, this);
            iBinding.SongKickList.addItemDecoration(new SongKickRecyclerSpacer());
            iBinding.SongKickList.setAdapter(songkickRecyclerViewAdapter);
        }

        songkickRecyclerViewAdapter.calenderEntries = jsonObject.getevents();
        songkickRecyclerViewAdapter.notifyDataSetChanged();

    }


    @Override
    public void installApp(File apkFile) {
        apkFile.setReadable(true,false);
        Intent intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
        intent.addCategory("android.intent.category.DEFAULT");
        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getApplicationContext().getPackageName() + ".provider", apkFile);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.setDataAndType(fileUri, "application/vnd.android.package-archive");
        startActivity(intent);
    }


    @Override
    public void SetKey(String Name, String Key) {
        Util.apiKeyMap.put(Name,Key);
    }

    @Override
    public String GetKey(String Name) {

        if(Util.apiKeyMap.containsKey(Name)){
            return Util.apiKeyMap.get(Name).toString();
        } else {
            return null;
        }
    }


    @Override
    public Context getContext() {
        return getApplicationContext();
    }



    @Override
    protected void onResume() {
        super.onResume();
        hideNavbar();
        registerReceiver(iTimeBasedExecutor, new IntentFilter(Intent.ACTION_TIME_TICK));
        //CMUsetup();
    }

    /**
     * Stop the recognizer.
     * Since cancel() does trigger an onResult() call,
     * we cancel the recognizer rather then stopping it.
     */
    @Override
    protected void onPause() {
        super.onPause();
        if (mRecognizer != null) {
            mRecognizer.removeListener(this);
            mRecognizer.cancel();
            mRecognizer.shutdown();
            Log.d(LOG_TAG, "PocketSphinx Recognizer was shutdown");
        }
        unregisterReceiver(iTimeBasedExecutor);
    }

    /**
     * Setup the Recognizer with a sensitivity value in the range [1..100]
     * Where 1 means no false alarms but many true matches might be missed.
     * and 100 most of the words will be correctly detected, but you will have many false alarms.
     */


    /////CMU Sphinx stuff
    private void CMUsetup() {
        try {
            final Assets assets = new Assets(MainActivity.this);
            final File assetDir = assets.syncAssets();
            mRecognizer = SpeechRecognizerSetup.defaultSetup()
                    .setAcousticModel(new File(assetDir, "models/en-us-ptm"))
                    .setDictionary(new File(assetDir, "models/lm/words.dic"))
                    .setKeywordThreshold(Float.valueOf("1.e-" + 2 * sensitivity))
                    .getRecognizer();
            mRecognizer.addKeyphraseSearch(WAKEWORD_SEARCH, getString(R.string.wake_word));
            mRecognizer.addListener(this);
            mRecognizer.startListening(WAKEWORD_SEARCH);
            Log.d(LOG_TAG, "... listening");
            Toast.makeText(getContext(),"....Listening",Toast.LENGTH_LONG);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.toString());
            Toast.makeText(getContext(),"Couldn't get microphone",Toast.LENGTH_LONG);
        }
    }

    //
    // RecognitionListener Implementation
    //

    @Override
    public void onBeginningOfSpeech() {
        Log.d(LOG_TAG, "Beginning Of Speech");

    }

    @Override
    public void onEndOfSpeech() {
        Log.d(LOG_TAG, "End Of Speech");

    }

    @Override
    public void onPartialResult(final Hypothesis hypothesis) {
        if (hypothesis != null) {
            final String text = hypothesis.getHypstr();
            Log.d(LOG_TAG, "on partial: " + text);
            if (text.equals(getString(R.string.wake_word))) {


                startActivity(new Intent(this, MainActivity.class));
                Toast.makeText(getContext(),"DETECTED HOTWORD!",Toast.LENGTH_SHORT);
                ///////////////
//                Intent intent = new Intent(RecognizerIntent.ACTION_VOICE_SEARCH_HANDS_FREE);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//                intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
//                startActivityForResult(intent, 1);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
        Log.d("Debug", result.toString());

    }

    @Override
    public void onResult(final Hypothesis hypothesis) {
        if (hypothesis != null) {
            Log.d(LOG_TAG, "on Result: " + hypothesis.getHypstr() + " : " + hypothesis.getBestScore());

        }
    }

    @Override
    public void onError(final Exception e) {
        Log.e(LOG_TAG, "on Error: " + e);
    }

    @Override
    public void onTimeout() {
        Log.d(LOG_TAG, "on Timeout");
    }


}