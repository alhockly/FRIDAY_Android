package com.example.friday_android;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.friday_android.databinding.ActivityMainBinding;

import java.util.Calendar;
import java.util.Locale;

import ai.kitt.snowboy.AppResCopy;
import ai.kitt.snowboy.MsgEnum;
import ai.kitt.snowboy.audio.AudioDataSaver;
import ai.kitt.snowboy.audio.PlaybackThread;
import ai.kitt.snowboy.audio.RecordingThread;

/**
 * Skeleton of an Android Things activity.
 * <p>
 * Android Things peripheral APIs are accessible through the class
 * PeripheralManagerService. For example, the snippet below will open a GPIO pin and
 * set it to HIGH:
 *
 * <pre>{@code
 * PeripheralManagerService service = new PeripheralManagerService();
 * mLedGpio = service.openGpio("BCM6");
 * mLedGpio.setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
 * mLedGpio.setValue(true);
 * }</pre>
 * <p>
 * For more complex peripherals, look for an existing user-space driver, or implement one if none
 * is available.
 *
 * @see <a href="https://github.com/androidthings/contrib-drivers#readme">https://github.com/androidthings/contrib-drivers#readme</a>
 */
public class MainActivity extends Activity implements IModifyUI{
    TimeBroadcastReceiver iTimeBroadcastReceiver;
    ActivityMainBinding iBinding;
    GsonWeatherParser WeatherJson;

    String londonKey = "328328";
    String accuWeatherKey = "jhAiVVyMWM8sE77cwPMxBZzeGMJYuamP";
    String weatherUrl = "https://dataservice.accuweather.com/forecasts/v1/daily/5day/"+londonKey+"?apikey="+ accuWeatherKey +"&metric=true";

    boolean isDetectionOn;
    private int preVolume = -1;
    private static long activeTimes = 0;

    private RecordingThread recordingThread;
    private PlaybackThread playbackThread;

    private TextView log;
    private ScrollView logView;
    static String strLog = null;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        iTimeBroadcastReceiver = new TimeBroadcastReceiver(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
        registerReceiver(iTimeBroadcastReceiver,filter);
        iBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        //initHotword();
        updateTimeDisplay();
        refreshSongKickData();
        updateWeather();

//        if (ActivityCompat.checkSelfPermission(this,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
//                ActivityCompat.checkSelfPermission(this,
//                        Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
//                initHotword();
//        } else {
//            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
//        }


        setProperVolume();

        AppResCopy.copyResFromAssetsToSD(this);

        activeTimes = 0;
        recordingThread = new RecordingThread(handle, new AudioDataSaver());
        playbackThread = new PlaybackThread();

        stopPlayback();
        sleep();
        startRecording();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

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

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(iTimeBroadcastReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(iTimeBroadcastReceiver);
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
    public void updateWeather() {
        new AccuweatherAsyncTask(weatherUrl, this).execute();

    }

    @Override
    public void refreshWeatherDisplay(GsonWeatherParser jsonObject) {

        if (jsonObject.DailyForecasts != null) {
            WeatherJson = jsonObject;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    // Stuff that updates the UI
                    try {
                        float maxTemp = WeatherJson.DailyForecasts.get(0).Temperature.Maximum.Value;
                        float minTemp = WeatherJson.DailyForecasts.get(0).Temperature.Minimum.Value;
                        iBinding.CurrentTemp.setText(minTemp+ "°C /"+maxTemp+"°C");

                    } catch (Exception e) {     //development builds be like
                        e.printStackTrace();
                    }
                }
            });

        }

    }

    @Override
    public void refreshSongKickDisplay(final GsonSongKickParser jsonObject) {
        if (jsonObject != null){
            final IModifyUI mod = this;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    iBinding.SongKickList.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                    SongkickRecyclerViewAdapter adapter = new SongkickRecyclerViewAdapter(jsonObject,mod);
                    iBinding.SongKickList.addItemDecoration(new SongKickRecyclerSpacer());
                    iBinding.SongKickList.setAdapter(adapter);

                }
            });
        }
    }


    @Override
    public void refreshSongKickData() {
        new SongKickAyncTask(this).execute();
    }

    @Override
    public Context getContext() {
        return getApplicationContext();
    }


    private void setMaxVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> preVolume = "+preVolume, "green");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> maxVolume = "+maxVolume, "green");
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> currentVolume = "+currentVolume, "green");
    }

    private void setProperVolume() {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        preVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> preVolume = "+preVolume, "green");
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> maxVolume = "+maxVolume, "green");
        int properVolume = (int) ((float) maxVolume * 0.2);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, properVolume, 0);
        int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        updateLog(" ----> currentVolume = "+currentVolume, "green");
    }

    private void restoreVolume() {
        if(preVolume>=0) {
            AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, preVolume, 0);
            updateLog(" ----> set preVolume = "+preVolume, "green");
            int currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            updateLog(" ----> currentVolume = "+currentVolume, "green");
        }
    }

    void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void startRecording() {
        recordingThread.startRecording();
        updateLog(" ----> recording started ...", "green");

    }

    private void stopRecording() {
        recordingThread.stopRecording();
        updateLog(" ----> recording stopped ", "green");

    }

    private void startPlayback() {
        updateLog(" ----> playback started ...", "green");

        // (new PcmPlayer()).playPCM();
        playbackThread.startPlayback();
    }

    private void stopPlayback() {
        updateLog(" ----> playback stopped ", "green");

        playbackThread.stopPlayback();
    }

    private void sleep() {
        try { Thread.sleep(500);
        } catch (Exception e) {}
    }



    public Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            MsgEnum message = MsgEnum.getMsgEnum(msg.what);
            switch(message) {
                case MSG_ACTIVE:
                    activeTimes++;
                    updateLog(" ----> Detected " + activeTimes + " times", "green");
                    // Toast.makeText(Demo.this, "Active "+activeTimes, Toast.LENGTH_SHORT).show();
                    showToast("Active "+activeTimes);
                    break;
                case MSG_INFO:
                    updateLog(" ----> "+message);
                    break;
                case MSG_VAD_SPEECH:
                    updateLog(" ----> normal voice", "blue");
                    break;
                case MSG_VAD_NOSPEECH:
                    updateLog(" ----> no speech", "blue");
                    break;
                case MSG_ERROR:
                    updateLog(" ----> " + msg.toString(), "red");
                    break;
                default:
                    super.handleMessage(msg);
                    break;
            }
        }
    };

    public void updateLog(final String text) {

        log.post(new Runnable() {
            @Override
            public void run() {
                if (currLogLineNum >= MAX_LOG_LINE_NUM) {
                    int st = strLog.indexOf("<br>");
                    strLog = strLog.substring(st+4);
                } else {
                    currLogLineNum++;
                }
                String str = "<font color='white'>"+text+"</font>"+"<br>";
                strLog = (strLog == null || strLog.length() == 0) ? str : strLog + str;
                log.setText(Html.fromHtml(strLog));
            }
        });
//        logView.post(new Runnable() {
//            @Override
//            public void run() {
//                logView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
    }

    static int MAX_LOG_LINE_NUM = 200;
    static int currLogLineNum = 0;

    public void updateLog(final String text, final String color) {
//        log.post(new Runnable() {
//            @Override
//            public void run() {
//                if(currLogLineNum>=MAX_LOG_LINE_NUM) {
//                    int st = strLog.indexOf("<br>");
//                    strLog = strLog.substring(st+4);
//                } else {
//                    currLogLineNum++;
//                }
//                String str = "<font color='"+color+"'>"+text+"</font>"+"<br>";
//                strLog = (strLog == null || strLog.length() == 0) ? str : strLog + str;
//                log.setText(Html.fromHtml(strLog));
//            }
//        });
//        logView.post(new Runnable() {
//            @Override
//            public void run() {
//                logView.fullScroll(ScrollView.FOCUS_DOWN);
//            }
//        });
    }

    private void emptyLog() {
        strLog = null;
        log.setText("");
    }

    @Override
    public void onDestroy() {
        restoreVolume();
        recordingThread.stopRecording();
        super.onDestroy();
    }

}
